package icky;

import robocode.*;

public class IAmAMess extends AdvancedRobot {
    public void run() {
        while(true) {
            ahead(100);
            turnGunRight(360);
            back(100);
            turnGunRight(360);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        fire(0.2);
    }
}