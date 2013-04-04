package arc;

import java.awt.Color;
import java.awt.Graphics2D;

public class ShoGun extends GunBrain 
{
	double leftBound;
	double rightBound;
	boolean turnLeft = true;
	public ShoGun(DataBox data) 
	{
		super(data);
	}
	public void process()
	{
		int oppCount = store.getOpponents().size();
		if (oppCount == 0)
		{
			//do nothing
		}
		else if (oppCount == 1)
		{
			//choose that one robot
			VirtualBot target = store.getOpponents().get(0);
			double tx = target.getLocation().coords[0];
			double ty = target.getLocation().coords[1];
			double mx = r.getX();
			double my = r.getY();
			double dx = tx-mx;
			double dy = ty-my;
			double degree = Math.atan2(dx, dy);
			double rotDeg = degree;
			leftBound = rotDeg+.35;
			rightBound = rotDeg-.35;
			System.out.println("left = "+leftBound+" right = "+rightBound);
		}
		else
		{
			//choose first active robot
		}
		//now that we have updated left and rights, figure out where to move
		if (turnLeft)
		{
			r.moveGunTo(leftBound);
			if (r.getGunTurnRemainingRadians()<.001)
			{
				turnLeft = false;
			}
		}
		else
		{
			r.moveGunTo(rightBound);
			if (r.getGunTurnRemainingRadians()<.001)
			{
				turnLeft = true;
			}
		}
		if (r.getGunHeat()<=.001);
		{
			r.setFire(.1);
		}
	}
	public void drawData(Graphics2D g)
	{
		g.setColor(Color.GREEN);
		g.drawString("500", 500, 500);
		double mx = r.getX();
		double my = r.getY();
		double dx = Math.cos(leftBound);
		double dy = Math.sin(leftBound);
		g.drawLine((int) mx, (int) my, (int) (mx+dx), (int) (my+dy));
		dx = Math.cos(rightBound);
		dy = Math.sin(rightBound);
		g.drawLine((int) mx, (int) my, (int) (mx+dx), (int) (my+dy));
	}
}
