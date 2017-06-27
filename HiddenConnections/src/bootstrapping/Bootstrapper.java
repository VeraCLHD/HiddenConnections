package bootstrapping;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.queryparser.classic.ParseException;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.util.StringUtils;
import io.Reader;
import io.Writer;
import overall.LuceneSearcher;
import overall.Pair;


public class Bootstrapper {
	
	private String SEED_CONNECTIONS_TXT;
	

	private String SEEDS_TXT;
	

	private String type;
	private String pathToSeeds;
	private String pathToComplementarySeeds;
	private String pathToIndexedCorpus = "IndexDirectory";
	private  String NEW_PATTERNS = "";
	private  String NEW_INSTANCES = "";
	private  String ALL_INSTANCES_AND_PATTERNS = "";
	
	private static final String pathToAllTerms = "terms/all_terms_and_variants_with10_filtered.txt";

	// key: plain strings that come from the seeds; value: POS patterns as list
	// example: such as: [JJ, NN]
	private Map<String, String> seedConnections = new HashMap<String, String>();
	
	// key: plain strings; value: POS patterns as list
	// example: such as: [JJ, NN]
	private Map<String, String> allConnections = new HashMap<String, String>();
	
	// the seed pairs: (caffeine, migrane pain), (wine, blood pressure)
	private Set<Pair<String>> seedsOnly = new HashSet<Pair<String>>();
	
	// the seed pairs: (caffeine, migrane pain), (wine, blood pressure)
	private Set<Pair<String>> found = new HashSet<Pair<String>>();
	
	// this set would be empty at the beginning
	// this is the collection for the final rated patterns
	private Set<String> patterns = new HashSet<String>();
	// scores for output at the end
	private Map<String, Double> scores = new HashMap<String, Double>();
	private static final int numberOfIterations = 50;

	private static Set<String> allTerms = new HashSet<String>();
	// here, all patterns and seeds are added to be rated
	private Map<String, Set<Pair<String>>> patternsToRate = new HashMap<String, Set<Pair<String>>>();
	
	public Bootstrapper(String type){
		this.setType(type);
		this.setPathToSeeds("SEEDS/"+ type + "/" + type + "_" +"seeds.txt");
		this.setPathToComplementarySeeds("SEEDS/"+ type + "/COMPLEMENTARY_TO_" + type + ".txt");
		this.setNEW_INSTANCES_ISA_TXT("SEEDS/"+ type + "/new_instances_" + type + ".txt");
		Writer.overwriteFile("", this.getNEW_INSTANCES());
		this.setNEW_PATTERNS("SEEDS/"+ type + "/patterns_with_highest_score_"+ type + ".txt");
		Writer.overwriteFile("", this.getNEW_PATTERNS());
		this.setALL_INSTANCES_AND_PATTERNS("SEEDS/"+ type + "/all_instances_and_patterns_" + type + ".txt");
		Writer.overwriteFile("", this.getALL_INSTANCES_AND_PATTERNS());
		this.setSEED_CONNECTIONS_TXT("SEEDS/" + type +"/seed_connections_" + type + ".txt");
		Writer.overwriteFile("", this.getSEED_CONNECTIONS_TXT());
		this.setSEEDS_TXT("SEEDS/" + type +"/seeds_" + type + ".txt");
		Writer.overwriteFile("", this.getSEEDS_TXT());
	}
	
	public static void main(String[] args) {
		
		Bootstrapper.readAllTerms();
		List<String> types = new ArrayList<String>();
		//types.add("IS-A");
		//types.add("HYPERNYMY");
		//types.add("PART-OF");
		types.add("PART-OF-I");
		
		for(String type: types){
			runForEachRelation(type);
		}

	}

	private static void runForEachRelation(String type) {
		Bootstrapper bootstrapper = new Bootstrapper(type);
		bootstrapper.readAndFilterSeedsFile();
		
		for(Pair<String> seeds: bootstrapper.getSeedsOnly()){
			Writer.appendLineToFile(seeds.first + "\t" + seeds.second, bootstrapper.getSEEDS_TXT());
		}
		
		for(String seed_connection: bootstrapper.getSeedConnections().keySet()){
			Writer.appendLineToFile(seed_connection, bootstrapper.getSEED_CONNECTIONS_TXT());
		}
		RelationsFilter.readComplementaryFile(bootstrapper.getPathToComplementarySeeds());
		bootstrapper.getFound().addAll(bootstrapper.getSeedsOnly());
		
	
		bootstrapper.bootstrapp();
		
		//System.out.println(isa.getPatterns().toString());
		bootstrapper.getFound().removeAll(bootstrapper.getSeedsOnly());
			
		
		for(Pair<String> pair: bootstrapper.getFound()){
			Writer.appendLineToFile(pair.first + "\t" + pair.second, bootstrapper.getNEW_INSTANCES());
		}
		
		
		
		for(String pattern: bootstrapper.getPatterns()){
			String posPattern = bootstrapper.getAllConnections().get(pattern);
			Double score = bootstrapper.getScores().get(pattern);
			// The pos pattern of following sequence was that frequent:
			
			Writer.appendLineToFile(pattern + "\t" + posPattern + "\t" +  score, bootstrapper.getNEW_PATTERNS());	
		}
		
		// a new file for all patterns and instances (including seeds)
		for(String pattern: bootstrapper.getPatterns()){
			for(Pair<String> instance: bootstrapper.getPatternsToRate().get(pattern)){
				Writer.appendLineToFile(instance.first + "\t" + instance.second 
						+ "\t" + pattern + "\t" + bootstrapper.getType(), bootstrapper.getALL_INSTANCES_AND_PATTERNS());
				
				
			}
		}
	}
	
	
	public Map<String, Double> getScores() {
		return scores;
	}


	public void getPathToSeeds(String type){
		this.getPathToSeeds();
	}
	
	
	public void bootstrapp(){
		LuceneSearcher ls = new LuceneSearcher();
		// the outer lopp for limiting the iterations
		for(int i= 1; i<=numberOfIterations ;i++){
			
			System.out.println("iteration " + String.valueOf(i));
			extractNewInstancesAndPatterns(ls);
			
		}
	}


	private void extractNewInstancesAndPatterns(LuceneSearcher ls) {
		//Set<Pair<String>> seeds = new HashSet<Pair<String>>();

		// for loop for the seed instances -> the found contain the seeds as well; will be excluded later
		for(Pair<String> instance: this.getFound()){
			
			 String t1 = instance.first;
			 String t2 = instance.second;
			 Set<String> set = new HashSet<String>();
			try {
				set = ls.doSearch("\"" + t1 +"\"" + "AND" + "\"" + t2 +"\"", "IndexDirectory" );
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				System.err.println("Could not parse " + "\"" + t1 +"\"" + "AND" + "\"" + t2 +"\"");
			}
			 if(!set.isEmpty()){
				 for(String path: set){
					 String sentenceString = Reader.readContentOfFile(path).toLowerCase();

					 Set<String> stdCase = lookForTermMatch(sentenceString, t1, t2);
					 Set<String> stdCase2 = lookForTermMatch(sentenceString, t2, t1);
					 
					 if(!stdCase.isEmpty()){
						 for(String match: stdCase){
							
							 handleOneSentence( match, t1, t2); 
						 }
						
					 } if(!stdCase2.isEmpty()){
						 for(String match: stdCase){
							 
							 handleOneSentence( match, t1, t2); 
						 }
						
					 }
					 
						
				 }
				
			 }
		}
		
		// for loop for the patterns: empty at the beginning
		//https://stackoverflow.com/questions/11624220/java-adding-elements-to-list-while-iterating-over-it
		Map<String, Double> scores = new HashMap<String, Double>();
		for(String pattern: patterns){
			
			// a positive match is defined as a match of a viable instance. Like could be 1000 in the texts but only 40 produce a viable instance.
			Set<String> set = new HashSet<String>();
			 
			try {
				set = ls.doSearch("\"" + pattern +"\"", "IndexDirectory");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				System.err.println("Could not parse " + "\"" + pattern +"\"");
			}

			if(!set.isEmpty()){
				for(String path: set){
					String sentence = Reader.readContentOfFile(path).toLowerCase();
					//Sentence sent = new Sentence(sentence);
					lookForPatternMatch(sentence, pattern);
					
				}
				
			}	
		}
		
		for(String pattern_to_rate: this.getPatternsToRate().keySet()){
			Double numberOfInstAllPatterns = (double) this.getPatternsToRate().values().size();
			Double numberOfInstPattern_I = (double) this.getPatternsToRate().get(pattern_to_rate).size();
			Double score = (numberOfInstPattern_I/numberOfInstAllPatterns)* (Math.log(numberOfInstPattern_I) / Math.log(2));
			scores.put(pattern_to_rate, score);
		}
		
		// rate the patterns from the collection patternsToRate and add the instances found for the good ones of them
		List<Double> list = new ArrayList<Double>(scores.values());
		Collections.sort(list, Collections.reverseOrder());
		List<Double> top5 = list.subList(0, Math.min(list.size(), 5));
		
		for(Entry<String, Double> entry: scores.entrySet()){
			if(top5.contains(entry.getValue())){
				// add pattern because score is high
				this.patterns.add(entry.getKey());
				// add instances for this pattern
				this.found.addAll(this.getPatternsToRate().get(entry.getKey()));
				this.scores.put(entry.getKey(), entry.getValue());
			}
		}
	
	
	}


	private void handleOneSentence(String match, String term1, String term2) {
			
			 if(!match.isEmpty() && !match.equals(" ") && !match.matches("\\s") && match != null && !match.matches("\\W")){
				 match = match.trim();
				 String[] splitted = match.split(" ");
				 // the pos tags of the candidate are being checked here, not of the sentence!
				 Sentence sent = new Sentence(match);
				 List<String> pos = sent.posTags();
				 String posString = pos.toString();
				 
			 if(!filterConnectionsForType(match, pos, splitted)){
				
				Set<Pair<String>> instancesForPattern =  this.getPatternsToRate().get(match);
				if(instancesForPattern !=null){
					instancesForPattern.add(new Pair<String>(term1, term2));
					this.getPatternsToRate().put(match, instancesForPattern); 
				} else{
					Set<Pair<String>> set = new HashSet<Pair<String>>();
					set.add(new Pair<String>(term1, term2));
					this.getPatternsToRate().put(match, set);
				}

				this.getAllConnections().put(match, posString);
				 }
			 }
			}
	
	
	//http://stackoverflow.com/questions/11255353/java-best-way-to-grab-all-strings-between-two-strings-regex
	//http://stackoverflow.com/questions/4769652/how-do-you-use-the-java-word-boundary-with-apostrophes
	public  Set<String> lookForTermMatch(String sentenceString, String term1, String term2) {
		//
		Set<String> candidates = new HashSet<String>();
		Matcher matcher = Pattern.compile(
				"\\b" +
				 Pattern.quote(term1) + "\\b"
				 + "(.*?)" +
				 "\\b"
				 + Pattern.quote(term2) + "\\b").matcher(sentenceString);
		
		while(matcher.find()){
			// the old candidate without the terms themselves
			String matchWithoutTerms = matcher.group(1);
			// match contains the strings of the terms themselves now

			candidates.add(matchWithoutTerms.trim());
			
		}
		
		return candidates;
	}
	
	/**
	 * look for the instances that match a pattern
	 * @param sentenceString
	 * @param pattern
	 */
	public void lookForPatternMatch(String sentenceString, String pattern) {
		Set<Pair<String>> candidates = new HashSet<Pair<String>>();
		String temp1 = "";
	    String temp2 = "";
		final Matcher matcher = Pattern.compile("\\b" +
				 Pattern.quote(pattern) + "\\b").matcher(sentenceString);
		while(matcher.find()){
		    String before = sentenceString.substring(0, matcher.start()).trim();
		    String after = sentenceString.substring(matcher.end()).trim();
		    
		    List<String> term1_candidate = new ArrayList<String>();
		    List<String> term2_candidate = new ArrayList<String>();
		    
		    if(!before.isEmpty()){
		    	Sentence beforeSentence = new Sentence(before);
		    	term1_candidate = beforeSentence.words();
		    } else{
		    	term1_candidate = Arrays.asList(before.split(" "));
		    }
		    
		    if(!after.isEmpty()){
		    	Sentence afterSentence = new Sentence(after);
				 term2_candidate = afterSentence.words();
		    } else{
		    	term2_candidate = Arrays.asList(after.split(" "));
		    }

		    //
		    for(int i = term1_candidate.size()-1; i>= 0; i--){
		    	String t1_candidate = String.join(" ", term1_candidate.subList(i, term1_candidate.size()));
		    	String t1_candidateWP = t1_candidate.replaceAll("[^a-zA-Z]+$", "").trim();
		    	if(this.allTerms.contains(t1_candidateWP)){
		    		temp1 = t1_candidateWP;
		    		continue;
		    	}// in the sentence string, punctiation is directly in the word 
		    	else{
		    		break;
		    	}
		    }
		    
		    for(int i = 1; i<= term2_candidate.size(); i++){
		    	String t2_candidate = String.join(" ", term2_candidate.subList(0, i));
		    	String t2_candidateWP = t2_candidate.replaceAll("[^a-zA-Z]+$", "").trim();
		    	if(this.allTerms.contains(t2_candidateWP)){
		    		temp2 = t2_candidateWP;
		    		continue;
		    	}
		    	else{
						break;
					}
		    		
		    	}
		
		  //Bootstrapper.allTerms.contains(t1_candidate)
		    if(!temp1.isEmpty() && !temp2.isEmpty()){
				
				Pair<String> pair = new Pair<String>(temp1, temp2);
				candidates.add(pair);
			}

	}
		
		for(Pair<String> candidate: candidates){
			Set<Pair<String>> instancesForPattern =  this.getPatternsToRate().get(pattern);
			if(instancesForPattern !=null){
				instancesForPattern.add(candidate);
				this.getPatternsToRate().put(pattern, instancesForPattern); 
			} else{
				Set<Pair<String>> set = new HashSet<Pair<String>>();
				set.add(candidate);
				this.getPatternsToRate().put(pattern, set);
			}
		}
		
	}
	
	public boolean isTempANP(String temp){
		boolean result = false;
		LexicalizedParser lp1 = LexicalizedParser.loadModel();
		Tree parse = lp1.parse(temp);
		TregexPattern patternM2 = TregexPattern.compile("(@NP !<< @NP)"); 
		// Run the pattern on one particular tree 
		TregexMatcher matcher = patternM2.matcher(parse);
		if (matcher.findNextMatchingNode()) { 
		  Tree match = matcher.getMatch(); 
		  // do what we want to with the subtree
		  result = true;
		 
		}
	return result;
	}
	
	
	public static void readAllTerms(){
		List<String> lines = Reader.readLinesList(Bootstrapper.pathToAllTerms);
		for(String line: lines){
			if(!line.isEmpty() && !line.equals(" ")){
				String term = line.trim().toLowerCase();
				allTerms.add(term);
			}
		}
	}
	
	public boolean filterConnectionsForType(String candidate, List<String> pos, String[] splitted) {
		boolean result = false;
		
			
			if(RelationsFilter.isInAnotherRelation(candidate, pos.toString(), this)||
				 RelationsFilter.candidateContainsOtherTerms(candidate)
				|| RelationsFilter.isIncompleteNP(pos)
				|| RelationsFilter.isSingleChar(candidate)
				|| splitted.length >= 8){
					 
				
				result = true;
		
		}
		
		return result;
	}
	
	public void readAndFilterSeedsFile(){
		List<String> lines = Reader.readLinesList(this.getPathToSeeds());
		for(String line: lines){
			if(!line.isEmpty() && !line.equals(" ")){
				String[] splitted = line.split("\t");
				if(splitted.length ==7){
				
					String term1 = splitted[1];
					String term2 = splitted[2];
					String connection = splitted[3];
					
					Sentence pos1 = new Sentence(term1);
					Sentence pos2 = new Sentence(term2);
					
					String postag1 = pos1.posTag(pos1.length()-1);
					String postag2 = pos2.posTag(pos2.length()-1);
					
					// if both are nouns, then it is IS-A; the POS pattern must end with a noun -> then it is an NP
					if(postag1.matches("NN|NNS|NNP|NNPS") && postag2.matches("NN|NNS|NNP|NNPS")){
						Pair<String> pair = new Pair<String>(term1, term2);
						this.getSeedsOnly().add(pair);
						String pos = splitted[5].trim();
						String pattern = splitted[3].trim();
						
						this.getSeedConnections().put(connection, pos);
						
						String posPattern = this.getSeedConnections().get(splitted[3]);
						if( posPattern != null){
							this.getSeedConnections().put(pattern, posPattern);
							this.getAllConnections().put(pattern, posPattern);
						} else{
							this.getSeedConnections().put(splitted[3], "");
							this.getAllConnections().put(pattern, "");
						}
						
						// this is for the rating of patterns later
						Set<Pair<String>> instancesForPattern =  this.getPatternsToRate().get(pattern);
						Pair<String> seedPair = new Pair<String>(splitted[1], splitted[2]);
						if(instancesForPattern !=null){
							instancesForPattern.add(seedPair);
							this.getPatternsToRate().put(pattern, instancesForPattern); 
						} else{
							Set<Pair<String>> set = new HashSet<Pair<String>>();
							set.add(seedPair);
							this.getPatternsToRate().put(pattern, set);
						}
					}
				    

					
				}
			}
		}
	}
	
	public Set<String> getPatterns() {
		return patterns;
	}
	public void setPatterns(Set<String> patterns) {
		this.patterns = patterns;
	}
	public Set<Pair<String>> getFound() {
		return found;
	}
	public void setFound(Set<Pair<String>> seeds) {
		this.found = seeds;
	}
	public static int getNumberofiterations() {
		return numberOfIterations;
	}
	public Map<String, String> getSeedConnections() {
		return seedConnections;
	}
	public Set<Pair<String>> getSeedsOnly() {
		return seedsOnly;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getPathToSeeds() {
		return pathToSeeds;
	}


	public void setPathToSeeds(String pathToSeeds) {
		this.pathToSeeds = pathToSeeds;
	}

	public static String getPathtoallterms() {
		return pathToAllTerms;
	}


	public static Set<String> getAllTerms() {
		return allTerms;
	}


	public static void setAllTerms(Set<String> allTerms) {
		Bootstrapper.allTerms = allTerms;
	}


	public Map<String, String> getAllConnections() {
		return allConnections;
	}


	public void setAllConnections(Map<String, String> allConnections) {
		this.allConnections = allConnections;
	}


	public String getPathToComplementarySeeds() {
		return pathToComplementarySeeds;
	}


	public  void setPathToComplementarySeeds(String pathToComplementarySeeds) {
		this.pathToComplementarySeeds = pathToComplementarySeeds;
	}


	public Map<String, Set<Pair<String>>> getPatternsToRate() {
		return patternsToRate;
	}


	public void setPatternsToRate(Map<String, Set<Pair<String>>> patternsToRate) {
		this.patternsToRate = patternsToRate;
	}
	
	public String getSEED_CONNECTIONS_TXT() {
		return SEED_CONNECTIONS_TXT;
	}


	public String getSEEDS_TXT() {
		return SEEDS_TXT;
	}
	
	public void setSEED_CONNECTIONS_TXT(String sEED_CONNECTIONS_TXT) {
		SEED_CONNECTIONS_TXT = sEED_CONNECTIONS_TXT;
	}


	public void setSEEDS_TXT(String sEEDS_TXT) {
		SEEDS_TXT = sEEDS_TXT;
	}
	
	// from here only paths
	public String getNEW_PATTERNS() {
		return NEW_PATTERNS;
	}


	public void setNEW_PATTERNS(String nEW_PATTERNS) {
		NEW_PATTERNS = nEW_PATTERNS;
	}


	public String getNEW_INSTANCES() {
		return NEW_INSTANCES;
	}


	public void setNEW_INSTANCES_ISA_TXT(String nEW_INSTANCES) {
		NEW_INSTANCES = nEW_INSTANCES;
	}
	
	public String getALL_INSTANCES_AND_PATTERNS() {
		return ALL_INSTANCES_AND_PATTERNS;
	}


	public void setALL_INSTANCES_AND_PATTERNS(String aLL_INSTANCES_AND_PATTERNS) {
		ALL_INSTANCES_AND_PATTERNS = aLL_INSTANCES_AND_PATTERNS;
	}


}