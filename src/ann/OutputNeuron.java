package ann;

import java.util.ArrayList;

public class OutputNeuron extends Neuron {

	public OutputNeuron(ArrayList<Neuron> in_edges) {
		super(in_edges);
	}
	public OutputNeuron(NLayer in_layer) {
		super(in_layer);
	}

}
