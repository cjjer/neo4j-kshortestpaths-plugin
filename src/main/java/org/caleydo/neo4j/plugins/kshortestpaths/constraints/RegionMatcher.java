package org.caleydo.neo4j.plugins.kshortestpaths.constraints;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;

import org.neo4j.graphdb.Path;

public class RegionMatcher implements ICompositePathContraint, ISequenceDependentConstraint {
	private final IPathConstraint c;
	private final MatchRegion region;

	public RegionMatcher(MatchRegion region, IPathConstraint c) {
		this.region = region;
		this.c = c;
	}
	
	public IPathConstraint getConstraint() {
		return c;
	}

	@Override
	public Iterable<IPathConstraint> children() {
		return Arrays.asList(c);
	}
	
	@Override
	public boolean accept(Path path) {
		return !matches(path).isEmpty();
	}

	@Override
	public SortedSet<MatchRegion> matches(Path path) {
		SortedSet<MatchRegion> matches = this.c.matches(path);
		MatchRegion r = this.region.toAbs(path.length());
		if (matches.contains(r)) {
			matches.clear();
			matches.add(r);
		} else {
			matches.clear();
		}
		return matches;
	}

	public boolean isStartRegion() {
		return this.region.isStart() && areAllConstraints();
	}
	public boolean isEndRegion() {
		return this.region.isEnd() && areAllConstraints();
	}

	private boolean areAllConstraints() {
		List<IPathConstraint> list = PathConstraints.flatten(c);
		for(IPathConstraint cc : list) {
			if (!(cc instanceof IConstraint)) {
				return false;
			}
		}
		return true;
	}
}