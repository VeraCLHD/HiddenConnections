package evaluation;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.queryparser.classic.ParseException;

import io.Reader;
import io.Writer;
import overall.LuceneSearcher;
import overall.Pair;

public class AutomaticEvaluation {
	private static final String DISTANT_CONNECTIONS_FINAL_TXT = "distant connections/FINAL.txt";
	public static final String FINAL_VARIANTS = "terms/finalVariants.txt";
	public Map<String, Set<String>> variations = new HashMap<String, Set<String>>();
	public LuceneSearcher ls = new LuceneSearcher();
	
	public Set<Pair<String>> pairsToEvaluate = new HashSet<Pair<String>>();
	private String EVAL_SOURCE;
	private String EVAL_SOURCE_NAME;
	private static final String EVALUATION_RANDOM_COMBINATIONS_TXT = "evaluation/randomCombinations.txt";
	

	

	public AutomaticEvaluation(String evaluationSourcePath, String evaluationSourceName) {
		this.setEVAL_SOURCE(evaluationSourcePath);
		this.setEVAL_SOURCE_NAME(evaluationSourceName);
	}

	public Map<String, Set<String>> getVariations() {
		return variations;
	}

	public static void main(String[] args) {
		BaselineRandom br = new BaselineRandom();
		br.readInformationContentFile();
		br.readTerms();
		br.buildRandomCombinations();
		//baseline 1
		//evaluate();
		evaluate("EVALUATION SETS/DOCDUMP/INDEX SENTENCES/", "DOCDUMP", DISTANT_CONNECTIONS_FINAL_TXT);
		evaluate("EVALUATION SETS/AUTHORITYNUTRITION/INDEX AUTHORITYUTRITION/", "AUTHORITY", DISTANT_CONNECTIONS_FINAL_TXT);
		evaluate("EVALUATION SETS/DOCDUMP/INDEX SENTENCES/", "DOCDUMP_RANDOM", EVALUATION_RANDOM_COMBINATIONS_TXT);
		evaluate("EVALUATION SETS/AUTHORITYNUTRITION/INDEX AUTHORITYUTRITION/", "AUTHORITY_RANDOM", EVALUATION_RANDOM_COMBINATIONS_TXT);

	}

	private static void evaluate(String path_toIndexed, String nameOfSource, String pathToConnections) {
		Writer.overwriteFile("", "evaluation/" + "evaluation_paths_" + nameOfSource + ".txt");
		Writer.overwriteFile("", "evaluation/evaluation_" + nameOfSource + ".txt");
		AutomaticEvaluation ae = new AutomaticEvaluation(path_toIndexed, nameOfSource);
		ae.readFinalVariants(FINAL_VARIANTS);
		ae.readNewPairs(pathToConnections);
		for(Pair<String> pair: ae.pairsToEvaluate){
			String line = pair.first + "\t" + pair.second + "\t";
			if(ae.findOnePairOfLemmas(pair.first, pair.second, path_toIndexed)){
				line += "1";
			
			} else{
				line +="0";
			}
			
			Writer.appendLineToFile(line, "evaluation/evaluation_" + nameOfSource + ".txt");
		}
	}
	
	public void readFinalVariants(String filename){
		List<String> lines = Reader.readLinesList(filename);
		for(String line: lines){
			if(!line.isEmpty()){
				String[] splitted = line.split("\t");
				String lemma = splitted[0];
				Set<String> variants = new HashSet<String>(Arrays.asList(splitted[1].split(",")));
				this.getVariations().put(lemma, variants);
			}
		}
	}
	
	public boolean findOnePairOfLemmas(String lemma1, String lemma2, String corpus){
		boolean result = false;
		Set<String> set = new HashSet<String>();
		Set<String> var1 = this.getVariations().get(lemma1);
		Set<String> var2 = this.getVariations().get(lemma2);
		for(String variant1: var1){
			for(String variant2: var2){
				if(!variant1.equals(variant2)){
					try {
						set.addAll(ls.doSearch("\"" + variant1 +"\"" + "AND" + "\"" + variant2 +"\"", corpus ));
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						System.err.println("Could not parse " + "\"" + variant1 +"\"" + "AND" + "\"" + variant2 +"\"");
					}
				}
			}
		}
		
		if(!set.isEmpty()){
			result = true;
			String line = lemma1 + "\t" + lemma2 + "\t" + set;
			Writer.appendLineToFile(line, "evaluation/evaluation_paths_" + this.getEVAL_SOURCE_NAME() + ".txt");
		}
		
		return result;
		
		
	}
	
	public void readNewPairs(String results){
		List<String> lines = Reader.readLinesList(results);
		for(String line: lines){
			if(!line.isEmpty()){
				String[] splitted = line.split("\t");
				String lemma1 = splitted[0];
				String lemma2 = splitted[splitted.length-1];
				Pair<String> pair = new Pair<String>(lemma1, lemma2);
				pairsToEvaluate.add(pair);
			}
		}
	}
	
	public static String getFinalVariants() {
		return FINAL_VARIANTS;
	}
	
	public String getEVAL_SOURCE() {
		return EVAL_SOURCE;
	}

	public void setEVAL_SOURCE(String eVAL_SOURCE) {
		EVAL_SOURCE = eVAL_SOURCE;
	}

	public String getEVAL_SOURCE_NAME() {
		return EVAL_SOURCE_NAME;
	}

	public void setEVAL_SOURCE_NAME(String eVAL_SOURCE_NAME) {
		EVAL_SOURCE_NAME = eVAL_SOURCE_NAME;
	}

}
