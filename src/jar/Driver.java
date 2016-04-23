package jar;

public class Driver extends Actor {
    Bot bot;
    public Driver(Bot b) {
        bot = b;
    }
    // these are used as predictors or output for current robot
    public double angular() {
        return 0.0;
    }
    public double linear() {
        return 0.0;
    }

    // this is used to update/try to predict the actions of another robot
    public void update() {
        // based on everything that the bot has done...
        // get ready to make a prediction about where it is going to move

        // also check past predictions to see how good they were and update
    }
}
