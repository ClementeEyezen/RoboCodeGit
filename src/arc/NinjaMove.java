package arc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class NinjaMove extends MoveBrain
{
	ArrayList<Point> eL1;
	ArrayList<Point> eL2;
	ArrayList<Point> ax1;
	ArrayList<Point> ax2;
	double roomWidth;
	double roomHeight;
	public NinjaMove(DataBox data) 
	{
		super(data);
		eL1 = new ArrayList<Point>();
		eL2 = new ArrayList<Point>();
		ax1 = new ArrayList<Point>();
		ax2 = new ArrayList<Point>();
		roomWidth = r.roomWidth;
		roomHeight = r.roomHeight;
	}
	@Override
	public void process()
	{
		//do what it needs to do to calculate information about what it needs to do, should result in calling of movements
		// move to Point via driveRobotTo(Point p)
		Point driveMeHere;
		//if there is only one other robot, revolve around it at radius varying from 100 to 200, with random movement ranging from 4-8
		if (store.getOpCount()<1)
		{
			driveMeHere = new Point(0,0);	
		}
		else if (store.getOpCount()==1)
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
			Point newPoint = new Point(desX, desY);
			Point retPoint = fitToRoom(newPoint);
			eL1.add(e);
			eL2.add(e);
			System.out.println("ax1 x:"+retPoint.getPoint()[0]+" y:"+retPoint.getPoint()[1]);
			ax1.add(retPoint);
			System.out.println("ax2 x:"+retPoint.getPoint()[0]+" y:"+retPoint.getPoint()[1]);
			ax2.add(retPoint);
			driveMeHere = retPoint;
		}
		else
		{
			//drive to the point to the nearest axis of two other robots 100 away from the nearer robot
			//next step is to move to the intersections of two axis or more, or 100 away points
			Point e1 = store.getOpponents().get(0).getLocation();
			Point e2 = store.getOpponents().get(1).getLocation();
			double b1to2 = Math.atan2(e2.coords[0]-e1.coords[0], e2.coords[1]-e1.coords[1]);
			double b2to1 = b1to2+Math.PI;
			b1to2 = -b1to2+(Math.PI/2);
			b2to1 = -b2to1+(Math.PI/2);
			Point axis1 = fitToRoom(new Point( e1.coords[0]+100*Math.cos(b2to1), e1.coords[1]+100*Math.sin(b2to1))); //end of the axis on e1 side
			Point axis2 = fitToRoom(new Point( e2.coords[0]+100*Math.cos(b1to2), e2.coords[1]+100*Math.sin(b1to2))); //end of the axis on e2 side
			eL1.add(e1);
			eL2.add(e2);
			System.out.println("ax1 "+ax1.size()+" x:"+axis1.getPoint()[0]+" y:"+axis1.getPoint()[1]);
			System.out.println("ax2 "+ax1.size()+" x:"+axis2.getPoint()[0]+" y:"+axis2.getPoint()[1]);
			ax1.add(axis1);
			ax2.add(axis2);
			// the goal is to make it so that if another robot shoots at it, it uses the other robot as cover
			if (r.selfPoint().distance(axis1) < r.selfPoint().distance(axis2))
			{
				driveMeHere = new Point(axis1.coords[0],axis1.coords[1]);
			}
			else
			{
				driveMeHere = new Point(axis2.coords[0],axis2.coords[1]);
			}
		}
		cleanUpLists();
		r.driveRobotTo(driveMeHere);
	}
	public void cleanUpLists()
	{
		if (eL1.size()>100) eL1.remove(0);
		if (eL2.size()>100) eL2.remove(0);
		if (ax1.size()>100) ax1.remove(0);
		if (ax2.size()>100) ax2.remove(0);
	}
	public Point fitToRoom(Point p)
	{
		System.out.println("Room dimensions: "+roomWidth+","+roomHeight);
		double x = p.getPoint()[0];
		double y = p.getPoint()[1];
		if (x>roomWidth-18) x = roomWidth-20;
		if (x<18) x = 20;
		if (y>roomHeight-18) y = roomHeight-20;
		if (y<18) y = 20;
		return new Point(x,y);
	}
	public void drawData(Graphics2D g)
	{
		System.out.println("Drawing NinjaMove data");
		g.setColor(Color.MAGENTA);
//		for (int i = 0; i<eL1.size()-1; i++)
//		{
//			System.out.println("Drawing el1");
//			int x1 = (int) eL1.get(i).getPoint()[0];
//			int y1 = (int) eL1.get(i).getPoint()[1];
//			int x2 = (int) eL1.get(i+1).getPoint()[0];
//			int y2 = (int) eL1.get(i+1).getPoint()[1];
//			//draw a line along the robot path
//			g.drawLine(x1, y1, x2, y2);
//		}
//		g.setColor(Color.RED);
//		for (int i = 0; i<eL2.size()-1; i++)
//		{
//			int x1 = (int) eL2.get(i).getPoint()[0];
//			int y1 = (int) eL2.get(i).getPoint()[1];
//			int x2 = (int) eL2.get(i+1).getPoint()[0];
//			int y2 = (int) eL2.get(i+1).getPoint()[1];
//			//draw a line along the robot path
//			g.drawLine(x1, y1, x2, y2);
//		}
		g.setColor(Color.CYAN);
		for (int i = 0; i<ax1.size()-1; i++)
		{
			g.drawString("Drawing ax1", 200, 200);
			int x1 = (int) ax1.get(i).getPoint()[0];
			int y1 = (int) ax1.get(i).getPoint()[1];
			int x2 = (int) ax1.get(i+1).getPoint()[0];
			int y2 = (int) ax1.get(i+1).getPoint()[1];
			//draw a line along the robot path
			g.drawLine(x1, y1, x2, y2);
		}
		g.setColor(Color.BLUE);
		for (int i = 0; i<ax2.size()-1; i++)
		{
			g.drawString("Drawing ax2", 200, 190);
			int x1 = (int) ax2.get(i).getPoint()[0];
			int y1 = (int) ax2.get(i).getPoint()[1];
			int x2 = (int) ax2.get(i+1).getPoint()[0];
			int y2 = (int) ax2.get(i+1).getPoint()[1];
			//draw a line along the robot path
			g.drawLine(x1, y1, x2, y2);
		}
		g.setColor(Color.ORANGE);
		g.drawLine((int) ax1.get(ax2.size()-1).getPoint()[0], (int) ax1.get(ax2.size()-1).getPoint()[1], 
					(int) ax2.get(ax2.size()-1).getPoint()[0], (int) ax2.get(ax2.size()-1).getPoint()[1]);
	}
}