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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.queryparser.classic.ParseException;

import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.StringUtils;
import io.Reader;
import io.Writer;
import overall.LuceneSearcher;
import overall.Pair;


public abstract class Bootstrapper {
	private String type;
	private String pathToSeeds;
	private String pathToComplementarySeeds;
	
	private static final String pathToAllTerms = "all_terms_and_variants.txt";

	// key: plain strings that come from the seeds; value: POS patterns as list
	// example: such as: [JJ, NN]
	private static Map<String, String> seedConnections = new HashMap<String, String>();
	
	// key: plain strings that come from the seeds; value: POS patterns as list
	// example: such as: [JJ, NN]
	private Map<String, String> allConnections = new HashMap<String, String>();
	
	// the seed pairs: (caffeine, migrane pain), (wine, blood pressure)
	private Set<Pair<String>> seedsOnly = new HashSet<Pair<String>>();
	
	// this set would be empty at the beginning
	private Set<String> patterns = new HashSet<String>();
	
	// the seed pairs: (caffeine, migrane pain), (wine, blood pressure)
	private Set<Pair<String>> found = new HashSet<Pair<String>>();
	
	// no matter if a connection was a seed or is found, all frequencies are stored here
	// same for the list of pos tags of each pattern: key: list of pos to String, value: frequency
	private Map<String, Integer> posFrequencyConnections = new HashMap<String, Integer>();
	
	
	
	private static final int numberOfIterations = 100;

	private static Set<String> allTerms = new HashSet<String>();

	
	public static void main(String[] args) {
		Writer.overwriteFile("", "seeds.txt");
		Writer.appendLineToFile("", "seed_connections.txt");
		
		IsABootstrapper isa = new IsABootstrapper();
		isa.readAllTerms();
		
		isa.readAndFilterSeedsFile();
		for(Pair<String> seeds: isa.getSeedsOnly()){
			Writer.appendLineToFile(seeds.first + "\t" + seeds.second, "seeds.txt");
		}
		
		for(String seed_connection: Bootstrapper.getSeedConnections().keySet()){
			Writer.appendLineToFile(seed_connection, "seed_connections.txt");
		}
		RelationsFilter.readComplementaryFile(isa.getPathToComplementarySeeds());
		isa.getFound().addAll(isa.getSeedsOnly());
		
		try {
			isa.bootstrapp();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		//System.out.println(isa.getPatterns().toString());
		isa.getFound().removeAll(isa.getSeedsOnly());
		System.out.println(isa.getPatterns());
			
		
		for(Pair<String> pair: isa.getFound()){
			Writer.appendLineToFile(pair.first + "\t" + pair.second, "new_instances_ISA.txt");
		}
		
		//Set<String> seeds = isa.getSeedConnections().keySet();
		//isa.getPatterns().removeAll(seeds);
		
		
		for(String pattern: isa.getPatterns()){
			String posPattern = isa.getAllConnections().get(pattern);
			int frequency = isa.getPosFrequencyConnections().get(posPattern);
			// The pos pattern of following sequence was that frequent:
			
			Writer.appendLineToFile(pattern + "\t" + frequency, "new_patterns_ISA.txt");	
		}
		

	}
	
	
	public void getPathToSeeds(String type){
		this.getPathToSeeds();
	}
	
	
	public abstract boolean filterConnectionsForType(String candidate, List<String> pos, String[] splitted);
	
	public void bootstrapp() throws IOException, ParseException{
		LuceneSearcher ls = new LuceneSearcher();
		// the outer lopp for limiting the iterations
		for(int i= 0; i<numberOfIterations ;i++){
			extractNewInstancesAndPatterns(ls);
			System.out.println("iteration " + String.valueOf(i));
		}
	}


	private void extractNewInstancesAndPatterns(LuceneSearcher ls) throws IOException, ParseException {
		Set<String> local_patterns = new HashSet<String>();
		Set<Pair<String>> seeds = new HashSet<Pair<String>>();

		// for loop for the seed instances -> the found contain the seeds as well; will be excluded later
		for(Pair<String> instance: this.getFound()){
			
			 String t1 = instance.first;
			 String t2 = instance.second;
			 Set<String> set = ls.doSearch("\"" + t1 +"\"" + "AND" + "\"" + t2 +"\"" );
			 if(!set.isEmpty()){
				 for(String path: set){
					 String sentenceString = Reader.readContentOfFile(path).toLowerCase();

					 Set<String> stdCase = lookForTermMatch(sentenceString, t1, t2);
					 Set<String> stdCase2 = lookForTermMatch(sentenceString, t2, t1);
					 
					 if(!stdCase.isEmpty()){
						 for(String match: stdCase){
							
							 handleOneSentence(local_patterns, sentenceString, match); 
						 }
						
					 } if(!stdCase2.isEmpty()){
						 for(String match: stdCase){
							 
							 handleOneSentence(local_patterns, sentenceString, match); 
						 }
						
					 }
					 
						
				 }
				
			 }
		}
		
		// rate patterns here and add them to this.patterns
		// a place holder for how to rate the patterns
		// the sum of frequencies of all pos patterns in the corpus that have been added (no trash)
		Integer freqsum = this.getPosFrequencyConnections().values().stream().reduce(0, Integer::sum);
		
		
		for(String pattern: local_patterns){
			String posPattern = this.getAllConnections().get(pattern);
			int frequencyP = this.getPosFrequencyConnections().get(posPattern);
			double ratio = (double) frequencyP/ freqsum;
			if(ratio > 0.05){
				this.patterns.add(pattern);
			}
		}
		
		
		// for loop for the patterns: empty at the beginning
		//https://stackoverflow.com/questions/11624220/java-adding-elements-to-list-while-iterating-over-it
		for(String pattern: patterns){
			// a positive match is defined as a match of a viable instance. Like could be 1000 in the texts but only 40 produce a viable instance.
			
			Set<String> set = ls.doSearch("\"" + pattern +"\"");

			if(!set.isEmpty()){
				for(String path: set){
					String sentence = Reader.readContentOfFile(path).toLowerCase();
					//Sentence sent = new Sentence(sentence);
					Set<Pair<String>> instances = lookForPatternMatch(sentence, pattern);
			
					seeds.addAll(instances);
					
				}
				
			}	
		}
		

		// a place holder for how to rate the instances
		this.found.addAll(seeds);
	
	}


	private void handleOneSentence(Set<String> local_patterns, String sentenceString, String match) {
			
			 if(!match.isEmpty() && !match.equals(" ") && !match.matches("\\s") && match != null && !match.matches("\\W")){
				 match = match.trim();
				 String[] splitted = match.split(" ");
				 // the pos tags of the candidate are being checked here, not of the sentence!
				 Sentence sent = new Sentence(match);
				 List<String> pos = sent.posTags();
				 String posString = pos.toString();
				 
			 if(!filterConnectionsForType(match, pos, splitted)){
				 
				 local_patterns.add(match);
				 
				 Integer posFrequency = this.getPosFrequencyConnections().get(posString);
				 if(posFrequency !=null){
					 
					 this.getPosFrequencyConnections().put(posString, posFrequency + 1);
					 this.getAllConnections().put(match, posString);
				 } else{
					 this.getPosFrequencyConnections().put(posString, 1);
					 this.getAllConnections().put(match, posString);
				 }
			 }
			} else{
				Integer freq = this.getPosFrequencyConnections().get("SPACE");
				 if(freq !=null){
					 
					 this.getPosFrequencyConnections().put("SPACE", freq + 1);
				 } else{
					 this.getPosFrequencyConnections().put("SPACE", 1);
				 }
			}
			
			
	}
	
	
	//http://stackoverflow.com/questions/11255353/java-best-way-to-grab-all-strings-between-two-strings-regex
	//http://stackoverflow.com/questions/4769652/how-do-you-use-the-java-word-boundary-with-apostrophes
	public  Set<String> lookForTermMatch(String sentenceString, String term1, String term2) {
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
	
	
	public Set<Pair<String>> lookForPatternMatch(String sentenceString, String pattern) {
		pattern = pattern.replaceAll("\\p{Punct}", "").trim();
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
		    	String t1_candidateWP = t1_candidate.replaceAll("\\p{P}", "");
		    	// has to contain t1_candidate the first time, after that temp1. How?
		    	if(Bootstrapper.allTerms.contains(t1_candidate) ){
		    		temp1 = t1_candidate;
		    		continue;
		    	}// in the sentence string, punctiation is directly in the word 
		    	else if(Bootstrapper.allTerms.contains(t1_candidateWP)){
		    		temp1 = t1_candidateWP;
		    		continue;
		    	} else{
		    		break;
		    	}
		    }
		    
		    for(int i = 1; i<= term2_candidate.size(); i++){
		    	String t2_candidate = String.join(" ", term2_candidate.subList(0, i));
		    	String t2_candidateWP = t2_candidate.replaceAll("\\p{P}", "");
		    	if(Bootstrapper.allTerms.contains(t2_candidate)){
		    		temp2 = t2_candidate;
		    		continue;
		    	}
		    	else if(Bootstrapper.allTerms.contains(t2_candidateWP)){
		    		temp2 = t2_candidateWP;
		    		continue;
		    	}else{
		    		break;
		    	}
		    }
		
		
		
		
		
		if(!temp1.isEmpty() && !temp2.isEmpty()){
			// both must be nouns or end with a noun -> otherwise no IS-A, PART-OF...
			Sentence pos1 = new Sentence(temp1);
			Sentence pos2 = new Sentence(temp2);
			
		    String postag1 = pos1.posTag(pos1.length()-1);
			String postag2 = pos2.posTag(pos2.length()-1);
			
			// if both are nouns, then it is IS-A; the POS pattern must end with a noun -> then it is an NP
			if(postag1.matches("NN|NNS|NNP|NNPS") && postag2.matches("NN|NNS|NNP|NNPS")){
				Pair<String> pair = new Pair<String>(temp1, temp2);
				candidates.add(pair);
			}
			
		}
	}
		return candidates;
	}
	
	
	public abstract void readAndFilterSeedsFile();
	
	public void readAllTerms(){
		List<String> lines = Reader.readLinesList(Bootstrapper.pathToAllTerms);
		for(String line: lines){
			if(!line.isEmpty() && !line.equals(" ")){
				String term = line.trim().toLowerCase();
				allTerms.add(term);
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
	public static Map<String, String> getSeedConnections() {
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


	public Map<String, Integer> getPosFrequencyConnections() {
		return posFrequencyConnections;
	}


	public void setPosFrequencyConnections(Map<String, Integer> posFrequencyConnections) {
		this.posFrequencyConnections = posFrequencyConnections;
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
	
	

	

	
	

}
