package distant_connections;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.ListSelectionEvent;

import io.Editor;
import io.Reader;
import io.Writer;
import overall.Pair;
import terms_processing.StanfordLemmatizer;

public class IndirectConnectionsFinder2 {
	private static Set<Quadruple<String>> allConnections = new HashSet<Quadruple<String>>();
	private static String pathToInstances = "SEEDS/CONCATENATED/ALL_RELATIONS_FINAL.txt";
	private static String pathToClusteredTerms = "terms/all_terms_and_variants_with10_filtered_clustered.txt";
	
	private static Map<String, String> foodDiseaseMapping = new HashMap<String, String>();
	

	private static Set<Quadruple<String>> newlyEmerged = new HashSet<Quadruple<String>>();
	// a variable to check if there were newly emerged in the last method call (method for identification is recursive)
	private static int newlyEmergedCount = 0;
	private static int run = 1;
	private static Set<String> generalTermsToExclude = new HashSet<String>();
	private static StanfordLemmatizer lemm = new StanfordLemmatizer();
	
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
				newLine += second + "\t" + first + "\t" + third + "\t" + type1;// + "-S";
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
	
	public static void readInformationContentFile(){
		List<String> lines = Reader.readLinesList("SEEDS/CONCATENATED/to_exclude.txt");
		for(String lineToExclude: lines){
			if(!lineToExclude.isEmpty()){
				String[] splitted =  lineToExclude.split("\t");
				IndirectConnectionsFinder2.getGeneralTermsToExclude().add(splitted[0]);
			}
			
		}
	}
	
	public static void readAllConnections(){
		List<String> lines = Reader.readLinesList(pathToInstances);
		for(String line: lines){
			if(!line.isEmpty() && !line.equals(" ")){
				String[] splitted = line.split("\t");
				if(splitted.length == 4){
					Quadruple<String> isAPair = new Quadruple<String>(splitted[0], splitted[1], splitted[2], splitted[3]);
					IndirectConnectionsFinder2.getAllConnections().add(isAPair);
				}
			}
	}
	}
	
	/**
	 * Checks if a term is too general -> not allowed as start and ending point of a relation.
	 * @param term
	 * @return
	 */
	public boolean isTooGeneral(String term){
		boolean result = false;
		String lemma = lemm.lemmatize(term);
		if(!term.contains(" ") && IndirectConnectionsFinder2.getGeneralTermsToExclude().contains(lemma)){
			result = true;
		}
		return result;
		
		}
	
	public static void readClusteredTerms(){
		List<String> terms = Reader.readLinesList(pathToClusteredTerms);
		for(String term: terms){
			if(!term.isEmpty()){
				String[] splitted = term.split("\t");
				IndirectConnectionsFinder2.getFoodDiseaseMapping().put(splitted[0].trim(), splitted[1].trim());
			}
		}
	}
	
	
	//____________________________________________________________________________
	// unnecessary duplicates arise: x-z, z-y -> x-y, y-x (not necessary when IS-A)
	public static void traverseAndFindHidden(Collection<Quadruple<String>> collection){
		IndirectConnectionsFinder2.setNewlyEmergedCount(0);
		IndirectConnectionsFinder2.run +=1; 
		List<Quadruple<String>> list1 = new ArrayList<Quadruple<String>>(collection);
		List<Quadruple<String>> list2 = new ArrayList<Quadruple<String>>(collection);
		
		for(int i= 0; i< list1.size(); i++){
			for(int j= i; j< list2.size(); j++){
				
			}
		}
		
			
				
				/*if(!list1.get(i).first.equals(list2.get(j).second)){
					if(list1.get(i).second.equals(list2.get(j).first)){
						//Quadruple<String> newPair = new Quadruple<String>(list1.get(i).first,list2.get(j).second);
						// the relation should go into one consistent direction
						if(!newPair.first.equals(newPair.second)){
							IndirectConnectionsFinder2.newlyEmerged.add(newPair);
							IndirectConnectionsFinder2.newlyEmergedCount +=1;
						}
						
					} /*else if(list1.get(i).first.equals(list2.get(j).second)){
						Pair<String> newPair = new Pair<String>(list1.get(i).second, list2.get(j).first);
						
						if(allConnectionsCopy.add(newPair)){
							IndirectConnectionsFinder.newlyEmerged.add(newPair);
							IndirectConnectionsFinder.newlyEmergedCount +=1;
						}
					}
				}
			}
		} 
		allConnections.addAll(allConnectionsCopy);
		if( IndirectConnectionsFinder2.run <= 2 && IndirectConnectionsFinder2.newlyEmergedCount > 0){
			traverseAndFindHidden(collection);
		}*/

	}
	
	/**
	 * A method to identify which relations are newly emerged.
	 */
	public static void filter(){
		newlyEmerged.remove(IndirectConnectionsFinder2.getAllConnections());
		Set<Quadruple<String>> fin = newlyEmerged;
		for(Quadruple<String> pair: fin){
			Writer.appendLineToFile(pair.first + "\t" + pair.second, "evaluation/toEvaluate_ISA.txt");
		}
	}
	
	public static void main(String[] args) {
		
		//IndirectConnectionsFinder2.filter();
		
		
		//rewriteResultsInSameDirection();
		// to exclude too general terms
		
		Writer.overwriteFile("", "evaluation/toEvaluate_all.txt");
		IndirectConnectionsFinder2.readAllConnections();
		readInformationContentFile();
		readClusteredTerms();
		//IndirectConnectionsFinder2.traverseAndFindHidden(allConnections);

	}
	
	
	public static Set<Quadruple<String>> getAllConnections() {
		return allConnections;
	}
	public static void setAllConnections(Set<Quadruple<String>> allConnections) {
		IndirectConnectionsFinder2.allConnections = allConnections;
	}

	public static int getNewlyEmergedCount() {
		return newlyEmergedCount;
	}

	public static void setNewlyEmergedCount(int newlyEmergedCount) {
		IndirectConnectionsFinder2.newlyEmergedCount = newlyEmergedCount;
	}
	public static Set<String> getGeneralTermsToExclude() {
		return generalTermsToExclude;
	}

	public static void setGeneralTermsToExclude(Set<String> generalTermsToExclude) {
		IndirectConnectionsFinder2.generalTermsToExclude = generalTermsToExclude;
	}
	public static Map<String, String> getFoodDiseaseMapping() {
		return foodDiseaseMapping;
	}
}
