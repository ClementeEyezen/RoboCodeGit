package cwru;

import java.util.ArrayList;

public class Projector extends Brain 
{
	//solely works on projecting where enemy robots will be based on current data
	//saves current projection tied with projected accuracy
	IDArray storageUnit;
	IDArray enemyDataUnit;
	RobotBite rb_standard;
	public Projector(LifeBox source) 
	{
		super(source);
		source.allocateArray(this, "All_Projections");
		storageUnit = source.request(this);
		enemyDataUnit = source.request(new Sonar(source));
				//request the IDArray that will contain as scanned enemy data (Radar)
		rb_standard = new RobotBite(0L, source.mainRobot, 0.0, 0.0, 0.0, 0.0, 0.0);
	}
	public void process()
	{
		long processTime = System.currentTimeMillis();
		enemyDataUnit = source.request(new Sonar(source)); //up to date enemy position data
		if (turnCounter < 18 || true)
		{
			simple_linear_projection(18);
		}
		else
		{
			//projections = projection system active
		}
		long totalTime = System.currentTimeMillis()-processTime;
		System.out.println("PRO calc time (millis):"+totalTime);
	}
	public Projection simple_linear_projection(int forecastTime)
	{
		//project using scanned velocity, heading
		//forecast time is the time ahead of current time to project to
		int x = 0;
		int y = 0;
		double guessAccuracy = 1.0;
		return new Projection(x,y,turnCounter,turnCounter+forecastTime,guessAccuracy);
		
		/*
		//projections = current position + velocity (linear)
		Object robot_bite;
		RobotBite robot_bite1;
		for (nameArray n : enemyDataUnit)
		{
			for (int i = 0; i<n.size(); i++)
			{
				robot_bite = n.get(i);
				if (robot_bite.getClass().equals(rb_standard.getClass()))
				{
					//if it's a robot bite
					robot_bite1 = (RobotBite) robot_bite;
					ArrayList<Double> xpos = new ArrayList<Double>();
					ArrayList<Double> ypos = new ArrayList<Double>();
					for (int j = 0; j<projLength; j++)
					{
						//attach a linear projection for 4 more turns ahead of the robot
						double vel = robot_bite1.cVelocity;
						double heading = robot_bite1.cHeading_radians;
						double math_heading = (-heading+Math.PI/2)%(2*Math.PI);
						double dx = vel*j*Math.cos(math_heading);
						double dy = vel*j*Math.sin(math_heading);
						xpos.add(dx+robot_bite1.cx);
						ypos.add(dy+robot_bite1.cy);
					}
					robot_bite1.attachProjection(source.mainRobot.getTime(), xpos, ypos);
				}
			}
		}
		*/
	}
	public Projection averageVelocityProjection(int forecastTime)
	{
		//project using point to point velocity, heading
		//forecast time is the time ahead of current time to project to
		int x = 0;
		int y = 0;
		double guessAccuracy = 1.0;
		return new Projection(x,y,turnCounter,turnCounter+forecastTime,guessAccuracy);
	}
	public Projection circularProjection(int forecastTime)
	{
		//project using scanned velocity, change in scanned velocity,
		//		scanned heading, change in scanned heading
		//forecast time is the time ahead of current time to project to
		int x = 0;
		int y = 0;
		double guessAccuracy = 1.0;
		return new Projection(x,y,turnCounter,turnCounter+forecastTime,guessAccuracy);
	}
}