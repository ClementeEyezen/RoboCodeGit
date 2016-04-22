package jar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

import robocode.AdvancedRobot;
import robocode.BattleEndedEvent;
import robocode.Bullet;
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

/*
 * TODO reorganize methods (Vis, Bot)
 * TODO check and see if I've implemented all the reverse data events
 * TODO check and see if I have most/all of the events covered
 * TODO every bot has an InverseDriver, InverseRaddar, InverseGunner that gets updated when I call bot.update...
 * TODO 	these are trying to predict what the bot is doing (including my own special case)
 * TODO then the main advanced robot (my jar.Vis) just runs its own Driver/Raddar/Gunner to actually generate stuff 
 */

// jar.Vis
public class Vis extends AdvancedRobot{
	// Battle Properties
		double room_width, room_height;
		double time;
		
		boolean debug = true;
		boolean fire_gun = false;
		
		boolean oneVoneAssumption = true;
		
		// Robot Properties
		Bot bot;
		Driver d;
		Gunner g;
		Raddar r;
		
		// Enemy Robots
		Map<String, Bot> bots;
		
		public void run()
		{
			// "Constructor"
			this.setRotateFree();
			this.setColor(Color.ORANGE);
			bots = new HashMap<String, Bot>();
			
			// Battle Properties
			room_width = this.getBattleFieldWidth();
			room_height = this.getBattleFieldHeight();
			
			setTurnRadarRightRadians(Math.PI*2);
			while(getRadarTurnRemainingRadians() > 0.005) {
				// While the robot is doing it's initial scan
				bot.update(this);
				execute();
			}
			
			while (true)
			{
				// default actions. Overwrite with better ones
				setTurnLeftRadians(d.angular());
				setAhead(d.linear());
				
				setTurnGunLeftRadians(g.angular());
				
				setTurnRadarLeftRadians(r.angular());
				if(r.fire()) {
					System.out.println("Trying to fire gun...");
					this.fire(1.0);
				}
				
				bot.update(this);
				
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
		
		public double distance(double x, double y) {
			return Math.sqrt(x*x-y*y);
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
			if(bots.containsKey(sre.getName())) {
				// if the robot is already tracked
				if (debug) {
					System.out.println("SCANNED ROBOT: "+sre.getName());
					System.out.println("sre heading: "+sre.getHeadingRadians());
				}
				bots.get(sre.getName()).update(this, sre);
			}
			else {
				if (debug) System.out.println("CREATED ROBOT MODEL: " + sre.getName());
				Bot new_scan = new Bot(this, sre);
				bots.put(sre.getName(), new_scan);
			}
			//rm.update(sre, getHeadingRadians(), getX(), getY());
		}
		public void onBulletHit(BulletHitEvent bhe) {
			// when my bullet hits another robot
			String otherName = bhe.getName();
			double otherEnergy = bhe.getEnergy();
			Bullet myBullet = bhe.getBullet();
			double x = myBullet.getX();
			double y = myBullet.getY();
			double heading = myBullet.getHeadingRadians();
			boolean active = myBullet.isActive();
			bot.update(this, bhe);
			
			HitByBulletEvent other = toHitByBullet(this, bhe);
			
			if (bots.containsKey(otherName)) {
				// send synthesized data to the other Bot model
				bots.get(otherName).update(other);
			}
		}
		private HitByBulletEvent toHitByBullet(Vis self, BulletHitEvent bhe) {
			double otherX = self.bots.get(bhe.getName()).lastX();
			double otherY = self.bots.get(bhe.getName()).lastY();
			double otherHeading = self.bots.get(bhe.getName()).lastHeading();
			double bulletX = bhe.getBullet().getX();
			double bulletY = bhe.getBullet().getY();
			
			double bearing = Math.atan2(bulletY-otherY, bulletX-otherX) - otherHeading;
			return new HitByBulletEvent(bearing, bhe.getBullet());
		}

		public void onBulletHitBullet(BulletHitBulletEvent bhbe) {
			// when my bullet hits another bullet
			Bullet myBullet = bhbe.getBullet();
			Bullet otherBullet = bhbe.getHitBullet();
			String otherName = otherBullet.getName();
			double x = myBullet.getX();
			double y = myBullet.getY();
			
			double myHeading = myBullet.getHeadingRadians();
			double otherHeading = otherBullet.getHeadingRadians();
			
			double myVelocity = myBullet.getVelocity();
			double otherVelocity = otherBullet.getVelocity();
			bot.update(this, bhbe);
			BulletHitBulletEvent flipped = flipBHBE(bhbe);
			if (bots.containsKey(otherName)) {
				// simulate a bullet hit bullet event for another robot
				bots.get(otherName).update(this, flipped);
			}
		}
		private BulletHitBulletEvent flipBHBE(BulletHitBulletEvent bhbe) {
			return new BulletHitBulletEvent(bhbe.getHitBullet(), bhbe.getBullet());
		}

		public void onBulletMissed(BulletMissedEvent bme) {
			// when my bullet hits the wall (misses)
			Bullet myBullet = bme.getBullet();
			double x = myBullet.getX();
			double y = myBullet.getY();
			double myHeading = myBullet.getHeadingRadians();
			double myVelocity = myBullet.getVelocity();
		}
		public void onHitByBullet(HitByBulletEvent hbbe) {
			// when my robot is hit by another bullet
			bot.update(this, hbbe);
			String otherName = hbbe.getName();
			
			BulletHitEvent flipped = toBulletHitEvent(hbbe);
			if (bots.containsKey(otherName)) {
				bots.get(otherName).update(this, flipped);
			}
		}
		
		private BulletHitEvent toBulletHitEvent(HitByBulletEvent hbbe) {
			return new BulletHitEvent(this.getName(), this.getEnergy(), hbbe.getBullet());
		}

		public void onHitRobot(HitRobotEvent hre) {
			// when my robot hits another robot
			bot.update(this, hre);
			String otherName = hre.getName();
			
			HitRobotEvent flipped = flipHRE(hre);
			if (bots.containsKey(otherName)) {
				bots.get(otherName).update(this, hre);
			}
		}
		
		private HitRobotEvent flipHRE(HitRobotEvent hre) {
			return new HitRobotEvent(this.getName(), hre.getBearingRadians()+Math.PI, this.getEnergy(), !hre.isMyFault());
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
		}
}
