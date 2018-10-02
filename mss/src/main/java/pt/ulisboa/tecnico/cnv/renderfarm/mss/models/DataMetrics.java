package pt.ulisboa.tecnico.cnv.renderfarm.mss.models;

import java.util.HashMap;
import java.util.Map;

public class DataMetrics {

	private String file;
	private Long sc;
	private Long sr;
	private Long wc;
	private Long wr;
	private Long coff;
	private Long roff;
	private Long instructions;

	public DataMetrics(String file,String sc, String sr, String wc, String wr, String coff, String roff, String instructions){
		this.file = file;
		this.sc = Long.parseLong(sc);
		this.sr = Long.parseLong(sr);
		this.wc = Long.parseLong(wc);
		this.wr = Long.parseLong(wr);
		this.coff = Long.parseLong(coff);
		this.roff = Long.parseLong(roff);
		this.instructions = Long.parseLong(instructions);
	}

	public DataMetrics(HashMap<String, String> query, Long instructions){
		this.instructions = instructions;
		this.file = query.get("f");
		this.sc = Long.parseLong(query.get("sc"));
		this.sr = Long.parseLong(query.get("sr"));
		this.wc = Long.parseLong(query.get("wc"));
		this.wr = Long.parseLong(query.get("wr"));
		this.coff = Long.parseLong(query.get("coff"));
		this.roff = Long.parseLong(query.get("roff"));
	}

	public String getFile(){
		return this.file ;
	}
	public Long getSc(){
		return this.sc;
	}
	public Long getSr(){
		return this.sr;
	}
	public Long getWc(){
		return this.wc ;
	}
	public Long getWr(){
		return this.wr ;
	}
	public Long getCoff(){
		return this.coff ;
	}
	public Long getRoff(){
		return this.roff;
	}
	public Long getInstructions(){
		return this.instructions;
	}

	@Override
	public String toString(){
		return this.getFile() + " " + this.getSc() + " " + this.getSr() + " " + this.getWc() + " " +
		this.getWr() + " " + this.getCoff() + " " + this.getRoff() + " " + this.getInstructions();
	}
}
