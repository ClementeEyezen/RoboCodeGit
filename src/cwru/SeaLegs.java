package cwru;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class SeaLegs extends Legs
{
	ArrayList<WaveModel> surf_bum;
	ArrayList<Double> move_here_x;
	ArrayList<Double> move_here_y;
	//ArrayList<ShotList> in_the_dark;
	double close_enough = 8;
	double[] test_x;
	double[] test_y;
	double[] test_function;
	double fraction = 32;
	double test_distance = 32;
	public SeaLegs(LifeBox source, cwruBase robot)
	{
		super(source, robot);
		surf_bum = new ArrayList<WaveModel>();
		move_here_x = new ArrayList<Double>();
		move_here_y = new ArrayList<Double>();
	}
	public void update_surf(long current_time)
	{
		for(WaveModel w : surf_bum)
		{
			w.update(current_time);
		}
		int i = 0;
		while (i<surf_bum.size())
		{
			if (surf_bum.get(i).wave_off_screen())
			{
				surf_bum.remove(i);
			}
			else
			{
				i++;
			}
		}
	}
	public void shorten_point_queue()
	{
		int i = 0;
		while (i<move_here_x.size() && i<move_here_y.size())
		{
			if (Brain.distance(robot.getX(), robot.getY(), move_here_x.get(i), move_here_y.get(i))
					<= close_enough)
			{
				move_here_x.remove(i);
				move_here_y.remove(i);
			}
			else
			{
				i++;
			}
		}
		//if either becomes empty (both should be concurrently), reset and do a new calc
		if(move_here_x.size()==0 || move_here_y.size()==0)
		{
			move_here_x = new ArrayList<Double>();
			move_here_y = new ArrayList<Double>();
		}
	}
	public double math_radians_to_point(double x, double y)
	{
		return Math.atan2(x-robot.getX(), y-robot.getY());
	}
	public double[] efficient_move_to_point(double x, double y, double c_math_heading)
	{
		//outputs the desired heading and move distance. Allows for backwards move
		double[] heading_distance = new double[2];
		double delta = (math_radians_to_point(x,y)-c_math_heading)%(Math.PI*2);
		double distance = Brain.distance(x, y, robot.getX(), robot.getY());
		if(Math.abs(delta)<=Math.PI/2) //if it is in the forward range
		{
			this.moveEndTheta = reduce_change(delta);
			if(distance>8)
			{
				this.moveEndDistance = 10; //go max forward
			}
			else
			{
				this.moveEndDistance = distance;
			}
		}
		else //the point is in the back half of movement
		{
			double delta__ = (math_radians_to_point(x,y)+Math.PI)%(Math.PI*2)-c_math_heading;
			moveEndTheta = reduce_change(delta__);
			if(distance>8)
			{
				this.moveEndDistance = -10;
			}
			else
			{
				this.moveEndDistance = -distance;
			}
		}
		return heading_distance;
	}
	public double reduce_change(double raw_delta)
	{
		double refined_delta = raw_delta%(Math.PI*2);
		if (refined_delta > Math.PI/2)
		{
			refined_delta = -((Math.PI*2)-refined_delta);
		}
		if (refined_delta < Math.PI/-2)
		{
			refined_delta = (Math.PI*-2)-refined_delta;
		}
		return refined_delta;
	}
	public double nearest_robot_or_wall(double x, double y)
	{
		//returns the distance to the nearest robot
		double distance = Math.min(
				Math.min(robot.getBattleFieldWidth()-x, x),
				Math.min(robot.getBattleFieldHeight()-y, y)
		);
		for (RoboCore rc : source.ronny)
		{
			if (distance>Brain.distance(rc.lastX, rc.lastY, x, y))
			{
				distance = Brain.distance(rc.lastX, rc.lastY, x, y);
			}
		}
		return distance;
	}
	public double function(double x, double y)
	{
		double value_at_point = 1;
		if(nearest_robot_or_wall(x,y)>=41)//if there isn't a robot close by, give it points
		{
			value_at_point*=Math.log((nearest_robot_or_wall(x,y)-40));
		}
		int wave_count = 0;
		for (WaveModel w : surf_bum)
		{
			if(w.point_in_wave(x, y))
			{
				wave_count++;
			}
		}
		if (wave_count == 0) value_at_point*=10;
		else if (wave_count == 1) value_at_point+=0;
		else value_at_point *= Math.pow(2, -wave_count);

		double bfw = robot.getBattleFieldWidth();
		double bfh = robot.getBattleFieldHeight();
		if (x>bfw-32 || x<32) value_at_point = -1;
		if (y>bfh-32 || y<32) value_at_point = -1;
		return value_at_point;
	}
	public void makeWaves()
	{
		for (RoboCore rc: source.ronny)
		{
			if (rc.extractEnergy().size()>2)
			{
				double energy_last = rc.extractEnergy().get(rc.extractEnergy().size()-1);
				double energy_not = rc.extractEnergy().get(rc.extractEnergy().size()-2);
				double delta = energy_last-energy_not;
				if (surf_bum.size()==0 || 
						(surf_bum.get(surf_bum.size()-1).late_origin_time!=
							robot.getTime()))
				{
					surf_bum.add(new WaveModel(
							rc.extractTime().get(rc.extractX().size()-2),
							rc.extractTime().get(rc.extractTime().size()-2), 
							Math.abs(delta), 
							rc.extractX().get(rc.extractX().size()-2),
							rc.extractY().get(rc.extractY().size()-2),
							rc.lastX, rc.lastY
							));
				}
			}
		}
	}
	@Override
	public void process()
	{
		//process removes any nearby points, then adds a point to the queue
		update_surf(robot.getTime()); //update wave list
		shorten_point_queue(); //remove nearby points

		//GENERATE A NEW POINT TO ADD TO THE LIST
		//This is where wave surfing happens

		//find the last point on the list to project from, if there is none, use robot loc
		double last_x = robot.getX();
		double last_y = robot.getY();
		if (move_here_x.size()>0 && move_here_y.size()>0)
		{
			int index = Math.min(move_here_x.size()-1,move_here_y.size()-1);
			last_x = move_here_x.get(index);
			last_y = move_here_y.get(index);
		}
		test_x = new double[(int) fraction];
		test_y = new double[(int) fraction];
		test_function = new double[(int) fraction];
		double div = Math.PI*2/fraction;
		for (int i = 0; i<fraction; i++)
		{
			test_x[i] = test_distance*Math.cos(i*div)+last_x;
			test_y[i] = test_distance*Math.sin(i*div)+last_y;
			test_function[i] = function(test_x[i],test_y[i]);
		}

		//Now all of the test points have values for the function
		double best_x = test_x[0];
		double best_y = test_y[0];
		double best_func = test_function[0];
		for (int i = 1; i<fraction; i++)
		{
			if (test_function[i]> best_func)
			{
				best_x = test_x[i];
				best_y = test_y[i];
			}
		}
		move_here_x.add(best_x);
		move_here_y.add(best_y);

		if (best_func<0 || Math.random()<.01)
		{
			//if all the points are bad or random 1:100, move to a completely new place 
			move_here_x = new ArrayList<Double>();
			move_here_y = new ArrayList<Double>();
			move_here_x.add(Math.random()*(robot.getBattleFieldWidth()-100)+50);
			move_here_y.add(Math.random()*(robot.getBattleFieldHeight()-100)+50);
		}

		//MOVE TO THE FIRST POINT ON THE LIST
		double c_game_heading = robot.getHeadingRadians();
		double c_math_heading = -c_game_heading+Math.PI/2;
		efficient_move_to_point(this.move_here_x.get(0), this.move_here_y.get(0),
				c_math_heading);
	}
	public void onPaint(Graphics2D g)
	{
		//draw the desired heading
		if(moveEndDistance>=0)
		{
			g.setColor(Color.GREEN);
		}
		else
		{
			g.setColor(Color.RED);
		}
		g.drawLine(
				(int) robot.getX(), 
				(int) robot.getY(), 
				(int) (robot.getX()+32*Math.cos(-(moveEndTheta+robot.getHeadingRadians())+Math.PI/2)), 
				(int) (robot.getY()+32*Math.sin(-(moveEndTheta+robot.getHeadingRadians())+Math.PI/2))
				);
		//draw the wave models
		WaveModel wave;
		for (int i = 0; i< surf_bum.size(); i++)
		{
			wave = surf_bum.get(i);
			System.out.println("Displaying Wave "+(i+1)+" of "+surf_bum.size());
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

		//draw all of the test points
		for(int i = 0; i<fraction; i++)
		{
			g.setColor(Color.GREEN);
			if(test_function[i]<0)
			{
				g.setColor(Color.BLACK);
			}
			else if(test_function[i]<.5)
			{
				g.setColor(Color.RED);
			}
			else if(test_function[i]<1)
			{
				g.setColor(Color.ORANGE);
			}
			g.fillRect((int) (double) test_x[i], (int) test_y[i], 4, 4);

		}

		//draw all the points that it is moving too
		for(int i = 0; i<move_here_x.size(); i++)
		{
			g.setColor(Color.CYAN);
			g.fillRect((int) (double) move_here_x.get(i), (int) (double) move_here_y.get(i), 4, 4);
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
		update(late_time);
	}
	public boolean point_in_wave(double x, double y)
	{
		double early_distance = Brain.distance(x, y, early_origin_x, early_origin_y);
		double late_distance = Brain.distance(x, y, late_origin_x, late_origin_y);
		if (late_radius<early_radius)
		{
			if (late_distance<early_distance)
			{
				return (late_distance>=late_radius && early_distance<=early_radius);
			}
			else
			{
				return (early_distance>=late_radius && late_distance<=early_radius);
			}
		}
		else
		{
			if (late_distance<early_distance)
			{
				return (late_distance>=early_radius && early_distance<=late_radius);
			}
			else
			{
				return (early_distance>=early_radius && late_distance<=late_radius);
			}
		}
	}
	public boolean wave_off_screen()
	{
		return (late_radius>=max_radius);
	}
	public void update(long current_time)
	{
		early_radius = speed*(current_time-early_origin_time);
		late_radius = speed*(current_time-late_origin_time);
	}
}
/*class ShotList
{
	//an arraylist of shots associated with a certain RoboCore
	RoboCore source;
	ArrayList<Double> all_energy_drops;
	ArrayList<Long> all_drop_times;

	public ShotList(RoboCore source)
	{
		all_energy_drops = new ArrayList<Double>();
		all_drop_times = new ArrayList<Long>();
	}
}*/