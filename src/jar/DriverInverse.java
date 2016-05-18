package jar;

import java.util.ArrayList;

public class DriverInverse extends Inverse {

    Bot b;
    GunnerInverse gi;
    
    boolean setup;
    long last_update = 0;
    
    KDTree kdt;
    
    ArrayList<KDNode> up_and_coming;
    
    public DriverInverse(Bot b, GunnerInverse gi) {
        this.b = b;
        this.gi = gi;
        
        up_and_coming = new ArrayList<KDNode>(); 
        
        setup = false;
    }
    
    @Override
    public void update(long nanos) {
        /*
         * Update the code based on the most recently available data for the remaining time
         */
        if (!setup) setup();
        // b.robotData;
        // b.bulletData;
        for (String name : b.robotData.keySet()) {
            if (name == b.name) {
                continue;
            }
            RobotState[] new_data = b.robotData.get(name).since(last_update);
            for (RobotState rs : new_data) {
                // push into KDNodes
                // append the KDNodes onto the tree
                KDNode next = new KDNode(rs);
                for (KDNode kdn : up_and_coming) {
                    kdn.push(rs);
                }
                
                up_and_coming.add(next);
                
                if (up_and_coming.size() >= 5) {
                    KD
                }
            }
        }
    }

    @Override
    public DriverPrediction predict(long timeSteps) {
        // TODO Auto-generated method stub
        return null;
    }
    
    private void setup() {
        setup = true;
    }
}