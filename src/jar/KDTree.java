package jar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class KDTree {
    boolean treed = false;
    List<KDNode> level;
    KDNode root;
    KDNode[] y;
    KDNode[] h;
    KDTree[] children;
    
    public KDTree() {
        level = new LinkedList<KDNode>();
    }
    
    public void add_node(KDNode kdn) {
        if (treed || level == null) {
            // add the kdn to a child
            add_to_child(kdn);
        } else {
            level.add(kdn);
            if (level.size() >= 6) {
                treeify();
            }
        }
    }
    private void add_to_child(KDNode kdn) {
        // add the node to one of the children of the tree
    }
    private void treeify() {
        treed = true;
        
        Collections.sort(level, new Comparator<KDNode>() {
            @Override
            public int compare(KDNode o1, KDNode o2) {
                return o1.compareTo(o2);
            }
        });
        root = level.get(3);
        
        for (KDNode kdn : level) {
            kdn.dimension++;
        }
        List<KDNode> lefts = level.subList(0, 2);
        List<KDNode> rights = level.subList(4, 6);
        
        Collections.sort(lefts, new Comparator<KDNode>() {
            @Override
            public int compare(KDNode o1, KDNode o2) {
                return o1.compareTo(o2);
            }
        });
        Collections.sort(rights, new Comparator<KDNode>() {
            @Override
            public int compare(KDNode o1, KDNode o2) {
                return o1.compareTo(o2);
            }
        });
        
        y[0] = lefts.get(1);
        y[1] = rights.get(1);
        
        h[0] = lefts.get(0);
        h[1] = lefts.get(2);
        h[2] = rights.get(0);
        h[3] = rights.get(2);
        
        if (level.size() > 7) {
            for (int ii = 7; ii < level.size(); ii++) {
                add_node(level.get(ii));
            }
        }
        level = null;
    }
}

class KDNode implements Comparable<KDNode> {

    public int dimension = 0;
    
    private int size = 5;
    
    private double base_x, base_y, base_heading, axis_x, axis_y; 
    
    RobotState[] dimensional;
    double[] nondim_x;
    double[] nondim_y;
    double[] nondim_theta;
    
    public KDNode(RobotState first) {
        dimensional = new RobotState[size];
        nondim_x = new double[size];
        nondim_y = new double[size];
        nondim_theta = new double[size];
        
        dimensional[0] = first;
        nondim_x[0] = 0.0;
        nondim_y[0] = 0.0;
        nondim_theta[0] = 0.0;
        
        base_x = dimensional[0].x;
        base_y = dimensional[0].y;
        base_heading = dimensional[0].heading;
        axis_x = Math.cos(dimensional[0].heading);
        axis_y = Math.sin(dimensional[0].heading);
    }
    
    public static void main(String[] args) {
        RobotState first = new RobotState(100.0, 100.0, Math.PI/2.0, 0.0, 0.0);
        KDNode dp = new KDNode(first);
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
        double[] nondim_h = nondim[2];
        
        System.out.println("nondims:\n x: "+Arrays.toString(nondim_x)+"\n y: "+Arrays.toString(nondim_y)+"\n h: "+Arrays.toString(nondim_h));
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
        double[][] result = new double[3][size];
        result[0] = nondim_x;
        result[1] = nondim_y;
        result[2] = nondim_theta;
        return result;
    }
    private void _calculate_nondimensional(int index) {
        if (index <= 0 || index >= size) {
            return;
        }
        
        double dx = dimensional[index].x - base_x;
        double dy = dimensional[index].y - base_y;
        
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
        
        double sign_of_y_dim = axis_x*(dy - along_y) - axis_y*(dx - along_x);
        
        if (Double.isNaN(y_dimension)) {
            System.out.println(index+": y_dimension is Nan");
            return;
        }
        
        nondim_x[index] = dot_product;
        if (sign_of_y_dim >= 0) {
            nondim_y[index] = y_dimension;
        } else {
            nondim_y[index] = -y_dimension;
        }
        nondim_theta[index] = dimensional[index].heading - base_heading;
    }
    
    
    class Pair {
        public double x, y;
        public Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    @Override
    public int compareTo(KDNode o) {
        if (dimension % 3 == 0) {
            if (nondim_x[dimension/3] == o.nondim_x[dimension/3]) {
                return 0;
            } else if (nondim_x[dimension/3] > o.nondim_x[dimension/3]) {
                return 1;
            } else {
                return -1;
            }
        } else if (dimension % 3 == 1) {
            if (nondim_y[dimension/3] == o.nondim_y[dimension/3]) {
                return 0;
            } else if (nondim_y[dimension/3] > o.nondim_y[dimension/3]) {
                return 1;
            } else {
                return -1;
            }
        } else {
            if (nondim_theta[dimension/3] == o.nondim_theta[dimension/3]) {
                return 0;
            } else if (nondim_theta[dimension/3] > o.nondim_theta[dimension/3]) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}