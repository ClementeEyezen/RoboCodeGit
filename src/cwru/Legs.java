package cwru;

public class Legs extends Brain
{
	double moveEndTheta;
	double moveEndDistance;
	public Legs(LifeBox source) 
	{
		super(source);
	}
	//Legs is the brain that controls movement of the robot
	public void process()
	{
		long processTime = System.currentTimeMillis();
		moveEndTheta = choose_radians_to_turn_left(); //calculate desired theta
		moveEndDistance = 0; //calculate desired distance
		long totalTime = System.currentTimeMillis()-processTime;
		System.out.println("MOV calc time (millis):"+totalTime);
	}
	public void set()
	{
		source.mainRobot.setTurnLeftRadians(moveEndTheta);
		source.mainRobot.setAhead(moveEndDistance);
	}
	public double choose_radians_to_turn_left()
	{
		double radian_result = 0;
		//calculate the radians to turn left
		if (robot_near_wall())//if the robot is near a wall
		{
			//move to center
			double width = source.battlefield_width;
			double heigh = source.battlefield_height;
			double randomW = (width/4)+Math.random()*(width/2);
			double randomH = (heigh/4)+Math.random()*(heigh/2);
			radian_result = direction_to_point(source.mainRobot.getX(), source.mainRobot.getY(),
					randomW, randomH);
		}
		else if (source.getRobot().getTurnRemainingRadians()>0)
		{
			//if it was turning, let it keep turning
			radian_result = source.getRobot().getTurnRemainingRadians();
		}
		else if (source.getRobot().getDistanceRemaining()>0)
		{
			//if it is still moving from a prior "set"
			radian_result = 0;
		}
		else
		{
			//do the normal calculation thing
			//do a random walk
		}
		return radian_result;
	}
	public double choose_distance_to_move()
	{
		double distance_result = 0;
		//calculate the radians to turn left
		return distance_result;
	}
	public boolean robot_near_wall()
	{
		boolean near_wall = false;
		if (source.getRobot().getX()<50 || 
				source.getRobot().getX()>(source.battlefield_width-50))
		{
			near_wall =  true;
		}
		if (source.getRobot().getY()<50 || 
				source.getRobot().getY()>(source.battlefield_height-50))
		{
			near_wall =  true;
		}
		return near_wall;
	}
	public double direction_to_point(double px,double py, double mx, double my)
	{
		double radians_return = 0;
		//TODO calculate the angle to a point
		Math.atan2(py-my, px-mx);
		return radians_return;
	}
}
