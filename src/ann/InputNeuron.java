package ann;

import java.util.ArrayList;

public class InputNeuron extends Neuron {

	double val;
	
	public InputNeuron(double value) {
		super(new ArrayList<Neuron>());
		val = value;
	}
	public void new_input(double value) {
		val = value;
	}
	@Override
	public void update_activation() {
		return;
	}
	@Override
	public double get_activation() {
		return val;
	}
	@Override
	public void prop_error(double err) {
		return;
	}

}
