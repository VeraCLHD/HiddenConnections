package bootstrapping;

import java.util.HashSet;
import java.util.Set;

import overall.Pair;

public abstract class Bootstrapper {
	// this set would be empty at the beginning
	private Set<String> patterns = new HashSet<String>();
	
	// the seed pairs: (caffeine, migrane pain), (wine, blood pressure)
	private Set<Pair<String>> seeds = new HashSet<Pair<String>>();
	private static final int numberOfIterations = 10;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public Set<String> getPatterns() {
		return patterns;
	}
	public void setPatterns(Set<String> patterns) {
		this.patterns = patterns;
	}
	public Set<Pair<String>> getSeeds() {
		return seeds;
	}
	public void setSeeds(Set<Pair<String>> seeds) {
		this.seeds = seeds;
	}
	public static int getNumberofiterations() {
		return numberOfIterations;
	}

}
