package arc;

import java.awt.Color;
import java.awt.Graphics2D;

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
	
	// Robot Properties
	double height, width;
	double energy;
	double gun_cooling_rate, gun_heading, gun_heat;
	double radar_heading;
	double heading, velocity;
	double x, y;
	String name;
	
	
	public void run()
	{
		// "Constructor"
		this.setRotateFree();
		this.setColor(Color.BLUE);
		
		// Battle Properties
		room_width = this.getBattleFieldWidth();
		room_height = this.getBattleFieldHeight();
		
		// Robot Properties
		energy = this.getEnergy();
		gun_cooling_rate = this.getGunCoolingRate();
		
		
		while (true)
		{
			setTurnRadarLeftRadians(1.0);
			setTurnRadarRightRadians(1.0);
			setTurnGunLeftRadians(1.0);
			setTurnGunRightRadians(1.0);
			setTurnLeftRadians(1.0);
			setTurnRightRadians(1.0);
			
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
	public void onScannedRobot(ScannedRobotEvent sre)
	{
		//when my robot scans another robot
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
	}
	
	
}
