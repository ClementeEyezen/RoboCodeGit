package cwru;

public class Legs extends Brain
{
	double moveEndTheta;
	double moveEndDistance;
	public Legs(LifeBox source) 
	{
		super(source);
	}
	//Legs is the brain that controls movement of the robot
	public void process()
	{
		long processTime = System.currentTimeMillis();
		moveEndTheta = 0; //calculate desired theta
		moveEndDistance = 0;
		long totalTime = System.currentTimeMillis()-processTime;
		System.out.println("MOV calc time (millis):"+totalTime);
	}
}
