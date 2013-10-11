package cwru;

import java.util.ArrayList;

public class LifeBox 
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
	ArrayList<IDArray> indexOfArrays;
	int defaultTime = 50;
	public LifeBox(cwruBase robot)
	{
		new LifeBox(50, robot);
	}
	public LifeBox(int maxTimeTracked, cwruBase robot)
	{
		defaultTime = maxTimeTracked;
		indexOfArrays = new ArrayList<IDArray>(0);
		mainRobot = robot;
		battlefield_height = robot.getBattleFieldHeight();
		battlefield_width = robot.getBattleFieldWidth();
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

}