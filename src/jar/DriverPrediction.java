package jar;

import java.util.Arrays;

public class DriverPrediction extends Prediction {

    private int size = 5;
    private boolean setup = false;
    
    private double base_x, base_y, axis_x, axis_y; 
    
    RobotState[] dimensional;
    double[] nondim_x;
    double[] nondim_y;
    
    public DriverPrediction(RobotState first) {
        dimensional = new RobotState[size];
        nondim_x = new double[size];
        nondim_y = new double[size];
        dimensional[0] = first;
        nondim_x[0] = 0.0;
        nondim_y[0] = 0.0;
        
        base_x = dimensional[0].x;
        base_y = dimensional[0].y;
        axis_x = Math.cos(dimensional[0].heading);
        axis_y = Math.sin(dimensional[0].heading);
    }
    
    public static void main(String[] args) {
        RobotState first = new RobotState(100.0, 100.0, 0.0, 0.0, 0.0);
        DriverPrediction dp = new DriverPrediction(first);
        RobotState second = new RobotState(108.0, 100.0, 0.0, 0.0, 0.0);
        RobotState third = new RobotState(108.0, 108.0, 0.0, 0.0, 0.0);
        RobotState fourth = new RobotState(108.0, 100.0, 0.0, 0.0, 0.0);
        RobotState fifth = new RobotState(108.0, 92.0, 0.0, 0.0, 0.0);
        
        dp.push(second);
        dp.push(third);
        dp.push(fourth);
        dp.push(fifth);
        
        double[][] nondim = dp.get_nondimensional();
        double[] nondim_x = nondim[0];
        double[] nondim_y = nondim[1];
        
        System.out.println("nondims:\n x: "+Arrays.toString(nondim_x)+"\n y: "+Arrays.toString(nondim_y)+"");
    }
    
    public void push(RobotState next) {
        for (int ii = 1; ii < size; ii++){
            if (dimensional[ii] == null) {
                dimensional[ii] = next;
                _calculate_nondimensional(ii);
                break;
            }
        }
    }
    public boolean full() {
        return dimensional[size-1] != null;
    }
    
    public double[][] get_nondimensional() {
        double[][] result = new double[2][size];
        result[0] = nondim_x;
        result[1] = nondim_y;
        return result;
    }
    private void _calculate_nondimensional(int index) {
        if (index <= 0 || index >= size) {
            return;
        }
        
        double dx = dimensional[index].x - dimensional[0].x;
        double dy = dimensional[index].y - dimensional[0].y;
        
        if (Double.isNaN(dx) || Double.isNaN(dy)) {
            System.out.println(index+": dx/dy Nan");
            return;
        }
        if (Double.isNaN(axis_x) || Double.isNaN(axis_y)) {
            System.out.println(index+": axis_x/axis_y Nan");
            return;
        }
        
        double dot_product = dx*axis_x + dy*axis_y; // distance along x axis
        
        if (Double.isNaN(dot_product)) {
            System.out.println(index+": dot product is Nan");
            return;
        }
        
        double along_x = axis_x * dot_product;
        double along_y = axis_y * dot_product;
        
        if (Double.isNaN(along_x) || Double.isNaN(along_y)) {
            System.out.println(index+": along x/y is Nan");
            return;
        }
        
        double y_dimension = Math.sqrt(Math.pow(dx-along_x, 2)+Math.pow(dy-along_y, 2));
        
        if (Double.isNaN(y_dimension)) {
            System.out.println(index+": y_dimension is Nan");
            return;
        }
        
        nondim_x[index] = dot_product;
        nondim_y[index] = y_dimension;
    }
    
    
    class Pair {
        public double x, y;
        public Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}