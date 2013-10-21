package cwru;

import java.util.ArrayList;

public class RoboCore 
{
	//A core container for storing data over time for a specific robot
	//essentially a storage list of robotbites

	//container also knows how to extract data in different forms, 
	//			i.e. return arraylist of x values
	public String name;
	double lastX = -1;
	double lastY = -1;
	ArrayList<RobotBite> data_points;
	public RoboCore(String robot_name)
	{
		name = robot_name;
		data_points = new ArrayList<RobotBite>();
	}
	public void add(RobotBite new_data_point)
	{
		data_points.add(new_data_point);
		lastX = new_data_point.cx;
		lastY = new_data_point.cy;
	}
	public ArrayList<RobotBite> rawData()
	{
		ArrayList<RobotBite> stata = new ArrayList<RobotBite>();
		for (RobotBite rb : data_points)
		{
			stata.add(rb);
		}
		return stata;
	}
	public ArrayList<Double> extractX()
	{
		//return all of the x values the robot was scanned at
		ArrayList<Double> stata = new ArrayList<Double>();
		for (RobotBite rb : data_points)
		{
			stata.add(rb.cx);
		}
		return stata;
	}
	public ArrayList<Double> extractY()
	{
		//return all of the y values the robot was scanned at
		ArrayList<Double> stata = new ArrayList<Double>();
		for (RobotBite rb : data_points)
		{
			stata.add(rb.cy);
		}
		return stata;
	}
	public ArrayList<Long> extractTime()
	{
		//return all of the times the robot was scanned
		ArrayList<Long> stata = new ArrayList<Long>();
		for (RobotBite rb : data_points)
		{
			stata.add(rb.cTime);
		}
		return stata;
	}
	public ArrayList<Double> extractEnergy()
	{
		//return all of the energies
		ArrayList<Double> stata = new ArrayList<Double>();
		for (RobotBite rb : data_points)
		{
			stata.add(rb.cEnergy);
		}
		return stata;
	}
	public ArrayList<Double> extractDistance()
	{
		//return all of the distances to the other robot
		ArrayList<Double> stata = new ArrayList<Double>();
		for (RobotBite rb : data_points)
		{
			stata.add(rb.cDistance);
		}
		return stata;
	}
	public ArrayList<Double> extractHeading()
	{
		//return all of the headings
		ArrayList<Double> stata = new ArrayList<Double>();
		for (RobotBite rb : data_points)
		{
			stata.add(rb.cHeading_radians);
		}
		return stata;
	}
	public ArrayList<Double> extractVelocity()
	{
		//return all of the velocities
		ArrayList<Double> stata = new ArrayList<Double>();
		for (RobotBite rb : data_points)
		{
			stata.add(rb.cVelocity);
		}
		return stata;
	}
	public ArrayList<RobotBite> captureTime(int last_x_scans)
	{
		ArrayList<RobotBite> return_this = new ArrayList<RobotBite>();
		if (data_points.size()>0)
		{
			for (int i = 0 ; i < last_x_scans && i < data_points.size() ; i++)
			{
				return_this.add(data_points.get(data_points.size()-(1+i)));
			}
		}
		return return_this;
	}
	public ArrayList<Projection> currentProjection(long time)
	{
		//get all of the projections for the current time
		//may be less useful, it only maps the projections, it doesn't associate them
		ArrayList<Projection> stata = new ArrayList<Projection>();
		for (RobotBite rb : data_points)
		{
			for (Projection p : rb.projec)
			{
				if (p.projTime == time)
				{
					stata.add(p);
				}
			}
		}
		return stata;
	}
}
