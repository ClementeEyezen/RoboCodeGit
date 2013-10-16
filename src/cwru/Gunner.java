package cwru;

public class Gunner extends Brain
{
	//NOTE TODO remember to be careful about switching rotated coordinate systems
	double gunEndTheta;
	double gun_energy;
	boolean fire;
	public Gunner(LifeBox source) 
	{
		super(source);
	}
	public void process()
	{
		long processTime = System.currentTimeMillis();
		if (turnCounter < 4)
		{
			gunEndTheta = 0;
			fire = false;
			gun_energy = .1;
		}
		else
		{
			gunEndTheta = 0; //calculate desired theta
			fire = true;
			gun_energy = .5;
		}
		long totalTime = System.currentTimeMillis()-processTime;
		System.out.println("GUN calc time (millis):"+totalTime);
	}
	//Gunner controls the gun
	//it controls when it fires, how hard it fires and where it fires\
	//it picks a target, fires at the best projection for the target, picks the next target, etc.
	public final void set()
	{
		//point the gun
		source.getRobot().setTurnGunLeft(gunEndTheta);
		//if the gun is supposed to fire, do it
		if (fire) source.getRobot().setFireBullet(gun_energy);
		//otherwise, do no firing
		else source.getRobot().setFireBullet(0.0);
	}
}
