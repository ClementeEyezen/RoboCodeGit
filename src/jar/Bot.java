package jar;

import java.util.HashMap;

import robocode.AdvancedRobot;
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

public class Bot {
    // model all robots uniformly
    String name;
    HashMap<String, History> data;
    Driver d;
    Raddar r;
    Gunner g;

    public Bot(String name) {
        this.name = name;
        d = new Driver(this);
        r = new Raddar(this);
        g = new Gunner(this);
        data = new HashMap<String, History>();
    }

    public Bot(AdvancedRobot self) {
        // based on my robot
        this(self.getName());
    }

    public Bot(AdvancedRobot self, ScannedRobotEvent sre) {
        // other robot
        this(sre.getName());
    }

    /*
     * Get info out
     */
    public double lastX() {
        // TODO
        return 0.0;
    }
    public double lastY() {
        // TODO
        return 0.0;
    }
    public double lastHeading() {
        // TODO
        return 0.0;
    }


    /* 
     * Evented Update
     */
    
    // loop update, spend time calculating
    public void update(AdvancedRobot self) {
        // update self
        if (!self.getName().equals(name)) {
            // don't update
            return;
        }
    }

    // scan - my robot
    public void update(Vis self, ScannedRobotEvent sre) {
        /*
         * The bot's responsibility is for tracking everything that I've seen, so that I can reference it later. 
         * When I'm updating for a scanned robot event, I'm asking the bot to save information about the robot that I saw based on
         * where I was.
         * 
         * For a bot for another robot, this update might be less helpful, and instead I should just generate the data for where the
         *  other robot was. 
         */
        double robocode_heading = sre.getBearingRadians() + self.getHeadingRadians();
        double true_heading = robocode_heading;
        double distance = sre.getDistance();
        double start_x = self.getX();
        double start_y = self.getY();
        
        double other_x = start_x + distance*Math.cos(true_heading);
        double other_y = start_y + distance*Math.sin(true_heading);
        double other_robocode_heading = sre.getHeadingRadians();
        double other_true_heading = other_robocode_heading;
        double other_velocity = sre.getVelocity();
        double other_energy = sre.getEnergy();
        
        State other = new State(other_x, other_y, other_true_heading, other_velocity, other_energy);
        
        if (!data.containsKey(sre.getName())) {
            data.put(sre.getName(), new History());
        }
        this.data.get(sre.getName()).put(self.getTime(), other);
    }
    
    // scan - other robot
    public void update(ScannedRobotEvent synth, double x, double y, double heading) {
        // pass information to another robot's bot about myself aka create a fake scanned robot event and use that to update a bot
        double robocode_heading = synth.getBearingRadians() + heading;
        double true_heading = robocode_heading;
        double distance = synth.getDistance();
        double start_x = x;
        double start_y = y;
        
        double other_x = start_x + distance*Math.cos(true_heading);
        double other_y = start_y + distance*Math.sin(true_heading);
        double other_robocode_heading = synth.getHeadingRadians();
        double other_true_heading = other_robocode_heading;
        double other_velocity = synth.getVelocity();
        double other_energy = synth.getEnergy();
        
        State other = new State(other_x, other_y, other_true_heading, other_velocity, other_energy);
        
        if (!data.containsKey(synth.getName())) {
            data.put(synth.getName(), new History());
        }
        this.data.get(synth.getName()).put(synth.getTime(), other);
    }
    
    // hbbe (hit by bullet event)
    public void update(HitByBulletEvent hbbe) {
        // my update? this will only be called on a robot that recieved hbbe
        //  this will only be called on the bot that got hit
    }
    
    public void update(AdvancedRobot self, HitByBulletEvent synth) {
        // this is called on another bot
    }

    // bhe (bullet hit event)
    public void update(BulletHitEvent bhe) {
        // this is for updating my bot
    }
    
    public void update(Vis self, BulletHitEvent synth) {
        // This is the other bot event
    }

    // BulletHitBullet
    public void update(BulletHitBulletEvent bhbe) {
        // this is my bot's event
    }
    
    public void update(Vis self, BulletHitBulletEvent bhbe) {
        // this is the other bot's update event
    }

    // HitRobotEvent
    public void update(HitRobotEvent hre) {
        // my hit robot event
    }
    
    public void update(Vis self, HitRobotEvent hre) {
        // other hit robot event

    }

    public void update(BulletMissedEvent bme) {
        // when my bullet misses
    }
    
    public void update(Vis self, BulletMissedEvent bme) {
        // currently not simulating another robot missing events
    }

    public void update(HitWallEvent hwe) {
        // my bot hit wall update
    }
    
    public void update(Vis self, HitWallEvent hwe) {
        // currently not simulating another robot hitting the wall
    }

    // these are currently unused events
    public void update(DeathEvent de) {}

    public void update(WinEvent we) {}

    public void update(RobotDeathEvent rde) {}

    public void update(RoundEndedEvent ree) {}
}

class History {
    HashMap<Long, State> save;
    public History() {
        save = new HashMap<Long, State>();
    }
    public State get_by_time(int time) {
        if (save.containsKey(time)) {
            return save.get(time);
        }
        return null;
    }
    public void put(long time, State state) {
        save.put(time, state);
    }
}

class State {
    double x, y, heading, velocity, energy;
    public State(double x, double y, double heading, double velocity, double energy) {
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.energy = energy;
        this.velocity = velocity;
    }
}