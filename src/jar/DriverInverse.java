package jar;

import java.util.List;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;

public class DriverInverse extends Inverse {

    @Override
    public void update(long nanos) {
        /*
         * Update the code based on the most recently available data for the remaining time
         */
        
    }

    @Override
    public Inverse.Prediction predict(long timeSteps) {
        // TODO Auto-generated method stub
        return null;
    }
    
    class Prediction extends Inverse.Prediction {

        @Override
        public double compare(jar.Inverse.Prediction other) {
            if (other instanceof DriverInverse.Prediction) {
                return 0.0;
            }
            return 0.0;
        }
        public Prediction(List<Pair> synth, long start_time, long end_time) {
            // create a prediction from synthesized data for the future
        }
        public Prediction(List<State> data, long start_time) {
            // create a prediction from data
            // use this to create a "Prediction" from actual data to compare to an existing prediction
        }
    }
}

class Pair {
    public double x, y;
    public Pair(int x, int y) {
        this.x = x;
        this.y = y;
    }
}