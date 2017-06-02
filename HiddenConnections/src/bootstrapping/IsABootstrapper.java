package bootstrapping;

import java.util.Arrays;
import java.util.List;

import edu.stanford.nlp.simple.Sentence;
import io.Reader;
import overall.Pair;

public class IsABootstrapper extends Bootstrapper {
	
	public IsABootstrapper(){
		this.setType("IS-A");
		this.setPathToSeeds("SEEDS/IS-A_seeds.txt");
		this.setPathToComplementarySeeds("SEEDS/COMPLEMENTARY_TO_ISA.txt");
	}
	

	@Override
	public boolean filterConnectionsForType(String candidate, List<String> pos, String[] splitted, Bootstrapper bs) {
		boolean result = false;
		
			
			if(RelationsFilter.candidateContainsOtherTerms(candidate)
				|| RelationsFilter.isIncompleteNP(pos)
				|| RelationsFilter.isSingleChar(candidate)
				|| splitted.length >= 8
				|| RelationsFilter.isInAnotherRelation(candidate, pos.toString(), bs)){
					 
				
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
						
						this.getSeedConnections().put(connection, pos);
						// handle the frequencies of POS patterns
						Integer posFrequency = this.getPosFrequencyConnections().get(pos);
						if(posFrequency !=null){
							this.getPosFrequencyConnections().put(pos, posFrequency + 1);
						} else{
							this.getPosFrequencyConnections().put(pos, 1);
						}
						
						String posPattern = this.getSeedConnections().get(splitted[3]);
						if( posPattern != null){
							this.getSeedConnections().put(splitted[3], posPattern);
							this.getAllConnections().put(splitted[3], posPattern);
						} else{
							this.getSeedConnections().put(splitted[3], "");
							this.getAllConnections().put(splitted[3], "");
						}
					}
				    

					
				}
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
