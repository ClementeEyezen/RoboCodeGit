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

    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        setAllColors(Color.ORANGE);

        setup();

        while(true) {
            pred.updatePrediction();
            dodge.updateDodge();

            // Drive in a circle at max velocity

            setAhead(10);
            setTurnLeftRadians(1);
            // setTurnGunRightRadians(6);

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
        // Where the other robot has been
        hist.onPaint(g);

        // Where the other robot will be
        pred.onPaint(g);

        // Where I want to be
        dodge.onPaint(g);
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

class Dodger {
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

    private AdvancedRobot self;
    private History h;

    public ArrayList<Double> goalX = new ArrayList<>();
    public ArrayList<Double> goalY = new ArrayList<>();
    public ArrayList<Double> goalT = new ArrayList<>();

    private HashMap<Integer, Integer> strikes = new HashMap<Integer, Integer>();
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

    public void updateDodge() {
        if (h.eTime.size() >= 2) {
            int lastIdx = h.eNRG.size() - 1;
            double energyDrop = h.eNRG.get(lastIdx) - h.eNRG.get(lastIdx - 1);
            if (energyDrop < 0.001) {
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
                // set up a plan here
                // TODO(buckbaskin): start here
            }
        }
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