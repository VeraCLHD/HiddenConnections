package bootstrapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;



public class RelationsFilter {
	/**
	 * A method that checks for incomplete nout phrases - if the first or the last elements of a candidate are nouns, the noun phrase is incomplete
	 * example: disease study about heart disease. Candidate: study about heart -> makes no sense.
	 * @param candidate
	 * @return
	 */
	public static boolean isIncompleteNP(List<String> pos, String candidate){
		
		boolean result = false;
		
		    if(!pos.isEmpty()){
		    	// avoids extracting incomplete noun phrases: if the first word of candidate is noun or the last is noun or adjective
		    	// VBG in the beginning: avoids poultry "producing" states as an incomplete NP
		    	result = pos.get(0).matches("NN|NNS|NNP|NNPS|VBG") || pos.get(pos.size()-1).matches("NN|NNS|NNP|NNPS|POS|JJ|JJR|JJS");

		    }
		
		return result;
	}
	
	
	public static boolean candidateContainsOtherTerms(String candidate){
		Set<String> set = Bootstrapper.getAllTerms();
		
		List<String> list = new ArrayList<String>(set);
	    
	    boolean match = list.stream().anyMatch(s -> candidate.contains(s));

		return match;
	}
	
	// When the word is health and the match is healthy, the connection starts with y"
		//
		public static boolean isSingleChar(String candidate){
			boolean result = false;
			if(candidate.matches("\\w(,)?\\s.*")){
				result = true;
			}
			
			return result;
		}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
