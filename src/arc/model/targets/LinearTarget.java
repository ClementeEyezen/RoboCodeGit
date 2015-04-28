package arc.model.targets;

import arc.model.TimeCapsule;
import arc.model.target.TargettingProjection;
import arc.model.target.TargettingType;
import arc.model.target.Wave;

public class LinearTarget extends TargettingType {

	@Override
	public TargettingProjection project(TimeCapsule tc, Wave w) {
		
		double v = tc.get_last(0).velocity();
		double x = tc.get_last(0).x();
		double y = tc.get_last(0).y();
		double h = correct_angle(tc.get_last(0).heading());
		int dt = (int) (w.distance(x, y)/w.velocity());
		double dx = v*Math.cos(h);
		double dy = v*Math.sin(h);
		System.out.println("Linear Target: dx: "+dx+" dy: "+dy);
		double estimate = w.percent_escape(x, y, x+dx*dt, y+dy*dt);
		return new TargettingProjection(estimate, w, this);
	}

}
