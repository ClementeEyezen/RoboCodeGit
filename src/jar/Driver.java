package jar;

public class Driver extends Actor {
    Bot bot;
    double angular, linear;
    public Driver(Bot b) {
        bot = b;
        angular = 0.0;
        linear = 0.0;
    }
    // these are used as predictors or output for current robot
    public double angular() {
        return angular;
    }
    public double linear() {
        return linear;
    }

    // this is used to update/try to predict the actions of another robot
    public void update(long nanos) {
        long start_nano = System.nanoTime();
        // based on everything that the bot has done...
        // get ready to make a prediction about where it is going to move

        // also check past predictions to see how good they were and update
        
        // the bot is given a fixed amount of time to update, and then it sets properties
        //  that are read by getters
        int x = 0;
        while (System.nanoTime() - start_nano < nanos && System.nanoTime() - start_nano > 0) {
            // do a calculation while the system still has time...
            x += 1; 
        }
        return;
    }
}
