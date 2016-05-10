package jar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class KDTree {
    // make it a simple KDTree. Each node has a parent and children
    KDNode root;
    public KDTree(KDNode root) {
        this.root = root;
    }
    
    public static void main(String[] args) {
        KDNode[] nodes = new KDNode[30];
        RobotState[] observations = new RobotState[30];
        
        for (int ii = 0; ii < observations.length; ii++) {
            observations[ii] = new RobotState(ii*2.0+(Math.random() - 0.5), 0.0+(Math.random() - 0.5), 0.0, 2.0, 100.0);
            
            nodes[ii] = new KDNode(observations[ii]);
            
            for (int jj = ii-4; jj < ii; jj++) {
                if (jj < 0 || jj > nodes.length) {
                    continue;
                }
                nodes[jj].push(observations[ii]);
            }
        }
        KDTree kdt = new KDTree(nodes[0]);
        
        for (KDNode kdn : nodes) {
            kdt.add_node(kdn);
        }
        
        for (int ii = 0; ii < nodes.length; ii++) {
            KDNode kdn = kdt.find_nearest(nodes[ii]);
            System.out.println("kdn pair: ");
            System.out.println("old: ("+nodes[ii].nondim_x[1]+","+nodes[ii].nondim_y[1]+","+nodes[ii].nondim_theta[2]+") ");
            System.out.println("new: ("+kdn.nondim_x[1]+","+kdn.nondim_y[1]+","+kdn.nondim_theta[2]+")");
        }
    }
    
    public KDNode find_nearest(KDNode other) {
        return find_nearest(other, 1)[0];
    }
    
    public KDNode[] find_nearest(KDNode other, int num_candidates) {
        if (num_candidates < 0) {
            return new KDNode[1];
        }
        
        KDNode nearest = find_nearest_down(other);
        KDNode[] result = find_nearest_up(other, nearest, num_candidates);
        return result;
    }

    private KDNode find_nearest_down(KDNode other) {
        // TODO
        return root;
    }
    private KDNode[] find_nearest_up(KDNode other, KDNode start, int num_candidates) {
        // TODO
        return new KDNode[num_candidates];
    }

    public void add_node(KDNode kdn) {
        root.add_node(kdn);
    }
}

class KDNode {

    public int dimension = 0;
    
    private int size = 5;
    
    private double base_x, base_y, base_heading, axis_x, axis_y; 
    
    RobotState[] dimensional;
    double[] nondim_x;
    double[] nondim_y;
    double[] nondim_theta;
    
    KDNode parent, left, right;
    
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
    
    /*
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
    */
    
    public void add_node(KDNode kdn) {
        add_node(kdn, 0);
    }
    
    private void add_node(KDNode kdn, int depth) {
        double[] my_data, other_data;
        if (depth % 3 == 0) {
            my_data = nondim_x;
            other_data = kdn.nondim_x;
        } else if (depth % 3 == 1) {
            my_data = nondim_y;
            other_data = kdn.nondim_y;
        } else {
            my_data = nondim_theta;
            other_data = kdn.nondim_theta;
        }
        
        int index = depth / 3;
        
        if (other_data[index] >= my_data[index]) {
            // right
            if (right == null) {
                right = kdn;
                right.parent = this;
            } else {
                right.add_node(kdn, depth+1);
            }
        } else {
            // left
         // right
            if (left == null) {
                left = kdn;
                left.parent = this;
            } else {
                left.add_node(kdn, depth+1);
            }
        }
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
}
