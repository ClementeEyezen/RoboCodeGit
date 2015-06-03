package ann;

import java.util.ArrayList;

public class HiddenNeuron extends Neuron{

	public HiddenNeuron(ArrayList<Neuron> in_edges) {
		super(in_edges);
	}
	public HiddenNeuron(NLayer in_layer) {
		super(in_layer);
	}

}
