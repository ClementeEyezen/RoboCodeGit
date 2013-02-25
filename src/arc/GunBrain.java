package arc;

public class GunBrain 
{
	DataBox store;
	double power = 1;
	double direction = 0;
	public GunBrain(DataBox data)
	{
		store = data;
	}
	public void process()
	{
		//do what it needs to do to calculate information about what it needs to do, should result in calling of movements
	}
	public double getPower()
	{
		//return the desired power for the bullet
		calcPower();
		return power;
	}
	public void calcPower()
	{
		power = 1;
	}
	public double getDirection()
	{
		calcDirection();
		return direction;
	}
	public void calcDirection()
	{
		direction = 0;
	}
	public boolean fireQ()
	{
		if (Math.abs(store.getGunDirection()-getDirection())<=1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
