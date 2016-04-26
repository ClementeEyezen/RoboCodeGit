package jar;

public class GunnerInverse extends Inverse {

    Bot b;
    DriverInverse di;
    
    public GunnerInverse(Bot bot, DriverInverse di) {
        this.b = bot;
        this.di = di;
    }

    @Override
    public void update(long nanos) {
        // TODO Auto-generated method stub

    }

    @Override
    public Prediction predict(long timeSteps) {
        // TODO Auto-generated method stub
        return null;
    }

}
