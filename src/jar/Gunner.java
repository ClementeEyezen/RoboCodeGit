package jar;

public class Gunner extends Actor {

    Bot me;
    
    public Gunner(Bot b) {
        me = b;
    }

    public double angular() {
        return 0.0;
    }

    @Override
    public void update(long nanos) {
        DriverPrediction fix = me.di.predict(10L);
    }

}
