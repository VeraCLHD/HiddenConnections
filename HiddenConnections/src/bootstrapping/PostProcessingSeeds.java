package bootstrapping;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.stanford.nlp.simple.Sentence;
import io.Editor;
import io.Reader;
import io.Writer;
import overall.Pair;
/**
 * A class that has the purpose to align the originally extracted seeds with the final set of manually adjusted terms.
 * 
 * @author Vera
 */
public class PostProcessingSeeds {


	private static final String seeds_old = "SEEDS/all_seeds_old.txt";
	private static final String seeds_new = "SEEDS/all_seeds_new.txt";
	private static Set<String> terms = new HashSet<String>();


	private static final String pathToAllTerms = "terms/all_terms_and_variants_with10_filtered.txt";
	
	public static void readTerms(){
		List<String> lines = Reader.readLinesList(PostProcessingSeeds.pathToAllTerms);
		for(String line: lines){
			if(!line.isEmpty() && !line.equals(" ")){
				PostProcessingSeeds.getTerms().add(line.trim());
			}
		}
	}
	
	public static void readAndFilterSeedsFile(String pathOld, String pathNew){
		Writer.overwriteFile("", pathNew);
		List<String> lines = Reader.readLinesList(pathOld);
		for(String line: lines){
			if(!line.isEmpty() && !line.equals(" ")){
				String[] splitted = line.split("\t");
				if(splitted.length ==7){
					String path = splitted[0];
					String term1 = splitted[1];
					String term2 = splitted[2];
					String candidate_relation = splitted[3];
					String type = splitted[4];
					
					
					String pos = splitted[5].trim();
					String freq = splitted[6];
					
				    if(terms.contains(term1) && terms.contains(term2)){
				    	String new_line = path + "\t" + term1 + "\t" + term2 + "\t" +
				    			candidate_relation + "\t" + type + "\t" + pos + "\t" + freq;
				    	Writer.appendLineToFile(new_line, pathNew);
				    }

					
				}
			}
		}
		
		Editor.transferFileName(pathOld, pathNew);
	}
	
	public static void readAndFilterAllInstances(String pathOld, String pathNew){
		Writer.overwriteFile("", pathNew);
		List<String> lines = Reader.readLinesList(pathOld);
		for(String line: lines){
			if(!line.isEmpty() && !line.equals(" ")){
				String[] splitted = line.split("\t");
				if(splitted.length ==4){
					
					String term1 = splitted[0];
					String term2 = splitted[1];
					String candidate_relation = splitted[2];
					String type = splitted[3];
					
					
				    if(terms.contains(term1) && terms.contains(term2)){
				    	String new_line = term1 + "\t" + term2 + "\t" +
				    			candidate_relation + "\t" + type;
				    	Writer.appendLineToFile(new_line, pathNew);
				    }

					
				}
			}
		}
		
		Editor.transferFileName(pathOld, pathNew);
	}
	
	public static void main(String[] args) {
		
		
	}

	public static void reWriteAllInstancesAndPatternsPostBootstrapping() {
		List<String> types = new ArrayList<String>();
		types.add("PART-OF");
		types.add("PART-OF-I");
		types.add("IS-A");
		types.add("HYPERNYMY");
		types.add("CAUSED-BY");
		types.add("CAUSE");
		types.add("EFFECT");
		types.add("LINKED-TO");

		for(String type: types){
			String pathOld = "SEEDS/" + type +"/all_instances_and_patterns_" + type + ".txt";
			String pathNew = "SEEDS/" + type +"/all_instances_and_patterns_" + type + "_new.txt";
			readAndFilterAllInstances(pathOld, pathNew);
			
		}
	}

	public static void fitSeedsToAdjustedTerms() {
		readTerms();
		readAndFilterSeedsFile("SEEDS/IS-A/IS-A_seeds.txt", "SEEDS/IS-A/IS-A_seeds_new.txt");
		readAndFilterSeedsFile("SEEDS/IS-A/COMPLEMENTARY_TO_IS-A.txt", "SEEDS/IS-A/COMPLEMENTARY_TO_IS-A_new.txt");
		readAndFilterSeedsFile("SEEDS/HYPERNYMY/HYPERNYMY_seeds.txt", "SEEDS/HYPERNYMY/HYPERNYMY_seeds_new.txt");
		readAndFilterSeedsFile("SEEDS/HYPERNYMY/COMPLEMENTARY_TO_HYPERNYMY.txt", "SEEDS/HYPERNYMY/COMPLEMENTARY_TO_HYPERNYMY_new.txt");
		readAndFilterSeedsFile("SEEDS/PART-OF/PART-OF_seeds.txt", "SEEDS/PART-OF/PART-OF_seeds_new.txt");
		readAndFilterSeedsFile("SEEDS/PART-OF/COMPLEMENTARY_TO_PART-OF.txt", "SEEDS/PART-OF/COMPLEMENTARY_TO_PART-OF_new.txt");
		readAndFilterSeedsFile("SEEDS/PART-OF-I/PART-OF-I_seeds.txt", "SEEDS/PART-OF-I/PART-OF-I_seeds_new.txt");
		readAndFilterSeedsFile("SEEDS/PART-OF-I/COMPLEMENTARY_TO_PART-OF-I.txt", "SEEDS/PART-OF-I/COMPLEMENTARY_TO_PART-OF-I_new.txt");
		readAndFilterSeedsFile("SEEDS/CAUSE/CAUSE_seeds.txt", "SEEDS/CAUSE/CAUSE_seeds_new.txt");
		readAndFilterSeedsFile("SEEDS/CAUSE/COMPLEMENTARY_TO_CAUSE.txt", "SEEDS/CAUSE/COMPLEMENTARY_TO_CAUSE_new.txt");
		readAndFilterSeedsFile("SEEDS/CAUSED-BY/CAUSED-BY_seeds.txt", "SEEDS/CAUSED-BY/CAUSED-BY_seeds_new.txt");
		readAndFilterSeedsFile("SEEDS/CAUSED-BY/COMPLEMENTARY_TO_CAUSED-BY.txt", "SEEDS/CAUSED-BY/COMPLEMENTARY_TO_CAUSED-BY_new.txt");
		readAndFilterSeedsFile("SEEDS/EFFECT/EFFECT_seeds.txt", "SEEDS/EFFECT/EFFECT_seeds_new.txt");
		readAndFilterSeedsFile("SEEDS/EFFECT/COMPLEMENTARY_TO_EFFECT.txt", "SEEDS/EFFECT/COMPLEMENTARY_TO_EFFECT_new.txt");
		readAndFilterSeedsFile("SEEDS/LINKED-TO/LINKED-TO_seeds.txt", "SEEDS/LINKED-TO/LINKED-TO_seeds_new.txt");
		readAndFilterSeedsFile("SEEDS/LINKED-TO/COMPLEMENTARY_TO_LINKED-TO.txt", "SEEDS/LINKED-TO/COMPLEMENTARY_TO_LINKED-TO_new.txt");
	}
	public static String getSeeds_old() {
		return seeds_old;
	}

	public static String getSeeds_new() {
		return seeds_new;
	}
	public static Set<String> getTerms() {
		return terms;
	}
}
