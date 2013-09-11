package cwru;

import java.util.ArrayList;

public class Projector extends Brain 
{
	//solely works on projecting where enemy robots will be based on current data
	//saves current projection tied with projected accuracy
	IDArray storageUnit;
	IDArray enemyDataUnit;
	public Projector(LifeBox source) 
	{
		super(source);
		source.allocateArray(this, "All_Projections");
		storageUnit = source.request(this);
		enemyDataUnit = source.request(new Sonar(source));
				//request the IDArray that will contain as scanned enemy data (Radar)
		
	}
	public void process()
	{
		long processTime = System.currentTimeMillis();
		enemyDataUnit = source.request(new Sonar(source)); //up to date enemy position data
		if (turnCounter < 4)
		{
			//projections = current position + velocity (linear)
		}
		else
		{
			//projections = projection system active
		}
		long totalTime = System.currentTimeMillis()-processTime;
		System.out.println("PRO calc time (millis):"+totalTime);
	}
	public Projection linearProjection(int forecastTime)
	{
		//project using scanned velocity, heading
		//forecast time is the time ahead of current time to project to
		int x = 0;
		int y = 0;
		double guessAccuracy = 1.0;
		return new Projection(x,y,turnCounter,turnCounter+forecastTime,guessAccuracy);
	}
	public Projection averageVelocityProjection(int forecastTime)
	{
		//project using point to point velocity, heading
		//forecast time is the time ahead of current time to project to
		int x = 0;
		int y = 0;
		double guessAccuracy = 1.0;
		return new Projection(x,y,turnCounter,turnCounter+forecastTime,guessAccuracy);
	}
	public Projection circularProjection(int forecastTime)
	{
		//project using scanned velocity, change in scanned velocity,
		//		scanned heading, change in scanned heading
		//forecast time is the time ahead of current time to project to
		int x = 0;
		int y = 0;
		double guessAccuracy = 1.0;
		return new Projection(x,y,turnCounter,turnCounter+forecastTime,guessAccuracy);
	}
}

class Projection
{
	int x; //projection location x
	int y; //projection location y
	int guessTime; //time the projection was made
	double guessError; //estimation of accuracy at time of projection looking forward
	int projTime; //time the projection is to be true
	double projError; //distance from reality to guess/(delta time*8*2)
	public Projection(int x, int y, int currentTime, int projTime, double estError)
	{
		this.x = x;
		this.y = y;
		this.guessTime = currentTime;
		this.guessError = estError;
		this.projTime = projTime;
		projError = -1;
	}
	public void update(int currentTime, int x, int y)
	{
		if (currentTime == projTime)
		{
			//0 = perfect
			//1 = equivalent to predicting no motion and the robot travels full speed
			//2 = robot went opposite direction at max compared to your projection at max
			projError = Brain.distance(this.x,this.y,x,y)/((projTime-guessTime)*8);
		}
	}
}
