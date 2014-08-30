package cepl.dataStorage;

import java.io.File;
import java.util.ArrayList;

import robocode.AdvancedRobot;

public class VirtualRobot 
{
	AdvancedRobot source;
	public String name;
	public ArrayList<Double> x;
	
	File data_file;
	
	public VirtualRobot()
	{
		x = new ArrayList<Double>();
		data_file = source.getDataFile(name);
	}
	
	public void setRobot(AdvancedRobot s)
	{
		source = s;
	}
	
}
