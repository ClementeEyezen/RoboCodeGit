package cepl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

import cepl.dataStorage.BotBin;
import cepl.dataStorage.DataPoint;

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
	
	int robot_scans;

	public boolean melee_mode;
	public boolean reset_radar;
	public boolean on_startup;
	
	public void run()
	{
		//================SETUP================
		melee_mode = false;
		on_startup = true;
		robot_scans = 0;
		
		this.setAdjustGunForRobotTurn(true);
		this.setAdjustRadarForGunTurn(true);
		this.setAdjustRadarForRobotTurn(true);
		
		System.out.println("new DataCollection()");
		ssd = new DataCollection();
		ssd.setRobot(this);
		System.out.println("new MovementControl()");
		driver = new MovementControl();
		driver.setRobot(this);
		System.out.println("new RadarControl()");
		antenna = new RadarControl();
		antenna.setRobot(this);
		System.out.println("new GunControl()");
		scope = new GunControl();
		scope.setRobot(this);
		
		System.out.println("data info");
		remaining_data_storage = getDataQuotaAvailable();
		data_directory = getDataDirectory();
		System.out.println("data_quota : "+remaining_data_storage);
		System.out.println("data_direc : "+data_directory.getAbsolutePath());
		
		//==========REPEATING ACTIONS==========
		System.out.println("standard run initiated");
		on_startup = false;
		while(true)
		{
			ssd.update();
			driver.update();
			antenna.update();
			scope.update();
			
			remaining_data_storage = getDataQuotaAvailable();
			execute();
		}
	}
	
	public void onScannedRobot(ScannedRobotEvent sre)
	{
		robot_scans += 1;
		ssd.update(sre);
	}
	
	public File getDataFile(String s)
	{
		return getDataFile(s);
	}
	
	@Override
	public void onDeath(DeathEvent de)
	{
		//writeDataToFile(ssd);
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
		//main
		BufferedWriter r = null;
		String current_file = "null";
		try
		{
			r = new BufferedWriter(new FileWriter(main,true));
			current_file = "main";
			r.write(writeMainFile());
			r.write("\n-----------\n");
			r.close();
			r = new BufferedWriter(new FileWriter(dataPoints,true));
			current_file = "data";
			r.write(writeDataFile(ssd));
			r.write("\n-----------\n");
			r.close();
			r = new BufferedWriter(new FileWriter(radarSettings,true));
			current_file = "radar";
			r.write(writeRadarFile(antenna));
			r.write("\n-----------\n");
			r.close();
			r = new BufferedWriter(new FileWriter(driveSettings,true));
			current_file = "move";
			r.write(writeDriveFile(driver));
			r.write("\n-----------\n");
			r.close();
			r = new BufferedWriter(new FileWriter(gunSettings,true));
			current_file = "gun";
			r.write(writeGunFile(scope));
			r.write("\n-----------\n");
			r.close();
			current_file = "done";
		}
		catch(IOException ioe)
		{
			System.out.println("Error occured on file write: "+current_file);
		}
	}
	
	public String writeMainFile()
	{
		String complete = new String();
		complete += "<Ceepl>"+"\n";
		complete += "<DataCollection>"+ssd.id+"</DataCollection>"+"\n";
		complete += "<MovementControl>"+driver.id+"</MovementControl>"+"\n";
		complete += "<RadarControl>"+antenna.id+"</RadarControl>"+"\n";
		complete += "<GunControl>"+scope.id+"</GunControl>"+"\n";
		
		complete += "<dataStorage>"+this.remaining_data_storage+"</dataStorage>"+"\n";
		complete += "<melee_mode>"+melee_mode+"</melee_mode>"+"\n";
		
		return complete;
	}
	public String writeDataFile(DataCollection data)
	{
		return data.toFile();
	}
	public String writeRadarFile(RadarControl radar)
	{
		return radar.toFile();
	}
	public String writeDriveFile(MovementControl driver)
	{
		return driver.toFile();
	}
	public String writeGunFile(GunControl scope)
	{
		return scope.toFile();
	}
	
	public void onPaint(Graphics2D g) {
		//paint existing data by robots
		BotBin current_robot;
		for(int i = 0; i < ssd.robots.size(); i++)
		{
			current_robot = ssd.robots.get(i);
			for (int j = 0; j < current_robot.info.size(); j++)
			{
				Color c = new Color((int) 255, (int) (i/ssd.robots.size()*255), (int) 0, 
						Math.max(0,(int) (255-(current_robot.info.size()-j))));
				DataPoint dp = current_robot.info.get(j);
				g.setColor(c);
				g.fillRect((int) (dp.x-1),(int) (dp.y-1), 3, 3);
			}
		}
		
	}
}
