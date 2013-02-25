package arc;

import robocode.AdvancedRobot;

public class RadarBrain 
{
	DataBox store;
	ArcBasicBot r;
	public RadarBrain(DataBox data)
	{
		store = data;
		r = data.getRobot();
	}
	public void process()
	{
		//do what it needs to do to calculate information about what it needs to do, should result in calling of movement for the radar
		r.moveRadarTo(store.getRadarDirection()+Math.PI);
	}
}
