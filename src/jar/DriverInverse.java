package jar;

public class DriverInverse extends Inverse {

    Bot b;
    GunnerInverse gi;
    
    public DriverInverse(Bot b, GunnerInverse gi) {
        this.b = b;
        this.gi = gi;
    }
    
    @Override
    public void update(long nanos) {
        /*
         * Update the code based on the most recently available data for the remaining time
         */
        
    }

    @Override
    public DriverPrediction predict(long timeSteps) {
        // TODO Auto-generated method stub
        return null;
    }
}