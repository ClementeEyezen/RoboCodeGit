package arc2;

public class Axis 
{
	//represents a line across a 2D Cartesian plane
	//written as two parametric equations
	double xconst; //rise
	double yconst; //run
	double x0;
	double y0;
	boolean isVertical;
	double ratio;
	
	public Axis(Point p1, Point p2)
	{
		xconst = p1.getPoint()[0]-p2.getPoint()[0];
		x0 = p1.getPoint()[0];
		yconst = p1.getPoint()[1]-p2.getPoint()[1];
		y0 = p1.getPoint()[1];
		if (xconst == 0)
		{
			isVertical = true;
			ratio = 0;
		}
		else
		{
			isVertical = false;
			ratio = yconst/xconst;
		}
	}
	public Axis(double xcon, double ycon)
	{
		xconst = xcon;
		yconst = ycon;
		x0 = 0;
		y0 = 0;
		if (xcon == 0)
		{
			isVertical = true;
			ratio = 0;
		}
		else
		{
			isVertical = false;
			ratio = yconst/xconst;
		}
	}
	public double xofT(double t)
	{
		return t*xconst + x0;
	}
	public double tofX(double x)
	{
		return (x - x0)/xconst;
	}
	public double yofT(double t)
	{
		return t*yconst + y0;
	}
	public double tofY(double y)
	{
		return (y - y0)/yconst;
	}
	public boolean parallel(Axis other)
	{
		if (other.getVert() && this.getVert()) return true;
		else if (other.getRatio() == this.getRatio()) return true;
		else return false;
	}
	public double getRatio()
	{
		return ratio;
	}
	public boolean getVert()
	{
		return isVertical;
	}
	public double getXc()
	{
		return xconst;
	}
	public double getYc()
	{
		return yconst;
	}
	public double getX0()
	{
		return x0;
	}
	public double getY0()
	{
		return y0;
	}
	public Point intersect(Axis other)
	{
		if (parallel(other)) return new Point (-1, -1);
		else
		{
			//if it isn't parallel
			if (this.getVert())
			{
				//return the x of this function
					//then the y coord or the t value of the x coord
				return new Point(this.getX0(), other.yofT(other.tofX(this.getX0())));
			}
			else if (other.getVert())
			{
				return new Point(other.getX0(), this.yofT(this.tofX(other.getX0())));
			}
			else
			{
				//if both aren't parallel and non-vertical
				return new Point(this.xofT((-this.getX0()+other.getX0())/(this.getXc()+other.getXc())),
									this.yofT((-this.getX0()+other.getX0())/(this.getXc()+other.getXc())));
				//I hope this works because it will be a b**** to debug
			}
		}
	}
}
