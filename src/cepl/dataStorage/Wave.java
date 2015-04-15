package cepl.dataStorage;

import cepl.Ceepl;

public class Wave {
	
	public String name;
	public BotBin robot;
	
	public double wave_x;
	public double wave_y;
	public long start_time;
	public double radius;
	double energy_drop;
	public double bullet_velocity;
	double max_battlefield_dimension;
	
	public double hot_head_on;
	public double hot_linear;
	public double hot_curve_linear;
	
	public boolean complete = false;
	public long hit_time = -1;
	public double true_hit_bearing;
	public double relative_hit_bearing;
	
	public Wave(String name, BotBin shooter, double x, double y, long start_time, double energy_drop, Ceepl source)
	{
		this.name = name;
		robot = shooter;
		this.wave_x = x;
		this.wave_y = y;
		this.start_time = start_time;
		this.energy_drop = energy_drop;
		radius = 0;
		bullet_velocity = 20-3*energy_drop;
		
		max_battlefield_dimension = Math.max(source.getBattleFieldHeight(),source.getBattleFieldWidth());
		double myX = source.getX();
		double myY = source.getY();
		hot_head_on = Math.atan2(myY-y, myX-x);
		double distance = Math.sqrt((myX-x)*(myX-x)+(myY-y)*(myY-y));
		double time = distance/bullet_velocity;
		double my_robo_heading = source.getHeadingRadians();
		double my_real_heading = -my_robo_heading + Math.PI/2;
		double myLinX = myX + source.getVelocity()*time*Math.cos(my_real_heading);
		double myLinY = myY + source.getVelocity()*time*Math.sin(my_real_heading);
		hot_linear = Math.atan2(myLinY-y, myLinX-x);
		
		BotBin old_data = source.self_locations();
		if(old_data.info.size()>1)
		{
			double old_v = old_data.info.get(old_data.info.size()-2).speed;
			double old_direction = old_data.info.get(old_data.info.size()-2).direction;
			double dv = source.getVelocity() - old_v;
			double d_direction = source.getHeadingRadians() - old_direction;
			double current_v = source.getVelocity();
			double current_d = source.getHeadingRadians();
			double new_v = Math.max(-8, Math.min(8,current_v+dv));
			double new_direction = current_d + d_direction;
			double new_real_heading = -new_direction + Math.PI/2;
			double myCLinX = myX + new_v*1*Math.cos(new_real_heading);
			double myCLinY = myY + new_v*1*Math.sin(new_real_heading);
			for(int i = 1; i < time; i++)
			{
				current_v = new_v;
				current_d = new_direction;
				new_v = Math.max(-8, Math.min(8,current_v+dv));
				new_direction = current_d + d_direction;
				new_real_heading = -new_direction + Math.PI/2;
				myCLinX = myCLinX + new_v*1*Math.cos(new_real_heading);
				myCLinY = myCLinY + new_v*1*Math.sin(new_real_heading);
			}
			hot_curve_linear = Math.atan2(myCLinY-y, myCLinX-x);
		}
		else
		{
			hot_curve_linear = hot_linear;
		}
	}
	public double update(long current_time)
	{
		long delta = current_time-start_time+2;
		radius = delta*bullet_velocity;
		if (radius > max_battlefield_dimension)
		{
			bullet_velocity = -1;
			radius = -1;
			complete = true;
		}
		return radius;
	}
	
}
