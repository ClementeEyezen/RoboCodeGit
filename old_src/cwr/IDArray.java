package cwr;

import java.util.ArrayList;

public class IDArray extends ArrayList<nameArray>
{
	Brain uniqueIdentifier;
	public IDArray(Brain origin)
	{
		super();
		uniqueIdentifier = origin;
	}
	public ArrayList<String> getDataTypes()
	{
		//returns the string name of every item within an IDArray
		ArrayList<String> nameList = new ArrayList<String>();
		for (nameArray n : this)
		{
			nameList.add(n.name());
		}
		return nameList;
	}
	public Brain ID()
	{
		return uniqueIdentifier;
	}
}