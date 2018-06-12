package icky;

import robocode.*;
import robocode.util.Utils;
import java.awt.Color;
import java.awt.Graphics2D;
import static java.lang.System.out;

public class IAmAMess extends AdvancedRobot {
    private double pi = Math.PI;
    private double radarRate;
    private boolean scanned = false;

    public void run() {
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        setAllColors(Color.ORANGE);

        setup();

        while(true) {
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
    public void onPaint(Graphics2D g) {}
    public void onRobotDeath(RobotDeathEvent e) {}
    public void onRoundEnded(RoundEndedEvent e) {}
    public void onScannedRobot(ScannedRobotEvent e) {
        scanned = true;
        out.println("onScannedRobot()");
        double relative_bearing = -e.getBearingRadians();
        out.println("rel: "+relative_bearing);
        double current_r_bearing = getHeadingRadians() - getRadarHeadingRadians();
        out.println("cur: "+current_r_bearing);
        radarRate = Utils.normalRelativeAngle(relative_bearing - current_r_bearing);
        out.println("rate: "+ radarRate);

        if (0 > radarRate && radarRate > -0.001) {
            radarRate = -0.001;
        }
        if (0 < radarRate && radarRate < -0.001) {
            radarRate = 0.001;
        }
        out.println("Turn radar right " + radarRate);
        // fire(0.2);
    }
    public void onStatus(StatusEvent e) {

    }
    public void onWin(WinEvent e) {}


    private void setup() {
        radarRate = 6.0;
    }
    private void loop_reset() {
        scanned = false;
    }
}