package arc.robots;

public class Twist {
	double linear_velocity;
	double angular_velocity;
	
	public Twist(double v, double w) {
		linear_velocity = v;
		angular_velocity = w;
	}

	public double angular() {
		return angular_velocity;
	}
	public double linear() {
		return linear_velocity;
	}
}
