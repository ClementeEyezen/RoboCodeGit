package arc.model;

public class TargetSolution {
	boolean fire;
	double targetting_heading;
	double fire_power;
	
	public TargetSolution(boolean fire, double heading, double power) {
		this.fire = fire;
		targetting_heading = heading;
		fire_power = power;
	}
	
	public boolean fire() {
		return fire;
	}
	public double heading() {
		return targetting_heading;
	}
	public double power() {
		return fire_power;
	}

}
