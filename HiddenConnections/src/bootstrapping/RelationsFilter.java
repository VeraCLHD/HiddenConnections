package bootstrapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.plexus.util.StringUtils;

import io.Reader;



public class RelationsFilter {
	// complementary pos patterns of connections
	private static Set<String> complementaryConnections = new HashSet<String>();
	
	/**
	 * A method that checks for incomplete nout phrases - if the first or the last elements of a candidate are nouns, the noun phrase is incomplete
	 * example: disease study about heart disease. Candidate: study about heart -> makes no sense.
	 * @return
	 */
	public static boolean isIncompleteNP(List<String> pos){
		
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
		
		/**
		 * If a connection is in the set of complementary connections, that is: in PART-OF, AND etc., it shouldn't be considered for a certain relation.
		 * @param match
		 * @return
		 */
		public static boolean isInAnotherRelation(String match, String pos){
			
			boolean result = false;
			Set<String> set = RelationsFilter.getComplementaryConnections();
			Set<String> set2 = Bootstrapper.getSeedConnections().keySet();
			    if(set.contains(pos) && !set2.contains(match)){
			    	result = true;

			    }
			
			return result;
		}

		public static void readComplementaryFile(String path){
			List<String> lines = Reader.readLinesList(path);
			for(String line: lines){
				if(!line.isEmpty() && !line.equals(" ")){
					String[] splitted = line.split("\t");
					if(splitted.length ==7){
						String connection = splitted[5];
						RelationsFilter.getComplementaryConnections().add(connection.trim());
						
					}
				}
			}


	}
		
		public static Set<String> getComplementaryConnections() {
			return complementaryConnections;
		}


		public static void setComplementaryConnections(Set<String> complementaryConnections) {
			RelationsFilter.complementaryConnections = complementaryConnections;
		}

}
