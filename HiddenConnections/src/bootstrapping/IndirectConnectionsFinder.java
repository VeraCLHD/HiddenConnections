package bootstrapping;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.Reader;
import io.Writer;
import overall.Pair;

public class IndirectConnectionsFinder {
	private static Set<Pair<String>> allConnections = new HashSet<Pair<String>>();
	private static Set<Pair<String>> allConnectionsCopy = new HashSet<Pair<String>>(allConnections);
	private static String pathToInstances = "new_instances_ISA - Copy.txt";
	private static Set<Pair<String>> newlyEmerged = new HashSet<Pair<String>>();
	// a variable to check if there were newly emerged in the last method call (method for identification is recursive)
	private static int newlyEmergedCount = 0;
	
	public static void readAllConnections(){
		List<String> lines = Reader.readLinesList(pathToInstances);
		for(String line: lines){
			if(!line.isEmpty() && !line.equals(" ")){
				String[] splitted = line.split("\t");
				if(splitted.length == 2){
					Pair<String> isAPair = new Pair<String>(splitted[0], splitted[1]);
					IndirectConnectionsFinder.getAllConnections().add(isAPair);
				}
			}
	}
	}
	
	// unnecessary duplicates arise: x-z, z-y -> x-y, y-x (not necessary when IS-A)
	public static void traverseAndFindHidden(Collection<Pair<String>> collection){
		IndirectConnectionsFinder.setNewlyEmergedCount(0);
		for(Pair<String> pair: collection){
			for(Pair<String> secondPair: collection){
				if(!pair.equals(secondPair)){
					if(pair.second.equals(secondPair.first)){
						Pair<String> newPair = new Pair<String>(pair.first,secondPair.second);
						
						if(allConnectionsCopy.add(newPair) ){
							IndirectConnectionsFinder.newlyEmerged.add(newPair);
							IndirectConnectionsFinder.newlyEmergedCount +=1;
						}
						
					} else if(pair.first.equals(secondPair.second)){
						Pair<String> newPair = new Pair<String>(pair.second, secondPair.first);
						
						if(allConnectionsCopy.add(newPair)){
							IndirectConnectionsFinder.newlyEmerged.add(newPair);
							IndirectConnectionsFinder.newlyEmergedCount +=1;
						}
					}
				}
			}
		} 
		allConnections.addAll(allConnectionsCopy);
		if(IndirectConnectionsFinder.newlyEmergedCount > 0){
			traverseAndFindHidden(collection);
		}
		
	}
	
	/**
	 * A method to identify which relations are newly emerged.
	 */
	public static void filter(){
		newlyEmerged.remove(IndirectConnectionsFinder.getAllConnections());
		Set<Pair<String>> fin = newlyEmerged;
		for(Pair<String> pair: fin){
			Writer.appendLineToFile(pair.first + "\t" + pair.second, "evaluation/toEvaluate_ISA.txt");
		}
	}
	
	public static void main(String[] args) {
		IndirectConnectionsFinder.readAllConnections();
		IndirectConnectionsFinder.traverseAndFindHidden(allConnections);
		IndirectConnectionsFinder.filter();

	}
	public static Set<Pair<String>> getAllConnections() {
		return allConnections;
	}
	public static void setAllConnections(Set<Pair<String>> allConnections) {
		IndirectConnectionsFinder.allConnections = allConnections;
	}

	public static int getNewlyEmergedCount() {
		return newlyEmergedCount;
	}

	public static void setNewlyEmergedCount(int newlyEmergedCount) {
		IndirectConnectionsFinder.newlyEmergedCount = newlyEmergedCount;
	}

	public static Set<Pair<String>> getAllConnectionsCopy() {
		return allConnectionsCopy;
	}

	public static void setAllConnectionsCopy(Set<Pair<String>> allConnectionsCopy) {
		IndirectConnectionsFinder.allConnectionsCopy = allConnectionsCopy;
	}

}
