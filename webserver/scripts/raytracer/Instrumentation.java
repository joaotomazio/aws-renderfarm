
import java.io.*;
import java.util.*;
import BIT.highBIT.*;
import BIT.lowBIT.*;

public class Instrumentation{

	public static long dyn_method_count = 0;
	public static long dyn_bb_count = 0;
	public static long dyn_instr_count = 0;

	public static long newcount = 0;
	public static long newarraycount = 0;
	public static long anewarraycount = 0;
	public static long multianewarraycount = 0;

	public static long loadcount = 0;
	public static long storecount = 0;
	public static long fieldloadcount = 0;
	public static long fieldstorecount = 0;

	public static void main(String[] args){
		File file_in = new File(args[0]);
		String[] infilenames = file_in.list();
		for (int i = 0; i < infilenames.length; i++) {
			String infilename = infilenames[i];
			if (!infilename.endsWith(".class")) continue;
			instrumentate(args[0]+'/', args[1]+'/', infilename);
		}
	}

	public static void instrumentate(String source_folder, String output_folder, String filename){

		ClassInfo ci = new ClassInfo(source_folder+filename);
		ClassFile cf = ci.getClassFile();
		java.util.Vector routines = ci.getRoutines();
		System.out.println("---STATIC ANALYSIS---");
		System.out.println("Class name:    " + ci.getClassName());
		System.out.println("Class size:    " + cf.size());
		System.out.println("Methods count: " + routines.size());
		for (Enumeration e = routines.elements(); e.hasMoreElements();) {
			Routine routine = (Routine) e.nextElement();
			BasicBlockArray bba = routine.getBasicBlocks();
			InstructionArray ia = routine.getInstructionArray();
			routine.addBefore("Instrumentation", "dynMethodCount", new Integer(1));
			System.out.println("Method name:        " + routine.getMethodName());
			System.out.println("Basic block count:  " + bba.size());
			System.out.println("Instructions count: " + ia.size());
			for (Enumeration b = bba.elements(); b.hasMoreElements(); ) {
				BasicBlock bb = (BasicBlock) b.nextElement();
				bb.addBefore("Instrumentation", "dynInstrCount", new Integer(bb.size()));
			}
			for (Enumeration instrs = ia.elements(); instrs.hasMoreElements(); ) {
				Instruction instr = (Instruction) instrs.nextElement();
				int opcode=instr.getOpcode();
				if ((opcode==InstructionTable.NEW) ||
				(opcode==InstructionTable.newarray) ||
				(opcode==InstructionTable.anewarray) ||
				(opcode==InstructionTable.multianewarray)) {
					instr.addBefore("Instrumentation", "allocCount", new Integer(opcode));
				}
				if (opcode == InstructionTable.getfield)
				instr.addBefore("Instrumentation", "LSFieldCount", new Integer(0));
				else if (opcode == InstructionTable.putfield)
				instr.addBefore("Instrumentation", "LSFieldCount", new Integer(1));
				else {
					short instr_type = InstructionTable.InstructionTypeTable[opcode];
					if (instr_type == InstructionTable.LOAD_INSTRUCTION) {
						instr.addBefore("Instrumentation", "LSCount", new Integer(0));
					}
					else if (instr_type == InstructionTable.STORE_INSTRUCTION) {
						instr.addBefore("Instrumentation", "LSCount", new Integer(1));
					}
				}
			}
		}
		ci.addAfter("Instrumentation", "printDynamic", "null");
		ci.addAfter("Instrumentation", "printAlloc", "null");
		ci.addAfter("Instrumentation", "printLoadStore", "null");
		ci.write(output_folder + filename);

		dyn_method_count = 0;
		dyn_bb_count = 0;
		dyn_instr_count = 0;

		newcount = 0;
		newarraycount = 0;
		anewarraycount = 0;
		multianewarraycount = 0;

		loadcount = 0;
		storecount = 0;
		fieldloadcount = 0;
		fieldstorecount = 0;
	}

	public static synchronized void dynMethodCount(int incr){
		dyn_method_count++;
	}

	public static synchronized void dynInstrCount(int incr){
		dyn_instr_count += incr;
		dyn_bb_count++;
	}

	public static synchronized void allocCount(int type){
		switch(type) {
			case InstructionTable.NEW:
			newcount++;
			break;
			case InstructionTable.newarray:
			newarraycount++;
			break;
			case InstructionTable.anewarray:
			anewarraycount++;
			break;
			case InstructionTable.multianewarray:
			multianewarraycount++;
			break;
		}
	}

	public static synchronized void LSFieldCount(int type){
		if (type == 0)
		fieldloadcount++;
		else
		fieldstorecount++;
	}

	public static synchronized void LSCount(int type){
		if (type == 0)
		loadcount++;
		else
		storecount++;
	}

	public static synchronized void printDynamic(String foo){
		System.out.println("---DYNAMIC ANALYSIS---");
		System.out.println("Methods count:" + dyn_method_count);
		System.out.println("Basic blocks count:" + dyn_bb_count);
		System.out.println("Instructions count:" + dyn_instr_count);
	}

	public static synchronized void printAlloc(String s){
		System.out.println("Objects allocated:" + newcount);
		System.out.println("Arrays of primitive types allocated:" + newarraycount);
		System.out.println("Arrays of complex types allocated:" + anewarraycount);
		System.out.println("Multidimensaional arrays allocated:" + multianewarraycount);
	}

	public static synchronized void printLoadStore(String s){
		System.out.println("Fields fetched from Objects:" + fieldloadcount);
		System.out.println("Fields set on Objects:" + fieldstorecount);
		System.out.println("Load Instructions:" + loadcount);
		System.out.println("Store Instructions:" + storecount);
	}
}
