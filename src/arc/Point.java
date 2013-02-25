package arc;

public class Point
{
	//considered from the top left, CS gridding
	int[] coords = new int[2]; //x coord, y coord
	public Point(int[] locs)
	{
		this(locs[0], locs[1]);
	}
	public Point(int x, int y)
	{
		coords[0] = x;
		coords[1] = y;
	}
	public int[] getPoint()
	{
		return coords;
	}
}
