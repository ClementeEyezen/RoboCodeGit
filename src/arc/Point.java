package arc;

public class Point
{
	//considered from the top left, CS gridding
	double[] coords; //x coord, y coord
	public Point(double[] locs)
	{
		this(locs[0], locs[1]);
	}
	public Point(double x, double y)
	{
		coords = new double[2];
		coords[0] = x;
		coords[1] = y;
	}
	public double[] getPoint()
	{
		return coords;
	}
	public double distance(Point other)
	{
		double distance = 0;
		double[] oCoords = other.getPoint();
		double dx = oCoords[0]-coords[0];
		double dy = oCoords[1]-coords[1];
		distance = Math.sqrt(Math.pow(dx, 2)+Math.pow(dy, 2));
		return distance;
	}
	public double bearing(Point other)
	{
		double bearing = 0;
		double[] oCoords = other.getPoint();
		double dx = oCoords[0]-coords[0];
		double dy = oCoords[1]-coords[1];
		double rotbearing = Math.atan2(dy, dx);
		bearing = -rotbearing + (Math.PI/2);
		return bearing;
	}
}
