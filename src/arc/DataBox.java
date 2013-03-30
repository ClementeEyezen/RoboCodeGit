package arc;

import java.awt.Graphics2D;
import java.util.ArrayList;

import robocode.AdvancedRobot;
import robocode.Robot;
import robocode.ScannedRobotEvent;

public class DataBox 
{
	ScannedRobotEvent lastScan;
	ArcBasicBot self;
	ArrayList<VirtualBot> opponents;
	public DataBox(ArcBasicBot r)
	{
		self = r;
		opponents = new ArrayList<VirtualBot>();
	}
	public ArcBasicBot getRobot()
	{
		return self;
	}
	public int getOpCount()
	{
		return opponents.size();
	}
	public void scanEvent(ScannedRobotEvent sre)
	{
		boolean previousBot = false;
		lastScan = sre;
		for (VirtualBot vb : opponents)
		{
			//System.out.println(""+vb.getName());
			if (sre.getName().equals(vb.getName()))
			{
				previousBot = true;
				vb.update(sre, self.selfPoint(), self.getHeadingRadians());
			}
		}
		if (!previousBot)
		{
			newOpponent(sre);
		}
	}
	public void drawData(Graphics2D g)
	{
		for (VirtualBot vb : opponents)
		{
			vb.drawData(g);
		}
	}
	public void newOpponent(ScannedRobotEvent sre)
	{
		//System.out.println("New opponent "+sre.getName());
		VirtualBot anime = new VirtualBot(sre.getName());
		anime.update(sre, self.selfPoint(), self.getHeadingRadians());
		opponents.add(anime);
	}
	public double getGunDirection()
	{
		return self.getGunHeadingRadians();
	}
	public double getRobotDirection()
	{
		return self.getHeadingRadians();
	}
	public double getRadarDirection()
	{
		return self.getRadarHeadingRadians();
	}
	public ArrayList<VirtualBot> getOpponents()
	{
		return opponents;
	}
}
