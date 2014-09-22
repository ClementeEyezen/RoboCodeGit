package cepl.dataStorage;

import java.util.ArrayList;

public class BotBin {
	public ArrayList<DataPoint> info;
	public String name;
	public int scan_data;
	public int fill_data;
	
	public int[] hitbin;
	public int sum;
	
	public BotBin(String name)
	{
		info = new ArrayList<DataPoint>();
		this.name = name;
		hitbin = new int[40];
	}
	
	public void addData(DataPoint dp)
	{
		if (info.size()>=1)
		{
			long delta_time = dp.time - info.get(info.size()-1).time;
			if (delta_time > 1)
			{
				ArrayList<DataPoint> generated_data = fill_data(info.get(info.size()-1),dp);
				for(int i = 0; i < generated_data.size(); i++)
				{
					generated_data.get(i).generated_data = true;
					info.add(generated_data.get(i));
					fill_data++;
				}
				info.add(dp);
				scan_data++;
			}
			else if (delta_time <= 0)
			{
				//do nothing, repeat entry
			}
			else
			{
				info.add(dp);
				scan_data++;
			}
		}
		else
		{
			//no other data, just add
			info.add(dp);
			scan_data++;
		}
		
	}
	public ArrayList<DataPoint> fill_data(DataPoint last_known, DataPoint new_scan)
	{
		//return the datapoints between the last known data point and the new point, not inclusive
		//speed, bearing are linearly varied
		//energy drops are assumed to have happened at the first step
		//points follow speed bearing shift, effectively euler's method
		
		long step_count = new_scan.time-last_known.time-1; //number of steps to run the for loop
		double delta_speed = (new_scan.speed-last_known.speed)/(double) (step_count+1);
		double delta_heading = (new_scan.direction-last_known.direction)/(double) (step_count+1);
		double oldx = last_known.x;
		double oldy = last_known.y;
		
		ArrayList<DataPoint> toReturn = new ArrayList<DataPoint>();
		/*
		if(delta_heading > Math.PI)
		{
			delta_heading = -((delta_heading%(2*Math.PI))-2*Math.PI);
		}
		else if (delta_heading < -Math.PI)
		{
			delta_heading = -(delta_heading%(2*Math.PI)-2*Math.PI);
		}
		*/
		for(int i = 1; i <= step_count; i++)
		{
			double x = oldx + (last_known.speed+i*delta_speed)*Math.cos(-(last_known.direction-Math.PI/2)+i*delta_heading);
			double y = oldy + (last_known.speed+i*delta_speed)*Math.sin(-(last_known.direction-Math.PI/2)+i*delta_heading);
			toReturn.add(new DataPoint(x, y, new_scan.energy, last_known.speed+i*delta_speed, 
					last_known.direction+i*delta_heading, last_known.time+i, last_known, true));
			oldx = x;
			oldy = y;
		}
		
		return toReturn;
	}
	
	public String percent_fill()
	{
		double percent = (double)(fill_data)/((double)(scan_data)+(double)(fill_data));
		String toReturn = percent+"";
		return toReturn.substring(0, 3);
	}
	
	public void increment_hitbin(double percent)
	{
		if(percent >=-1 && percent <=1)
		{//if its a valid input
			sum++;
			if(percent == 1.0)
			{
				hitbin[hitbin.length-1]++;
			}
			else
			{
				int index = (int) Math.floor((percent*((double)hitbin.length)/2+hitbin.length/2));
				hitbin[index]++;
			}
		}
	}
}
