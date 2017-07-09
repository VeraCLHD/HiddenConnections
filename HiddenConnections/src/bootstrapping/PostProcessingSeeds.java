package bootstrapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.stanford.nlp.simple.Sentence;
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
	}
	
	public static void main(String[] args) {
		
		readTerms();
		//readAndFilterSeedsFile("SEEDS/IS-A/IS-A_seeds.txt", "SEEDS/IS-A/IS-A_seeds_new.txt");
		//readAndFilterSeedsFile("SEEDS/IS-A/COMPLEMENTARY_TO_IS-A_old.txt", "SEEDS/IS-A/COMPLEMENTARY_TO_IS-A.txt");
		//readAndFilterSeedsFile("SEEDS/HYPERNYMY/HYPERNYMY_seeds_old.txt", "SEEDS/HYPERNYMY/HYPERNYMY_seeds.txt");
		//readAndFilterSeedsFile("SEEDS/HYPERNYMY/COMPLEMENTARY_TO_HYPERNYMY_old.txt", "SEEDS/HYPERNYMY/COMPLEMENTARY_TO_HYPERNYMY.txt");
		//readAndFilterSeedsFile("SEEDS/PART-OF/PART-OF_seeds_old.txt", "SEEDS/PART-OF/PART-OF_seeds.txt");
		//readAndFilterSeedsFile("SEEDS/PART-OF/COMPLEMENTARY_TO_PART-OF_old.txt", "SEEDS/PART-OF/COMPLEMENTARY_TO_PART-OF.txt");
		//readAndFilterSeedsFile("SEEDS/PART-OF-I/PART-OF-I_seeds_old.txt", "SEEDS/PART-OF-I/PART-OF-I_seeds.txt");
		//readAndFilterSeedsFile("SEEDS/PART-OF-I/COMPLEMENTARY_TO_PART-OF-I_old.txt", "SEEDS/PART-OF-I/COMPLEMENTARY_TO_PART-OF-I.txt");
		//readAndFilterSeedsFile("SEEDS/CAUSE/CAUSE_seeds_old.txt", "SEEDS/CAUSE/CAUSE_seeds.txt");
		//readAndFilterSeedsFile("SEEDS/CAUSE/COMPLEMENTARY_TO_CAUSE_old.txt", "SEEDS/CAUSE/COMPLEMENTARY_TO_CAUSE.txt");
		//readAndFilterSeedsFile("SEEDS/CAUSED-BY/CAUSED-BY_seeds_old.txt", "SEEDS/CAUSED-BY/CAUSED-BY_seeds.txt");
		//readAndFilterSeedsFile("SEEDS/CAUSED-BY/COMPLEMENTARY_TO_CAUSED-BY_old.txt", "SEEDS/CAUSED-BY/COMPLEMENTARY_TO_CAUSED-BY.txt");
		//readAndFilterSeedsFile("SEEDS/EFFECT/EFFECT_seeds_old.txt", "SEEDS/EFFECT/EFFECT_seeds.txt");
		//readAndFilterSeedsFile("SEEDS/EFFECT/COMPLEMENTARY_TO_EFFECT_old.txt", "SEEDS/EFFECT/COMPLEMENTARY_TO_EFFECT.txt");
		//readAndFilterSeedsFile("SEEDS/LINKED-TO/LINKED-TO_seeds_old.txt", "SEEDS/LINKED-TO/LINKED-TO_seeds.txt");
		//readAndFilterSeedsFile("SEEDS/LINKED-TO/COMPLEMENTARY_TO_LINKED-TO_old.txt", "SEEDS/LINKED-TO/COMPLEMENTARY_TO_LINKED-TO.txt");
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
