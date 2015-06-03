package ann;

import java.util.ArrayList;

public class NeuralNet {
	
	NLayer<InputNeuron> in;
	NLayer<OutputNeuron> out;
	NLayer<HiddenNeuron>[] hidden;
	
	public NeuralNet(int inputs, int outputs, int hidden_layers, int quantity_per_layer) {
		
		if(hidden_layers < 1 || quantity_per_layer < 1) {
			throw new IndexOutOfBoundsException();
		}
		ArrayList<InputNeuron> ins  = new ArrayList<InputNeuron>(inputs);
		for(int i = 0; i < inputs; i++) {
			ins.add(new InputNeuron(1.0));
		}
		in = new NLayer<InputNeuron>(ins);
		
		ArrayList<HiddenNeuron> h = new ArrayList<HiddenNeuron>(quantity_per_layer);
		for(int i = 0; i < quantity_per_layer; i++) {
			h.add(new HiddenNeuron(in));
		}
		hidden[0] = new NLayer<HiddenNeuron>(h);
		
		for(int j = 1 ; j < hidden_layers; j++) {
			h = new ArrayList<HiddenNeuron>(quantity_per_layer);
			for(int i = 0; i < quantity_per_layer; i++) {
				h.add(new HiddenNeuron(hidden[j-1]));
			}
			in = new NLayer<InputNeuron>(ins);
			
		}
		
		ArrayList<OutputNeuron> outs  = new ArrayList<OutputNeuron>(inputs);
		for(int i = 0; i < inputs; i++) {
			outs.add(new OutputNeuron(hidden[hidden.length-1]));
		}
		out = new NLayer<OutputNeuron>(outs);
	}
	
	public ArrayList<Double> f(ArrayList<Double> input) {
		in.set_input(input);
		for(int i =0 ; i < hidden.length; i++) {
			hidden[i].update_activation();
		}
		return out.calculate();
	}

}
