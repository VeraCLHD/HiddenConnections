package terms_processing;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bootstrapping.Bootstrapper;
import edu.mit.jwi.*;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ILexFile;
import edu.mit.jwi.item.ISenseKey;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;
import io.Reader;
import io.Writer;

public class TermClusterer {
	
	

	private static Map<String, String> termWordNet = new HashMap<String, String>();
	public static final String pathToAllTermsAfterWordNet = "terms/all_terms_and_variants_with10_filtered_WN.txt";
	
	public static void main(String[] args) throws IOException{
		Writer.overwriteFile("", pathToAllTermsAfterWordNet);
        IDictionary dict = prepareExtraction();
        TermClusterer tc = new TermClusterer();
        readAllTerms();
        for(String t: termWordNet.keySet()){
        	 tc.findLexNameForTerm(dict, t);
		}
        
        writeAllTerms();
       
        
    }

	private void findLexNameForTerm(IDictionary dict, String term) {
		 WordnetStemmer stem =  new WordnetStemmer(dict);
         Set<String> termAndLemma = new HashSet<String>();
         List<String> stems = stem.findStems(term, POS.NOUN);
         if(stems !=null){
         	termAndLemma.addAll(stems);
         }
         
         termAndLemma.add(term);
         for(String termOrLemma: termAndLemma){
     		IIndexWord idxWord = dict.getIndexWord(termOrLemma, POS.NOUN);
            if(idxWord !=null){
            	IWordID wordID = idxWord.getWordIDs().get(0);
                IWord word = dict.getWord(wordID);
                ISynset synset = word.getSynset();
                String LexFileName = synset.getLexicalFile().getName();
                
                if(LexFileName.equals("noun.food") || LexFileName.equals("noun.plant") || term.contains("food")){
                	
                		TermClusterer.getTermWordNet().put(term, "FOOD");
                }
            } 
        	 
         }

	}
	
	public static void readAllTerms(){
		List<String> lines = Reader.readLinesList(Bootstrapper.pathToAllTerms);
		for(String line: lines){
			if(!line.isEmpty() && !line.equals(" ")){
				String term = line.trim().toLowerCase();
				TermClusterer.termWordNet.put(term.trim(), "INTERMEDIARY_CONCEPT");
			}
		}
	}
	
	public static void writeAllTerms(){
		for(String t: termWordNet.keySet()){
			Writer.appendLineToFile(t + "\t" + termWordNet.get(t), pathToAllTermsAfterWordNet);
		}
	}

	private static IDictionary prepareExtraction() throws MalformedURLException, IOException {
		//construct URL to WordNet Dictionary directory on the computer
        String wordNetDirectory = "WordNet 2.1";
        String path = wordNetDirectory + File.separator + "dict";
        URL url = new URL("file", null, path);      

        //construct the Dictionary object and open it
        IDictionary dict = new Dictionary(url);
        dict.open();
		return dict;
	}
	
	public static Map<String, String> getTermWordNet() {
		return termWordNet;
	}

}
