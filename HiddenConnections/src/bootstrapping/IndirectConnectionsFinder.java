package bootstrapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.Reader;
import io.Writer;
import overall.Pair;

public class IndirectConnectionsFinder {
	private static Set<Pair<String>> allConnections = new HashSet<Pair<String>>();
	private static String pathToInstances = "new_instances_ISA - Copy.txt";
	private static Set<Pair<String>> newlyEmerged = new HashSet<Pair<String>>();
	
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
	
	public static void traverseAndFindHidden(){
		
		for(Pair<String> pair: IndirectConnectionsFinder.getAllConnections()){
			for(Pair<String> secondPair: IndirectConnectionsFinder.getAllConnections()){
				if(!pair.equals(secondPair)){
					if(pair.second.equals(secondPair.first)){
						Pair<String> newPair = new Pair<String>(pair.first,secondPair.second);
						IndirectConnectionsFinder.newlyEmerged.add(newPair);
					}
				}
			}
		}
		
		newlyEmerged.remove(IndirectConnectionsFinder.getAllConnections());
		Set<Pair<String>> fin = newlyEmerged;
		for(Pair<String> pair: fin){
			Writer.appendLineToFile(pair.first + "\t" + pair.second, "evaluation/toEvaluate_ISA.txt");
		}
	}
	public static void main(String[] args) {
		IndirectConnectionsFinder.readAllConnections();
		IndirectConnectionsFinder.traverseAndFindHidden();

	}
	public static Set<Pair<String>> getAllConnections() {
		return allConnections;
	}
	public static void setAllConnections(Set<Pair<String>> allConnections) {
		IndirectConnectionsFinder.allConnections = allConnections;
	}

}
