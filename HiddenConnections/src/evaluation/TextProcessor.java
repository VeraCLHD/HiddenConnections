package evaluation;

import java.io.File;
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
	private static final String EVALUATION_PATH_DOCDUMP_SENTENCES = "EVALUATION SETS/DOCDUMP/DOCDUMP SENTENCES/";
	
	private static final String DOCDUMP_TXT = "EVALUATION SETS/DOCDUMP/doc_dump.txt";
	private static final String INDEX_DOCDUMP_TEXTST = "EVALUATION SETS/DOCDUMP/INDEX TEXTS/";
	private static final String INDEX_DOCDUMP_PARAGRAHPS = "EVALUATION SETS/DOCDUMP/INDEX PARAGRAPHS/";
	private static final String INDEX_DOCDUMP_SENTENCES = "EVALUATION SETS/DOCDUMP/INDEX DOCDUMP/";
	
	//docdump 1 text
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
	
	
	public void readAndRewriterDocDumpSentence(String pathToSource, int IndexOfText, String pathToOutput){
		List<String> linesOfDump = Reader.readLinesList(pathToSource);
		int artid = 0;
		for (int i=0;i< linesOfDump.size();i++) {
			artid += 1;
			String line = linesOfDump.get(i);
			String[] elements = line.split("\t");
			String id = elements[0].trim();
			if(id.length() > 7){
				id = String.valueOf(artid);
			}
			String text = elements[IndexOfText];
			
			Document doc = new Document(text);
			List<Sentence> sentences = doc.sentences();
			for(int sent = 0; sent< sentences.size(); sent++){
				//id +=1; 
				String sentenceString = sentences.get(sent).toString().toLowerCase();
				
				Writer.writeEmptyFile(pathToOutput + id +  "_" + sent + "_.txt");
				Writer.overwriteFile(sentenceString, pathToOutput + id +  "_" + sent + "_.txt");
			}	
		}
	}
	
	private  void rewriteAndIndexTextsOfOneSource(String sourceFile, String pathToTexts, String pathToIndexedTexts, int index) {
		this.readAndRewriterDocDumpSentence(sourceFile, index, pathToTexts);
		LuceneDemoIndexer.indexAllTexts(pathToIndexedTexts, pathToTexts);
	}
	/**
	 * original for docdump
	 * @param sourceFile: if the evaluation source is in one file and needs rewriting
	 * @param pathToTexts
	 * @param pathToParagraphs
	 * @param pathToIndexedTexts
	 * @param pathToIndexedParagraphs
	 * @param index
	 */
	private  void rewriteAndIndexTextsOfOneSource(String sourceFile, String pathToTexts,String pathToParagraphs, String pathToIndexedTexts, String pathToIndexedParagraphs, int index) {
		this.readAndRewriterDocDumpSingeText(sourceFile, index, pathToTexts);
		this.readAndRewriterDocDumpParagraphs(sourceFile, index, pathToParagraphs);
		LuceneDemoIndexer.indexAllTexts(pathToIndexedTexts, pathToTexts);
		LuceneDemoIndexer.indexAllTexts(pathToIndexedParagraphs, pathToParagraphs);
	}
	
	public static void main(String[] args) {
		TextProcessor tp = new TextProcessor();
		/**
		 * Commented out for rewriting docdump in texts and paragraphs and indexing them
		 */
		//tp.rewriteAndIndexTextsOfOneSource(DOCDUMP_TXT, EVALUATION_PATH_DOCDUMP_SENTENCES, INDEX_DOCDUMP_SENTENCES, 3);
		//tp.concatenateRestOfAuthorityFiles("EVALUATION SETS/AUTHORITYNUTRITION/");
		tp.rewriteAuthorityNutritionRest("EVALUATION SETS/AUTHORITYNUTRITION/concatenatedRest_Authority.txt");
		tp.rewriteAndIndexTextsOfOneSource("EVALUATION SETS/AUTHORITYNUTRITION/pages_plain.txt", "EVALUATION SETS/AUTHORITYNUTRITION/AUTHORITYNUTRITION SENTENCES/", "EVALUATION SETS/AUTHORITYNUTRITION/INDEX AUTHORITYUTRITION/", 1);
	}

	
	public void concatenateRestOfAuthorityFiles(String pathToAll){
		List<String> finalFiles = new ArrayList<String>();
		File concatenated = new File(pathToAll);
		File[] listFiles = concatenated.listFiles();
		for(File file: listFiles){
			finalFiles.add(file.getAbsolutePath());
		}
		
		Writer.concatenateFiles(finalFiles.toArray(new String[finalFiles.size()]), "concatenatedRest_Authority.txt");
	}
	
	public void rewriteAuthorityNutritionRest(String pathRest){
		List<String> linesOfDump = Reader.readLinesList(pathRest);
		for (int i=0;i< linesOfDump.size();i++) {
			String line = linesOfDump.get(i);
			if(!line.isEmpty()){
				Document doc = new Document(line);
				List<Sentence> sentences = doc.sentences();
				for(int sent = 0; sent< sentences.size(); sent++){
					String sentenceString = sentences.get(sent).toString().toLowerCase();
					
					Writer.writeEmptyFile("EVALUATION SETS/AUTHORITYNUTRITION/AUTHORITYNUTRITION SENTENCES/REST" +  "_" + i + "_.txt");
					Writer.overwriteFile(sentenceString, "EVALUATION SETS/AUTHORITYNUTRITION/AUTHORITYNUTRITION SENTENCES/REST" +  "_" + i + "_.txt");
				}	
			}
		}
	}
	
	
	

}
