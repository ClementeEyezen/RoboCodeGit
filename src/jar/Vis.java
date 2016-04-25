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
import robocode.SkippedTurnEvent;
import robocode.WinEvent;

/*
 * TODO every bot has an InverseDriver, InverseRaddar, InverseGunner that gets updated when I call bot.update...
 * TODO 	these are trying to predict what the bot is doing (including my own special case)
 * TODO then the main advanced robot (my jar.Vis) just runs its own Driver/Raddar/Gunner to actually generate stuff 
 */

// jar.Vis
public class Vis extends AdvancedRobot {
    boolean printEvents = true;
    // Battle Properties
    double room_width, room_height;
    double time;

    boolean debug = true;
    boolean fire_gun = false;

    boolean oneVoneAssumption = true;
    long millis_test = 1;
    
    long run_avg = 67;
    long min_skip = 36;

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

        bot = new Bot(this);
        d = new Driver(bot);
        g = new Gunner(bot);
        r = new Raddar(bot);
        
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

            long start_time = System.nanoTime();
            // System.out.println("Begin wait ("+millis_test+") @ "+start_time/1000000);
            while (System.nanoTime() - start_time < 1000000*millis_test && System.nanoTime() - start_time > 0) {
                // wait
            }
            // System.out.println("End wait ("+millis_test+") @ "+System.nanoTime()/1000000);
            millis_test += 1;
            
            execute();
        }
        /*
			setTurnGunLeftRadians(1.0);
			setTurnLeftRadians(1.0);
         */
    }
    /*
     * Debug printing options
     */
    private void printEvent(robocode.Event e) {
        System.out.println(""+System.nanoTime()+" "+e.getClass());
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
        if (printEvents) printEvent(bee);

    }

    public void onScannedRobot(ScannedRobotEvent sre) {
        if (printEvents) printEvent(sre);
        
        bot.update(this, sre);
        
        if (oneVoneAssumption) {
            // run a tight laser scan
            double current_radar = this.getRadarHeadingRadians();
            
            double goal_radar = 0.0;
            
            double radar_delta = goal_radar - current_radar;
            if (radar_delta > 0.0 && radar_delta < 0.1) {
                
            }
        }

        // simulate a scan data for that robot
        ScannedRobotEvent flipped = new ScannedRobotEvent(this.getName(), this.getEnergy(), sre.getBearingRadians()+Math.PI, 
                sre.getDistance(), this.getHeadingRadians(), this.getVelocity(), false);
        if (!bots.containsKey(sre.getName())) {
            bots.put(sre.getName(), new Bot(sre.getName()));
        }
        double robocode_heading = sre.getBearingRadians() + this.getHeadingRadians();
        double true_heading = robocode_heading;
        double other_x = this.getX() + sre.getDistance()*Math.cos(true_heading);
        double other_y = this.getY() + sre.getDistance()*Math.sin(true_heading);
        
        bots.get(sre.getName()).update(flipped, other_x, other_y, sre.getHeadingRadians());
    }

    public void onBulletHit(BulletHitEvent bhe) {
        if (printEvents) printEvent(bhe);
        
        // when my bullet hits another robot
        String otherName = bhe.getName();
//        double otherEnergy = bhe.getEnergy();
//        Bullet myBullet = bhe.getBullet();
//        double x = myBullet.getX();
//        double y = myBullet.getY();
//        double heading = myBullet.getHeadingRadians();
//        boolean active = myBullet.isActive();
        bot.update(bhe);

        HitByBulletEvent flipped = toHitByBullet(this, bhe);

        if (!bots.containsKey(otherName)) {
            // send synthesized data to the other Bot model
            bots.put(otherName, new Bot(otherName));
        }
        bots.get(otherName).update(this, flipped);
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
        if (printEvents) printEvent(bhbe);

//        Bullet myBullet = bhbe.getBullet();
        Bullet otherBullet = bhbe.getHitBullet();
        String otherName = otherBullet.getName();
//        double x = myBullet.getX();
//        double y = myBullet.getY();
//
//        double myHeading = myBullet.getHeadingRadians();
//        double otherHeading = otherBullet.getHeadingRadians();
//
//        double myVelocity = myBullet.getVelocity();
//        double otherVelocity = otherBullet.getVelocity();
        
        bot.update(bhbe);
        
        BulletHitBulletEvent flipped = flipBHBE(bhbe);
        if (!bots.containsKey(otherName)) {
            // simulate a bullet hit bullet event for another robot
            bots.put(otherName, new Bot(otherName));
        }
        bots.get(otherName).update(this, flipped);
    }
    private BulletHitBulletEvent flipBHBE(BulletHitBulletEvent bhbe) {
        return new BulletHitBulletEvent(bhbe.getHitBullet(), bhbe.getBullet());
    }

    public void onBulletMissed(BulletMissedEvent bme) {
        // when my bullet hits the wall (misses)
        if (printEvents) printEvent(bme);
        
        Bullet myBullet = bme.getBullet();
        double x = myBullet.getX();
        double y = myBullet.getY();
        double myHeading = myBullet.getHeadingRadians();
        double myVelocity = myBullet.getVelocity();
        
        bot.update(bme);
    }
    public void onHitByBullet(HitByBulletEvent hbbe) {
        // when my robot is hit by another bullet
        if (printEvents) printEvent(hbbe);
        
        bot.update(hbbe);
        String otherName = hbbe.getName();

        BulletHitEvent flipped = toBulletHitEvent(hbbe);
        if (!bots.containsKey(otherName)) {
            bots.put(otherName, new Bot(otherName));
        }
        bots.get(otherName).update(this, flipped);
    }

    private BulletHitEvent toBulletHitEvent(HitByBulletEvent hbbe) {
        return new BulletHitEvent(this.getName(), this.getEnergy(), hbbe.getBullet());
    }

    public void onHitRobot(HitRobotEvent hre) {
        // when my robot hits another robot
        if (printEvents) printEvent(hre);
        
        bot.update(hre);
        String otherName = hre.getName();

        HitRobotEvent flipped = flipHRE(hre);
        if (!bots.containsKey(otherName)) {
            bots.put(otherName, new Bot(otherName));
        }
        bots.get(otherName).update(this, flipped);
    }

    private HitRobotEvent flipHRE(HitRobotEvent hre) {
        return new HitRobotEvent(this.getName(), hre.getBearingRadians()+Math.PI, this.getEnergy(), !hre.isMyFault());
    }

    public void onHitWall(HitWallEvent hwe) {
        // when my robot hits the wall
        if (printEvents) printEvent(hwe);
        
        bot.update(hwe);
    }

    /*
     * Game Events
     */
    public void onSkippedTurn(SkippedTurnEvent ste) {
        if (printEvents) printEvent(ste);
        run_avg = (run_avg + millis_test)/2;
        if (millis_test < min_skip) {
            min_skip = millis_test;
        }
        System.out.println("millis_test: last: "+millis_test+" avg: "+run_avg+" min: "+min_skip);
        millis_test = millis_test/2;
    }
    public void onDeath(DeathEvent de) {
        // when my robot dies/loses
        if (printEvents) printEvent(de);
        
        bot.update(de);
    }
    public void onWin(WinEvent we) {
        // when my robot wins
        if (printEvents) printEvent(we);
        
        bot.update(we);
    }
    public void onRobotDeath(RobotDeathEvent rde) {
        // when another robot dies
        if (printEvents) printEvent(rde);
        
        bot.update(rde);
    }
    public void onRoundEnded(RoundEndedEvent ree) {
        // called when a round ends
        if (printEvents) printEvent(ree);
        
        bot.update(ree);
    }

    /*
     * Visual Effects
     */
    public void onPaint(Graphics2D g) {
        // when my robot is painted
        System.out.println("Print Robot Model");
    }
}
