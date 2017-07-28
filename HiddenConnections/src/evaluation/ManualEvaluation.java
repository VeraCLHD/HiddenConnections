package evaluation;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import distant_connections.Quadruple;
import io.Reader;
import io.Writer;

public class ManualEvaluation {
	private static final String DISTANT_CONNECTIONS_FINAL = "distant connections/FINAL.txt";
	private static final String DISTANT_CONNECTIONS_FINAL_ID = "distant connections/FINAL_ID.txt";
	private static final String DISTANT_CONNECTIONS_FINAL_MANUAL = "distant connections/FINAL_MANUAL.txt";
	private static Set<Integer> randomInts = new HashSet<Integer>();
	
	
	public static void readInstancesFromFinal(){
		Writer.overwriteFile("", DISTANT_CONNECTIONS_FINAL_MANUAL);
		List<String> lines1 = Reader.readLinesList(DISTANT_CONNECTIONS_FINAL_ID);
		for(String line: lines1){
			if(!line.isEmpty()){
				String newLine = "";
				String[] splitted = line.split("\t");
				for(int i=0; i<splitted.length; i++){
					if(i==0){
						newLine += splitted[i] + "\t";
					}
					if(i%2 !=0){
						newLine += splitted[i] + "\t";
					} else{
						if(splitted[i].equals("PART-OF")){
							newLine += "in" + "\t";
							
						} else if(splitted[i].equals("PART-OF-I")){
							newLine += "have" + "\t";
							
						} else if(splitted[i].equals("CAUSE")){
							newLine += "cause" + "\t";
							
						} else if(splitted[i].equals("CAUSED-BY")){
							newLine += "caused by" + "\t";
						} else if(splitted[i].equals("EFFECT")){
							newLine += "has an effect on" + "\t";
							
						} else if(splitted[i].equals("EFFECT-I")){
							newLine += "is affected by" + "\t";
							
						} else if(splitted[i].equals("HYPERNYMY")){
							newLine += "is a" + "\t";
							
						} else if(splitted[i].equals("IS-A")){
							newLine += "such as" + "\t";
							
						} else if(splitted[i].equals("LINKED-TO")){
							newLine += "associated with" + "\t";
							
						} else if(splitted[i].equals("LINKED-TO-I")){
							newLine += "associated with" + "\t";
							
						} 
					}
				}
				
				
				Writer.appendLineToFile(newLine, DISTANT_CONNECTIONS_FINAL_MANUAL);
			}
		}
	}
	
	
	public static void rewriteWithIds(){
		Writer.overwriteFile("", DISTANT_CONNECTIONS_FINAL_ID);
		List<String> lines1 = Reader.readLinesList(DISTANT_CONNECTIONS_FINAL);
		for(String line: lines1){
			String newLine = "";
			if(!line.isEmpty()){
				int randomInt = new Random().nextInt(Integer.MAX_VALUE);
				while(true){
					if(!randomInts.add(randomInt)){
						randomInt = new Random().nextInt(Integer.MAX_VALUE);
					} else{
						break;
					}
				}
				newLine += String.valueOf(randomInt) + "\t";
				newLine += line;
			}
			
			Writer.appendLineToFile(newLine, DISTANT_CONNECTIONS_FINAL_ID);
		}
	}

	public static void main(String[] args) {
		rewriteWithIds();
		readInstancesFromFinal();

	}

}
