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
	private static String pathToInstances = "SEEDS/CONCATENATED/IS-A_final - Copy.txt";
	private Map<String, Set<String>> isAPairs = new HashMap<String, Set<String>>();
	private StanfordLemmatizer lemm = new StanfordLemmatizer();
	private int N;
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
		this.setN(this.getIsAPairs().keySet().size() + this.getIsAPairs().values().size());
		
		
		allTerms.addAll(this.getIsAPairs().keySet());
		for(String key: this.getIsAPairs().keySet()){
			allTerms.addAll(this.getIsAPairs().get(key));
		}
		
		for(String term: allTerms){
			Double freq = 1.0;
			Double f =countFreqForTerm(freq, term);
			Double information_content = - Math.log10(f/N);
			this.getInformation_content().put(term, information_content);
		}

	}

	private Double countFreqForTerm(Double freq, String term) {
		Set<String> concrete = this.getIsAPairs().get(term);
		if(concrete !=null && !concrete.isEmpty()){
			freq +=concrete.size();
			for(String cterm: concrete){
				countFreqForTerm(freq, cterm);
			}
		}
		return freq;
	
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
		Writer.overwriteFile("", "SEEDS/CONCATENATED/isA_ic.txt");
		for(String term: this.getInformation_content().keySet()){
			Writer.appendLineToFile(term + "\t" + Double.toString(this.getInformation_content().get(term)), "SEEDS/CONCATENATED/isA_ic.txt");
		}
	}

	public static void main(String[] args) {
		InformationContent ic = new InformationContent();
		ic.computeInformationContent();
		ic.writeIC();
		System.out.println(ic.getIsAPairs());
		

	}

	public Map<String, Double> getInformation_content() {
		return information_content;
	}

	public void setInformation_content(Map<String, Double> information_content) {
		this.information_content = information_content;
	}

}
