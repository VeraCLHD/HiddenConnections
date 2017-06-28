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
	
	public static void readAndFilterSeedsFile(){
		List<String> lines = Reader.readLinesList(PostProcessingSeeds.seeds_old);
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
				    	Writer.appendLineToFile(new_line, seeds_new);
				    }

					
				}
			}
		}
	}
	
	public static void main(String[] args) {
		Writer.overwriteFile("", seeds_new);
		readTerms();
		readAndFilterSeedsFile();

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
