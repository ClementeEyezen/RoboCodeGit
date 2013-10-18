package cwru;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import robocode.Rules;

public class Sprinter extends Legs implements Paintable
{
	//Sprinter is a wave surfing movement extension of legs
	ArrayList<WaveModel> surf_bum = new ArrayList<WaveModel>();
	long oldTime;
	public Sprinter(LifeBox source, cwruBase cwruBase) 
	{
		super(source, cwruBase);
		// TODO Auto-generated constructor stub
		oldTime = 0;

	}
	public void process()
	{
		super.process();
		System.out.println("Surf's UP");
		//run the default process first, so that it will always generate some movement
		//then update all of the waves to the most up to date time and location
		check_surf();
		System.out.println(" tracking "+surf_bum.size()+" waves");
		long current_time = robot.getTime();
		for (int i = 0; i < surf_bum.size(); i++)
		{
			update(surf_bum.get(i),current_time);
		}
	}
	public double[] update(WaveModel w, long current_turn)
	{
		double[] early_late_radius = new double[2];
		w.early_radius = w.speed*(current_turn-w.early_origin_time);
		w.late_radius = w.speed*(current_turn-w.late_origin_time);
		early_late_radius[0] = w.early_radius;
		early_late_radius[1] = w.late_radius;
		if (w.late_radius > w.max_radius)
		{
			removem(w);
		}
		return early_late_radius;
	}
	public void removem(WaveModel wm)
	{
		int i = 0;
		while (i < surf_bum.size())
		{
			if (surf_bum.get(i).equals(wm))
			{
				surf_bum.remove(i);
			}
			else
			{
				i++;
			}
		}
	}
	public void check_surf()
	{
		for (RoboCore rc : source.ronny)
		{
			ArrayList<RobotBite> testData = rc.captureTime(2);
			int tester = testData.size();
			System.out.println("Capturing full data"+(tester==2));
			if (testData.size()==2)
			{
				RobotBite one = testData.get(testData.size()-1);
				RobotBite two = testData.get(testData.size()-2);
				System.out.println("  Delta energy: "+ Math.abs(one.cEnergy - two.cEnergy));
				System.out.println("old time = "+oldTime);
				System.out.println("new time = "+testData.get(0).cTime);
				if(Math.max(Rules.MIN_BULLET_POWER,0.0) <= Math.abs(one.cEnergy-two.cEnergy)
						&& Math.abs(one.cEnergy - two.cEnergy) <= Rules.MAX_BULLET_POWER
						&& (testData.get(0).cTime > (oldTime)))
				{
					System.out.println("Adding wave model:");
					System.out.println("time:"+two.cTime+" x:"+two.cx+" y:"+two.cy);
					WaveModel wm = new WaveModel(two.cTime,one.cTime,two.cEnergy-one.cEnergy,
							two.cx, two.cy, one.cx, one.cy);
					oldTime = two.cTime;
					surf_bum.add(wm);
				}
			}
		}
	}
	public void onPaint(Graphics2D g) 
	{
		System.out.println("spray painting prepared");
		for (WaveModel wave : surf_bum)
		{
			g.setColor(Color.BLUE);
			g.drawOval((int)(wave.early_origin_x), 
					(int)(wave.early_origin_y), 
					(int) (wave.early_radius*2+robot.getHeight()),
					(int) (wave.early_radius*2+robot.getHeight()));
			g.drawOval((int)(wave.late_origin_x), 
					(int)(wave.late_origin_y), 
					(int) (wave.late_radius*2-robot.getHeight()),
					(int) (wave.late_radius*2-robot.getHeight()));
		}
		for (RoboCore rc : source.ronny)
		{
			g.setColor(Color.RED);
			ArrayList<Double> locXData = rc.extractX();
			ArrayList<Double> locYData = rc.extractY();
			for (int i = 0; i<locXData.size(); i++)
			{
				int x = (int) (double) locXData.get(i);
				int y = (int) (double) locYData.get(i);
				g.drawRect(y, x, 5, 5);
			}
		}
	}
}

class WaveModel
{
	long early_origin_time;
	long late_origin_time;
	double energy;
	double speed;
	double early_origin_x;
	double early_origin_y;
	double late_origin_x;
	double late_origin_y;
	double early_radius;
	double late_radius;
	double max_radius = 1000;
	public WaveModel(long early_time, long late_time, double energy, 
			double x1, double y1, double x2, double y2)
	{
		early_origin_time = early_time;
		late_origin_time = late_time;
		this.energy = energy;
		early_origin_x = x1;
		early_origin_y = y1;
		late_origin_x = x2;
		late_origin_y = y2;

		speed = 20-3*energy;
	}
}