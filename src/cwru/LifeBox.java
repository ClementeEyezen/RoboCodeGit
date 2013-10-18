package cwru;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class LifeBox implements Paintable
{
	//a class that stores all data passed to it by radar or other means
	//		LifeBox takes inputs and stores them in arrays
	/* 
	 * Structure is as follows
	 * indexOfArrays of IDArrays (that have an associated brain)
	 * each brain then can add storage for whatever data inside its own IDArray
	 * 		by using a nameArray that associates a string ("name") with the array
	 * 
	 * indexOfArray
	 *   > IDArray
	 *     > nameArray
	 *       > [data]
	 */
	cwruBase mainRobot;
	double battlefield_width;
	double battlefield_height;
	//Array of Arraylists for each robot
	ArrayList<RoboCore> ronny = new ArrayList<RoboCore>(1);
	/*
	ArrayList<IDArray> indexOfArrays;
	*/
	int defaultTime = 500;
	public LifeBox(cwruBase robot)
	{
		new LifeBox(50, robot);
	}
	public LifeBox(int maxTimeTracked, cwruBase pass_this_robot)
	{
		defaultTime = maxTimeTracked;
		//indexOfArrays = new ArrayList<IDArray>(0);
		mainRobot = pass_this_robot;
		battlefield_height = pass_this_robot.getBattleFieldHeight();
		battlefield_width = pass_this_robot.getBattleFieldWidth();
	}
	public cwruBase getRobot()
	{
		return mainRobot;
	}
	public double bfw()
	{
		return battlefield_width;
	}
	public double bfh()
	{
		return battlefield_height;
	}
	public void store(RobotBite store_this)
	{
		System.out.println("inputing a LARRY");
		if (ronny.size()>0) //make sure there are cores to scan
		{
			for (RoboCore rc : ronny)
			{
				//run through all of the existing cores and see if it matches pre-collected data
				if (rc.name.equals(store_this.name))
				{
					rc.add(store_this);
				}
			}
		}
		else
		{
			ronny.add(new RoboCore(store_this.name)); //add a new core
			System.out.println("New total robocores =======> "+ronny.size());
			store(store_this); //run the test again
		}
		
	}
	//Commenting out the code that uses IDArrays, moving to a more simple soluction
	/*
	public boolean allocateArray(Brain origin, String name)
	{
		//use to check for existing array or to add a new array

		//returns true if new array is created
		//returns false if the array already exists
		if (indexOfArrays.size()>0)
		{
			for (IDArray id : indexOfArrays)
			{
				if (id.ID().equals(origin))
				{
					return false;
				}
			}
		}
		IDArray temp = new IDArray(origin);
		indexOfArrays.add(temp);
		return true;
	}
	public IDArray request(Brain origin)
	{
		//looks for the specific origin brain
		//if that isn't found, then it initiates a search by type
		//if that isn't found, then null is returned
		if (requestUnique(origin) != null)
		{
			return requestUnique(origin);
		}
		else
		{
			return requestByType(origin);
		}
	}
	public IDArray requestUnique(Brain origin)
	{
		//looks for the specific origin brain
		for (IDArray id : indexOfArrays)
		{
			if (origin.equals(id.ID()))
			{
				//if the input class is the same as the IDArray's brain class
				return id;
			}
		}
		return null;
	}
	public IDArray requestByType(Brain type)
	{
		//search through the IDArrays by Brain type (Sonar, Gunner, etc.)
		//if the types do not match then null is returned

		//used so that the movement brain can request enemy location data from radar etc.
		for (IDArray id : indexOfArrays)
		{
			if (type.getClass().equals(id.ID().getClass()))
			{
				//if the input class is the same as the IDArray's brain class
				return id;
			}
		}
		return null;
	}
*/
	//Start new code storage system
	@Override
	public void onPaint(Graphics2D g) 
	{
		for (RoboCore rc : ronny)
		{
			ArrayList<Double> eng = rc.extractEnergy();
			ArrayList<Double> dis = rc.extractDistance();
			ArrayList<Double> x = rc.extractX();
			ArrayList<Double> y = rc.extractY();
			for (int i = 0; i<eng.size(); i++)
			{
				g.setColor(Color.GREEN);
				int dx = (int) (double) x.get(i);
				int dy = (int) (double) y.get(i);
				int radi = (int) (double) dis.get(i);
				int e = (int) (double) eng.get(i);
				g.drawRect(dx, dy, 5, e);
			}
		}
	}
	
}