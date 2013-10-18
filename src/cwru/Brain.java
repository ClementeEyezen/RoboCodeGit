package cwru;

import java.awt.Graphics2D;

public class Brain implements Paintable
{
	//a generic super class for any type of brain for a robocode robot
	LifeBox source;
	int turnCounter;
	public Brain(LifeBox source)
	{
		this.source = source;
		turnCounter = 0;
	}
	public void process()
	{
		//do calculations associated with running
		turnCounter+=1;
		//example construct, not necessarily useful
		if (turnCounter < 4)
		{
			//initial code
		}
		else
		{
			//insert running code here
		}
	}
	public void set()
	{
		//set the actions to happen. These need to just be reading an existing variable
		//that way all calculation happens before execute, timing is controllable
	}
	public static double distance(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2,2));
	}
	@Override
	public void onPaint(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}
}
