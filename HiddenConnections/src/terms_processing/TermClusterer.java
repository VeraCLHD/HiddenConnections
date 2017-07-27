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
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.codehaus.plexus.util.StringUtils;

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
import terms_processing.StanfordLemmatizer;

public class TermClusterer {
	private static final String MESH_VARIANTS_ENRICHED_TXT = "mesh/meshVariants_enriched.txt";
	private static Map<String, String> categorizedTerms = new HashMap<String, String>();
	// key: meshId, value: list of all entry terms for id + term itself
	private static Map<String, Set<String>> meshTerms =  new HashMap<String, Set<String>>();
	// key: meshId, value: list of all tree structures
	private static Map<String, Set<String>> meshTrees =  new HashMap<String, Set<String>>();
	
	public static final String pathToAllTermsAfterWordNet = "terms/all_terms_and_variants_with10_filtered_clustered.txt";
	// this variable is for adding the lemma to the variants
	private static StanfordLemmatizer lemmatizer = new StanfordLemmatizer();
	
	private static final String MESH_DESC2017_ASCII = "mesh/d2017.bin";
	private static final String MESH_MESHTREES_ASCII = "mesh/mtrees2017.bin";
	// this file is actually secondary
	private static final String MESH_QUAL2017_ASCII = "mesh/q2017.bin";
	private static final String MESH_SUPP2017_ASCII = "mesh/c2017.bin";
	
	public static void main(String[] args) throws IOException{
		TermClusterer tc = new TermClusterer();
		tc.clusterTerms();    
    }

	public void clusterTerms() throws MalformedURLException, IOException {
		String[] files = {MESH_DESC2017_ASCII};
		Writer.overwriteFile("", pathToAllTermsAfterWordNet);
        IDictionary dict = prepareExtraction();
        
        readAllTerms();
        // is only needed once
        createEnrichedMeshDescFile();
        //createEnrichedMeshSuppFile();
        useMeshEntries();
        for(String t: categorizedTerms.keySet()){
        	 this.findLexNameForTerm(dict, t);
        	 this.categorizeTermWithMesh(t);
        	 this.finalCategorizationFoods(t);
        	 
		}
        
        for(Entry<String, String> t: categorizedTerms.entrySet()){
        	if(t.getValue().equals("INTERMEDIARY_CONCEPT")){
        		this.candidateContainsOtherTerms(t.getKey());
        	}
        	
        }
        writeAllTerms();
	}
	
	public static void useMeshEntries(){
		List<String> lines = Reader.readLinesList(MESH_VARIANTS_ENRICHED_TXT);
		for(String line: lines){
			if(!line.matches("\\s+") && !line.isEmpty()){
				Set<String> tree_set = new HashSet<String>();
				Set<String> variants_set = new HashSet<String>();
				
				String[] lineArray = line.split("\t");
				String term = lineArray[0];
				String id = lineArray[1];
				String trees = lineArray[2];
				String[] variants_a = {};
				
				if(lineArray.length>3){
					String variants = lineArray[3];
					variants_a = variants.split(",");
				}
				
				
				String[] trees_a = trees.split(",");
				
				
				for(String tree_entry: trees_a){
					if(!tree_entry.equals("")){
						tree_set.add(tree_entry.trim());	
					}
				}
				
				for(String variant: variants_a){
					if(!variants_set.equals("")){
						variants_set.add(variant.trim());
					}
						
				}
				
				variants_set.add(term);
				
			TermClusterer.getMeshTerms().put(id, variants_set);
			TermClusterer.getMeshTrees().put(id, tree_set);
				
			}
		}
	}
	/**
	 * This method takes care of finding the lexicographer's file name: noun.food, noun.plant
	 * @param dict
	 * @param term
	 */
	public void findLexNameForTerm(IDictionary dict, String term) {
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
                	
                		TermClusterer.getcategorizedTerms().put(term, "FOOD");
                }
            } 
        	 
         }

	}
	
	public void categorizeTermWithMesh(String term){
		if(TermClusterer.getcategorizedTerms().get(term).equals("INTERMEDIARY_CONCEPT")){
			Set<Entry<String, Set<String>>> set = TermClusterer.getMeshTerms().entrySet();
			for(Entry<String, Set<String>> entry: set){
				if(entry.getValue().contains(term)){
					String id = entry.getKey();
					Set<String> mesh_tree = TermClusterer.getMeshTrees().get(id);
					for(String tree_entry: mesh_tree){
						// C: all diseases, F03.: psychological disorders, B04: viruses
						if(tree_entry.startsWith("C") ||tree_entry.startsWith("F03.") || tree_entry.startsWith("B04.")){
							TermClusterer.getcategorizedTerms().put(term, "DISEASE");
							// B01.650: plants, J02 foods
						} else if(tree_entry.startsWith("B01.650") || tree_entry.startsWith("J02.")){
							TermClusterer.getcategorizedTerms().put(term, "FOOD");
						} else if(tree_entry.equals("-")){
							// supplementary concept files
							if(TermClusterer.isDisease(term)){
								TermClusterer.getcategorizedTerms().put(term, "DISEASE");
							}
						}
					}
					
				} else{
					if(TermClusterer.isDisease(term)){
						TermClusterer.getcategorizedTerms().put(term, "DISEASE");
					}
				}
			}	
		}	
	}
	
	public void finalCategorizationFoods(String term){
		if(term.contains(" ")){
			String[] termArray = term.split(" ");
			String lastWord = termArray[termArray.length-1];
			
			if(TermClusterer.getcategorizedTerms().get(term).equals("INTERMEDIARY_CONCEPT")){
				Set<Entry<String, Set<String>>> set = TermClusterer.getMeshTerms().entrySet();
				for(Entry<String, Set<String>> entry: set){
					if(entry.getValue().contains(lastWord)){
						String id = entry.getKey();
						Set<String> mesh_tree = TermClusterer.getMeshTrees().get(id);
						for(String tree_entry: mesh_tree){
							if(tree_entry.startsWith("B01.650") || tree_entry.startsWith("J02.")){
								TermClusterer.getcategorizedTerms().put(term, "FOOD");
							} 
						}
			
					}
					}
				}
			}
	}
	public static boolean isDisease(String term_variant){
		boolean result = false;
		String[] candidate_disease = {
				"disease", "diseases", "disorder", "disorders",
				"deficiency", "deficiencies", 
				"injury", "injuries", 
				"condition", "conditions",
				"cancer", "cancers",
				"dysfunction", "dysfunction"};
		for(String criterium: candidate_disease){
			if(term_variant.endsWith(criterium)){
				result = true;
			}
		}
		
		return result;
	}
	
	/**
	 * Prepares WordNet extraction.
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
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
	
	public static void readAllTerms(){
		List<String> lines = Reader.readLinesList(Bootstrapper.pathToAllTerms);
		for(String line: lines){
			if(!line.isEmpty() && !line.equals(" ")){
				String term = line.trim().toLowerCase();
				// the first step is to declare everything as intermediary concept.
				TermClusterer.categorizedTerms.put(term.trim(), "INTERMEDIARY_CONCEPT");
			}
		}
	}
	
	public static void writeAllTerms(){
		for(String t: categorizedTerms.keySet()){
			String lemma = "-";
			String lemmatized = lemmatizer.lemmatize(t);
			if(!t.contains(" ")){
				if(lemmatized !=null){
					lemma = lemmatized;
				}	
			}
			
			Writer.appendLineToFile(t + "\t" + lemma + "\t" + categorizedTerms.get(t), pathToAllTermsAfterWordNet);
			
			
		}
	}

	public static void createEnrichedMeshDescFile(){
		
		Writer.overwriteFile("", MESH_VARIANTS_ENRICHED_TXT);
		// here: read the ready MeshVariants file -> write also the tree number (MN) and search for id
		Set<String> tree_number = new HashSet<String>();
		String name = "";
		String id = "";
		Set<String> meshVariants = new HashSet<String>();
		

			List<String> lines = Reader.readLinesList(MESH_DESC2017_ASCII);
			for(String line: lines){
				if(line.equals("*NEWRECORD")){
					String newLine = "";
					if(name.contains(", ")){
						String[] arr = name.split(",");
						String first_word = arr[1].trim();
						String second_word = arr[0].trim();
						name = first_word + " " + second_word;
					}
					newLine += name +"\t";
					newLine += id +  "\t";
					
					for(String tn: tree_number){
						newLine += tn + ",";
					}
					newLine += "\t";
					
					for(String mv: meshVariants){
						newLine += mv + ",";
					}
					
					
					Writer.appendLineToFile(newLine, MESH_VARIANTS_ENRICHED_TXT);
					tree_number = new HashSet<String>();
					name = "";
					id = "";
					meshVariants = new HashSet<String>();
				} else if(line.startsWith("MH =")){
					String[] array = line.split("=");
					name = array[1].trim().toLowerCase();
				} else if(line.startsWith("MN =")){
					String[] array = line.split("=");
					tree_number.add(array[1].trim());
				} else if(line.startsWith("UI")){
					String[] array = line.split("=");
					id = array[1].trim();
				} else if(line.startsWith("ENTRY") || line.startsWith("PRINT ENTRY")){
					String[] array = line.split("=");
					String entry = array[1].trim().toLowerCase();
					String meshVariant = "";
					if(entry.contains("|")){
						meshVariant = entry.substring(0, entry.indexOf("|")).toLowerCase();
					} else{
						meshVariant = entry;
					}
					
					
					if(meshVariant.contains(", ")){
						String[] arr = meshVariant.split(",");
						String first_word = arr[1].trim();
						String second_word = arr[0].trim();
						meshVariant = first_word + " " + second_word;
					}
					
					meshVariants.add(meshVariant);
				}
			}
		}

public static void createEnrichedMeshSuppFile(){
		// no tree number in supplementary files
		String tree_number = "-";
		String name = "";
		String id = "";
		Set<String> meshVariants = new HashSet<String>();
		

			List<String> lines = Reader.readLinesList(MESH_SUPP2017_ASCII);
			for(String line: lines){
				if(line.equals("*NEWRECORD")){
					String newLine = "";
					if(name.contains(", ")){
						String[] arr = name.split(",");
						String first_word = arr[1].trim();
						String second_word = arr[0].trim();
						name = first_word + " " + second_word;
					}
					newLine += name +"\t";
					newLine += id +  "\t";
					newLine += tree_number + ",";
					
					newLine += "\t";
					
					for(String mv: meshVariants){
						newLine += mv + ",";
					}
					
					
					Writer.appendLineToFile(newLine, MESH_VARIANTS_ENRICHED_TXT);
					name = "";
					id = "";
					meshVariants = new HashSet<String>();
				} else if(line.startsWith("NM =")){
					String[] array = line.split("=");
					name = array[1].trim().toLowerCase();
				} else if(line.startsWith("SY") || line.startsWith("HM")){
					String[] array = line.split("=");
					String entry = array[1].trim().toLowerCase().replaceAll("\\*", "");
					String meshVariant = "";
					if(entry.contains("|")){
						meshVariant = entry.substring(0, entry.indexOf("|")).toLowerCase();
					} else{
						meshVariant = entry;
					}
					
					
					if(meshVariant.contains(", ")){
						String[] arr = meshVariant.split(",");
						String first_word = arr[1].trim();
						String second_word = arr[0].trim();
						meshVariant = first_word + " " + second_word;
					}
					
					meshVariants.add(meshVariant);
				} 
				else if(line.startsWith("UI")){
					String[] array = line.split("=");
					id = array[1].trim();
				} 
			}
		}
	
	/**
	 * if one term contains other diseases or foods, then it is a food itself
	 * @param candidate
	 * @return
	 */
	public void candidateContainsOtherTerms(String candidate){
		Set<String> set = categorizedTerms.keySet();
		for(String term: set){
			if(candidate.startsWith(term) || candidate.endsWith(term) 
					|| candidate.matches("(\\w+\\b)*" + Pattern.quote(term) + "(\\w+\\b)*")){
				String betterCategory = categorizedTerms.get(term);
				categorizedTerms.put(candidate, betterCategory);
				break;
			}
		}
	}

	
	public static Map<String, String> getcategorizedTerms() {
		return categorizedTerms;
	}

	public static Map<String, Set<String>> getMeshTerms() {
		return meshTerms;
	}

	public static Map<String, Set<String>> getMeshTrees() {
		return meshTrees;
	}

}
