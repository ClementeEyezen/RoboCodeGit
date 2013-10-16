package cwru;

public class Projection
{
	String robot_name;
	double x; //projection location x
	double y; //projection location y
	long guessTime; //time the projection was made
	double guessError; //estimation of accuracy at time of projection looking forward
	long projTime; //time the projection is to be true
	double projError; //distance from reality to guess/(delta time*8*2)
	public Projection(double x, double y, long currentTime, long projTime, double estError)
	{
		this.x = x;
		this.y = y;
		this.guessTime = currentTime;
		this.guessError = estError;
		this.projTime = projTime;
		projError = -1;
	}
	public void update(long currentTime, double x, double y)
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