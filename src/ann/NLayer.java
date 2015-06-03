package ann;

import java.util.ArrayList;

import com.sun.corba.se.impl.io.TypeMismatchException;
import com.sun.xml.internal.bind.v2.util.TypeCast;

public class NLayer<N extends Neuron> {
	ArrayList<N> neurons;
	
	public NLayer(ArrayList<N> neurons) {
		this.neurons = new ArrayList<N>();
		for(int i = 0; i < neurons.size(); i++) {
			this.neurons.add(neurons.get(i));
		}
		
	}
	
	public void update_activation() {
		for(N n : neurons) {
			((Neuron) n).update_activation();
		}
	}
	
	public ArrayList<N> elements() {
		return neurons;
	}

	public void set_input(ArrayList<Double> input) {
		try {
			for(int i = 0; i < input.size(); i++) {
				InputNeuron in = (InputNeuron) neurons.get(i);
				in.new_input(input.get(i));
			}
		}
		catch(ClassCastException cce) {
			throw(cce);
		}
	}
}
