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
	double last_far_x = -1;
	double last_far_y = -1;
	double last_near_x = -1;
	double last_near_y = -1;
	public Sprinter(LifeBox source, cwruBase cwruBase) 
	{
		super(source, cwruBase);
		oldTime = 0;
	}
	public void process()
	{
		super.process();
		//run the default process first, so that it will always generate some movement
		//then update all of the waves to the most up to date time and location
		check_surf();
		System.out.println(" tracking "+surf_bum.size()+" waves");
		long current_time = robot.getTime();
		for (int i = 0; i < surf_bum.size(); i++)
		{
			update(surf_bum.get(i),current_time);
		}
		double[] output = inner_points_test(long_points_test());
		double desired_math_heading = Math.atan2(robot.getY()-output[1], robot.getX()-output[0]);
		double desired_heading = (-desired_math_heading+Math.PI/2)%(2*Math.PI);
		double current_heading = robot.getHeading();
		moveEndTheta = desired_heading-current_heading;
		moveEndDistance = Brain.distance(output[0], output[1], robot.getX(), robot.getY());
		//TODO IDEAPAD
		/*
		 * The robot should test 32 points at radius of 50 around itself, and evaluate
		 * 	based on the waves washing over that point. 
		 * 	This uses a similar function to below, with distance from current heading being good
		 * 	divided by the number of waves washing over
		 *  	f: (1-cos(delta angle)/2*(waves+1)
		 *  
		 * The bearing to that point is stored.
		 * Then randomly generate 8 points at random that are at less than 50 units away
		 *  , and rate them based on how close they are in bearing to the first point 
		 *  (cos of delta bearing) and wave crossings (sum of waves washing over) for a 
		 *  	f: cos(bearing_50-bearing_8)/(num of waves+1).
		 *  
		 * 	Use a move to point function to move to the interior point 
		 * 	with the highest rating
		 * 
		 * 	TODO implement this first. It shouldn't be to hard, 
		 *  //DONE create a function that takes a point and counts wave overlap
		 *  //DONE Then create a function that generates outer points, tests and returns the 
		 *  	optimal outer point bearing
		 *  //DONE From there, use a different function to generate random inner points (8<=d<50),
		 *  	choose the best one, and then output the point to move to.
		 *  TODO test
		 */
		//TODO Improvements
		/*
		 * Filter our waves that are not from shootings (recharge time, etc)
		 * Other sources:
		 * 		Walls
		 * 		Self-bullet contacts
		 * 		Other bullet contacts
		 * 		Ramming
		 */
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
			System.out.println("Check surfing rc for: "+rc.data_points.get(0).name);
			ArrayList<RobotBite> testData = rc.captureTime(2);
			int tester = testData.size();
			//System.out.println("Capturing full data"+(tester==2));
			if (testData.size()==2)
			{
				RobotBite one = testData.get(testData.size()-1);
				RobotBite two = testData.get(testData.size()-2);
				//System.out.println("  Delta energy: "+ Math.abs(one.cEnergy - two.cEnergy));
				//System.out.println("old time = "+oldTime);
				//System.out.println("new time = "+testData.get(0).cTime);
				if(Math.max(Rules.MIN_BULLET_POWER,0.0) <= Math.abs(one.cEnergy-two.cEnergy)
						&& Math.abs(one.cEnergy - two.cEnergy) <= Rules.MAX_BULLET_POWER
						&& (testData.get(0).cTime > (oldTime)))
				{
					//System.out.println("Adding wave model:");
					//System.out.println("time:"+two.cTime+" x:"+two.cx+" y:"+two.cy);
					WaveModel wm = new WaveModel(two.cTime,one.cTime,two.cEnergy-one.cEnergy,
							two.cx, two.cy, one.cx, one.cy);
					oldTime = two.cTime;
					surf_bum.add(wm);
				}
			}
		}
	}
	public int waves_over(double x, double y)
	{
		int current_waves = 0;
		for (WaveModel wave : surf_bum)
		{
			double distance_early = Brain.distance(wave.early_origin_x, wave.early_origin_y,
					x, y);
			double distance_late = Brain.distance(wave.late_origin_x, wave.late_origin_y, 
					x, y);
			if (distance_early<=wave.early_radius && distance_late>=wave.late_radius)
			{
				current_waves++;
			}
		}
		return current_waves;
	}
	public double long_points_test()
	{
		//returns the bearing to the optimal point 50 pixels away
		double optimal_bearing = 0;
		double optimal_function = -1;
		double fraction = 32;
		double distance = 50;
		double increment = Math.PI/(fraction/2);
		double this_x = robot.getX();
		double this_y = robot.getY();
		double this_heading = robot.getHeadingRadians();
		for (int i = 0; i<fraction; i++)
		{
			double test_bearing = i*increment;
			double test_x = distance*Math.cos(test_bearing)+this_x;
			double test_y = distance*Math.sin(test_bearing)+this_y;
			int wave_count = waves_over(test_x, test_y);
			double delta_heading = test_bearing-this_heading;
			double function = (1-Math.cos(delta_heading))/(2*(wave_count+1));
			if (function > optimal_function)
			{
				optimal_bearing = test_bearing;
				optimal_function = function;
				last_far_x = test_x;
				last_far_y = test_y;
			}
		}
		double demathed_bearing = -optimal_bearing+Math.PI/2;
		return optimal_bearing;
	}
	public double[] inner_points_test(double optimal_bearing)
	{
		double[] xyPoints = new double[2];
		double this_x = robot.getX();
		double this_y = robot.getY();
		double optimal_function = -2;
		for (int i = 0; i<8; i++)
		{
			double random_distance = Math.random()*(50-8)+8; //picks a random distance 8-50
			double random_bearing = Math.random()*2*Math.PI-Math.PI; //rand bearing -pi to pi
			double test_x = random_distance*Math.cos(random_bearing)+this_x;
			double test_y = random_distance*Math.sin(random_bearing)+this_y;
			int wave_count = waves_over(test_x, test_y);
			double delta_heading = random_bearing-optimal_bearing;
			double function = Math.cos(delta_heading)/(wave_count+1);
			if (function > optimal_function)
			{
				optimal_function = function;
				xyPoints[0] = test_x;
				xyPoints[1] = test_y;
				last_near_x = test_x;
				last_near_y = test_y;
			}
		}
		return xyPoints;
	}
	public void onPaint(Graphics2D g) 
	{
		//System.out.println("spray painting prepared");
		for (WaveModel wave : surf_bum)
		{
			g.setColor(Color.BLUE);
			g.drawOval((int)(wave.early_origin_x-wave.early_radius-(robot.getHeight()/2)), 
					(int)(wave.early_origin_y-wave.early_radius-(robot.getHeight()/2)), 
					(int) (wave.early_radius*2+robot.getHeight()),
					(int) (wave.early_radius*2+robot.getHeight()));
			g.drawOval((int)(wave.late_origin_x-wave.late_radius+(robot.getHeight()/2)), 
					(int)(wave.late_origin_y-wave.late_radius+(robot.getHeight()/2)), 
					(int) (wave.late_radius*2-robot.getHeight()),
					(int) (wave.late_radius*2-robot.getHeight()));
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
		this.energy = Math.abs(energy);
		//System.out.println("bullet energy = "+this.energy);
		early_origin_x = x1;
		early_origin_y = y1;
		late_origin_x = x2;
		late_origin_y = y2;

		speed = 20+(3*energy);
		//System.out.println("bullet speed = "+speed);
	}
}