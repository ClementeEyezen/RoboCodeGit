package cepl.dataStorage;

import robocode.AdvancedRobot;

public class Wave {
	double x;
	double y;
	long start_time;
	double radius;
	double energy_drop;
	double bullet_velocity;
	double max_battlefield_dimension;
	
	double hot_head_on;
	double hot_linear;
	
	public Wave(double x, double y, long start_time, double energy_drop, AdvancedRobot source)
	{
		this.x = x;
		this.y = y;
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
		
	}
	public double update(long current_time)
	{
		long delta = current_time-start_time;
		radius = delta*bullet_velocity;
		if (radius > max_battlefield_dimension)
		{
			bullet_velocity = -1;
			radius = -1;
		}
		return radius;
	}
	
}
