package arc.robots;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import arc.model.RobotModel;
import robocode.AdvancedRobot;
import robocode.BattleEndedEvent;
import robocode.BulletHitBulletEvent;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobotDeathEvent;
import robocode.RoundEndedEvent;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;

public class Fractal extends AdvancedRobot {

	// Battle Properties
	double room_width, room_height;
	double time;
	
	boolean debug = true;
	
	boolean oneVoneAssumption = true;
	
	// Robot Properties
	RobotModel rm;
	
	// Enemy Robots
	Map<String, RobotModel> enemy;
	
	public void run()
	{
		// "Constructor"
		this.setRotateFree();
		this.setColor(Color.ORANGE);
		enemy = new HashMap<String, RobotModel>();
		
		// Battle Properties
		room_width = this.getBattleFieldWidth();
		room_height = this.getBattleFieldHeight();
		
		// Robot Properties
		rm = new RobotModel(this);
		rm.update();
		
		setTurnRadarRightRadians(Math.PI*2);
		while(getRadarTurnRemainingRadians() > 0.005) {
			// While the robot is doing it's initial scan
			rm.update();
			execute();
		}
		
		while (true)
		{
			// default actions. Overwrite with better ones
			setTurnLeftRadians(0.0);
			setAhead(0.0);
			
			rm.update();
			
			
			
			/*
			setTurnRadarLeftRadians(1.0);
			setTurnRadarRightRadians(1.0);
			setTurnGunLeftRadians(1.0);
			setTurnGunRightRadians(1.0);
			setTurnLeftRadians(1.0);
			setTurnRightRadians(1.0);
			*/
			
			execute();
		}
	}
	
	/*
	 * Utilities
	 */
	
	public void setRotateFree()
	{
		this.setAdjustGunForRobotTurn(true);
		this.setAdjustRadarForGunTurn(true);
		this.setAdjustRadarForRobotTurn(true);
	}
	public void setColor(Color c)
	{
		// body, gun, radar, bullet, scan arc
		this.setColors(c, c, c, c, c);
	}
	
	/*
	 * Event Handlers
	 */
	public void onBattleEnded(BattleEndedEvent bee) {
		
	}
	public void onScannedRobot(ScannedRobotEvent sre) {
		if(oneVoneAssumption) {
			// keep tight radar
			double x = RobotModel.getX(sre, getHeadingRadians(), getX());
			double y = RobotModel.getY(sre, getHeadingRadians(), getY());
			x += Math.cos(sre.getHeadingRadians()) * sre.getVelocity();
			y += Math.sin(sre.getHeadingRadians()) * sre.getVelocity();
			double delta = Math.atan2(y-getY(), x-getX())-RobotModel.correct_angle(getRadarHeadingRadians());
			while(delta > 2*Math.PI) {
				delta -= Math.PI*2;
			}
			while(delta < -2*Math.PI) {
				delta += Math.PI*2;
			}
			if(delta > Math.PI) {
				delta = Math.PI * -2 + delta; 
			}
			if(delta < -1*Math.PI) {
				delta = Math.PI * 2 + delta;
			}
			setTurnRadarLeftRadians(delta);
			
		}
		//when my robot scans another robot
		if(enemy.containsKey(sre.getName())) {
			// if the robot is already tracked
			if (debug) {
				System.out.println("SCANNED ROBOT: "+sre.getName());
				System.out.println("sre heading: "+sre.getHeadingRadians());
				System.out.println("sre correced: "+RobotModel.correct_angle(sre.getHeadingRadians()));
			}
			enemy.get(sre.getName()).update(sre, getHeadingRadians(), getX(), getY());
		}
		else {
			if (debug) System.out.println("CREATED ROBOT MODEL: " + sre.getName());
			RobotModel new_scan = new RobotModel(sre, this);
			enemy.put(sre.getName(), new_scan);
		}
		//rm.update(sre, getHeadingRadians(), getX(), getY());
	}
	public void onBulletHit(BulletHitEvent bhe) {
		// when my bullet hits another robot
	}
	public void onBulletHitBullet(BulletHitBulletEvent bhbe) {
		// when my bullet hits another bullet
	}
	public void onBulletMissed(BulletMissedEvent bme) {
		// when my bullet hits the wall (misses)
	}
	public void onHitByBullet(HitByBulletEvent hbbe) {
		// when my robot is hit by another bullet
		rm.update(hbbe);
	}
	public void onHitRobot(HitRobotEvent hre) {
		// when my robot hits another robot
	}
	public void onHitWall(HitWallEvent hwe) {
		// when my robot hits the wall
	}
	
	/*
	 * Game Events
	 */
	public void onDeath(DeathEvent de) {
		// when my robot dies/loses
	}
	public void onWin(WinEvent we) {
		// when my robot wins
	}
	public void onRobotDeath(RobotDeathEvent rde) {
		// when another robot dies
	}
	public void onRoundEnded(RoundEndedEvent ree) {
		// called when a round ends
	}
	
	/*
	 * Visual Effects
	 */
	public void onPaint(Graphics2D g) {
		// when my robot is painted
		System.out.println("Print Robot Model");
		rm.onPaint(g);
		for (RobotModel e : enemy.values()) {
			e.onPaint(g);
		}
	}
	
	
}
