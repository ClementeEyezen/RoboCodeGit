package cepl.stateSearch;

import cepl.Movement.MoveScript;

public class StateSearch {
	boolean hit_prune; //checks whether or not to prune based on first bullet hit
	// with no options, the state will be pruned for every wave passing over
	// with one or more options, the wave will be pruned with only the selected options
	boolean head_on;
	boolean linear;
	boolean curve_linear;
	boolean prior_hits;
	
	double precision;
	int max_depth;
	
	public StateSearch(int max_depth, boolean hit_prune, double precision)
	{
		this.max_depth = max_depth;
		this.hit_prune = hit_prune;
		this.precision = precision;
	}
	public MoveScript best_move_option(State current)
	{
		
		
		MoveScript toReturn = new MoveScript();
		//TODO
		return toReturn;
	}
}
