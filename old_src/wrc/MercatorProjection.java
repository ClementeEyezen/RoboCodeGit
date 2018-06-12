package wrc;

public class MercatorProjection 
{ //movement class for DataCatch
	public double[] mappingData(int robotIndex, double[][] eX,double[][] eY, double[][] eDirection, double[][] eSpeed, double[][] eBearing, double[][] eDistance, 
			double[][] eEnergy, double[][] eHeading, double[][] eGunHeat, double[][] eEnergyDrop, double[][] eScanTime, double[] mX, double[] mY, 
			double[] mDirection, double[] mSpeed, double[] mEnergy, double[] mHeading, double[] mGunHeat, double[] mEnergyDrop)
	{
		//System.out.println("begin mapping Data");
		//return code only below this point
		double returnCourse[] = new double[2];
		returnCourse[0] = eHeading[robotIndex][0]+180;//bearing, in robocode degrees (40-10-0 had against Walls/Spinbot)
		returnCourse[1] = 8;//speed
		//System.out.println("Send response to Captain Kirk");
		return returnCourse; //returns the desired bearing,distance for the robot based on all data
	}
}
