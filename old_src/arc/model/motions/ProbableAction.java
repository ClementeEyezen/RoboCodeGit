package arc.model.motions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import arc.model.TimeCapsule;
import arc.model.motion.TransitionMotionType;

public class ProbableAction extends TransitionMotionType {
	
	Random random;
	
	TransitionMap transition_map;

	public ProbableAction(TimeCapsule data_source) {
		super(data_source);
		random = new Random();
		transition_map = new TransitionMap();
	}

	/* UPDATE */
	
	@Override
	public void update() {}
	@Override
	public void update(ScannedRobotEvent sre) {
		ArrayList<TimeCapsule.StateVector> datal = data.last(3);
		include_new_state(datal.get(0), datal.get(1), datal.get(2));
	}
	@Override
	public void update(AdvancedRobot ar) {
		ArrayList<TimeCapsule.StateVector> datal = data.last(3);
		include_new_state(datal.get(0), datal.get(1), datal.get(2));
	}
	private void include_new_state(TimeCapsule.StateVector s0, TimeCapsule.StateVector s1, TimeCapsule.StateVector s2) {
		// data from last state
		int v0 = (int) Math.floor(s1.velocity());
		int omega0 = (int) Math.floor(s1.heading() - s0.heading());
		// data from second to last state
		int v1 = (int) Math.floor(s2.velocity());
		int omega1 = (int) Math.floor(s2.heading() - s1.heading());
		
		Pair<Integer,Integer> state0 = new Pair<Integer, Integer>(v0,omega0);
		Pair<Integer,Integer> state1 = new Pair<Integer, Integer>(v1,omega1);
		
		transition_map.put(state0, state1);
	}

	/* TRANSITION VARIABLES */
	
	@Override
	public double velocity(TimeCapsule.StateVector s0, TimeCapsule.StateVector s1) {
		// calculate a new velocity based on the old state
		double r_val = random.nextDouble();
		return transition_map.find(s0, s1, r_val).t();
	}
	
	@Override
	public double heading(TimeCapsule.StateVector s0, TimeCapsule.StateVector s1) {
		// calculate a new heading based on the old state
		return transition_map.find(s0, s1, random.nextDouble()).u()+s1.heading();
	}
	
	// UTILITIES
	
	/* TEST Probable Action */

	private static boolean test() {
		return true;
	}
	
	public static void main(String[] args) {
		boolean t = test();
		if(t)
			System.out.println("Succesful test.");
		else
			System.out.println("Failed test.");
	}

	
	class Pair<T,U> {
		T t;
		U u;
		public Pair(T t_, U u_) {
			t = t_;
			u = u_;
		}
		public T t() {
			return t;
		}
		public U u() {
			return u;
		}
	}
	
	class TransitionMap {
		HashMap<Pair<Integer,Integer>, HashMap<Pair<Integer,Integer>, Double>> transition_map;
		double default_value;
		int start_value, range;
		
		public TransitionMap() {
			this(16, -8, 1.0/256.0);
		}
		public TransitionMap(int range, int start_value, double default_value) {
			transition_map = new HashMap<Pair<Integer,Integer>, HashMap<Pair<Integer,Integer>, Double>>();
			this.default_value = default_value;
			this.start_value = start_value;
			this.range = range;
		}
		
		public void put(Pair<Integer,Integer> state0, Pair<Integer,Integer> state1) {
			// if the initial state has already been seen at least once
			if(transition_map.containsKey(state0)) {
				// if the transitioned-to state has already been seen at least once
				if(transition_map.get(state0).containsKey(state1)) {
					transition_map.get(state0).put(state1, transition_map.get(state0).get(state1)+1.0);
				}
				// transition has never before been observed
				else {
					transition_map.get(state0).put(state1, 1.0);
				}
			}
			// initial+transition has never before been observed
			else {
				transition_map.put(state0, new HashMap<Pair<Integer,Integer>, Double>());
				transition_map.get(state0).put(state1, new Double(1));
			}
		}
		public Pair<Integer,Integer> find(TimeCapsule.StateVector s0, TimeCapsule.StateVector s1, double rand) {
			int v = (int) Math.floor(s1.velocity());
			int omega = (int) Math.floor(s1.heading()-s0.heading());
			Pair<Integer,Integer> state0 = new Pair<Integer,Integer>(v,omega);
			double total = get2D(state0);
			double rand_val = rand*total; // random value from 0 to total
			for(int i = start_value; i < range; i++) {
				for (int j = start_value; j < range; j++) {
					rand_val -= getOne(state0, new Pair<Integer,Integer>(i,j));
					if (rand_val <= 0.0) {
						return new Pair<Integer,Integer>(i,j);
					}
				}
			}
			return new Pair<Integer,Integer>(start_value+range/2,start_value+range/2);
		}
		
		public double getOne(Pair<Integer,Integer> state0, Pair<Integer,Integer> state1) {
			if(transition_map.containsKey(state0)) {
				// if the initial state has already been seen at least once
				if(transition_map.get(state0).containsKey(state1)) {
					// the transitioned-to state has already been seen at least once
					return transition_map.get(state0).get(state1);
				}
			}
			return default_value;
		}
		private double get1D(Pair<Integer,Integer> state0, Integer state1) {
			double accum = 0;
			for(int i = start_value; i < range; i++) {
				Pair<Integer, Integer> var = new Pair<Integer,Integer>(state1, i);
				accum += getOne(state0, var);
			}
			return accum;
		}
		private double get2D(Pair<Integer,Integer> state0) {
			double accum = 0;
			for(int i = start_value; i < range; i++) {
				accum += get1D(state0, i);
			}
			return accum;
		}
		private double get3D(Integer state0) {
			double accum = 0;
			for(int i = start_value; i < range; i++) {
				Pair<Integer, Integer> var = new Pair<Integer,Integer>(state0, i);
				accum += get2D(var);
			}
			return accum;
		}
		private double get4D() {
			double accum = 0;
			for(int i = start_value; i < range; i++) {
				accum += get3D(i);
			}
			return accum;
		}
		public double total() {
			return get4D();
		}
	}
}
