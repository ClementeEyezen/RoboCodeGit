package arc;

public class NinjaMove extends MoveBrain
{
	
	public NinjaMove(DataBox data) 
	{
		super(data);
	}
	@Override
	public void process()
	{
		//do what it needs to do to calculate information about what it needs to do, should result in calling of movements
		//either move to Point via driveRobotTo(Point p)
		//or set direction and heading via driveRobotTo(double direction, double distance);
		
		//drive in a circle at max speed and max turn rate
		Point driveMeHere;
		//if there is only one other robot, revolve around it at radius 100, with random movement ranging from 4-8
		if (store.getOpCount()<=1)
		{
			Point e = store.getOpponents().get(0).getLocation();
			Point me = store.getRobot().selfPoint();
			double distance = e.distance(me);
			double bearing = e.bearing(me);
			double randRange = Math.random()*2-1; //random number range -1 to 1
			double desiredBearing = bearing+(8/100); //move myself a little further around the circle
			double desiredDistance = 150+randRange*50; //desired distance between 100 and 200
			double ex = e.getPoint()[0];
			double ey = e.getPoint()[1];
			double mx = me.getPoint()[0];
			double my = me.getPoint()[1];
			double rB = (desiredBearing+(Math.PI/2)); //rotated bearing
			double desX = mx + desiredDistance*Math.cos(rB);
			double desY = my + desiredDistance*Math.sin(rB);
			driveMeHere = new Point(desX, desY);
		}
		else
		{
			//drive to the point to the nearest axis of two other robots 100 away from the nearer robot
			//next step is to move to the intersections of two axis or more, or 100 away points
			
			// the goal is to make it so that if another robot shoots at it, it uses the other robot as cover
			driveMeHere = new Point(0,0);
		}
		r.driveRobotTo(driveMeHere);
	}
}