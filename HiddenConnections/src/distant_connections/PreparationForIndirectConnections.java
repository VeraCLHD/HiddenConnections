package distant_connections;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.event.ListSelectionEvent;

import io.Editor;
import io.Reader;
import io.Writer;
import overall.Pair;
import terms_processing.StanfordLemmatizer;
import terms_processing.TermClusterer;

public class PreparationForIndirectConnections {
	private static final String DISTANT_CONNECTIONS_FINAL_INPUT = "distant connections/ALL_RELATIONS_WITH_RELEVANT_INFO.txt";
	// here, the too general connections are already filtered
	private static final String DISTANT_CONNECTIONS_FILTERED = "distant connections/ALL_RELATIONS_FINAL.txt";
	private static final String DISTANT_CONNECTIONS_TOO_GENERAL_RELATIONS_TXT = "distant connections/too_general_relations.txt";
	private static final String TERMS_LEMMATIZED_TXT = "terms/lemmatized.txt";
	private static final String TERMS_TO_EXCLUDE_TXT = "SEEDS/INFORMATION CONTENT/to_exclude.txt";
	private Set<Quadruple<String>> allConnections = new HashSet<Quadruple<String>>();
	private static String pathToInstances = "SEEDS/CONCATENATED/ALL_RELATIONS_FINAL.txt";
	private static String pathToClusteredTerms = "terms/all_terms_and_variants_with10_filtered_clustered.txt";
	
	private Map<String, String> foodDiseaseMapping = new HashMap<String, String>();
	
	private Set<String> generalTermsToExclude = new HashSet<String>();
	private StanfordLemmatizer lemm = new StanfordLemmatizer();
	
	private Map<String, String> lemmatized = new HashMap<String, String>();


	/**
	 * A method that switches the direction of some results (IS-A, HYPERNYMY have to be in the same direction).
	 * Type 1 is the leading: how to rewrite the other one.
	 */
	public void prepareInstances(String type1, String type2){
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
	public void concatenateFinalFiles(String[] files){
		Writer.concatenateFiles(files, "SEEDS/" + "all_relations_final.txt");
		
	}
	
	
	/**
	 * Rewrites all relations of the same type in the same direction, then concatenates to a single file.
	 */
	public void rewriteResultsInSameDirection() {
		// Direction schema: this schema is going to be used even if the pattern reads the other way around
		//G in P (contained term at the beginning) PART-OF
		//P such as X, (more general term at the beginning) IS-A
		//X triggered by Y CAUSED-BY
		String outfilename = "SEEDS/CONCATENATED/" + "ALL_RELATIONS_FINAL.txt";
		Writer.overwriteFile("", outfilename);
		Writer.overwriteFile("", "SEEDS/CONCATENATED/" + "CAUSED-BY" + "_final.txt");
		Writer.overwriteFile("", "SEEDS/CONCATENATED/" + "IS-A" + "_final.txt");
		Writer.overwriteFile("", "SEEDS/CONCATENATED/" + "PART-OF" + "_final.txt");
		this.prepareInstances("IS-A", "HYPERNYMY");
		this.prepareInstances("CAUSED-BY", "CAUSE");
		this.prepareInstances("PART-OF", "PART-OF-I");
		List<String> finalFiles = new ArrayList<String>();
		
		
		File concatenated = new File("SEEDS/CONCATENATED/");
		File[] listFiles = concatenated.listFiles();
		for(File file: listFiles){
			finalFiles.add(file.getAbsolutePath());
		}
		
		finalFiles.add("SEEDS/EFFECT/" + "all_instances_and_patterns_EFFECT.txt");
		finalFiles.add("SEEDS/LINKED-TO/" + "all_instances_and_patterns_LINKED-TO.txt");
		String[] filesArray = new String[finalFiles.size()];
		filesArray = finalFiles.toArray(filesArray);
		
		
		Writer.concatenateFiles(filesArray, outfilename);
	}
	
	
	public void prepareInstancesDirectedPair(String type1, String type2){
		String filename_type1 = "SEEDS/" + type1 +"/all_instances_and_patterns_" + type1 + ".txt";
		String filename_type2 = "SEEDS/" + type2 +"/all_instances_and_patterns_" + type2 + ".txt";
		
		String filename_type1_inverted = "SEEDS/" + type1 +"/all_instances_and_patterns_" + type1 + "_inverted.txt";
		String filename_type2_inverted = "SEEDS/" + type2 +"/all_instances_and_patterns_" + type2 + "_inverted.txt";
		Writer.overwriteFile("", filename_type2_inverted);
		Writer.overwriteFile("", filename_type1_inverted);
		
		List<String> lines2 = Reader.readLinesList(filename_type2);
		for(String line: lines2){
			if(!line.isEmpty()){
				String newLine = "";
				String[] splitted = line.split("\t");
				String first = splitted[0];
				String second = splitted[1];
				String third = splitted[2];
				// S stands for "switched"
				newLine += second + "\t" + first + "\t" + third + "\t" + type1;// + "-S";
				Writer.appendLineToFile(newLine, filename_type2_inverted);
			}
		}
		
		List<String> lines1 = Reader.readLinesList(filename_type1);
		for(String line: lines1){
			if(!line.isEmpty()){
				String newLine = "";
				String[] splitted = line.split("\t");
				String first = splitted[0];
				String second = splitted[1];
				String third = splitted[2];
				// S stands for "switched"
				newLine += second + "\t" + first + "\t" + third + "\t" + type2;// + "-S";
				Writer.appendLineToFile(newLine, filename_type1_inverted);
			}
		}
		
		String[] filenames = {filename_type1,filename_type2_inverted, filename_type1_inverted, filename_type2};
		Writer.concatenateFiles(filenames, "SEEDS/CONCATENATED/" + type1 + "-" + type2 +"_final.txt");
	}
	
	public void prepareInstancesDirectedSingle(String type1){
		String filename_type1 = "SEEDS/" + type1 +"/all_instances_and_patterns_" + type1 + ".txt";
		String filename_type1_inverted = "SEEDS/" + type1 +"/all_instances_and_patterns_" + type1 + "_inverted.txt";
		Writer.overwriteFile("", filename_type1_inverted);
		
		
		List<String> lines1 = Reader.readLinesList(filename_type1);
		for(String line: lines1){
			if(!line.isEmpty()){
				String newLine = "";
				String[] splitted = line.split("\t");
				String first = splitted[0];
				String second = splitted[1];
				String third = splitted[2];
				// S stands for "switched"
				newLine += second + "\t" + first + "\t" + third + "\t" +type1+ "-I";// + "-S";
				Writer.appendLineToFile(newLine, filename_type1_inverted);
			}
		}
		
		String[] filenames = {filename_type1, filename_type1_inverted};
		Writer.concatenateFiles(filenames, "SEEDS/CONCATENATED/" + type1  + "_final.txt");
	}
	
	public void rewriteResultsDirected() {
		// Direction schema: this schema is going to be used even if the pattern reads the other way around
		//G in P (contained term at the beginning) PART-OF
		//P such as X, (more general term at the beginning) IS-A
		//X triggered by Y CAUSED-BY
		String outfilename = "SEEDS/CONCATENATED/" + "ALL_RELATIONS_FINAL.txt";
		Writer.overwriteFile("", outfilename);
		Writer.overwriteFile("", "SEEDS/CONCATENATED/" + "CAUSED-BY-CAUSE" + "_final.txt");
		Writer.overwriteFile("", "SEEDS/CONCATENATED/" + "IS-A-HYPERNYMY" + "_final.txt");
		Writer.overwriteFile("", "SEEDS/CONCATENATED/" + "PART-OF-PART-OF-I" + "_final.txt");
		Writer.overwriteFile("", "SEEDS/CONCATENATED/" + "EFFECT" + "_final.txt");
		Writer.overwriteFile("", "SEEDS/CONCATENATED/" + "LINKED-TO" + "_final.txt");
		this.prepareInstancesDirectedPair("IS-A", "HYPERNYMY");
		this.prepareInstancesDirectedPair("CAUSED-BY", "CAUSE");
		this.prepareInstancesDirectedPair("PART-OF", "PART-OF-I");
		this.prepareInstancesDirectedSingle("EFFECT");
		this.prepareInstancesDirectedSingle("LINKED-TO");
		List<String> finalFiles = new ArrayList<String>();
		
		
		File concatenated = new File("SEEDS/CONCATENATED/");
		File[] listFiles = concatenated.listFiles();
		for(File file: listFiles){
			finalFiles.add(file.getAbsolutePath());
		}
		
		String[] filesArray = new String[finalFiles.size()];
		filesArray = finalFiles.toArray(filesArray);
		
		
		Writer.concatenateFiles(filesArray, outfilename);
	}
	
	public void readInformationContentFile(){
		List<String> lines = Reader.readLinesList(TERMS_TO_EXCLUDE_TXT);
		for(String lineToExclude: lines){
			if(!lineToExclude.isEmpty()){
				String[] splitted =  lineToExclude.split("\t");
				this.getGeneralTermsToExclude().add(splitted[0]);
			}
			
		}
	}
	
	public void readAllConnectionsAndFilterTooGeneral(){
		Writer.overwriteFile("", DISTANT_CONNECTIONS_TOO_GENERAL_RELATIONS_TXT);
		Writer.overwriteFile("", DISTANT_CONNECTIONS_FILTERED);
		List<String> lines = Reader.readLinesList(pathToInstances);
		for(String line: lines){
			if(!line.isEmpty() && !line.equals(" ")){
				String[] splitted = line.split("\t");
				if(splitted.length == 4){

					String first = splitted[0];
					String second = splitted[1];
					String firstLemma = this.getLemm().lemmatize(first);
					String secondLemma = this.getLemm().lemmatize(second);
					
					Set<String> generalTerms = this.getGeneralTermsToExclude();
					Quadruple<String> instance = new Quadruple<String>(first, second, splitted[2], splitted[3]);
					if(first.contains(" ") && second.contains(" ")){
						if(!generalTerms.contains(first) &&
							!generalTerms.contains(second)){
								
								this.getAllConnections().add(instance);
								Writer.appendLineToFile(instance.toString(), DISTANT_CONNECTIONS_FILTERED);
							} else{
								Writer.appendLineToFile(instance.toString(), DISTANT_CONNECTIONS_TOO_GENERAL_RELATIONS_TXT);
							}
					} else if(first.contains(" ") && !second.contains(" ")){
						if(!generalTerms.contains(first) && secondLemma !=null &&
							!generalTerms.contains(secondLemma)){
								
								this.getAllConnections().add(instance);
								Writer.appendLineToFile(instance.toString(), DISTANT_CONNECTIONS_FILTERED);
							} else{
								Writer.appendLineToFile(instance.toString(), DISTANT_CONNECTIONS_TOO_GENERAL_RELATIONS_TXT);
							}
					} else if(!first.contains(" ") && second.contains(" ")){
						if(
							!generalTerms.contains(second) && firstLemma != null &&
							!generalTerms.contains(firstLemma)
							){
								
								this.getAllConnections().add(instance);
								Writer.appendLineToFile(instance.toString(), DISTANT_CONNECTIONS_FILTERED);
							} else{
								Writer.appendLineToFile(instance.toString(), DISTANT_CONNECTIONS_TOO_GENERAL_RELATIONS_TXT);
							}
						
					} else if(!first.contains(" ") && !second.contains(" ")){
						if(firstLemma != null & secondLemma !=null &&
								!generalTerms.contains(firstLemma) && 
								!generalTerms.contains(secondLemma)){
								
								this.getAllConnections().add(instance);
								Writer.appendLineToFile(instance.toString(), DISTANT_CONNECTIONS_FILTERED);
							} else{
								Writer.appendLineToFile(instance.toString(), DISTANT_CONNECTIONS_TOO_GENERAL_RELATIONS_TXT);
							}
					}
					
					
					
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
		if((!term.contains(" ") && this.getGeneralTermsToExclude().contains(lemma))
				|| (term.contains(" ") && this.getGeneralTermsToExclude().contains(term))){
			result = true;
		}
		return result;
		
		}
	
	// reads the clustered terms, puts them into a map and writes the lemmas of all single word terms into a file btw
	public void readClusteredTerms(){
		List<String> terms = Reader.readLinesList(pathToClusteredTerms);
		for(String termLine: terms){
			if(!termLine.isEmpty()){
				String[] splitted = termLine.split("\t");
				String term = splitted[0].trim();
				String lemma = splitted[1];
				if(!lemma.equals("-")){
					this.getFoodDiseaseMapping().put(lemma, splitted[2].trim());
				} else{
					this.getFoodDiseaseMapping().put(term, splitted[2].trim());
				}
				
				
			}
		}
		
		for(Entry<String, String> term: this.getLemmatized().entrySet()){
			Writer.appendLineToFile(term.getKey() + "\t" + term.getValue(), TERMS_LEMMATIZED_TXT);
		}
	}
	
	/**
	 * Input for graph traversal algorithm
	 */
	public void rewriteInstances(){
		// die produzierte Datei enthält noch california, adjectives
		Writer.overwriteFile("", DISTANT_CONNECTIONS_FINAL_INPUT);
		for(Quadruple<String> relation: this.getAllConnections()){
			String first = relation.first;
			String second = relation.second;
			String newLine = first + "\t";
			
			String firstLemma = this.getLemm().lemmatize(relation.first);
			String secondLemma = this.getLemm().lemmatize(relation.second);
			if(!first.contains(" ") && firstLemma !=null){
				newLine += firstLemma +"\t";
				newLine += this.getFoodDiseaseMapping().get(firstLemma) + "\t";
			} else if(first.contains(" ")){
				newLine += first +"\t";
				newLine += this.getFoodDiseaseMapping().get(first) + "\t";
			}
			
			newLine += second + "\t";
			
			if(!second.contains(" ") && secondLemma !=null){
				newLine += secondLemma +"\t";
				newLine += this.getFoodDiseaseMapping().get(secondLemma) +"\t";
			} else if(second.contains(" ")){
				newLine += second +"\t";
				newLine += this.getFoodDiseaseMapping().get(second) +"\t";
			}
			
			newLine += relation.third +"\t";
			newLine += relation.forth;
			Writer.appendLineToFile(newLine, DISTANT_CONNECTIONS_FINAL_INPUT);
		}
	}
	
	
	public static void main(String[] args) throws MalformedURLException, IOException {
		PreparationForIndirectConnections prep = new PreparationForIndirectConnections();
		prep.rewriteResultsDirected();
		
		InformationContent ic = new InformationContent();
		ic.computeInformationContent();
		ic.writeIC();
		System.out.println("DONE with Information Content");
		// setzt voraus, dass information content file already there
		prep.readInformationContentFile();
		TermClusterer tc = new TermClusterer();
		tc.clusterTerms();
		
		prep.readClusteredTerms();
		System.out.println("DONE with clustered Terms");
		prep.readAllConnectionsAndFilterTooGeneral();
		System.out.println("DONE filtering too general");
		// write big file as input for graph
		prep.rewriteInstances();
		

	}
	
	
	public Set<Quadruple<String>> getAllConnections() {
		return allConnections;
	}
	public void setAllConnections(Set<Quadruple<String>> allConnections) {
		this.allConnections = allConnections;
	}

	
	public Set<String> getGeneralTermsToExclude() {
		return generalTermsToExclude;
	}

	public void setGeneralTermsToExclude(Set<String> generalTermsToExclude) {
		this.generalTermsToExclude = generalTermsToExclude;
	}
	public  Map<String, String> getFoodDiseaseMapping() {
		return foodDiseaseMapping;
	}
	
	public StanfordLemmatizer getLemm() {
		return lemm;
	}

	public Map<String, String> getLemmatized() {
		return lemmatized;
	}

	public void setLemmatized(Map<String, String> lemmatized) {
		this.lemmatized = lemmatized;
	}
}
