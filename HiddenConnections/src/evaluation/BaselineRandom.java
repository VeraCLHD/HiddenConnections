package evaluation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import io.Reader;
import io.Writer;
import overall.Pair;

public class BaselineRandom {
	private static final String EVALUATION_RANDOM_COMBINATIONS_TXT = "evaluation/randomCombinations.txt";
	private static final String TERMS_TO_EXCLUDE_TXT = "SEEDS/INFORMATION CONTENT/to_exclude.txt";
	private int HOW_MANY_COMBINATIONS = 163;
	private Set<String> setFoods = new HashSet<String>();
	private Set<String> setDiseases = new HashSet<String>();
	public Set<Pair<String>> pairsToEvaluate = new HashSet<Pair<String>>();
	private Set<String> generalTermsToExclude = new HashSet<String>();
	
	public void readTerms(){
		List<String> lines = Reader.readLinesList("terms/finalVariants.txt");
		for(String line: lines){
			if(!line.isEmpty()){
				String[] splitted = line.split("\t");
				String lemma1 = splitted[0];
				String type = splitted[2];
				if(type.equals("DISEASE")){
					this.getSetDiseases().add(lemma1);
				} else if(type.equals("FOOD")){
					this.getSetFoods().add(lemma1);
				}
				
			}
		}
	}
	
	
	
	public void buildRandomCombinations(){
		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		Writer.overwriteFile("", EVALUATION_RANDOM_COMBINATIONS_TXT);;
		for(int i = 1; i<=HOW_MANY_COMBINATIONS; i++){
			
			
			Pair<String> pair = new Pair<String>("term1", "term2");
			while(true){
				pair = buildOneRandomCombination();
				if(!this.generalTermsToExclude.contains(pair.first) && !this.generalTermsToExclude.contains(pair.second)){
					break;
				}
			}
			
			pairsToEvaluate.add(pair);
			Writer.appendLineToFile(pair.first + "\t"+pair.second, EVALUATION_RANDOM_COMBINATIONS_TXT);
		}
		
	}
	
	public Pair<String> buildOneRandomCombination(){
		int randomNumFood = ThreadLocalRandom.current().nextInt(0, this.getSetFoods().size());
		int randomNumDisease = ThreadLocalRandom.current().nextInt(0, this.getSetDiseases().size());
		List<String> foods = new ArrayList<String>(this.getSetFoods());
		List<String> diseases = new ArrayList<String>(this.getSetDiseases());
		String food = foods.get(randomNumFood);
		String disease = diseases.get(randomNumDisease);
		Pair<String> pair = new Pair<String>(food, disease);
		return pair;
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		BaselineRandom br = new BaselineRandom();
		br.readTerms();
		br.readInformationContentFile();
		br.buildRandomCombinations();
	}
	
	public int getHOW_MANY_COMBINATIONS() {
		return HOW_MANY_COMBINATIONS;
	}

	public void setHOW_MANY_COMBINATIONS(int hOW_MANY_COMBINATIONS) {
		HOW_MANY_COMBINATIONS = hOW_MANY_COMBINATIONS;
	}

	public Set<String> getSetFoods() {
		return setFoods;
	}

	public Set<String> getSetDiseases() {
		return setDiseases;
	}



	public Set<String> getGeneralTermsToExclude() {
		return generalTermsToExclude;
	}



	public void setGeneralTermsToExclude(Set<String> generalTermsToExclude) {
		this.generalTermsToExclude = generalTermsToExclude;
	}


}
