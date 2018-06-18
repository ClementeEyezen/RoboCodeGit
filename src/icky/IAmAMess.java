package icky;

import robocode.*;
import robocode.util.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.awt.Color;
import java.awt.Graphics2D;
import static java.lang.System.out;

public class IAmAMess extends AdvancedRobot {
    private double pi = Math.PI;
    private double radarRate;
    private boolean scanned = false;

    private History hist;
    private Predictor pred;
    private Dodger dodge;

    private double selectedX, selectedY;
    private double corrected_heading, myHeading;
    private double frontX, frontY, backX, backY;

    private long current_driving_time = 0;

    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        setAllColors(Color.ORANGE);

        setup();

        while(true) {
            out.println("--- "+getTime()+"---");
            pred.updatePrediction();
            dodge.updateDodge();

            drive(dodge);

            if (!scanned) {
                if (radarRate >= 0) {
                    radarRate = 5.0;
                } else {
                    radarRate = -5.0;
                }
            }
            setTurnRadarLeftRadians(radarRate);

            loop_reset();
            execute();
        }
    }

    public void onBattleEnded(BattleEndedEvent e) {}
    public void onBulletHit(BulletHitEvent e) {}
    public void onBulletHitBullet(BulletHitBulletEvent e) {}
    public void onBulletMissed(BulletMissedEvent e) {}
    public void onDeath(DeathEvent e) {}
    public void onHitByBullet(HitByBulletEvent e) {}
    public void onHitRobot(HitRobotEvent e) {}
    public void onHitWall(HitWallEvent e) {}
    public void onRobotDeath(RobotDeathEvent e) {}
    public void onRoundEnded(RoundEndedEvent e) {}
    public void onScannedRobot(ScannedRobotEvent e) {
        scanned = true;
        hist.onScannedRobot(e);

        double relative_bearing = -e.getBearingRadians();
        double current_r_bearing = getHeadingRadians() - getRadarHeadingRadians();
        radarRate = Utils.normalRelativeAngle(relative_bearing - current_r_bearing);

        if (0 > radarRate && radarRate > -0.001) {
            radarRate = -0.001;
        }
        if (0 < radarRate && radarRate < -0.001) {
            radarRate = 0.001;
        }

        // double angle = (getHeadingRadians() + e.getBearingRadians()) % (2 * pi);
        // scanX = (int)(getX() + Math.sin(angle) * e.getDistance());
        // scanY = (int)(getY() + Math.cos(angle) * e.getDistance());

    }
    public void onStatus(StatusEvent e) {

    }
    public void onWin(WinEvent e) {}


    public void onPaint(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        int x = 100;
        while (x < getBattleFieldWidth()) {
            g.drawLine(x, 0, x, (int)getBattleFieldHeight());
            x += 100;
        }
        int y = 100;
        while (y < getBattleFieldHeight()) {
            g.drawLine(0, y, (int)getBattleFieldWidth(), y);
            y += 100;
        }
        // Where the other robot has been
        hist.onPaint(g);

        // Where the other robot will be
        pred.onPaint(g);

        // Where I want to be
        dodge.onPaint(g);

        // Driver paint
        // Draw current heading
        g.setColor(Color.RED);
        myHeading = getHeadingRadians();
        double hX = getX() + 50 * Math.sin(myHeading);
        double hY = getY() + 50 * Math.cos(myHeading);
        g.drawLine((int)getX(), (int)getY(), (int)hX, (int)hY);

        // Draw selected point
        g.setColor(Color.PINK);
        if (selectedX != 0 && selectedY != 0) {
            out.println("paint sel x: "+(int)selectedX + " y: " + (int)selectedY);
        }
        g.fillRect((int)(selectedX-5), (int)(selectedY-5), 10, 10);
        // Draw desired heading
        g.setColor(Color.ORANGE);
        hX = getX() + 50 * Math.sin(corrected_heading);
        hY = getY() + 50 * Math.cos(corrected_heading);
        g.drawLine((int)getX(), (int)getY(), (int)hX, (int)hY);

        // Draw front and back points
        g.setColor(Color.GREEN);
        g.fillRect((int)(frontX-2), (int)(frontY-2), 4, 4);
        g.setColor(Color.YELLOW);
        g.fillRect((int)(backX-2), (int)(backY-2), 4, 4);
    }

    private void setup() {
        radarRate = 6.0;

        hist = new History(this);
        pred = new Predictor(this, hist);
        dodge = new Dodger(this, hist);
    }
    private void loop_reset() {
        scanned = false;
    }

    private void drive(Dodger d) {
        long cTime = getTime();
        if (d.plans.size() < 1) {
            // if there is no plan, drive in a circle
            setAhead(10);
            setTurnLeftRadians(1);
        } else {
            Plan p = d.plans.get(0);
            ArrayList<Integer> reachable_bins = new ArrayList<>();

            int max_hits = 1;

            for (int i = -p.max_avoid_bins; i <= p.max_avoid_bins; i++) {
                double maybeX = p.baseX + p.dodgeX * 5 * i;
                double maybeY = p.baseY + p.dodgeY * 5 * i;

                if (maybeX <= 20 || maybeY <= 20) {
                    continue;
                } else if (maybeX >= getBattleFieldWidth() - 20 ||
                    maybeY >= getBattleFieldHeight() - 20) {
                    continue;
                }

                double X = getX();
                double Y = getY();

                double dx = X - maybeX;
                double dy = Y - maybeY;

                double distance = Math.sqrt(dx*dx + dy*dy);
                long usable_time = p.goalTime - cTime;
                double usable_distance = usable_time * 8;
                if (distance - usable_distance < 20) {
                    // can reach that circle
                    reachable_bins.add(i);
                    max_hits = Math.max(max_hits, dodge.strikes.get(i));
                }
            }
            // Now I have a list of reachable bins, and through strikes, their
            //  hit rate

            boolean reselect_xy = true;
            // TODO(buckbaskin): don't recalculate until reached the desired goal, or won't reach existing plan
            if (reselect_xy) {
                HashMap<Integer, Integer> flipped_strikes = new HashMap<>();
                for (int i = 0; i < reachable_bins.size(); i++) {
                    int binId = reachable_bins.get(i);
                    int flipped_strike = max_hits + 1 - dodge.strikes.get(binId);
                    flipped_strikes.put(binId, flipped_strike);
                }
                long large_rando = 37 + 19 * (getTime() + 11);

                int reachIdx = 0;
                while (large_rando > 0) {
                    int binId = reachable_bins.get(reachIdx);
                    large_rando -= flipped_strikes.get(binId);
                    if (large_rando <= 0) {
                        // we have a winner! use this bin
                        break;
                    }

                    reachIdx += 1;
                    reachIdx = reachIdx % reachable_bins.size();
                }

                int selectedBin = reachable_bins.get(reachIdx);
                selectedX = p.baseX + p.dodgeX * 5 * selectedBin;
                selectedY = p.baseY + p.dodgeY * 5 * selectedBin;
                out.println("sel x: "+(int)selectedX + " y: " + (int)selectedY);
            }

            double myX = getX();
            double myY = getY();
            myHeading = getHeadingRadians();
            double myVel = getVelocity();
            double max_turn_deg = 10 - (0.75 * Math.abs(myVel));
            double max_turn_rad = max_turn_deg / 180 * pi;

            // double myMaxVel = 8;
            // double myMinVel = -8;
            // if (myVel > 0) {
            //     myMaxVel = Math.min(myMaxVel, myVel + 1);
            //     myMinVel = Math.max(myMinVel, myVel - 2);
            // } else if (myVel < 0){
            //     myMaxVel = Math.min(myMaxVel, myVel + 2);
            //     myMinVel = Math.max(myMinVel, myVel - 1);
            // } else {
            //     myMaxVel = 1;
            //     myMinVel = 1;
            // }

            // Now, determine if point is ahead or behind robot
            frontX = myX + 16 * Math.sin(myHeading);
            frontY = myY + 16 * Math.cos(myHeading);

            double dx = frontX - selectedX;
            double dy = frontY - selectedY;

            // TODO(buckbaskin): fix the front/back selection

            double frontDist = Math.sqrt(dx*dx + dy*dy);

            backX = myX - 16 * Math.sin(myHeading);
            backY = myY - 16 * Math.cos(myHeading);

            dx = backX - selectedX;
            dy = backY - selectedY;

            double backDist = Math.sqrt(dx*dx + dy*dy);

            out.println("front: " + frontDist + " vs. back: " + backDist);

            dx = myX - selectedX;
            dy = myY - selectedY;
            double desired_heading = Math.atan2(dy, dx); // In normal world coordinates, radians
            corrected_heading = -desired_heading + pi/2; // In robocode coordinates, radians
            if (frontDist - backDist < 0) {
                out.println("Point in front");
                corrected_heading += -pi;

                double heading_err = corrected_heading - myHeading;
                while (heading_err > pi) {
                    out.println("before "+heading_err);
                    heading_err = -2*pi + heading_err;
                    out.println("after "+heading_err);
                }
                while (heading_err < -pi) {
                    out.println("before "+heading_err);
                    heading_err = 2*pi + heading_err;
                    out.println("after "+heading_err);
                }

                double travel_left = Math.sqrt(dx*dx + dy*dy);
                setAhead(travel_left + 1);
                out.println("Turning right "+ (heading_err / pi * 180));
                setTurnRightRadians(heading_err);

            } else if (frontDist - backDist > 0) {
                out.println("Point in back");
                // TODO(buckbaskin): do the math on how to move backwards
                double heading_err = corrected_heading - myHeading;
                while (heading_err > pi) {
                    heading_err = -2*pi + heading_err;
                }
                while (heading_err < -pi) {
                    heading_err = 2*pi + heading_err;
                }

                double travel_left = Math.sqrt(dx*dx + dy*dy);
                setAhead(-(travel_left + 1));
                out.println("Turning right "+ (heading_err / pi * 180));
                setTurnRightRadians(heading_err);

            } else {
                setAhead(0);
                // TODO(buckbaskin): choose left or right turn based on sign
                out.println("I don't know what to do!!!");
            }
        }
    }
}

class History {
    private AdvancedRobot self;
    private double pi = Math.PI;

    public ArrayList<Long> eTime = new ArrayList<>();
    public ArrayList<Double> eX = new ArrayList<>();
    public ArrayList<Double> eY = new ArrayList<>();
    public ArrayList<Double> eNRG = new ArrayList<>();

    public History(AdvancedRobot parent) {
        this.self = parent;
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double angle = (self.getHeadingRadians() + e.getBearingRadians()) % (2 * pi);
        double scanX = (int)(self.getX() + Math.sin(angle) * e.getDistance());
        double scanY = (int)(self.getY() + Math.cos(angle) * e.getDistance());

        eTime.add(e.getTime());
        eX.add(scanX);
        eY.add(scanY);
        eNRG.add(e.getEnergy());
    }

    public void onPaint(Graphics2D g) {
        g.setColor(new Color((float)1.0, (float)0.0, (float)0.0, (float)0.2));
        // g.drawLine((int)scanX, (int)scanY, (int)getX(), (int)getY());
        
        int startIdx = Math.max(0, eTime.size() - 20);
        for (int i = startIdx; i < eTime.size(); i+=2) {
            double pastX = eX.get(i);
            double pastY = eY.get(i);
            g.fillRect((int)pastX - 20, (int)pastY - 20, 40, 40);
        }
    }
}

class Plan {
    public double baseX;
    public double baseY;
    public double dodgeX;
    public double dodgeY;

    public long goalTime;
    public int max_avoid_bins;

    public Plan(double bx, double by, double dx, double dy, long time, int bins) {
        baseX = bx;
        baseY = by;
        dodgeX = dx;
        dodgeY = dy;
        goalTime = time;
        max_avoid_bins = bins;
    }
}

class Dodger {
    private AdvancedRobot self;
    private History h;

    public ArrayList<Double> goalX = new ArrayList<>();
    public ArrayList<Double> goalY = new ArrayList<>();
    public ArrayList<Double> goalT = new ArrayList<>();

    public ArrayList<Plan> plans = new ArrayList<>();

    public HashMap<Integer, Integer> strikes = new HashMap<Integer, Integer>();
    private int total_strikes = 0;

    public Dodger(AdvancedRobot self, History h) {
        this.self = self;
        this.h = h;

        for (int i = -10; i <= 10; i++) {
            if (i == 0) {
                continue;
            }
            strikes.put(i, 1);
            total_strikes += 1;
        }
        strikes.put(0, 10);
        total_strikes += 10;
    }

    private void checkForNewPlan() {
        if (h.eTime.size() >= 2) {
            int lastIdx = h.eNRG.size() - 1;
            double ending_energy = h.eNRG.get(lastIdx);
            double starting_energy = h.eNRG.get(lastIdx - 1);
            double energyDrop = starting_energy - ending_energy;
            out.println("NRG: " + starting_energy + " - " + ending_energy);
            if (energyDrop > 0.001) {
                double velocity = bulletVel(energyDrop);

                double eX = h.eX.get(h.eX.size() - 1);
                double eY = h.eY.get(h.eY.size() - 1);

                double X = self.getX();
                double Y = self.getY();

                double fireX = X - eX;
                double fireY = Y - eY;

                double dodgeX = -fireY;
                double dodgeY = fireX;

                double norm = Math.sqrt(dodgeX*dodgeX + dodgeY*dodgeY);
                dodgeX = dodgeX / norm * 8;
                dodgeY = dodgeY / norm * 8;

                double distance = Math.sqrt(fireX*fireX + fireY*fireY);

                double time = distance/velocity;
                double max_avoid_dist = time * 8; // not entirely accurate, depends on robot vel

                int max_avoid_bins = (int)(Math.floor(max_avoid_dist / 40));
                if (max_avoid_bins < 1) {
                    // use some special edge case to turn diagonally away from the robot and make space
                }
                for (int i = -max_avoid_bins; i <= max_avoid_bins; i++) {
                    if (!strikes.containsKey(i)) {
                        strikes.put(i, 1);
                        total_strikes += 1;
                    }
                }
                // only do this when shots fired
                // set up a plan here
                plans.add(new Plan(
                    self.getX(), self.getY(), dodgeX, dodgeY,
                    self.getTime() + (long)time, max_avoid_bins
                    ));
                out.println("Added Plan to list for " +
                    (self.getTime() + (long)time) + " at time " + self.getTime());
            }
        }
    }

    private void filterPlans() {
        long cTime = self.getTime();
        // Filter out past times
        while (plans.size() > 0) {
            if (plans.get(0).goalTime <= cTime) {
                plans.remove(0);
            } else {
                break;
            }
        }
        // Filter out unreachable plans
        while (plans.size() > 0) {
            Plan p = plans.get(0);
            int reachable_bins = 0;
            for (int i = -p.max_avoid_bins; i <= p.max_avoid_bins; i++) {
                double maybeX = p.baseX + p.dodgeX * 5 * i;
                double maybeY = p.baseY + p.dodgeY * 5 * i;

                if (maybeX <= 20 || maybeY <= 20) {
                    continue;
                } else if (maybeX >= self.getBattleFieldWidth() - 20 ||
                    maybeY >= self.getBattleFieldHeight() - 20) {
                    continue;
                }

                double X = self.getX();
                double Y = self.getY();

                double dx = X - maybeX;
                double dy = Y - maybeY;

                double distance = Math.sqrt(dx*dx + dy*dy);
                long usable_time = p.goalTime - cTime;
                double usable_distance = usable_time * 8;
                if (distance - usable_distance < 0) {
                    // can reach that circle
                    reachable_bins += 1;
                    break;
                }
            }
            if (reachable_bins > 0) {
                break;
            } else {
                plans.remove(0);
            }
        }
    }

    public void updateDodge() {
        checkForNewPlan();
        filterPlans();
        // Driver takes the plan for the next point in time
        // for each avoid bin available at the time of shooting, check if its
        //  reachable now.
        // if it is, note its current value in the strikes map.
        // among reachable values, track the maximum
        // Then, pick an available value at random, with lower priority for the 
        //  pieces with higher strikes
        // Set that as the movement goal
    }

    public void onPaint(Graphics2D g) {
        // Draw bins
        if (h.eTime.size() > 1) {
            double eX = h.eX.get(h.eX.size() - 1);
            double eY = h.eY.get(h.eY.size() - 1);

            double X = self.getX();
            double Y = self.getY();

            double fireX = X - eX;
            double fireY = Y - eY;

            double dodgeX = -fireY;
            double dodgeY = fireX;

            double norm = Math.sqrt(dodgeX*dodgeX + dodgeY*dodgeY);
            dodgeX = dodgeX / norm * 8;
            dodgeY = dodgeY / norm * 8;

            double distance = Math.sqrt(fireX*fireX + fireY*fireY);
            double max_vel = bulletVel(0);
            double min_vel = bulletVel(3);
            double max_time = Math.floor(distance / min_vel);
            double min_time = Math.floor(distance / max_vel);

            for (int i = -(int)max_time; i <= (int)max_time; i++) {
                if ((i * 8) % 40 == 0) {
                    
                    double centX = X + i * dodgeX;
                    double centY = Y + i * dodgeY;
                    g.setColor(new Color((float)0.0, (float)1.0, (float)1.0, (float)0.5));
                    g.drawOval((int)(centX - 20), (int)(centY - 20), 40, 40);

                    if (!strikes.containsKey(i / 5)) {
                        strikes.put(i / 5, 1);
                        total_strikes += 1;
                    }
                    float local_strike = (float)(strikes.get(i/5));
                    float fill_percent = local_strike / total_strikes * 4;
                    if (fill_percent > 1.0) {
                        fill_percent = 1.0f;
                    }
                    g.setColor(new Color((float)0.0, (float)1.0, (float)1.0, fill_percent));
                    g.fillOval((int)(centX - 20), (int)(centY - 20), 40, 40);
                }
            }
        }
    }

    private double bulletVel(double power) {
        double bullet_vel = 20 - 3 * power;
        return bullet_vel;
    }

}

class Predictor {
    private AdvancedRobot self;
    private History h;

    public ArrayList<Double> pX = new ArrayList<>();
    public ArrayList<Double> pY = new ArrayList<>();
    public ArrayList<Long> pT = new ArrayList<>();

    public Predictor(AdvancedRobot self, History h) {
        this.self = self;
        this.h = h;
    }

    public void updatePrediction() {
        // predict 10 steps
        pT = new ArrayList<>();
        pX = new ArrayList<>();
        pY = new ArrayList<>();
        if (h.eTime.size() >= 2) {
            int end = h.eX.size();
            long endTime = h.eTime.get(end-1);
            long startTime = h.eTime.get(end-2);
            long dT = endTime - startTime;
            double endX = h.eX.get(end-1);
            double endY = h.eY.get(end-1);
            // print("Paint")
            // print("dT " + dT);
            // print("sx " + dT);
            // print("sy " + dT);
            // print("ex " + dT);
            // print("ey " + dT);
            double dX = (endX - h.eX.get(end-2)) / dT;
            double dY = (endY - h.eY.get(end-2)) / dT;
            for (int i = 0; i < 10; i++) {
                pT.add(endTime + i);
                pX.add(endX + dX * i);
                pY.add(endY + dY * i);
            }
        }
    }

    public void onPaint(Graphics2D g) {
        g.setColor(new Color((float)0.0, (float)1.0, (float)0.0, (float)0.1));
        for (int i = 0; i < pT.size(); i++) {
            double predX = pX.get(i);
            double predY = pY.get(i);
            g.drawRect((int)predX - 20, (int)predY - 20, 40, 40);
        }
    }
}