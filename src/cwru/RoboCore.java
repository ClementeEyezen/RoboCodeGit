package cwru;

import java.util.ArrayList;

public class RoboCore 
{
	//A core container for storing data over time for a specific robot
	//essentially a storage list of robotbites
	
	//container also knows how to extract data in different forms, 
	//			i.e. return arraylist of x values
	String name;
	ArrayList<RobotBite> data_points;
	public RoboCore(String robot_name)
	{
		name = robot_name;
	}
	public void add(RobotBite new_data_point)
	{
		data_points.add(new_data_point);
	}
}
