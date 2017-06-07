package evaluation;

import java.util.List;

import io.Reader;
import io.Writer;

public class TextProcessor {
	
	private static final String EVALUATION_PATH_DOCDUMP = "EVALUATION SETS/DOCDUMP TEXTS/";
	private static final String DOCDUMP_TXT = "EVALUATION SETS/doc_dump.txt";
	
	public void readAndRewriterDocDumpSingeText(){
		List<String> linesOfDump = Reader.readLinesList(DOCDUMP_TXT);
		for (int i=0;i< linesOfDump.size();i++) {
			String line = linesOfDump.get(i);
			String[] elements = line.split("\t");
			String id = elements[0].trim();
			
			
			
			Writer.writeEmptyFile(EVALUATION_PATH_DOCDUMP + id + "_.txt");
			Writer.overwriteFile(elements[3], EVALUATION_PATH_DOCDUMP + id + "_.txt");
			
		}
	}
	public static void main(String[] args) {
		TextProcessor tp = new TextProcessor();
		TextProcessor.readAndRewriterDocDumpSingeText();

	}

}
