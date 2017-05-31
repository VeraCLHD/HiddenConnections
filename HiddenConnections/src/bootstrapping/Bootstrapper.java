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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.queryparser.classic.ParseException;

import edu.stanford.nlp.simple.Sentence;
import io.Reader;
import overall.LuceneSearcher;
import overall.Pair;


public abstract class Bootstrapper {
	private String type;
	private String pathToSeeds;
	// key: plain strings that come from the seeds; value: POS patterns as list
	// example: such as: [JJ, NN]
	private Map<String, List<String>> seedConnections = new HashMap<String, List<String>>();
	// the seed pairs: (caffeine, migrane pain), (wine, blood pressure)
	private Set<Pair<String>> seedsOnly = new HashSet<Pair<String>>();
	
	// this set would be empty at the beginning
	private Set<String> patterns = new HashSet<String>();
	
	// the seed pairs: (caffeine, migrane pain), (wine, blood pressure)
	private Set<Pair<String>> found = new HashSet<Pair<String>>();
	
	// no matter if a connection was a seed or is found, all frequencies are stored here
	private Map<String, Integer> frequencyConnections = new HashMap<String, Integer>();
	
	private static final int numberOfIterations = 10;
	
	private Set<String> allTerms = new HashSet<String>();
	private static final String pathToAllTerms = "all_terms_and_variants.txt";
	
	public static void main(String[] args) {
		IsABootstrapper isa = new IsABootstrapper();
		isa.readSeedsFile();
		isa.getFound().addAll(isa.getSeedsOnly());
		isa.readAllTerms();
		try {
			isa.bootstrapp();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void getPathToSeeds(String type){
		this.getPathToSeeds();
	}
	
	
	public abstract void filterConnectionsForType(String type);
	
	public void bootstrapp() throws IOException, ParseException{
		 LuceneSearcher ls = new LuceneSearcher();
		// the outer lopp for limiting the iterations
		for(int i= 0; i<=numberOfIterations ;i++){
			Set<String> patterns = new HashSet<String>();
			Set<Pair<String>> seeds = new HashSet<Pair<String>>();
			
			// for loop for the patterns: empty at the beginning
			for(String pattern: patterns){
				Set<String> set = ls.doSearch("\"" +pattern + "\"");
				// angenommen, der pattern würde so gefunden werden
				if(!set.isEmpty()){
					for(String path: set){
						String sentence = Reader.readContentOfFile(path).toLowerCase();
						seeds.addAll(lookForPatternMatch( sentence, pattern));
						
					}
					
				}
				
			}
			
			// rate the instances here, temp solution addAll
			this.found.addAll(seeds);
			// for loop for the seed instances -> the found contain the seeds as well; will be excluded later
			for(Pair<String> instance: this.getFound()){
				
				 String t1 = instance.first;
				 String t2 = instance.second;
				 Set<String> set = ls.doSearch("\"" + t1 +"\"" + "AND" + "\"" + t2 +"\"" );
				 if(!set.isEmpty()){
					 for(String path: set){
						 String sentenceString = Reader.readContentOfFile(path).toLowerCase();
						 
						 // pair: first argument is the string without terms, second is the string with terms
						 Set<String> stdCase = lookForTermMatch(sentenceString, t1, t2);
						 Set<String> stdCase2 = lookForTermMatch(sentenceString, t1, t2);
						 
						 if(!stdCase.isEmpty()){
							 for(String match: stdCase){
								 patterns.add(match);
								} 
						 } if(!stdCase2.isEmpty()){
							 for(String match: stdCase2){
								 patterns.add(match);
								} 
						 }
						 
							
					 }
					
				 }
			}
			// rate patterns here and add them to this.patterns
			// temp solution: add all
			this.patterns.addAll(patterns);
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

			candidates.add(matchWithoutTerms);
			
		}
		return candidates;
	}
	
	
	public Set<Pair<String>> lookForPatternMatch(String sentenceString, String pattern) {
		Set<Pair<String>> candidates = new HashSet<Pair<String>>();
		String temp1 = "";
	    String temp2 = "";
		final Matcher matcher = Pattern.compile("\\b" +
				 Pattern.quote(pattern) + "\\b").matcher(sentenceString);
		if(matcher.find()){
		    String before = sentenceString.substring(matcher.end()).trim();
		    String after = sentenceString.substring(0, matcher.start()).trim();
		    
		    List<String> term1_candidate = Arrays.asList(before.split(" "));
		    List<String> term2_candidate = Arrays.asList(before.split(" "));
		    
		    
		    
		    for(int i = before.length()-1; i>= 0; i--){
		    	String t1_candidate = String.join(" ", term1_candidate.subList(i, term1_candidate.size()));
		    	if(this.allTerms.contains(t1_candidate)){
		    		temp1 = t1_candidate;
		    		continue;
		    	} else{
		    		break;
		    	}
		    }
		    
		    for(int i = 0; i>= after.length()-1; i++){
		    	String t2_candidate = String.join(" ", term2_candidate.subList(i, term2_candidate.size()));
		    	if(this.allTerms.contains(t2_candidate)){
		    		temp2 = t2_candidate;
		    		continue;
		    	} else{
		    		break;
		    	}
		    }
		
		
		}
		
		if(!temp1.isEmpty() && !temp2.isEmpty()){
			Pair<String> pair = new Pair<String>(temp1, temp2);
			candidates.add(pair);
		}
		
		return candidates;
	}
	
	
	public void readSeedsFile(){
		List<String> lines = Reader.readLinesList(this.pathToSeeds);
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
						List<String> pos = Arrays.asList(splitted[5]);
						this.getSeedConnections().put(connection, pos);
						Integer frequency = this.frequencyConnections.get(splitted[3]);
						if( frequency == null){
							this.frequencyConnections.put(splitted[3], Integer.parseInt(splitted[6]));
						} else{
							this.frequencyConnections.put(splitted[3], frequency + Integer.parseInt(splitted[6]));
						}
					}
				    

					
				}
			}
		}
	}
	
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
	public Map<String, List<String>> getSeedConnections() {
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


	public Map<String, Integer> getFrequencyConnections() {
		return frequencyConnections;
	}



}
