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

    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        setAllColors(Color.ORANGE);

        setup();

        while(true) {
            pred.updatePrediction();

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
    }

    private void setup() {
        radarRate = 6.0;

        hist = new History(this);
        pred = new Predictor(this, hist);
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