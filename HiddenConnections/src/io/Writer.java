package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;


/**
 * Contains methods that write (in) files.
 * 
 * @author Vera Boteva, Demian Gholipour
 *
 */

public class Writer {
	private static final String NFDUMP_TXT = "nfdump.txt";
	/**
	 * Appends a line to a file.
	 */
	
	public static void appendLineToFile(String text, String filename) {
		File f = new File(filename);
		String s = "";
		if (f.exists()) {
			s += "\r\n";
		}
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, true), "UTF-8"))) {	
		    bw.write(s + text);
		    bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Writes an empty file.
	 */
	
	public static void writeEmptyFile(String filename) {
		File f = new File(filename);
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Write string in file, overwrite if file exists. 
	 */
	
	public static void overwriteFile(String content, String outputfile){
		try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputfile), "UTF-8"))){
			bw.write(content);
			bw.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Concatenates several files that are written in lines. 
	 * The last line of the resulting file is empty and gets deleted.
	 */
	
	public static void concatenateFiles(String[] filenames, String outfilename) {
		
		for (String filename: filenames) {
			File file = new File(filename);
			if(file.exists()){
				try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"))) {
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfilename, true), "UTF-8"));
					String line = new String();
					while (br.ready()) {
						line = br.readLine();
						bw.write(line);
						bw.write("\r\n");
					}
					bw.close();
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
			
		}
		Editor.deleteLastLine(outfilename);
	}
	
public static void rewriteDumpInSentences(){
		
		
		List<String> linesOfDump = Reader.readLinesList(NFDUMP_TXT);
		for (int i=0;i< linesOfDump.size();i++) {
			String line = linesOfDump.get(i);
			String[] elements = line.split("\t");
			String id = elements[0].trim();
			
			String processedDoc = elements[3];
			
			// inserts a whitespace after a sentence if there is none.
			processedDoc = processedDoc.replaceAll("([\\p{Lower}\\d\\\\p{Punct}][,.!?;:])" +
					 "(\\p{Upper})", "$1 $2").replaceAll("\\s+", " ");
			
			Document doc = new Document(processedDoc);
			List<Sentence> sentences = doc.sentences();
			

			for(int sent = 0; sent< sentences.size(); sent++){
				String sentenceString = sentences.get(sent).toString();
				String file = "Indexed_Corpus/" + id + "_" + sent + ".txt";
				
				Writer.appendLineToFile(sentenceString, file);
			}
			
		}
			
	}

	public static void main(String[] args) {
		Writer.rewriteDumpInSentences();
		
	}
}
