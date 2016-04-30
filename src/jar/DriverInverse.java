package jar;

public class DriverInverse extends Inverse {

    Bot b;
    GunnerInverse gi;
    
    boolean setup;
    long last_update = 0;
    
    public DriverInverse(Bot b, GunnerInverse gi) {
        this.b = b;
        this.gi = gi;
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