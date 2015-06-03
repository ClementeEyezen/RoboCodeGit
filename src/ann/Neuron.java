package ann;

import java.util.ArrayList;
import java.util.Random;

public abstract class Neuron {
	ArrayList<Neuron> inputs;
	ArrayList<Double> weights;
	double learning_rate = 0.2;
	
	double active_level = 0.0;
	
	public Neuron(ArrayList<Neuron> in_edges) {
		inputs.addAll(in_edges);
		weights = new ArrayList<Double>(in_edges.size()+1);
		random_initialize();
	}
	public Neuron(NLayer in_layer) {
		inputs.addAll(in_layer.elements());
		weights = new ArrayList<Double>(in_layer.elements().size()+1);
		random_initialize();
	}
	public void random_initialize() {
		Random r = new Random();
		for(int i = 0; i < weights.size(); i++) {
			weights.set(i, new Double(r.nextDouble()));
		}
	}
	public void update_activation() {
		double total_activation = 0.0;
		for(int i = 0; i < inputs.size(); i++) {
			total_activation += inputs.get(i).get_activation()*weights.get(i);
		}
		total_activation += 1.0*weights.get(weights.size()-1); // add in bias
		active_level = total_activation;
	}
	public double get_activation() {
		return active_level;
	}
	public void prop_error(double err) {
		// update error at this node, then prop back
		
		ArrayList<Double> new_weights = new ArrayList<Double>(weights.size());
		new_weights.addAll(weights);
		
		double total_activation = 0.0;
		for(int i = 0; i < inputs.size(); i++) {
			total_activation += inputs.get(i).get_activation()*weights.get(i);
		}
		for(int i = 0; i < inputs.size(); i++) {
			// propagate error back to previous nodes
			double active_frac = inputs.get(i).get_activation()*weights.get(i)/total_activation;
			inputs.get(i).prop_error(err*active_frac);
			
			// active frac is contribution to weight
			// ex. error = 1, active frac = .05, decrease weight by .05
			new_weights.set(i, new Double(weights.get(i)-learning_rate*err*active_frac));
		}
		weights = new_weights;
	}
}
