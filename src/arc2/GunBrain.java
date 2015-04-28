package arc2;

import java.awt.Graphics2D;
import java.awt.Color;

public class GunBrain 
{
	DataBox store;
	double power = 1;
	double direction = 0;
	ArcBasicBot r;
	public GunBrain(DataBox data)
	{
		store = data;
		r = data.getRobot();
	}
	public void process()
	{
		//do what it needs to do to calculate information about what it needs to do, should result in calling of movement and firing for the gun
		double pow = getPower();
		if (pow > 0) r.setGunFire(pow);
		r.moveGunTo(direction);
	}
	public void drawData(Graphics2D g)
	{
		double rotDir = -(direction-(Math.PI/2));
		int x1 = (int) r.getX();
		int y1 = (int) r.getY();
		int x2 = (int) (x1 + 100*Math.cos(rotDir));
		int y2 = (int) (y1 + 100*Math.sin(rotDir));
		g.setColor(Color.MAGENTA);
		g.drawLine(x1, y1, x2, y2);
	}
	public double getPower()
	{
		//return the desired power for the bullet
		calcPower();
		if (fireQ()) return power;
		else return -1;
	}
	public void calcPower()
	{
		power = 1;
	}
	public double getDirection()
	{
		calcDirection();
		return direction;
	}
	public void calcDirection()
	{
		Point target = new Point(-10000,-10000);
		double minDis = 10000;
		double distance = 0;
		double dx = 0;
		double dy = 0;
		for (VirtualBot vb : store.opponents)
		{
			distance = vb.location.get(vb.location.size()-1).distance(r.selfPoint()); //other point
			if (distance<minDis)
			{
				dx = vb.location.get(vb.location.size()-1).getPoint()[0]-r.selfPoint().getPoint()[0];
				dy = vb.location.get(vb.location.size()-1).getPoint()[1]-r.selfPoint().getPoint()[1];
			}
		}
		direction = Math.atan2(dx, dy);
		//System.out.println("desired gun direction: "+direction);
	}
	public boolean fireQ()
	{
		//System.out.println("Close to firing? "+(Math.abs(ArcBasicBot.minimizeRotation(store.getGunDirection(),getDirection()))));
		if (Math.abs(ArcBasicBot.minimizeRotation(store.getGunDirection(),getDirection()))<=.02)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
