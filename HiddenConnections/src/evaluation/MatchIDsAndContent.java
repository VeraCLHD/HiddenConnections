package evaluation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import distant_connections.FilterTooGeneralAfterEvaluation;
import io.Reader;
import io.Writer;

/**
 * The problem is that the evaluated data contained some too general instances. On the other hand, it was build with a non-final distant connections/FINAL.txt
 * Everything must be done again but to avoid manual evaluation to be repeated, the manually evaluated must be matched with the new to keep the ids and to add the not evaluated instances.
 * If something in the data changes, run the normal process again:
 * 1. PreparationForIndirectConnections (term assembler is run by default)
 * 3. Knoten
 * 4. Automatic Evaluation
 * 5. Manual Evaluation
 * Put evaluated file in distant connections/evaluated/ 
 * 6. this
 * @author XMobile
 *
 */
public class MatchIDsAndContent {
	private static final String DISTANT_CONNECTIONS_FINAL_MANUAL = "distant connections/FINAL_MANUAL.txt";
	private static String pathToConnectionsFiltered = "evaluation/manually evaluated/EVALUATED_VERA_FILTERED.txt";
	private static String pathToConnectionsFinal = "evaluation/manually evaluated/EVALUATED_VERA_FILTERED_FINAL.txt";
	
	// from final_manual.txt, before manual evaluation, after update
	private static Set<String> setWithMoreInstances = new HashSet<String>();
	private static Set<String> evaluated = new HashSet<String>();
	private static Set<Integer> randomInts = new HashSet<Integer>();
	
	public static void readFinalRelations(String finalManual, int index, String set){
		List<String> lines = Reader.readLinesList(finalManual);
		
		for(String line: lines){
			if(!line.isEmpty() && !line.equals(" ")){
				String[] splitted = line.split("\t");
				if(index ==3 && !String.valueOf(splitted[2]).startsWith("N")){
					randomInts.add(Integer.parseInt(splitted[2]));
				}
				
				List<String> list = Arrays.asList(splitted);
				String instance = String.join("\t", list.subList(index, list.size()));
				if(set.equals("MOREINSTANCES")){
					MatchIDsAndContent.getSetWithMoreInstances().add(instance.trim());
				} else if(set.equals("EVAL")){
					MatchIDsAndContent.getEvaluated().add(instance.trim());
				}
				
				
			}
	}
	}
	
	public static void reWriteEvalRelations(String eval){
		List<String> lines = Reader.readLinesList(eval);
		
		for(String line: lines){
			if(!line.isEmpty() && !line.equals(" ")){
				String[] splitted = line.split("\t");
				
				List<String> list = Arrays.asList(splitted);
				String instance = String.join("\t", list.subList(3, list.size()));
				if(MatchIDsAndContent.getSetWithMoreInstances().contains(instance)){
					Writer.appendLineToFile(line, pathToConnectionsFinal);
				}
				
				
			}
	}
	}
	

	public static void main(String[] args) {
		FilterTooGeneralAfterEvaluation filter = new FilterTooGeneralAfterEvaluation();
		filter.readInformationContentFile();
		filter.readAllConnectionsAndFilterTooGeneral("evaluation/manually evaluated/EVALUATED_VERA.txt", "evaluation/manually evaluated/EVALUATED_VERA_FILTERED.txt", "evaluation/manually evaluated/too_general_vera.txt");
		
		Writer.overwriteFile("", pathToConnectionsFinal);
		MatchIDsAndContent.readFinalRelations( DISTANT_CONNECTIONS_FINAL_MANUAL,  1, "MOREINSTANCES");
		MatchIDsAndContent.readFinalRelations( pathToConnectionsFiltered,  3, "EVAL");

		for(String forgottenInstance: MatchIDsAndContent.getSetWithMoreInstances()){
			if(!MatchIDsAndContent.getEvaluated().contains(forgottenInstance)){
				String line = "";
				int randomInt = new Random().nextInt(Integer.MAX_VALUE);
				while(true){
					if(!randomInts.add(randomInt)){
						randomInt = new Random().nextInt(Integer.MAX_VALUE);
					} else{
						break;
					}
				}
				
				line = "toFill" + "\t" + "toFill" +  "\t" +"N"+ String.valueOf(randomInt) + "\t" + forgottenInstance;
				Writer.appendLineToFile(line, pathToConnectionsFinal);
			}

		}
		
		reWriteEvalRelations(pathToConnectionsFiltered);
	}

	public static Set<String> getSetWithMoreInstances() {
		return setWithMoreInstances;
	}

	public static void setSetWithMoreInstances(Set<String> setWithMoreInstances) {
		MatchIDsAndContent.setWithMoreInstances = setWithMoreInstances;
	}

	public static Set<String> getEvaluated() {
		return evaluated;
	}

	public static void setEvaluated(Set<String> evaluated) {
		MatchIDsAndContent.evaluated = evaluated;
	}

}
