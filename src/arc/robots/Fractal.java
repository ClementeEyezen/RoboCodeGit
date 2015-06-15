package arc.robots;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import arc.model.RobotModel;
import arc.model.TimeCapsule;
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
			setTurnLeftRadians(driveStep().angular());
			setAhead(driveStep().linear());
			setTurnGunLeftRadians(gunStep());
			double radarStep = radarStep();
			System.out.println("radarStep "+radarStep);
			setTurnRadarLeftRadians(radarStep);
			setAhead(0.0);
			
			rm.update();
			
			execute();
		}
		/*
		setTurnGunLeftRadians(1.0);
		setTurnLeftRadians(1.0);
		*/
	}
	/*
	 * Step by Step updates
	 */
	
	public Twist driveStep() {
		return new Twist(0.0, 0.0);
	}
	
	public double gunStep() {
		return 0.0;
	}
	
	public double radarStep() {
		if(oneVoneAssumption) {
			if(enemy.keySet().size() > 0) {
				try {
					RobotModel e = enemy.values().iterator().next();
					TimeCapsule.StateVector last_observed = e.current_history().last().get(0);
					
					double target_x = last_observed.x();
					double target_y = last_observed.y();
					double dx = target_x - this.getX();
					double dy = target_y - this.getY();
					double target_angle = Math.atan2(dy, dx);
					double current_angle = RobotModel.correct_angle(this.getRadarHeadingRadians());
					double delta = RobotModel.minim(target_angle-current_angle);
					double min = 0.00001;
					if(delta >= 0.0 && delta < min) {
						delta = min;
					}
					if(delta <= 0.0 && delta > -1 * min) {
						delta = -1 * min;
					}
					return delta;
				}
				catch(IndexOutOfBoundsException ioobe){}
			}
		}
		return Math.PI*2;
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
			
		}
		//when my robot scans another robot
		if(enemy.containsKey(sre.getName())) {
			// if the robot is already tracked
			if (debug) {
				System.out.println("SCANNED ROBOT: "+sre.getName());
				System.out.println("sre heading: "+sre.getHeadingRadians());
				System.out.println("sre corrected: "+RobotModel.correct_angle(sre.getHeadingRadians()));
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
