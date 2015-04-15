package wcb;

import java.awt.Graphics2D;

public class WcbGoToND extends WcbCircularTarget
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
	
	public void run()
	{
		startCase();
		execute();
		runModCall(); // the call that goes iterates the robot
	}
	public void runModCall() //the part of the run method that is repeated for the robot's actions
	{
		System.out.println("Turn ["+getTime()+"]");
		setTurnRadarRight(360);
		setAhead(8);
		myX = this.getX();
		myY = this.getY();
		//System.out.println("acknowledge call for nodeAvoidance");
		System.out.println("Nano time before node avoidance"+(System.nanoTime()-naneX));
		naneX = System.nanoTime();
		nodeAvoidance();
		//System.out.println("node Avoidance ended in runModCall");
		distanceCalls = 0;
		System.out.println("about to call execute");
		System.out.println("Nano time before execute "+(System.nanoTime()-naneX));
		naneX = System.nanoTime();
		execute();
		System.out.println("execute called successfully");
		runModCall();
	}
	public void startCase()
	{
		super.startCase();
		double spaceWidth = (this.getBattleFieldWidth()/nDimension);
		double spaceHeight = (this.getBattleFieldHeight()/nDimension);
		nodeMaxRange = (double) (2*Math.max((int) spaceWidth, (int) spaceHeight));

		for (int i=0;i<nDimension;i++)
		{
			nodeX[i] = (i*spaceWidth+(i+1)*spaceWidth)/2;
			nodeY[i] = (i*spaceHeight+(i+1)*spaceWidth)/2;
		}
	}
	public void nodeAvoidance()
	{
		//System.out.println("call for nodeAvoidance acknowledged");

		//System.out.println("aknowledge call for nodeTest");
		nodeTest((int) nodeX[0], (int) nodeX[nDimension-1], (int) nodeY[0], (int) nodeY[nDimension-1]);
		double turnRightValue = turnToHeading(moveToPoint(nearestN[0],nearestN[1]));
		//System.out.println("acknowledge recieve heading dH:"+turnRightValue);
		//if (Math.abs(this.getTurnRemaining())<=180)
		//{
		setTurnLeft(turnRightValue);
		//}
		//System.out.println("end nodeAvoidance");
	}
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
	{//runs 68 times per turn
		//System.out.println("call for nearestNode acknowledged");
		double setDistance = 1000000.01; 
		nearestN[0] = 100;
		nearestN[1] = 100;
		for (int  i = 0;i<nDimension;i++)
		{
			for (int  j = 0;j<nDimension;j++)
			{
				/*overDoseCount = overDoseCount+1;
				if (overDoseCount>maxLoopCount) maxLoopCount = overDoseCount;
				System.out.println("overDoseCount nearNode"+overDoseCount);
				System.out.println("maxLoopCount "+maxLoopCount);
				*/
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
			double compareRadius = 1; //(bulletCircle[6][0]+bulletCircle[7][0])/2;
			double compareDistance = distancePtoP(compareX,compareY,mX,mY)-compareRadius;
			if (compareDistance<setDistance)
			{
				setDistance = compareDistance;
			}
		}
		return setDistance;
	}
	public double singNodeReturn(double nX, double nY)
	{
		//System.out.println("call for singNodeReturn acknowledged");
		// double nodeX, nodeY
		nearestRobot(nX,nY);
		double nearestX = nearestXY[0];
		double nearestY = nearestXY[1];
		double nearestWave = nearestWave(nX,nY); //distance to the nearest circle/predicted bullet location
		double factorLocation = Math.min((distancePtoP(nearestX,nearestY,nX,nY)/nodeMaxRange),1);
		double factorBullet = Math.min((nearestWave/nodeMaxRange), 1);
		double factorMe = Math.min((distancePtoP(myX,myY,nX,nY)/nodeMaxRange),1);
		double allFactor = (factorLocation+factorBullet+factorMe)/3;
		//System.out.println("singNodeReturn completed, returning "+allFactor);
		return allFactor;
	}
	public void nodeTest(int minX, int maxX, int minY, int maxY) //sets desired location to move to
	{
		//System.out.println("call for nodeTest aknowledged");
		//sets the X,Y of most optimal node, recursively
		minX =(int) minX;
		maxX =(int) maxX;
		minY =(int) minY;
		maxY =(int) maxY;
		for (int j = 0;j<7;j++)
		{
			System.out.println("nodeTest loop #"+j);
			int deltaX = maxX-minX;
			int deltaY = maxY-minY;
			if (deltaX<(nodeMaxRange/2) && deltaY<(nodeMaxRange/2) )
			{
				//nearestNode(minX,minY);
				nearestN[0] = minX;//(minX+maxX)/2;
				nearestN[1] = minY;//(minY+maxY)/2;
				//System.out.println("call for nodeTest complete via size outtake");
				break;
			}
			double halfOne = 0;
			double halfTwo = 0;
			counter = counter*(-1);
			//System.out.println("initializing variables to test summation");
			int i = 0;
			int kafka = 0;
			overDoseCount = 0;
			for (i= 0;i<nDimension;i++)
			{
				//the range of values to be tested is from minX to maxX
				for (kafka= 0;kafka<nDimension;kafka++)
				{
				//the range of values to be tested is from minY to maxY
					if (counter>0) // LeftRight case
					{
						if ((int) ((i/(nodeMaxRange/4))-1)<8 && nodeX[(int) ((i/(nodeMaxRange/4)))]<maxX)
						{
							//System.out.println("acknowledge call for singNodeReturn");
							halfOne = halfOne+singNodeReturn(nodeX[i],nodeY[kafka]);
						}
						else
						{
							//System.out.println("acknowledge call for singNodeReturn");
							halfTwo = halfTwo+singNodeReturn(nodeX[i],nodeY[kafka]);
						}
					}
					else //UpDown case
					{
						if ((int) ((i/(nodeMaxRange/4))-1)<8 && nodeY[(int) ((i/(nodeMaxRange/4)))]<maxY)
						{
							//System.out.println("acknowledge call for singNodeReturn");
							halfOne = halfOne+singNodeReturn(nodeX[i],nodeY[kafka]);
						}
						else
						{
							//System.out.println("acknowledge call for singNodeReturn");
							halfTwo = halfTwo+singNodeReturn(nodeX[i],nodeY[kafka]);
						}
					}
				}
			}
			//System.out.println("begin set new boundaries");
			if (halfOne>halfTwo)
			{
				if (counter>0) // LeftRight case
				{
					minX = minX;
					maxX = (maxX+minX)/2;
				}
				else
				{
					minY = minY;
					maxY = (maxY+minY)/2;
				}
			}
			else
			{
				if (counter>0) // LeftRight case
				{
					minX = (maxX+minX)/2;
					maxX = maxX;
				}
				else
				{
					minY = (maxY+minY)/2;
					maxY = maxY;
				}
			}
			//System.out.println("end set new boundaries");
		}
		//System.out.println("end of NodeTest via time out chair");
	}
	public void onPaint(Graphics2D painter)
	{
		//super.onPaint(painter);
		//painting each node based on desireablility
		painter.setColor(java.awt.Color.CYAN);
		painter.drawLine((int) this.getX(),(int) this.getY(),(int) nearestN[0],(int) nearestN[1]);
		for (int i = 0;i<nDimension;i++)
		{
			for (int j = 0;j<nDimension;j++)
			{
				double storeCase = singNodeReturn(nodeX[i],nodeY[j]);
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
	}
}
