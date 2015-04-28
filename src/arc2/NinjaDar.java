package arc2;

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
			System.out.println("not normal @Time "+r.getTime()+" @ OpCount "+store.getOpCount());
			r.moveRadarTo(Double.POSITIVE_INFINITY);
		}
		else
		{
			System.out.println("minimizing radar arc @Time"+r.getTime());
			double turnHere = Double.POSITIVE_INFINITY;
			
			r.moveRadarTo(turnHere);
		}
	}
	public void drawData(Graphics2D g)
	{
		System.out.println("drawing NinjaDar data");
		g.setColor(Color.BLUE);
		g.fillArc((int) (r.getX()-20), (int) (r.getY()+20), 40, 40, (int) (counterLeft), (int) (clockRight-counterLeft));
		g.drawString("abc test String dar1", 300, 400);
		int x1 = (int) r.getX();
		int y1 = (int) r.getY();
		int x2 = (int) (x1 + 100*Math.cos(clockRight));
		int y2 = (int) (y1 + 100*Math.sin(clockRight));
		g.setColor(Color.ORANGE);
		g.drawLine(x1, y1, x2, y2);
		x2 = (int) (x1 + 100*Math.cos(counterLeft));
		y2 = (int) (y1 + 100*Math.sin(counterLeft));
		g.setColor(Color.YELLOW);
		g.drawLine(x1, y1, x2, y2);
	}
}
