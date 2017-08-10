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
	private static final String TERMS_FINAL_VARIANTS_TXT = "terms/finalVariants.txt";
	private static final String TERMS_TO_EXCLUDE_TXT = "SEEDS/INFORMATION CONTENT/to_exclude.txt";
	private static final String DISTANT_CONNECTIONS_FINAL_TXT = "distant connections/FINAL.txt";

	private Set<String> setFoods = new HashSet<String>();
	private Set<String> onlyResultFoods = new HashSet<String>();
	private Set<String> setDiseases = new HashSet<String>();
	public Set<Pair<String>> pairsToEvaluate = new HashSet<Pair<String>>();
	private Set<String> generalTermsToExclude = new HashSet<String>();

	
	public void readAndAddFoodsDiseases(){
		List<String> lines = Reader.readLinesList(TERMS_FINAL_VARIANTS_TXT);
		for(String line: lines){
			if(!line.isEmpty()){
				String[] splitted = line.split("\t");
				String lemma1 = splitted[0];
				String type = splitted[2];
				if(type.equals("DISEASE") && !this.getGeneralTermsToExclude().contains(lemma1)){
					this.getSetDiseases().add(lemma1);
				// this is for evaluating all foods against diseases
				} else if(type.equals("FOOD")&& !this.getGeneralTermsToExclude().contains(lemma1)){
						this.getSetFoods().add(lemma1);
				}

			}
		}
	}
	
	
	public void readOnlyFoodsFromResults(){
		List<String> lines = Reader.readLinesList(DISTANT_CONNECTIONS_FINAL_TXT);
		for(String line: lines){
			if(!line.isEmpty()){
				String[] splitted = line.split("\t");
				String lemma1 = splitted[0].trim();
				if(this.getSetFoods().contains(lemma1)){
					this.getOnlyResultFoods().add(lemma1);
				}
				
			}
		}
	}
	public void buildRandomCombinations(Set<String> set, String output){
		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		Writer.overwriteFile("", output);
		for(String food: set){
			Pair<String> pair = buildOneRandomCombination(food);
			pairsToEvaluate.add(pair);
			Writer.appendLineToFile(pair.first + "\t"+pair.second, output);
		}
		
	}
	
	public Pair<String> buildOneRandomCombination(String food){
		
		int randomNumDisease = ThreadLocalRandom.current().nextInt(0, this.getSetDiseases().size());
		List<String> diseases = new ArrayList<String>(this.getSetDiseases());
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


	public Set<String> getOnlyResultFoods() {
		return onlyResultFoods;
	}


	public void setOnlyResultFoods(Set<String> onlyResultFoods) {
		this.onlyResultFoods = onlyResultFoods;
	}


}
