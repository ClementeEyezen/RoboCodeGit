package arc;

public class MoveBrain 
{
	DataBox store;
	ArcBasicBot r;
	public MoveBrain(DataBox data)
	{
		store = data;
		r = data.getRobot();
	}
	public void process()
	{
		//do what it needs to do to calculate information about what it needs to do, should result in calling of movements
		//either move to Point via driveRobotTo(Point p)
		//or set direction and heading via driveRobotTo(double direction, double distance);
	}
}
