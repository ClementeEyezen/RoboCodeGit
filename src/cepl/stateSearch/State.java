package cepl.stateSearch;

import java.util.ArrayList;

import cepl.dataStorage.BotBin;
import cepl.dataStorage.DataPoint;
import cepl.dataStorage.Wave;

public class State {
	DataPoint self;
	//DataPoint[] others; //this will instead come from a forward projection engine
	Wave[] shots;
	ArrayList<State> children;
	State parent;
	StateSearch source;

	int vmax = 8;
	int depth;


	public State(StateSearch source,State parent, int depth, DataPoint self, 
			//DataPoint[] other_robots, 
			Wave[] shots_fired)
	{
		this.source = source;
		this.parent = parent;
		this.self = self;
		//others = other_robots;
		shots = shots_fired;
	}
	public void generateNextStates(int resolution)
	{
		//resolution is the intended number of child states
		if (depth> source.max_depth) children = null;
		//enumerate possibilities
		//velocity
		int[] possible_velocities;
		long int_speed = Math.round(self.speed);
		if (int_speed == 0)
		{
			possible_velocities = new int[3];
			possible_velocities[0] = -1;
			possible_velocities[1] = 0;
			possible_velocities[2] = 1;
		}
		else if (int_speed == 1)
		{
			possible_velocities = new int[3];
			possible_velocities[0] = 0;
			possible_velocities[1] = 1;
			possible_velocities[2] = 2;
		}
		else if (int_speed == -1)
		{
			possible_velocities = new int[3];
			possible_velocities[0] = -2;
			possible_velocities[1] = -1;
			possible_velocities[2] = 0;
		}
		else if (int_speed < 0)
		{
			possible_velocities = new int[4];
			possible_velocities[0] = (int) (int_speed-1);
			possible_velocities[1] = (int) (int_speed);
			possible_velocities[2] = (int) (int_speed+1);
			possible_velocities[3] = (int) (int_speed+2);
		}
		else
		{
			possible_velocities = new int[4];
			possible_velocities[0] = (int) (int_speed-2);
			possible_velocities[1] = (int) (int_speed-1);
			possible_velocities[2] = (int) (int_speed);
			possible_velocities[3] = (int) (int_speed+1);
		}
		//direction (robocode)
		double maximum_rate = 10.0-.75*((double)(Math.abs(self.speed)));
		double[] possible_directions = new double[11];
		for(int i = -5; i <= 5; i++)
		{
			possible_directions[i] = self.direction + ((double) i)*(maximum_rate/5.0);
		}
		//randomly select from the possible options into a list
		children = new ArrayList<State>();
		for (int i = 0; i < resolution; i++)
		{
			double velocity = possible_velocities[
			                                      (int) Math.floor(Math.random()*possible_velocities.length)
			                                      ];
			double robo_direction = possible_directions[
			                                            (int) Math.floor(Math.random()*possible_directions.length)
			                                            ];
			double real_direction = -robo_direction + Math.PI/2;
			double dx = velocity*Math.cos(real_direction);
			double dy = velocity*Math.sin(real_direction);
			boolean bullet_problems =false;
			if(source.hit_prune)
			{
				bullet_problems = check_bullet_hit(source, dx,dy);
			}
			if(!bullet_problems)
			{
				//add the new state to the children
				DataPoint forward_self = new DataPoint(self.x+dx, self.y+dy,
						self.energy, 
						velocity, robo_direction, 
						self.time+1, 
						false, self, true);
				children.add(new State(source, this, depth+1, forward_self, shots));
			}
		}
	}

	public boolean check_bullet_hit(StateSearch source, double dx, double dy)
	{
		//assumes time +1
		for (int i = 0; i < shots.length; i++)
		{
			double distance = Math.sqrt(((self.x+dx)-shots[i].wave_x)*((self.x+dx)-shots[i].wave_x)+
					((self.y+dy)-shots[i].wave_y)*((self.y+dy)-shots[i].wave_y));
			double dt = self.time+1-shots[i].start_time;
			double radius = shots[i].bullet_velocity*dt;
			if(Math.abs(distance-radius)<=36/2*Math.sqrt(2))
			{
				//if in the hitbox for the radius...
				if(!source.head_on && !source.linear && 
						!source.curve_linear && !source.prior_hits)
				{
					//if no options selected
					return true;
				}
				else
				{
					return (source.head_on&&check_head_on(shots[i],(self.x+dx),(self.y+dy))) ||
							(source.linear&&check_linear(shots[i],(self.x+dx),(self.y+dy))) ||
							(source.curve_linear&&check_curve_linear(shots[i],(self.x+dx),(self.y+dy))) || 
							(source.prior_hits&&check_prior_hits(shots[i],(self.x+dx),(self.y+dy)));
				}
			}
		}
		return false;
	}
	public boolean check_head_on(Wave w, double x, double y)
	{
		//W = the wave that was calculated to overlap the robot
		//x,y = the position the robot was calculated to be in at the time of the overlap
		double hit_real_heading = Math.atan2(y-w.wave_y,x-w.wave_x);
		return Math.abs(hit_real_heading-w.hot_head_on)<source.precision;
	}
	public boolean check_linear(Wave w, double x, double y)
	{
		//W = the wave that was calculated to overlap the robot
		//x,y = the position the robot was calculated to be in at the time of the overlap
		double hit_real_heading = Math.atan2(y-w.wave_y,x-w.wave_x);
		return Math.abs(hit_real_heading-w.hot_linear)<source.precision;
	}
	public boolean check_curve_linear(Wave w, double x, double y)
	{
		//W = the wave that was calculated to overlap the robot
		//x,y = the position the robot was calculated to be in at the time of the overlap
		double hit_real_heading = Math.atan2(y-w.wave_y,x-w.wave_x);
		return Math.abs(hit_real_heading-w.hot_curve_linear)<source.precision;
	}
	public boolean check_prior_hits(Wave w, double x, double y)
	{
		//W = the wave that was calculated to overlap the robot
		//x,y = the position the robot was calculated to be in at the time of the overlap
		//cycle through old (complete) waves, looking for hit angles.
		//if this matches, return true;
		BotBin goal = null;
		for(BotBin robot : source.robot.ssd.robots)
		{
			if(robot.name.equals(w.name))
			{
				//This is the right robot
				goal = robot;
			}
		}
		if (goal != null)
		{
			double test_angle = Math.atan2(x-w.wave_x,y-w.wave_y);
			double wave_head_angle = w.hot_head_on;
			double delta_from_head_on = wave_head_angle-test_angle;
			double max_escape_angle = Math.atan(8/w.bullet_velocity);
			if (delta_from_head_on < -max_escape_angle)
			{
				delta_from_head_on = -Math.PI*(-2)-delta_from_head_on;
			}
			else if (delta_from_head_on > max_escape_angle)
			{
				delta_from_head_on = Math.PI*2-delta_from_head_on;
			}
			double percent = delta_from_head_on/max_escape_angle;
			return goal.check_hitbin(percent);
		}
		return false;
	}
}
