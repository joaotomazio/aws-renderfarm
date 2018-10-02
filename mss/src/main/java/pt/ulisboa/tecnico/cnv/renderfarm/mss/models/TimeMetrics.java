package pt.ulisboa.tecnico.cnv.renderfarm.mss.models;

public class TimeMetrics {

	private Long instructions;
	private Long time;

	public TimeMetrics(String instructions, String time){
		this.instructions = Long.parseLong(instructions);
		this.time = Long.parseLong(time);
	}

	public TimeMetrics(Long instructions){
		this.instructions = instructions;
	}

	public Long getInstructions(){
		return this.instructions;
	}

	public Long getTime(){
		return this.time;
	}
}
