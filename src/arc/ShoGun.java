package arc;

public class ShoGun extends GunBrain 
{
	public ShoGun(DataBox data) 
	{
		super(data);
	}
	public void process()
	{
		store.getOpponents();
	}
}
