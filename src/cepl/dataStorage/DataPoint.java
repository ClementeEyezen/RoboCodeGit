package cepl.dataStorage;

public class DataPoint {

	public double x;
	public double y;
	public double energy;
	double speed;
	double direction;
	public long time;
	
	boolean shot_flag;
	
	double ds;
	double theta;
	
	DataPoint prior;
	
	boolean generated_data;
	
	// full constructor
	public DataPoint(double x, double y, double energy, double speed, double direction, long time, 
			boolean shot_flag, DataPoint prior, boolean generated) {
		this.x = x;
		this.y = y;
		this.energy = energy;
		this.speed = speed;
		this.direction = direction;
		this.time = time;
		
		this.prior = prior;
		
		this.shot_flag = shot_flag;
		this.generated_data = generated;
		
		if (prior != null)
		{
			double dx = x - prior.x;
			double dy = y - prior.y;
			this.ds = Math.sqrt(dx*dx + dy*dy);
			this.theta = Math.atan2(dy, dx);
		}
		else
		{
			ds = speed;
			theta = direction;
		}
	}
	
	//not given shot flag, just prior
	public DataPoint(double x, double y, double energy, double speed, double direction, long time,
			DataPoint prior, boolean generated) {
		this.x = x;
		this.y = y;
		this.energy = energy;
		this.speed = speed;
		this.direction = direction;
		this.time = time;
		
		this.prior = prior;
		
		if (prior != null)
		{
			double dx = x - prior.x;
			double dy = y - prior.y;
			this.ds = Math.sqrt(dx*dx + dy*dy);
			this.theta = Math.atan2(dy, dx);
			
			if (energy - prior.energy >.1 && energy - prior.energy < 2)
			{
				shot_flag = true;
			}
			else
			{
				shot_flag = false;
			}
		}
		else
		{
			this.ds = speed;
			this.theta = direction;
		}
	}
	
	public String toString()
	{
		return "<DataPoint x="+(x+"      ").substring(0, 6)
		+" y="+(y+"      ").substring(0,6)
		+" energy="+(energy)
		+" speed="+(speed)
		+" direction="+(direction+"      ").substring(0,6)
		+" time="+(time)
		+" generated_data="+(generated_data)
		+">";
	}
}
