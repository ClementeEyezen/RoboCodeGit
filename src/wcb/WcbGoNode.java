package wcb;

import java.awt.Graphics2D;

public class WcbGoNode extends WcbCircularTarget
{
	public static int nDimension = 8;
	public static double[] nodeX = new double[nDimension];
	public static double[] nodeY = new double[nDimension];
	public static double nodeMaxRange = 100;
	public static double nearestXY[] = new double[2];
	public static double nearestN[] = new double[2];
	public static int counter = 1;
	public static double myX = 0;
	public static double myY = 0;
	public static int count = 0;
	public static long naneX = 0;
	public static int overDoseCount = 0;
	public static int maxLoopCount = 1;
	public static int distanceCalls = 0;
	public static double randomizer = 0;
	public static double qtr = 0;
	public static double qbr = 0;
	public static double qtl = 0;
	public static double qbl = 0;
	public static double spaceWidth;
	public static double spaceHeight;
	public static double midX;
	public static double midY;

	public void run()
	{
		startCase();
		System.out.println("startcase finished");
		execute();
		while(true)
		{
			//arbitrary beginnings, defaults for each turn if nothing else is changed
			setTurnRadarRight(360);
			setTurnLeft(90);
			//makes movement random, making it harder for most other bots to hit (varies 4 to 8)
			randomizer = 4*Math.random()-2;
			setAhead(6+randomizer);
			//this allow me to only call getX, getY once, I was having overrun problems
			myX = this.getX();
			myY = this.getY();
			//System.out.println("arbitrary arbitration finished");
			//now we look to change the setTurnLeft to move to a desired point
			//this looks at "nodes" on the battlefield and chooses the best one
			//System.out.println("choose node called");
			//ignore this, it doesn't work
			//chooseNode();
			double curseit = turnToHeading(basicSquare());
			setTurnLeft(curseit);
			execute();
		}
	}
	public double basicSquare() 
	{
		double LX = myX-36;
		double MX = myX;
		double RX = myX+36;
		double TY = myY+36;
		double MY = myY;
		double BY = myY-36;
		int direction = (int) (8*Math.random());
		double point = 0;
		switch (direction)
		{
		case 0:
			if (TY<(this.getBattleFieldHeight()-18)) 
			{
				point = direction*45;
				break;
			}
			else
			{
				direction = 1;
			}
		case 1:
			if (TY<(this.getBattleFieldHeight()-18) && RX<(this.getBattleFieldWidth()-18)) 
			{
				point = direction*45;
				break;
			}
			else
			{
				direction = 2;
			}
		case 2:
			if (RX<(this.getBattleFieldWidth()-18)) 
			{
				point = direction*45;
				break;
			}
			else
			{
				direction = 3;
			}
		case 3:
			if (RX<(this.getBattleFieldWidth()-18) && BY>18) 
			{
				point = direction*45;
				break;
			}
			else
			{
				direction = 4;
			}
		case 4:
			if (BY>(18)) 
			{
				point = direction*45;
				break;
			}
			else
			{
				direction = 5;
			}
		case 5:
			if (BY>(18) && LX>(18)) 
			{
				point = direction*45;
				break;
			}
			else
			{
				direction = 6;
			}
		case 6:
			if (LX>(18)) 
			{
				point = direction*45;
				break;
			}
			else
			{
				direction = 7;
			}
		case 7:
			if (LX>(18)) 
			{
				point = direction*45;
				break;
			}
			else
			{
				point = 0;
			}
		default:
			{
				point = 0;
			}
		}
		return point;
	}
	//Begin executive method
	public void startCase()
	{
		super.startCase();
		spaceWidth = (this.getBattleFieldWidth()/nDimension);
		spaceHeight = (this.getBattleFieldHeight()/nDimension);
		nodeMaxRange = (double) (2*Math.max((int) spaceWidth, (int) spaceHeight));

		for (int i=0;i<nDimension;i++)
		{
			nodeX[i] = (i*spaceWidth+(i+1)*spaceWidth)/2;
			nodeY[i] = (i*spaceHeight+(i+1)*spaceHeight)/2;
		}
	}
	/*public void chooseNode()
	{
		System.out.println("nodeTest called");
		nodeTest();
		System.out.println("nodeTest finished");
		//turn left to suggested point
		double anglePoint = moveToPoint(nearestN[0],nearestN[1]);
		double turnRightValue = turnToHeading(anglePoint);
		setTurnLeft(turnRightValue);
	}
	public void nodeTest() //sets desired location to move to, assuming the entire field
	{
		//the range of values one might look in (field width between nodes +1 on either side)
		double minX =(int) nodeX[0]-1;
		double maxX =(int) nodeX[nDimension-1]+1;
		double minY =(int) nodeY[0]-1;
		double maxY =(int) nodeY[nDimension-1]+1;
		System.out.println("max and min range set, X:"+minX+"-"+maxX+" Y:"+minY+"-"+maxY);
		//lets process this how many times (dividing into quarters)
		int processLevel = 4;
		for (int i = 0;i<processLevel;i++)
		{
			//reset the evaluation boxes at each processing level
			qtr = 0;
			qbr = 0;
			qtl = 0;
			qbl = 0;
			int jmin = (int) (minX/spaceWidth);
			int jmax = (int) (maxX/spaceWidth);
			int kmin = (int) (minY/spaceHeight);
			int kmax = (int) (maxY/spaceHeight);
			System.out.println(jmin+"|"+jmax+"|"+kmin+"|"+kmax);
			//now lets divide the field into sets
			//start by running through all node points
			for (int j = jmin;j<jmax;j++)
			{//j is the x variable to put into nodeX[j]
				for (int k = kmin;k<kmax;k++)
				{//k is the y variable to put into nodeY[k]
					//now to slot them into quarters
					//System.out.println("condition one"+(nodeX[j]>(minX+maxX)/2));
					if (nodeX[j]<(minX+maxX)/2)//right side
					{
						if (nodeY[k]<(minY+maxX)/2)//top side
						{
							//top right
							//the set of values for the top right is incremented the value of the node tested, because it is in the right box
							qtr = qtr+singleNode(nodeX[j],nodeY[k]);
						}
						else //bottom side
						{
							//bottom right
							qbr = qbr+singleNode(nodeX[j],nodeY[k]);
						}
					}
					else //left side
					{
						if (nodeY[k]<(minY+maxX)/2)//top side
						{
							//top left
							qtl = qtl+singleNode(nodeX[j],nodeY[k]);
						}
						else //bottom side
						{
							//bottom left
							qbl = qbl+singleNode(nodeX[j],nodeY[k]);
						}
					}
				}
			}
			//now we have incremented through all of our nodes, and presumably got a sum for each quadrant
			//the midpoints for each side are now set for use later
			midX = (minX+maxX)/2+1;
			midY = (minY+maxY)/2+1;
			System.out.println("Values by quarter");
			System.out.println(qtl+"|"+qtr);
			System.out.println("----------------------");
			System.out.println(qbl+"|"+qbr);
			if (qtr>=qbr && qtr>=qtl && qtr>=qbl)
			{
				//top right quadrant best, set min values to average
				minX = midX;
				minY = midY;
			}
			else if (qbr>=qtr && qbr>=qtl && qbr>=qbl)
			{
				//bottom right quadrant best, set minX to average, maxY to average
				minX = midX;
				maxY = midY;
			}
			else if (qtl>=qtr && qtl>=qbr && qtl>=qbl)
			{
				//top left quadrant best, set minY to average, maxX to average
				maxX = midX;
				minY = midY;
			}
			else
			{
				//bottom left quadrant best, set max values to average
				maxX = midX;
				maxY = midY;
			}
			System.out.println("max and min range set, X:"+minX+"-"+maxX+" Y:"+minY+"-"+maxY);
			//the new sizes of the box are set, the cycle resets itself to run more times
		}
		//the cycle has run its course, and is now looking to find out where it wants to look, choosing the nearest node to its current location
		nearestNode(minX,minY);
		if (Math.abs(nearestN[0]-myX)<18) nearestN[0] = spaceWidth-nearestN[0];
		if (Math.abs(nearestN[1]-myY)<18) nearestN[1] = spaceHeight-nearestN[1];

	}
	public double singleNode(double nX, double nY)
	{
		//System.out.println("call for singNodeReturn acknowledged");
		// double nodeX, nodeY
		nearestRobot(nX,nY);
		double nearestX = nearestXY[0];
		double nearestY = nearestXY[1];
		//double nearestWave = nearestWave(nX,nY); //distance to the nearest circle/predicted bullet location
		double factorLocation = Math.min((distancePtoP(nearestX,nearestY,nX,nY)/spaceWidth),1);
		//double factorBullet = Math.min((nearestWave/nodeMaxRange), 1);
		double factorMe = Math.min((distancePtoP(myX,myY,nX,nY)/spaceWidth),1);
		System.out.println("factorMe"+factorMe);
		double allFactor = (factorLocation+factorMe)/2;
		System.out.println("singNodeReturn completed, returning "+allFactor);
		return allFactor;
	}*/
	//BEGIN spatial methods
	public double moveToPoint(double tX, double tY) //returns angle to a point
	{
		//double targetX, targetY
		double mX = myX;
		double mY = myY;
		double dX = tX-mX; //deltaX, change in X
		double dY = tY-mY; //deltaY, change in Y
		double sA = 0; //suggestedAngle
		if (dX == 0) //account for 0 case
		{
			if (dY< 0)
			{
				sA = 180;
			}
			else
			{
				sA = 0;
			}
		}
		else
		{
			double dRatio = dY/dX; //input into atan func
			double tA = deg(Math.atan(dRatio)); //true angle
			double rA = -(tA-90); //rotated, realigned angle for roboCode
			sA = rA;
		}
		if (dX<0)
		{
			sA = sA+180;
		}
		return sA;

	}
	//Begin spacial filters
	public double turnToHeading(double sH) //returns the number of degrees to turn right to a desired heading
	{
		//double suggestedHeading
		double mH = this.getHeading(); //mhHeading
		double dH = mH - sH; //change in Heading needed
		//filters, so that it never goes more than 360 to an angle
		//System.out.println("my Heading = "+mH+" desired heading = "+sH+" current change in heading = "+dH);
		if (Math.abs(dH)>=360)
		{
			while (Math.abs(dH)>=360)
			{
				if (dH >0)
				{
					dH = dH-360;
				}
				else
				{
					dH = dH+360;
				}
			}
			//System.out.println("current change in heading (360) = "+dH);
		}
		//filters, so that it never goes more than 180 to an angle
		if (Math.abs(dH)>=180)
		{
			while (Math.abs(dH)>=180)
			{
				if (dH >0)
				{
					dH = -(360-dH);
				}
				else
				{
					dH = -(360-dH);
				}
			}
			//System.out.println("current change in heading (180) = "+dH);
		}
		//System.out.println("to return dH = "+dH);
		return dH;
	}
	public double distancePtoP(double X1, double Y1, double X2, double Y2) //returns distance between two point sets
	{//called 344 times per turn
		distanceCalls = distanceCalls+1;
		//System.out.println("overDose on distance "+distanceCalls);
		double dX = X2-X1;
		double dY = Y2-Y1;
		double distance = Math.sqrt(Math.pow(dX,2)+Math.pow(dY,2));
		return distance;
	}
	public void nearestRobot(double mX, double mY) //sets nearestXY for robot to a node
	{
		//System.out.println("call for nearestRobot acknowledged");
		// the looking point's X, looking point's X (named myX, myY)
		// input reference point
		double setDistance = 1000000.01; 
		for (int  i = 0;i<maxNumRobots;i++)
		{
			double compareX = X[i][0];
			double compareY = Y[i][0];
			double compareDistance = distancePtoP(compareX,compareY,mX,mY);
			if (compareDistance<setDistance)
			{
				nearestXY[0] = compareX;
				nearestXY[1] = compareY;
				setDistance = compareDistance;
			}
		}

	}
	public void nearestNode(double mX, double mY) //sets nearestN for node to a point
	{
		System.out.println("looking for node nearest ("+mX+","+mY+")");
		double setDistance = 1000000.01; 
		nearestN[0] = 100;
		nearestN[1] = 100;
		for (int  i = 0;i<nDimension;i++)
		{
			for (int  j = 0;j<nDimension;j++)
			{
				double compareX = nodeX[i];
				double compareY = nodeY[j];
				double compareDistance = distancePtoP(compareX,compareY,mX,mY);

				if (compareDistance<setDistance)
				{
					nearestN[0] = compareX;
					nearestN[1] = compareY;
					setDistance = compareDistance;
				}
			}
		}
		System.out.println("nearest Node ("+nearestN[0]+","+nearestN[1]+")");
	}
	public double nearestWave(double mX,double mY) //returns the nearest distance to a node of a bullet wave
	{
		// the looking point's X, looking point's X (named myX, myY)
		// input reference point
		overDoseCount = 0;
		double setDistance = 1000000.01; 
		for (int  i = 0;i<maxNumRobots;i++)
		{
			/*overDoseCount = overDoseCount+1;
			if (overDoseCount>maxLoopCount) maxLoopCount = overDoseCount;
			System.out.println("overDoseCount nearWave"+overDoseCount);
			System.out.println("maxLoopCount "+maxLoopCount);
			 */
			double compareX = X[i][0];
			double compareY = Y[i][0];
			double compareRadius = (bulletCircle[6][0]+bulletCircle[7][0])/2;
			double compareDistance = distancePtoP(compareX,compareY,mX,mY)-compareRadius;
			if (compareDistance<setDistance)
			{
				setDistance = compareDistance;
			}
		}
		return setDistance;
	}
	/*public void onPaint(Graphics2D painter)
	{
		//super.onPaint(painter);
		//painting each node based on desireablility
		painter.setColor(java.awt.Color.CYAN);
		painter.drawLine((int) this.getX(),(int) this.getY(),(int) nearestN[0],(int) nearestN[1]);
		for (int i = 0;i<nDimension;i++)
		{
			for (int j = 0;j<nDimension;j++)
			{
				double storeCase = singleNode(nodeX[i],nodeY[j]);
				if (storeCase<=.25)
				{
					painter.setColor(java.awt.Color.RED);
				}
				else if (storeCase<=.5)
				{
					painter.setColor(java.awt.Color.ORANGE);
				}
				else if (storeCase<=.75)
				{
					painter.setColor(java.awt.Color.YELLOW);
				}
				else
				{
					painter.setColor(java.awt.Color.GREEN);
				}
				painter.fillRect((int) nodeX[i],(int) nodeY[j],5,5);
			}
		}
		if (qtr>=qbr && qtr>=qtl && qtr>=qbl)
		{
			painter.setColor(java.awt.Color.RED);
		}
		else if (qbr>=qtr && qbr>=qtl && qbr>=qbl)
		{
			painter.setColor(java.awt.Color.ORANGE);
		}
		else if (qtl>=qtr && qtl>=qbr && qtl>=qbl)
		{
			painter.setColor(java.awt.Color.YELLOW);
		}
		else
		{
			painter.setColor(java.awt.Color.GREEN);
		}
		painter.fillRect((int) midX,(int) midY,20,20);
	} */
}
