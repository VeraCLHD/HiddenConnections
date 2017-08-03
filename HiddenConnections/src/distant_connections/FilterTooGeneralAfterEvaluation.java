package distant_connections;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.Reader;
import io.Writer;

public class FilterTooGeneralAfterEvaluation {
	private static String pathToConnections = "distant connections/FINAL_MANUAL.txt";
	private static String pathToConnectionsFiltered = "distant connections/FINAL_MANUAL_Filtered.txt";
	private static String pathToConnectionsExcluded = "distant connections/FINAL_MANUAL_EXCLUDED.txt";
	private static final String TERMS_TO_EXCLUDE_TXT = "SEEDS/INFORMATION CONTENT/to_exclude.txt";
	private Set<String> generalTermsToExclude = new HashSet<String>();
	
	public void readInformationContentFile(){
		List<String> lines = Reader.readLinesList(TERMS_TO_EXCLUDE_TXT);
		for(String lineToExclude: lines){
			if(!lineToExclude.isEmpty()){
				String[] splitted =  lineToExclude.split("\t");
				this.getGeneralTermsToExclude().add(splitted[0]);
			}
			
		}
	}
	
	public void readAllConnectionsAndFilterTooGeneral(){
		Writer.overwriteFile("", pathToConnectionsFiltered);
		Writer.overwriteFile("", pathToConnectionsExcluded);
		List<String> lines = Reader.readLinesList(pathToConnections);
		for(String line: lines){
			
			if(!line.isEmpty() && !line.equals(" ")){
				boolean lineIsViable = true;
				String[] splitted = line.split("\t");

					// loop for finding the terms
					for(int i=0; i<splitted.length; i++){
						
						if(i%2 !=0){
							if(this.getGeneralTermsToExclude().contains(splitted[i])){
								// we don't need this instance
								lineIsViable = false;
								break;
							}
						} 
					}
					
					if(lineIsViable == true){
						Writer.appendLineToFile(line, pathToConnectionsFiltered);
					} else{

						Writer.appendLineToFile(line, pathToConnectionsExcluded);
					}
					
				}
	}
	}
	
	public static void main(String[] args) {
		FilterTooGeneralAfterEvaluation filter = new FilterTooGeneralAfterEvaluation();
		filter.readInformationContentFile();
		filter.readAllConnectionsAndFilterTooGeneral();
		

	}

	public Set<String> getGeneralTermsToExclude() {
		return generalTermsToExclude;
	}

	public void setGeneralTermsToExclude(Set<String> generalTermsToExclude) {
		this.generalTermsToExclude = generalTermsToExclude;
	}
}
