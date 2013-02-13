package wrc;


public class StarlightScope 
{ //targeting class for DataCatch
	public static double finalXJ;
	public static double finalYJ;
	public static double finalXT;
	public static double finalYT;
	public static double recX;
	public static double recY;
	public double[] aimBot(int robotIndex, boolean timeChange, double[][] eX,double[][] eY, double[][] eDirection, double[][] eSpeed, double[][] eBearing, double[][] eDistance, 
			double[][] eEnergy, double[][] eHeading, double[][] eGunHeat, double[][] eEnergyDrop, double[][] eScanTime, double[] mX, double[] mY, 
			double[] mDirection, double[] mSpeed, double[] mEnergy, double[] mHeading, double[] mGunHeat, double[] mEnergyDrop)
	{
		//Begin location projection
		double returnAngle = 45;
		double eCX = eX[robotIndex][0];
		double eCY = eY[robotIndex][0];
		//locations
		double eCS = eSpeed[robotIndex][0];
		//current speed
		double eCH = eHeading[robotIndex][0];
		//current heading
		double eLH = eHeading[robotIndex][1];
		//last heading before the most recent
		double eDH = eCH - eLH;
		//change in heading
		//make sure it is within reasonable bounds
		double dT = eScanTime[robotIndex][0]-eScanTime[robotIndex][1];
		//change in time since last scan
		double stanDH = eDH/dT; 
		//average distance turn per tick
		double bulletFlightTime = eDistance[robotIndex][0]/(20-(3));
		double totalDH = bulletFlightTime*stanDH;
		double angularSpeed = (stanDH/180)*Math.PI*.5;//angle travelled through the circle in radians
		double totalaS = angularSpeed*bulletFlightTime;
		double triTheta = totalaS/2;
		double radius = eCS/angularSpeed;
		double hypoten = radius;
		double farCyde = 2*(hypoten*Math.sin((triTheta)));
		double relativeSpeed = farCyde/bulletFlightTime;
		double relativeChangeHeading = totalDH/2;
		if (angularSpeed == 0) 
		{
			relativeSpeed = eCS/(.75);
			relativeChangeHeading = 0;
		}
		double throughput = relativeChangeHeading+eCH;
		double rotOutput= -(throughput-90);
		double endX = eCX+bulletFlightTime*.6*relativeSpeed*Math.cos(Math.toRadians(rotOutput));
		double endY = eCY+bulletFlightTime*.6*relativeSpeed*Math.sin(Math.toRadians(rotOutput));
		double changeX = mX[0]-endX;
		double changeY = mY[0]-endY;
		if (changeX ==0)
		{
			if (changeY<=0)
			{
				returnAngle = 0;
			}
			else
			{
				returnAngle = 180;
			}
		}
		else if (changeX > 0)
		{
			returnAngle = Math.toDegrees(Math.atan(changeY/changeX));
		}
		else
		{
			returnAngle = 180+Math.toDegrees(Math.atan(changeY/changeX));
		}
		returnAngle = -(returnAngle+90);
		//End location projection		
		double returnFire[] = new double[5];
		returnFire[0] = returnAngle;//bearing, in robocode degrees
		returnFire[1] = 1.1;//firepower optimal damagepertick and bullet speed
		returnFire[2] = endX; //used for painting targetting info
		returnFire[3] = endY;
		returnFire[4] = relativeChangeHeading+eCH;
		return returnFire; //returns the desired bearing,firepower for the gun based on all data
	}
}
