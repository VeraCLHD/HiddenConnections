package bootstrapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.codehaus.plexus.util.StringUtils;



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
	
	// if candidate starts with or ends with or contains another term somewhere in between, then it shouldn't be taken into account.
	public static boolean candidateContainsOtherTerms(String candidate){
		boolean result = false;
		Set<String> set = Bootstrapper.getAllTerms();
		for(String term: set){
			if(candidate.startsWith(term) || candidate.endsWith(term) 
					|| candidate.matches("(\\w+\\b)*" + Pattern.quote(term) + "(\\w+\\b)*")){
				result = true;
				break;
			}
		}
		
		/*int index = StringUtils.indexOfAny(candidate, set.toArray(new String[set.size()]));
	    if(index !=-1){
	    	result = true;
		
	    }*/
		

		return result;
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
