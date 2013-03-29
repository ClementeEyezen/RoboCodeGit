package arc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;

public class NinjaDar extends RadarBrain
{
	ArrayList<Double> opBearings = new ArrayList<Double>(); //the most up to date bearings for any robot, not rotated
	ArrayList<Double> deltaBearing = new ArrayList<Double>(); //the differences between each of the robots, the last is last to first
	double counterLeft = 0;
	double clockRight = 0;
	public NinjaDar(DataBox data) 
	{
		super(data);
	}
	public void process()
	{
		if (store.getOpCount()<=1 || r.getTime()<17 || store.getOpCount()>=8)
		{
			r.moveRadarTo(Double.POSITIVE_INFINITY);
		}
		else
		{
			double turnHere = 0; //radians
			boolean clockOrNot = true;
			for (VirtualBot v: store.getOpponents())
			{
				double ex = v.getLocation().coords[0];
				double ey = v.getLocation().coords[1];
				double mx = r.getX();
				double my = r.getY();
				double dx = ex-mx;
				double dy = ey-my;
				double bearing = Math.atan2(dy, dx);
				opBearings.add(bearing);
			}
			Collections.sort(opBearings);
			for( int i = 0; i<store.getOpCount()-1; i++)
			{
				deltaBearing.add(opBearings.get(i+1)-opBearings.get(i));
			}
			double lastDelta = opBearings.get(0) - opBearings.get(store.getOpCount()-1);
			deltaBearing.add(lastDelta);
			int bigGapInt = store.getOpCount()-1;
			counterLeft = opBearings.get(store.getOpCount()-1);
			clockRight = opBearings.get(0);
			for (int i = 0; i<store.getOpCount()-1; i++)
			{
				if (deltaBearing.get(i)>deltaBearing.get(bigGapInt))
				{
					bigGapInt = i;
					counterLeft = opBearings.get(i);
					counterLeft = opBearings.get(i+1);
				}
			}
			double cRaHead = r.getRadarHeadingRadians();
			if (clockOrNot)
			{
				turnHere = Math.min(cRaHead+(Math.PI/4),clockRight);
			}
			else
			{
				turnHere = Math.max(cRaHead-(Math.PI/4),counterLeft);
			}
			r.moveRadarTo(turnHere);
		}
	}
	public void drawData(Graphics2D g)
	{
		g.setColor(Color.BLUE);
		g.fillArc((int) (r.getX()-20), (int) (r.getY()+20), 40, 40, (int) (counterLeft), (int) (clockRight-counterLeft));
	}
}
