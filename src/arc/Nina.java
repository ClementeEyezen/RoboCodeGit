package arc;

import java.awt.Color;
import java.awt.Graphics2D;

public class Nina extends ArcBasicBot 
{
	//Nina is the compilation of Ninja brains that builds off ArcBasicBot
	NinjaMove mary;
	NinjaDar rarely;
	public void run()
	{
		//make the ports move independently
		this.setRotateFree();
		this.setColor(Color.BLUE);
		roomWidth = this.getBattleFieldWidth();
		roomHeight = this.getBattleFieldHeight();
		System.out.println("r.roomWidth = "+roomWidth);
		System.out.println("r.roomHeight = "+roomHeight);
		//create new brains
			//Nina has a NinjaMove brain, a standard DataBox
			//Nina will have a NinjaDar and a NinjaGun
		dan = new DataBox(this);
		mary = new NinjaMove(dan);
		gary = new GunBrain(dan);
		rarely = new NinjaDar(dan);
		//begin run through of calculations
		while (true)
		{
			mary.process();
			gary.process();
			rarely.process();
			execute();
		}
	}
	@Override
	public void onPaint(Graphics2D g)
	{
		super.onPaint(g);
		mary.drawData(g);
		rarely.drawData(g);
	}
}
