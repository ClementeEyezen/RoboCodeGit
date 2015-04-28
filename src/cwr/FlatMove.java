package cwr;

import robocode.Robot;

public class FlatMove extends Legs 
{
	public boolean movingRightNow = false;
	public double goalx = 500;
	public double goaly = 500;

	public FlatMove(LifeBox source, cwruBase cwruBase) 
	{
		super(source, cwruBase);
		// TODO Auto-generated constructor stub
	}

	public void process()
	{
		if (distance_to_point(source.getRobot().cx,source.getRobot().cy,goalx,goaly)<5)
		{
			movingRightNow = false;
		}
		if (!movingRightNow)
		{
			pick_a_new_move();
			movingRightNow = true;
		}
	}
	
	public void pick_a_new_move()
	{
		moveEndTheta = 0;
		moveEndDistance = 100;
		if( source.ronny.size() != 0)
		{
			RoboCore focus = source.ronny.get(0);
			double opX = focus.lastX;
			double opY = focus.lastY;
			double meX = source.getRobot().cx;
			double meY = source.getRobot().cy;
			double angleToOp = direction_to_point( opX , meX , opY , meY );
			double travelAngle = angleToOp+(Math.PI/2);
			moveEndTheta = travelAngle;
			
		}
	}

}
