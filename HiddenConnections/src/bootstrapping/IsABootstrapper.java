package bootstrapping;

import java.util.Arrays;
import java.util.List;

import edu.stanford.nlp.simple.Sentence;
import io.Reader;
import overall.Pair;

public class IsABootstrapper extends Bootstrapper {
	private  String NEW_PATTERNS_ISA_TXT = "";
	private  String NEW_INSTANCES_ISA_TXT = "";
	
	public IsABootstrapper(){
		this.setType("IS-A");
		this.setPathToSeeds("SEEDS/IS-A_seeds.txt");
		this.setPathToComplementarySeeds("SEEDS/COMPLEMENTARY_TO_ISA.txt");
		this.setNEW_INSTANCES_ISA_TXT("new_instances_ISA.txt");
		this.setNEW_PATTERNS_ISA_TXT("new_patterns_ISA.txt");
	}
	

	@Override
	public boolean filterConnectionsForType(String candidate, List<String> pos, String[] splitted) {
		boolean result = false;
		
			
			if(RelationsFilter.isInAnotherRelation(candidate, pos.toString())||
				 RelationsFilter.candidateContainsOtherTerms(candidate)
				|| RelationsFilter.isIncompleteNP(pos)
				|| RelationsFilter.isSingleChar(candidate)
				|| splitted.length >= 8){
					 
				
				result = true;
		
		}
		
		return result;
	}
	
	@Override
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
						String pos = splitted[5];
						
						Bootstrapper.getSeedConnections().put(connection, pos);
						
						String posPattern = Bootstrapper.getSeedConnections().get(splitted[3]);
						if( posPattern != null){
							Bootstrapper.getSeedConnections().put(splitted[3], posPattern);
							this.getAllConnections().put(splitted[3], posPattern);
						} else{
							Bootstrapper.getSeedConnections().put(splitted[3], "");
							this.getAllConnections().put(splitted[3], "");
						}
					}
				    

					
				}
			}
		}
	}
	
	public String getNEW_PATTERNS_ISA_TXT() {
		return NEW_PATTERNS_ISA_TXT;
	}


	public void setNEW_PATTERNS_ISA_TXT(String nEW_PATTERNS_ISA_TXT) {
		NEW_PATTERNS_ISA_TXT = nEW_PATTERNS_ISA_TXT;
	}


	public String getNEW_INSTANCES_ISA_TXT() {
		return NEW_INSTANCES_ISA_TXT;
	}


	public void setNEW_INSTANCES_ISA_TXT(String nEW_INSTANCES_ISA_TXT) {
		NEW_INSTANCES_ISA_TXT = nEW_INSTANCES_ISA_TXT;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
