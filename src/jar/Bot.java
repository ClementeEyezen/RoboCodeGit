package jar;

import java.util.HashMap;

import robocode.AdvancedRobot;
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
 * What does a bot do?
 *  It collects updates, and saves everything in an efficient searchable store
 * 
 * What does an update do?
 *  First: It saves the data for the robot that it is representing.
 *      Ex. 1: I call a SRE. It saves the state of the other robot in the scan
 *      Ex. 2: I synth a SRE and pass it to Bot("bot 2"). It saves that it saw me at a given state. This may later be replaced by a
 *          RadarInverse generating a SRE. Right now it assumes every radar scan I see, the other robot sees
 *  Second: It calls update on the Driver
 */
public class Bot {
    /*
     * A Bot class encapsulates all of the information that a robot knows about itself and other robots
     */
    String name;
    AdvancedRobot reference;
    HashMap<String, History<RobotState>> robotData;
    HashMap<String, History<BulletState>> bulletData;
    DriverInverse di;
    // Raddar r;
    GunnerInverse gi;

    public Bot(String name) {
        this.name = name;
        di = new DriverInverse(this, gi);
        // r = new Raddar(this);
        gi = new GunnerInverse(this, di);
        robotData = new HashMap<String, History<RobotState>>();
        bulletData = new HashMap<String, History<BulletState>>();
    }

    public Bot(AdvancedRobot self) {
        // based on my robot
        this(self.getName());
        reference = self;
    }

    public Bot(AdvancedRobot self, ScannedRobotEvent sre) {
        // other robot
        this(sre.getName());
        reference = self;
    }
    
    public double flip_rotation(double other_heading) {
        double temp = -other_heading + Math.PI/2;
        while (temp > Math.PI * 2) {
            temp = temp - Math.PI * 2;
        }
        while (temp < -Math.PI * 2) {
            temp = temp + Math.PI * 2;
        }
        return temp;
    }


    /* 
     * Evented Update
     */
    
    public void update(AdvancedRobot self) {
        // update self
        RobotState interim = new RobotState(self.getX(), self.getY(), flip_rotation(self.getHeadingRadians()), self.getVelocity(),
                self.getEnergy());
        long time = self.getTime();
        if (!robotData.containsKey(self.getName())) {
            robotData.put(self.getName(), new History<RobotState>());
        }
        robotData.get(self.getName()).put(time, interim);
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
        double true_heading = flip_rotation(robocode_heading);
        double distance = sre.getDistance();
        double start_x = self.getX();
        double start_y = self.getY();
        
        double other_x = start_x + distance*Math.cos(true_heading);
        double other_y = start_y + distance*Math.sin(true_heading);
        double other_robocode_heading = sre.getHeadingRadians();
        double other_true_heading = flip_rotation(other_robocode_heading);
        double other_velocity = sre.getVelocity();
        double other_energy = sre.getEnergy();
        
        RobotState other = new RobotState(other_x, other_y, other_true_heading, other_velocity, other_energy);
        
        if (!robotData.containsKey(sre.getName())) {
            robotData.put(sre.getName(), new History<RobotState>());
        }
        this.robotData.get(sre.getName()).put(self.getTime(), other);
    }
    
    // scan - other robot
    public void update(ScannedRobotEvent synth, double x, double y, double heading) {
        // pass information to another robot's bot about myself aka create a fake scanned robot event and use that to update a bot
        double robocode_heading = synth.getBearingRadians() + heading;
        double true_heading = flip_rotation(robocode_heading);
        double distance = synth.getDistance();
        double start_x = x;
        double start_y = y;
        
        double other_x = start_x + distance*Math.cos(true_heading);
        double other_y = start_y + distance*Math.sin(true_heading);
        double other_robocode_heading = synth.getHeadingRadians();
        double other_true_heading = flip_rotation(other_robocode_heading);
        double other_velocity = synth.getVelocity();
        double other_energy = synth.getEnergy();
        
        RobotState other = new RobotState(other_x, other_y, other_true_heading, other_velocity, other_energy);
        
        if (!robotData.containsKey(synth.getName())) {
            robotData.put(synth.getName(), new History<RobotState>());
        }
        this.robotData.get(synth.getName()).put(synth.getTime(), other);
    }
    
    // hbbe (hit by bullet event)
    public void update(HitByBulletEvent hbbe) {
        // my update? this will only be called on a robot that recieved hbbe
        //  this will only be called on the bot that got hit
        Bullet b = hbbe.getBullet();
        BulletState newb = new BulletState(b.getX(), b.getY(), b.getHeading(), b.getVelocity(), b.getPower());
        if (!bulletData.containsKey(hbbe.getName())) {
            bulletData.put(hbbe.getName(), new History<BulletState>());
        }
        bulletData.get(hbbe.getName()).put(hbbe.getTime(), newb);
    }
    
    public void update(AdvancedRobot self, HitByBulletEvent synth) {
        // this is called on another robot's representative bot
        Bullet b = synth.getBullet();
        BulletState newb = new BulletState(b.getX(), b.getY(), b.getHeading(), b.getVelocity(), b.getPower());
        if (!bulletData.containsKey(synth.getName())) {
            bulletData.put(synth.getName(), new History<BulletState>());
        }
        bulletData.get(synth.getName()).put(synth.getTime(), newb);
    }

    // bhe (bullet hit event)
    public void update(BulletHitEvent bhe) {
        // this is for updating my bot
        Bullet b = bhe.getBullet();
        BulletState newb = new BulletState(b.getX(), b.getY(), b.getHeading(), b.getVelocity(), b.getPower());
        if (!bulletData.containsKey(b.getName())) {
            // this is my bullet we're talking about here
            bulletData.put(b.getName(), new History<BulletState>());
        }
        bulletData.get(b.getName()).put(bhe.getTime(), newb);
        
        String otherName = bhe.getName();
        if (!robotData.containsKey(otherName)) {
            robotData.put(otherName, new History<RobotState>());
        }
        if (!robotData.get(otherName).containsKey(reference.getTime())) {
            // if there is not already data for the robot at this time
            double x = b.getX();
            double y = b.getY();
            double energy = bhe.getEnergy();
            RobotState rs = new RobotState(x, y, 0.0, 0.0, energy);
            rs.setBullet();
            robotData.get(otherName).put(reference.getTime(), rs);
        }
    }
    
    public void update(Vis self, BulletHitEvent synth) {
        // This is the other bot event
        Bullet b = synth.getBullet();
        BulletState newb = new BulletState(b.getX(), b.getY(), b.getHeading(), b.getVelocity(), b.getPower());
        // I need to get the other robot's name
        if (!bulletData.containsKey(b.getName())) {
            // this is my bullet we're talking about here
            bulletData.put(b.getName(), new History<BulletState>());
        }
        bulletData.get(b.getName()).put(synth.getTime(), newb);
        
        String otherName = synth.getName();
        if (!robotData.containsKey(otherName)) {
            robotData.put(otherName, new History<RobotState>());
        }
        if (!robotData.get(otherName).containsKey(reference.getTime())) {
            // if there is not already data for the robot at this time
            double x = b.getX();
            double y = b.getY();
            double energy = synth.getEnergy();
            RobotState rs = new RobotState(x, y, 0.0, 0.0, energy);
            rs.setBullet();
            robotData.get(otherName).put(reference.getTime(), rs);
        }
    }

    // BulletHitBullet
    public void update(BulletHitBulletEvent bhbe) {
        // this is my bot's event
        Bullet myB = bhbe.getBullet();
        BulletState myNewb = new BulletState(myB.getX(), myB.getY(), myB.getHeading(), myB.getVelocity(), myB.getPower());
        Bullet otherB = bhbe.getHitBullet();
        BulletState otherNewb = new BulletState(otherB.getX(), otherB.getY(), otherB.getHeading(), 
                otherB.getVelocity(), otherB.getPower());
        if (!bulletData.containsKey(myB.getName())) {
            bulletData.put(myB.getName(), new History<BulletState>());
        }
        bulletData.get(myB.getName()).put(bhbe.getTime(), myNewb);
        if (!bulletData.containsKey(otherB.getName())) {
            bulletData.put(otherB.getName(), new History<BulletState>());
        }
        bulletData.get(otherB.getName()).put(bhbe.getTime(), otherNewb);
    }
    
    public void update(Vis self, BulletHitBulletEvent bhbe) {
        // this is the other bot's update event
        Bullet myB = bhbe.getHitBullet();
        Bullet otherB = bhbe.getBullet();
        BulletState myNewb = new BulletState(myB.getX(), myB.getY(), myB.getHeading(), myB.getVelocity(), myB.getPower());
        BulletState otherNewb = new BulletState(otherB.getX(), otherB.getY(), otherB.getHeading(), 
                otherB.getVelocity(), otherB.getPower());
        if (!bulletData.containsKey(myB.getName())) {
            bulletData.put(myB.getName(), new History<BulletState>());
        }
        bulletData.get(myB.getName()).put(bhbe.getTime(), myNewb);
        if (!bulletData.containsKey(otherB.getName())) {
            bulletData.put(otherB.getName(), new History<BulletState>());
        }
        bulletData.get(otherB.getName()).put(bhbe.getTime(), otherNewb);
    }

    // HitRobotEvent
    public void update(HitRobotEvent hre) {
        // my hit robot event
        String otherName = hre.getName();
        if (!robotData.containsKey(otherName)) {
            robotData.put(otherName, new History<RobotState>());
        }
        double dist = 36;
        double robocode_bearing = hre.getBearingRadians();
        double true_bearing = flip_rotation(robocode_bearing);
        double x = reference.getX() + dist*Math.cos(true_bearing);
        double y = reference.getY() + dist*Math.sin(true_bearing);
        double energy = hre.getEnergy();
        
        double heading = robocode_bearing + Math.PI;
        
        RobotState rs = new RobotState(x, y, heading, 0.0, energy);
        rs.setHit();
        robotData.get(otherName).put(hre.getTime(), rs);
    }
    
    public void update(Vis self, HitRobotEvent hre) {
        // other hit robot event
        String otherName = hre.getName();
        if (!robotData.containsKey(otherName)) {
            robotData.put(otherName, new History<RobotState>());
        }

        RobotState rs = new RobotState(self.getX(), self.getY(), self.getHeadingRadians(), self.getVelocity(), self.getEnergy());
        rs.setHit();
        robotData.get(otherName).put(hre.getTime(), rs);
    }

    public void update(BulletMissedEvent bme) {
        // when my bullet misses
        // TODO
    }
    
    public void update(Vis self, BulletMissedEvent bme) {
        // currently not simulating another robot missing events
        // TODO
    }

    public void update(HitWallEvent hwe) {
        // my bot hit wall update
        // TODO
    }
    
    public void update(Vis self, HitWallEvent hwe) {
        // currently not simulating another robot hitting the wall
        // TODO
    }

    // these are currently unused events
    public void update(DeathEvent de) {}

    public void update(WinEvent we) {}

    public void update(RobotDeathEvent rde) {}

    public void update(RoundEndedEvent ree) {}
}

class History<S extends State> {
    HashMap<Long, S> save;
    
    long latest_data;
    
    public History() {
        save = new HashMap<Long, S>();
    }
    public S get_by_time(long time) {
        if (save.containsKey(time)) {
            return save.get(time);
        }
        return null;
    }
    public void put(long time, S state) {
        save.put(time, state);
        if (time > latest_data) {
            latest_data = time;
        }
    }
    public boolean containsKey(long time) {
        return save.containsKey(time);
    }
    
    public S last() {
        return get_by_time(latest_data);
    }
}
class State {
    double x, y, heading, velocity, energy;
}
class RobotState extends State {
    private boolean robotHitRobot;
    private boolean bulletInference;
    
    public RobotState(double x, double y, double heading, double velocity, double energy) {
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.energy = energy;
        this.velocity = velocity;
        robotHitRobot = false;
        bulletInference = false;
    }
    public void setHit() {
        robotHitRobot = true;
    }
    public void setBullet() {
        bulletInference = true;
    }
    public boolean collision() {
        return robotHitRobot;
    }
    public boolean bullet() {
        return bulletInference;
    }
}
class BulletState extends State {
    public BulletState(double x, double y, double heading, double velocity, double energy) {
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.energy = energy;
        this.velocity = velocity;
    }
    
    public BulletState atTime(long dt) {
        // TODO(buckbaskin): implement this
        // at time 0, return a clone of this
        // at time -1, return one timestep back, etc.
        return this;
    }
}