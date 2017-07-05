package distant_connections;

import java.util.ArrayList;
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
	private static String pathToInstances = "new_instances_ISA_5incomplete terms_only seed filtering.txt";
	private static Set<Pair<String>> newlyEmerged = new HashSet<Pair<String>>();
	// a variable to check if there were newly emerged in the last method call (method for identification is recursive)
	private static int newlyEmergedCount = 0;
	private static int run = 1;
	
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
		IndirectConnectionsFinder.run +=1; 
		List<Pair<String>> list1 = new ArrayList<Pair<String>>(collection);
		List<Pair<String>> list2 = new ArrayList<Pair<String>>(collection);
		
		for(int i= 0; i< list1.size(); i++){
			for(int j= i; j< list2.size(); j++){
				if(!list1.get(i).first.equals(list2.get(j).second)){
					if(list1.get(i).second.equals(list2.get(j).first)){
						Pair<String> newPair = new Pair<String>(list1.get(i).first,list2.get(j).second);
						// the relation should go into one consistent direction
						if(allConnectionsCopy.add(newPair)  && !newPair.first.equals(newPair.second)){
							IndirectConnectionsFinder.newlyEmerged.add(newPair);
							IndirectConnectionsFinder.newlyEmergedCount +=1;
						}
						
					} /*else if(list1.get(i).first.equals(list2.get(j).second)){
						Pair<String> newPair = new Pair<String>(list1.get(i).second, list2.get(j).first);
						
						if(allConnectionsCopy.add(newPair)){
							IndirectConnectionsFinder.newlyEmerged.add(newPair);
							IndirectConnectionsFinder.newlyEmergedCount +=1;
						}
					}*/
				}
			}
		} 
		allConnections.addAll(allConnectionsCopy);
		if( IndirectConnectionsFinder.run <= 2 && IndirectConnectionsFinder.newlyEmergedCount > 0){
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
		Writer.overwriteFile("", "evaluation/toEvaluate_ISA.txt");
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
