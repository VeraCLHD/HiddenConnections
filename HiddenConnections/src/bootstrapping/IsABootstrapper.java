package bootstrapping;

import java.util.List;

import edu.stanford.nlp.simple.Sentence;

public class IsABootstrapper extends Bootstrapper {
	
	public IsABootstrapper(){
		this.setType("IS-A");
		this.setPathToSeeds("SEEDS/IS-A_seeds.txt");
	}
	

	@Override
	public boolean filterConnectionsForType(String candidate) {
		boolean result = false;
		if(!candidate.isEmpty() && !candidate.equals(" ")){
			Sentence sent = new Sentence(candidate);
			List<String> pos = sent.posTags();
			String[] splitted = candidate.split(" ");
			if(RelationsFilter.candidateContainsOtherTerms(candidate)
				|| RelationsFilter.isIncompleteNP(pos, candidate)
				|| RelationsFilter.isSingleChar(candidate)
				|| splitted.length >= 8){
				
				result = true;
		}
		
		}
		
		return result;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
