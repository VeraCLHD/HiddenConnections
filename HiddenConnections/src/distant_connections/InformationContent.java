package distant_connections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.Reader;
import io.Writer;
import overall.Pair;
import terms_processing.StanfordLemmatizer;

/**
 * A class that looks at the current IS-A hierarchy and marks terms as general concept
 * if they have very low information content.
 * @author Vera
 *
 */
public class InformationContent {
	private static final String SEEDS_INFORMATION_CONTENT_IS_A_IC_TXT = "SEEDS/INFORMATION CONTENT/isA_ic.txt";
	private static final String TERMS_TO_EXCLUDE_TXT = "SEEDS/INFORMATION CONTENT/to_exclude.txt";
	private static final double MIN_INFORMATION_CONTENT = 2.0;
	private static String pathToInstances = "SEEDS/INFORMATION CONTENT/IS-A_final.txt";
	// key: general term, value: list of children
	private Map<String, Set<String>> isAPairs = new HashMap<String, Set<String>>();
	private StanfordLemmatizer lemm = new StanfordLemmatizer();
	private int N;
	private Double freq = 1.0;
	

	private Map<String, Double> information_content = new HashMap<String, Double>();
	
	public void readIsAHierarchy(){
		List<String> lines = Reader.readLinesList(pathToInstances);
		for(String line: lines){
			if(!line.isEmpty() && !line.equals(" ")){
				String[] splitted = line.split("\t");
				String general = splitted[0];
				String concrete = splitted[1];
				if(!general.contains(" ")){
					general = lemm.lemmatize(general);
				} 
				
				if(!concrete.contains(" ")){
					concrete = lemm.lemmatize(concrete);
				}
				
				if(!general.equals(concrete)){
					Set<String> listForGeneral = this.getIsAPairs().get(general);
					
					if(listForGeneral !=null){
						if(!this.getIsAPairs().containsKey(concrete)){
							listForGeneral.add(concrete);
							this.getIsAPairs().put(general, listForGeneral);
						}
						
						
					} else{
						Set<String> listForGen = new HashSet<String>();
						if(!this.getIsAPairs().containsKey(concrete)){
							listForGen.add(concrete);
						}
						
						this.getIsAPairs().put(general, listForGen);
					}
				}
			}
		}
	}
	
	public void computeInformationContent(){
		Set<String> allTerms = new HashSet<String>();
		this.readIsAHierarchy();
		
		allTerms.addAll(this.getIsAPairs().keySet());
		for(String key: this.getIsAPairs().keySet()){
			allTerms.addAll(this.getIsAPairs().get(key));
		}
		
		this.setN(allTerms.size());
		
		for(String term: allTerms){
			this.setFreq(1.0);
			countFreqForTerm(term);
			Double information_content = - Math.log10(this.getFreq()/N);
			this.getInformation_content().put(term, information_content);
		}

	}

	private void countFreqForTerm(String term) {
		Set<String> concrete = this.getIsAPairs().get(term);
		if(concrete !=null && !concrete.isEmpty()){
			Double f = freq + concrete.size();
			this.setFreq(f);
			for(String cterm: concrete){
				countFreqForTerm(cterm);
			}
		}
	
	}
	
	
	
	public void setN(int n) {
		N = n;
	}

	public int getN() {
		return N;
	}

	public Map<String, Set<String>> getIsAPairs() {
		return isAPairs;
	}

	public void setIsAPairs(Map<String, Set<String>> isAPairs) {
		this.isAPairs = isAPairs;
	}
	
	public void writeIC(){
		Writer.overwriteFile("", SEEDS_INFORMATION_CONTENT_IS_A_IC_TXT);
		for(String term: this.getInformation_content().keySet()){
			
			if( this.getInformation_content().get(term) >= MIN_INFORMATION_CONTENT){
				Writer.appendLineToFile(term + "\t" + Double.toString(this.getInformation_content().get(term)), SEEDS_INFORMATION_CONTENT_IS_A_IC_TXT);
			} else{
				Writer.appendLineToFile(term + "\t" + Double.toString(this.getInformation_content().get(term)), TERMS_TO_EXCLUDE_TXT);
			}
			
		}
	}

	public static void main(String[] args) {
		
		

	}

	public Map<String, Double> getInformation_content() {
		return information_content;
	}

	public void setInformation_content(Map<String, Double> information_content) {
		this.information_content = information_content;
	}
	public Double getFreq() {
		return freq;
	}

	public void setFreq(Double freq) {
		this.freq = freq;
	}

}
