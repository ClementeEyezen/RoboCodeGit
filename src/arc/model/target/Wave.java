package arc.model.target;

public class Wave {
	double f_x, f_y;
	double s_x, s_y;
	double radius;
	double energy;
	long create_time;
	
	public Wave(double fired_x, double fired_y, long time, double delta_energy, 
			double self_x, double self_y) {
		this.f_x = fired_x;
		this.f_y = fired_y;
		create_time = time;
		energy = delta_energy;
	}
	
	// TODO start here
	// maximum escape angle, percent of above, projection integration
	
	public boolean check_past(double x, double y, long time) {
		// returns true if the wave is passed the x y position
		// used to filter possible hits/etc. for wave
		return (distance(x,y) < (time-create_time)*velocity());
	}
	public double distance(double x,double y) {
		return Math.sqrt((x-f_x)*(x-f_x) + (y-f_y)*(y-f_y));
	}
	public double velocity() {
		return 20.0-3.0*energy;
	}
}
