package arc;

public class GunBrain 
{
	DataBox store;
	double power = 1;
	double direction = 0;
	ArcBasicBot r;
	public GunBrain(DataBox data)
	{
		store = data;
		r = data.getRobot();
	}
	public void process()
	{
		//do what it needs to do to calculate information about what it needs to do, should result in calling of movement and firing for the gun
		r.setGunFire(getPower());
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
