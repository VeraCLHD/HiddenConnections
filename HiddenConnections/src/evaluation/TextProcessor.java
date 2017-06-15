package evaluation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.lucene.queryparser.classic.ParseException;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import io.Reader;
import io.Writer;
import overall.LuceneDemoIndexer;
import overall.LuceneSearcher;

public class TextProcessor {
	
	private static final int HOW_MANY_SENTENCES = 2;
	private static final String EVALUATION_PATH_DOCDUMP = "EVALUATION SETS/DOCDUMP/DOCDUMP TEXTS/";
	
	private static final String EVALUATION_PATH_DOCDUMP_PARAGRAPHS = "EVALUATION SETS/DOCDUMP/DOCDUMP PARAGRAPHS/";
	private static final String DOCDUMP_TXT = "EVALUATION SETS/DOCDUMP/doc_dump.txt";
	private static final String INDEX_DOCDUMP_TEXTST = "EVALUATION SETS/DOCDUMP/INDEX TEXTS/";
	private static final String INDEX_DOCDUMP_PARAGRAHPS = "EVALUATION SETS/DOCDUMP/INDEX PARAGRAPHS/";
	
	public void readAndRewriterDocDumpSingeText(String pathToSource, int IndexOfText, String pathToOutput){
		List<String> linesOfDump = Reader.readLinesList(pathToSource);
		for (int i=0;i< linesOfDump.size();i++) {
			String line = linesOfDump.get(i);
			String[] elements = line.split("\t");
			String id = elements[0].trim();
			
			
			
			Writer.writeEmptyFile(pathToOutput + id + "_.txt");
			Writer.overwriteFile(elements[IndexOfText], pathToOutput + id + "_.txt");
			
		}
	}
	/**
	 * Prereqiuisite: odd number of how many sentences in paragraph
	 */
	public void readAndRewriterDocDumpParagraphs(String pathToSource, int IndexOfText, String pathToOutput){
		List<String> linesOfDump = Reader.readLinesList(pathToSource);
		for (int i=0;i< linesOfDump.size();i++) {
			String line = linesOfDump.get(i);
			String[] elements = line.split("\t");
			String id = elements[0].trim();
			
			String text = elements[IndexOfText];
			
			Document doc = new Document(text);
			List<Sentence> sentences = doc.sentences();
			
			for(int sent = 0; sent< sentences.size(); sent++){
				String paragraphString = "";
				int startSentIndex = sent;
				int endSentString = startSentIndex + HOW_MANY_SENTENCES +1;
				int howManyBeforeAndAfter = (HOW_MANY_SENTENCES-1)/2;
				List<Sentence> paragraph = sentences.subList(Math.min(startSentIndex - howManyBeforeAndAfter,sent), Math.min(endSentString, sentences.size()));
				for(Sentence sentence: paragraph){
					paragraphString += sentence.toString() + " ";
				}
				
				Writer.writeEmptyFile(pathToOutput + id +  "_" + sent + "_.txt");
				Writer.overwriteFile(paragraphString, pathToOutput + id +  "_" + sent + "_.txt");
			}	
		}
	}
	public static void main(String[] args) {
		TextProcessor tp = new TextProcessor();
		/**
		 * Commented out for rewriting docdump in texts and paragraphs and indexing them
		 */
		//tp.rewriteAndIndexTextsOfOneSource(DOCDUMP_TXT, EVALUATION_PATH_DOCDUMP, EVALUATION_PATH_DOCDUMP_PARAGRAPHS, INDEX_DOCDUMP_TEXTST, INDEX_DOCDUMP_PARAGRAHPS, 3);
		LuceneDemoIndexer.indexAllTexts(INDEX_DOCDUMP_TEXTST, EVALUATION_PATH_DOCDUMP);
		LuceneDemoIndexer.indexAllTexts(INDEX_DOCDUMP_PARAGRAHPS, EVALUATION_PATH_DOCDUMP_PARAGRAPHS);
		/*LuceneSearcher ls = new LuceneSearcher();  
		Set<String> set;
		try {
			set = ls.doSearch("\"" + "estrogen" +"\"" + "is" + "AND"  + "\"" + "steroid" +"\"" ,  INDEX_DOCDUMP_TEXTST);
			 for(String path: set){
				  String str = Reader.readContentOfFile(path);
				  System.out.println(path);
			  }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		 

	}
	private  void rewriteAndIndexTextsOfOneSource(String sourceFile, String pathToTexts,String pathToParagraphs, String pathToIndexedTexts, String pathToIndexedParagraphs, int index) {
		this.readAndRewriterDocDumpSingeText(sourceFile, index, pathToTexts);
		this.readAndRewriterDocDumpParagraphs(sourceFile, index, pathToParagraphs);
		LuceneDemoIndexer.indexAllTexts(pathToIndexedTexts, pathToTexts);
		LuceneDemoIndexer.indexAllTexts(pathToIndexedParagraphs, pathToParagraphs);
	}

}
