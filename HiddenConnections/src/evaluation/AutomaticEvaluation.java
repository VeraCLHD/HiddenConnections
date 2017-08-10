package evaluation;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.queryparser.classic.ParseException;

import edu.stanford.nlp.simple.Sentence;
import io.Reader;
import io.Writer;
import overall.LuceneSearcher;
import overall.Pair;

public class AutomaticEvaluation {
	private static final String EVALUATION_SETS_AUTHORITYNUTRITION_INDEX_AUTHORITYNUTRITION = "EVALUATION SETS/AUTHORITYNUTRITION/INDEX AUTHORITYNUTRITION/";
	private static final String EVALUATION_SETS_DOCDUMP_INDEX_DOCDUMP = "EVALUATION SETS/DOCDUMP/INDEX DOCDUMP/";
	private String EVALUATION_ALL_EVALS_TXT;
	private static final String DISTANT_CONNECTIONS_FINAL_TXT = "distant connections/FINAL.txt";
	public static final String FINAL_VARIANTS = "terms/finalVariants.txt";
	public Map<String, Set<String>> variations = new HashMap<String, Set<String>>();
	public LuceneSearcher ls = new LuceneSearcher();
	
	public Set<Pair<String>> pairsToEvaluate = new HashSet<Pair<String>>();
	private String EVAL_SOURCE;
	private String EVAL_SOURCE_NAME;
	private String EVALUATION_RANDOM_COMBINATIONS_TXT;
	private String whichFoods;


	public AutomaticEvaluation(String evaluationSourcePath, String evaluationSourceName, String whichFoods) {
		this.setEVAL_SOURCE(evaluationSourcePath);
		this.setEVAL_SOURCE_NAME(evaluationSourceName);
		this.setEVALUATION_ALL_EVALS_TXT("evaluation/" + whichFoods + "/allEvals.txt");
		this.setEVALUATION_RANDOM_COMBINATIONS_TXT( "evaluation/" + whichFoods +"/randomCombinations.txt");
		this.setWhichFoods(whichFoods);
	}

	public Map<String, Set<String>> getVariations() {
		return variations;
	}

	public static void main(String[] args) {

		evaluateAllOrOnlyRelevant("ALL");
		evaluateAllOrOnlyRelevant("RESULTS_ONLY");

	}

	private static void evaluateAllOrOnlyRelevant(String whichFoods) {
		Writer.overwriteFile("", "evaluation/" + whichFoods + "/allEvals.txt");
		
		BaselineRandom br = new BaselineRandom();
		br.readInformationContentFile();
		br.readAndAddFoodsDiseases();
		br.readOnlyFoodsFromResults();
		
		AutomaticEvaluation ae = new AutomaticEvaluation("IndexDirectory/", "Baseline1", whichFoods);
		ae.evaluate(DISTANT_CONNECTIONS_FINAL_TXT);
		AutomaticEvaluation docdump = new AutomaticEvaluation(EVALUATION_SETS_DOCDUMP_INDEX_DOCDUMP, "DOCDUMP", whichFoods);
		docdump.evaluate(DISTANT_CONNECTIONS_FINAL_TXT);
		AutomaticEvaluation auth = new AutomaticEvaluation(EVALUATION_SETS_AUTHORITYNUTRITION_INDEX_AUTHORITYNUTRITION, "AUTHORITY", whichFoods);
		auth.evaluate(DISTANT_CONNECTIONS_FINAL_TXT);
	
		for(int i=0; i<10;i++){
			if(whichFoods.equals("ALL")){
				br.buildRandomCombinations(br.getSetFoods(), "evaluation/" + "ALL" +"/randomCombinations.txt");
			} else if(whichFoods.equals("RESULTS_ONLY")){
				br.buildRandomCombinations(br.getOnlyResultFoods(), "evaluation/" + "RESULTS_ONLY" +"/randomCombinations.txt");
			}
			
			AutomaticEvaluation randomDoc = new AutomaticEvaluation(EVALUATION_SETS_DOCDUMP_INDEX_DOCDUMP, "DOCDUMP_RANDOM" + i, whichFoods);
			randomDoc.evaluate(randomDoc.EVALUATION_RANDOM_COMBINATIONS_TXT);
			AutomaticEvaluation randomAuth = new AutomaticEvaluation(EVALUATION_SETS_AUTHORITYNUTRITION_INDEX_AUTHORITYNUTRITION, "AUTHORITY_RANDOM" + i, whichFoods);
			randomAuth.evaluate(randomAuth.EVALUATION_RANDOM_COMBINATIONS_TXT);
		}
	}

	public String getEVALUATION_ALL_EVALS_TXT() {
		return EVALUATION_ALL_EVALS_TXT;
	}

	public void setEVALUATION_ALL_EVALS_TXT(String eVALUATION_ALL_EVALS_TXT) {
		EVALUATION_ALL_EVALS_TXT = eVALUATION_ALL_EVALS_TXT;
	}

	public String getEVALUATION_RANDOM_COMBINATIONS_TXT() {
		return EVALUATION_RANDOM_COMBINATIONS_TXT;
	}

	public void setEVALUATION_RANDOM_COMBINATIONS_TXT(String eVALUATION_RANDOM_COMBINATIONS_TXT) {
		EVALUATION_RANDOM_COMBINATIONS_TXT = eVALUATION_RANDOM_COMBINATIONS_TXT;
	}

	private void evaluate(String pathToConnections) {
		Writer.overwriteFile("", "evaluation/" + this.getWhichFoods() +  "/evaluation_paths_" + this.getEVAL_SOURCE_NAME() + ".txt");
		Writer.overwriteFile("", "evaluation/" + this.getWhichFoods() + "/evaluation_" + this.getEVAL_SOURCE_NAME() + ".txt");
		
		this.readFinalVariants(FINAL_VARIANTS);
		this.readNewPairs(pathToConnections);
		double countTotal = 0.0;
		double truePositives = 0.0;
		double falsePositives = 0.0;
		for(Pair<String> pair: this.pairsToEvaluate){
			countTotal +=1.0;
			String line = pair.first + "\t" + pair.second + "\t";
			if(this.findOnePairOfLemmas(pair.first, pair.second, this.getEVAL_SOURCE())){
				line += "1";
				truePositives +=1.0;
			
			} else{
				line +="0";
				falsePositives +=1.0; 
			}
			
			Writer.appendLineToFile(line, "evaluation/" + this.getWhichFoods() + "/evaluation_" + this.getEVAL_SOURCE_NAME() + ".txt");
		}
		
		Writer.appendLineToFile(this.getEVAL_SOURCE_NAME() + "\t" +truePositives + "\t" + falsePositives + "\t" + countTotal + "\t" + truePositives/countTotal, EVALUATION_ALL_EVALS_TXT);
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
		addLemmatizedMultiword(lemma1, var1);
		addLemmatizedMultiword(lemma2, var2);
		
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
			Writer.appendLineToFile(line, "evaluation/" + this.getWhichFoods() +"/evaluation_paths_" + this.getEVAL_SOURCE_NAME() + ".txt");
		}
		
		return result;
		
		
	}

	private void addLemmatizedMultiword(String lemma1, Set<String> var1) {
		if(lemma1.contains(" ")){
			List<String> splitted = Arrays.asList(lemma1.split(" "));
			String last = splitted.get(splitted.size()-1).trim();
	        String tokenLemma = new Sentence(last).lemma(0);
	        String multiword = String.join(" ", splitted.subList(0, splitted.size()-1)) +" " + tokenLemma;
	        //System.out.println(lemma1 + " " + multiword);
	        var1.add(multiword);
		}
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

	public String getWhichFoods() {
		return whichFoods;
	}

	public void setWhichFoods(String whichFoods) {
		this.whichFoods = whichFoods;
	}

}
