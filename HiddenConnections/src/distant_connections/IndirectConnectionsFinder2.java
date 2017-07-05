package distant_connections;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.event.ListSelectionEvent;

import io.Editor;
import io.Reader;
import io.Writer;
import overall.Pair;

public class IndirectConnectionsFinder2 {
	private static Set<Pair<String>> allConnections = new HashSet<Pair<String>>();
	private static Set<Pair<String>> allConnectionsCopy = new HashSet<Pair<String>>(allConnections);
	private static String pathToInstances = "new_instances_ISA_5incomplete terms_only seed filtering.txt";
	private static String concatenated = "CONCATENATED/";
	private static Set<Pair<String>> newlyEmerged = new HashSet<Pair<String>>();
	// a variable to check if there were newly emerged in the last method call (method for identification is recursive)
	private static int newlyEmergedCount = 0;
	private static int run = 1;
	
	/**
	 * A method that switches the direction of some results (IS-A, HYPERNYMY have to be in the same direction).
	 * Type 1 is the leading: how to rewrite the other one.
	 */
	public static void prepareInstances(String type1, String type2){
		String filename_type2 = "SEEDS/" + type2 +"/all_instances_and_patterns_" + type2 + ".txt";
		String filename_type1 = "SEEDS/" + type1 +"/all_instances_and_patterns_" + type1 + ".txt";
		String filename_type2_inverted = "SEEDS/" + type2 +"/all_instances_and_patterns_" + type2 + "_inverted.txt";
		Writer.overwriteFile("", filename_type2_inverted);
		
		List<String> lines2 = Reader.readLinesList(filename_type2);
		for(String line: lines2){
			if(!line.isEmpty()){
				String newLine = "";
				String[] splitted = line.split("\t");
				String first = splitted[0];
				String second = splitted[1];
				String third = splitted[2];
				String forth = splitted[3];
				// S stands for "switched"
				newLine += second + "\t" + first + "\t" + third + "\t" + type1 + "-S";
				Writer.appendLineToFile(newLine, filename_type2_inverted);
			}
		}
		String[] filenames = {filename_type1,filename_type2_inverted};
		Writer.concatenateFiles(filenames, "SEEDS/CONCATENATED/" + type1 + "_final.txt");
	}
	
	/**
	 * Concatenates all the switched files from all relations into one.
	 * @param dir
	 */
	public static void concatenateFinalFiles(String[] files){
		Writer.concatenateFiles(files, "SEEDS/" + "all_relations_final.txt");
		
	}
	
	
	public static void readAllConnections(){
		List<String> lines = Reader.readLinesList(pathToInstances);
		for(String line: lines){
			if(!line.isEmpty() && !line.equals(" ")){
				String[] splitted = line.split("\t");
				if(splitted.length == 2){
					Pair<String> isAPair = new Pair<String>(splitted[0], splitted[1]);
					IndirectConnectionsFinder2.getAllConnections().add(isAPair);
				}
			}
	}
	}
	
	// unnecessary duplicates arise: x-z, z-y -> x-y, y-x (not necessary when IS-A)
	public static void traverseAndFindHidden(Collection<Pair<String>> collection){
		IndirectConnectionsFinder2.setNewlyEmergedCount(0);
		IndirectConnectionsFinder2.run +=1; 
		List<Pair<String>> list1 = new ArrayList<Pair<String>>(collection);
		List<Pair<String>> list2 = new ArrayList<Pair<String>>(collection);
		
		for(int i= 0; i< list1.size(); i++){
			for(int j= i; j< list2.size(); j++){
				if(!list1.get(i).first.equals(list2.get(j).second)){
					if(list1.get(i).second.equals(list2.get(j).first)){
						Pair<String> newPair = new Pair<String>(list1.get(i).first,list2.get(j).second);
						// the relation should go into one consistent direction
						if(allConnectionsCopy.add(newPair)  && !newPair.first.equals(newPair.second)){
							IndirectConnectionsFinder2.newlyEmerged.add(newPair);
							IndirectConnectionsFinder2.newlyEmergedCount +=1;
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
		if( IndirectConnectionsFinder2.run <= 2 && IndirectConnectionsFinder2.newlyEmergedCount > 0){
			traverseAndFindHidden(collection);
		}
		
	}
	
	/**
	 * A method to identify which relations are newly emerged.
	 */
	public static void filter(){
		newlyEmerged.remove(IndirectConnectionsFinder2.getAllConnections());
		Set<Pair<String>> fin = newlyEmerged;
		for(Pair<String> pair: fin){
			Writer.appendLineToFile(pair.first + "\t" + pair.second, "evaluation/toEvaluate_ISA.txt");
		}
	}
	
	public static void main(String[] args) {
		//Writer.overwriteFile("", "evaluation/toEvaluate_ISA.txt");
		//IndirectConnectionsFinder2.readAllConnections();
		//IndirectConnectionsFinder2.traverseAndFindHidden(allConnections);
		//IndirectConnectionsFinder2.filter();
		
		
		rewriteResultsInSameDirection();
		

	}
	
	/**
	 * Rewrites all relations of the same type in the same direction, then concatenates to a single file.
	 */
	private static void rewriteResultsInSameDirection() {
		String outfilename = "SEEDS/CONCATENATED/" + "ALL_RELATIONS_FINAL.txt";
		Writer.overwriteFile("", outfilename);
		Writer.overwriteFile("", "SEEDS/CONCATENATED/" + "CAUSE" + "_final.txt");
		Writer.overwriteFile("", "SEEDS/CONCATENATED/" + "IS-A" + "_final.txt");
		Writer.overwriteFile("", "SEEDS/CONCATENATED/" + "PART-OF" + "_final.txt");
		IndirectConnectionsFinder2.prepareInstances("IS-A", "HYPERNYMY");
		IndirectConnectionsFinder2.prepareInstances("CAUSE", "CAUSED-BY");
		IndirectConnectionsFinder2.prepareInstances("PART-OF", "PART-OF-I");
		List<String> finalFiles = new ArrayList<String>();
		
		
		File concatenated = new File("SEEDS/CONCATENATED/");
		File[] listFiles = concatenated.listFiles();
		for(File file: listFiles){
			finalFiles.add(file.getAbsolutePath());
		}
		
		finalFiles.add("EFFECT/" + "all_instances_and_patterns_EFFECT.txt");
		String[] filesArray = new String[finalFiles.size()];
		filesArray = finalFiles.toArray(filesArray);
		
		
		Writer.concatenateFiles(filesArray, outfilename);
	}
	public static Set<Pair<String>> getAllConnections() {
		return allConnections;
	}
	public static void setAllConnections(Set<Pair<String>> allConnections) {
		IndirectConnectionsFinder2.allConnections = allConnections;
	}

	public static int getNewlyEmergedCount() {
		return newlyEmergedCount;
	}

	public static void setNewlyEmergedCount(int newlyEmergedCount) {
		IndirectConnectionsFinder2.newlyEmergedCount = newlyEmergedCount;
	}

	public static Set<Pair<String>> getAllConnectionsCopy() {
		return allConnectionsCopy;
	}

	public static void setAllConnectionsCopy(Set<Pair<String>> allConnectionsCopy) {
		IndirectConnectionsFinder2.allConnectionsCopy = allConnectionsCopy;
	}

}
