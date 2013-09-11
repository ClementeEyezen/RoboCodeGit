package cwru;

public class Gunner extends Brain
{
	double gunEndTheta;
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
		}
		else
		{
			gunEndTheta = 0; //calculate desired theta
		}
		long totalTime = System.currentTimeMillis()-processTime;
		System.out.println("GUN calc time (millis):"+totalTime);
	}
	//Gunner controls the gun
	//it controls when it fires, how hard it fires and where it fires
}
