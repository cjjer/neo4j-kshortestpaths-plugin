package org.caleydo.neo4j.plugins.kshortestpaths.constraints;

import java.util.BitSet;
import java.util.List;
import java.util.SortedSet;

import org.neo4j.graphdb.Path;

public class CompositePathConstraint implements ICompositePathContraint {
	private final List<IPathConstraint> constraints;
	private final boolean isAnd;
	
	public CompositePathConstraint(boolean isAnd, List<IPathConstraint> constraints) {
		this.isAnd = isAnd;
		this.constraints = constraints;
	}
	
	@Override
	public Iterable<IPathConstraint> children() {
		return constraints;
	}
	
	@Override
	public boolean accept(Path path) {
		for(IPathConstraint c : constraints) {
			if (isAnd != c.accept(path)) {
				return !isAnd;
			}
		}
		return isAnd;
	}
	
	@Override
	public SortedSet<MatchRegion> matches(Path path) {
		BitSet total = new BitSet();
		if (this.isAnd) {
			//just the intersection of the region
			total.set(0, path.length());
			for(IPathConstraint p: constraints) {
				total.and(MatchRegion.toSet(p.matches(path), path.length()));
			}
		} else {
			//combine all regions to a large one
			for(IPathConstraint p: constraints) {
				for(MatchRegion r : p.matches(path)) {
					r.toBits(total, path.length());
				}
			}
		}
		return MatchRegion.from(total);
	}
}
