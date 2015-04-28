package arc.model.target;

public class Wave {
	double f_x, f_y;
	//double s_x, s_y;
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
	
	public double percent_escape(double x_start, double y_start, double x_now, double y_now) {
		
		// double time = distance(x_start, y_start)/velocity();
		// s = r theta. r is dist, s = max travel = time * 8
		// time = distance / velocity
		// theta = time * 8 / dist();
		// theta = 8 * (dist / vel) / dist
		// theta = 8 / vel
		
		double max_angle = 8 / velocity();
		// difference in angle between start and finish
		double current_angle = Math.atan2(x_now-f_x,y_now-f_y) - Math.atan2(x_start-f_x,y_start-f_y);

		return current_angle/max_angle;
	}
	
	public boolean check_beyond(double x, double y, long time) {
		// returns true if the wave is passed the x y position
		// used to filter possible hits/etc. for wave
		return (distance(x,y) < (time-create_time)*velocity());
	}
	public double distance(double x,double y) {
		return Math.sqrt((x-f_x)*(x-f_x) + (y-f_y)*(y-f_y));
	}
	public double velocity() {
		if (20.0 - 3.0 * energy < .005) {
			return .005;
		}
		return 20.0-3.0*energy;
	}
}
