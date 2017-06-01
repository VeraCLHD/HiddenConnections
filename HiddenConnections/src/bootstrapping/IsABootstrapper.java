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
	}
	

	@Override
	public boolean filterConnectionsForType(String candidate, List<String> pos) {
		boolean result = false;
		
			
			String[] splitted = candidate.split(" ");
			
			if(RelationsFilter.candidateContainsOtherTerms(candidate)
				|| RelationsFilter.isIncompleteNP(pos, candidate)
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
						List<String> pos = Arrays.asList(splitted[5]);
						this.getSeedConnections().put(connection, pos);
						Integer frequency = this.getFrequencyConnections().get(splitted[3]);
						if( frequency == null){
							this.getFrequencyConnections().put(splitted[3], Integer.parseInt(splitted[6]));
						} else{
							this.getFrequencyConnections().put(splitted[3], frequency + Integer.parseInt(splitted[6]));
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
