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
    }
    
    public static void main(String[] args) {
        RobotState first = new RobotState(100.0, 100.0, 0.0, 0.0, 0.0);
        DriverPrediction dp = new DriverPrediction(first);
        RobotState second = new RobotState(100.0, 100.0, 0.0, 0.0, 0.0);
        RobotState third = new RobotState(100.0, 100.0, 0.0, 0.0, 0.0);
        RobotState fourth = new RobotState(100.0, 100.0, 0.0, 0.0, 0.0);
        RobotState fifth = new RobotState(100.0, 100.0, 0.0, 0.0, 0.0);
        
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
                if (ii == 1) {
                    _setup_nondimensional();
                }
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
        if (index <= 1 || index >= size) {
            return;
        }
        
        double dx = dimensional[index].x - dimensional[0].x;
        double dy = dimensional[index].y - dimensional[0].y;
        
        double dot_product = dx*axis_x + dy*axis_y; // distance along x axis
        
        double along_x = axis_x * dot_product;
        double along_y = axis_y * dot_product;
        
        double y_dimension = Math.sqrt(Math.pow(dx-along_x, 2)+Math.pow(dy-along_y, 2));
        
        nondim_x[index] = dot_product;
        nondim_y[index] = y_dimension;
    }
    
    private void _setup_nondimensional() {
        base_x = dimensional[0].x;
        base_y = dimensional[0].y;
        double next_x = dimensional[1].x;
        double next_y = dimensional[1].y;
        
        axis_x = next_x - base_x;
        axis_y = next_y - base_y;
        
        double distance = Math.sqrt(axis_x*axis_x+axis_y*axis_y);
        axis_x = axis_x / distance;
        axis_y = axis_y / distance;
        
        nondim_x[1] = distance;
        nondim_y[1] = 0.0;
        
        setup = true;
    }
    
    
    class Pair {
        public double x, y;
        public Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}