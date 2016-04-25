package jar;

public abstract class Inverse {
    public abstract void update(long nanos);
    public abstract Prediction predict(long timeSteps);
    
    abstract class Prediction {
        public abstract double compare(Prediction other);
    }
}

