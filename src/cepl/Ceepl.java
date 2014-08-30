package cepl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

import robocode.AdvancedRobot;
import robocode.DeathEvent;
import robocode.ScannedRobotEvent;

public class Ceepl extends AdvancedRobot 
{
	DataCollection ssd;
	MovementControl driver;
	RadarControl antenna;
	GunControl scope;
	
	long remaining_data_storage;
	File data_directory;
	File data_file;

	public boolean melee_mode;
	public boolean on_startup;
	
	public void run()
	{
		//================SETUP================
		melee_mode = false;
		on_startup = true;
		
		ssd = new DataCollection();
		ssd.setRobot(this);
		driver = new MovementControl();
		driver.setRobot(this);
		antenna = new RadarControl();
		antenna.setRobot(this);
		scope = new GunControl();
		scope.setRobot(this);
		
		remaining_data_storage = getDataQuotaAvailable();
		data_directory = getDataDirectory();
		//data_file = getDataFile(this.getName());
		
		//==========REPEATING ACTIONS==========
		while(true)
		{
			ssd.update();
			driver.update();
			antenna.update();
			scope.update();
			
			remaining_data_storage = getDataQuotaAvailable();
		}
	}
	
	public void onScannedRobot(ScannedRobotEvent sre)
	{
		ssd.update(sre);
	}
	
	public File getDataFile(String s)
	{
		return getDataFile(s);
	}
	
	@Override
	public void onDeath(DeathEvent de)
	{
		writeDataToFile(ssd);
	}
	public void writeDataToFile(DataCollection storage)
	{
		File main = getDataFile(this.getName());
		File dataPoints = getDataFile(this.getName()+"_data");
		File radarSettings = getDataFile(this.getName()+"_radar");
		File driveSettings = getDataFile(this.getName()+"_drive");
		File gunSettings = getDataFile(this.getName()+"_shoot");
		System.out.println("main file path: "+main.getAbsolutePath());
		System.out.println("add _data, _radar, _drive, _shoot for respective files");
		
		BufferedWriter r = null;
		try
		{
			r = new BufferedWriter(new FileWriter(main,true));
			r.write("<Ceepl>"+"\n");
			r.write("ssd: "+ssd.id+"\n");
			r.write("mov: "+driver.id+"\n");
			r.write("rad: "+antenna.id+"\n");
			r.write("gun: "+scope.id+"\n");
			r.write("<data_volume_remaining>"+this.remaining_data_storage+"</data_volume_remaining>");
			r.write("<data>"+dataPoints.getAbsolutePath()+"</data>"+"\n");
			//TODO Start here
		}
		catch(IOException ioe)
		{
			
		}
	}
}
