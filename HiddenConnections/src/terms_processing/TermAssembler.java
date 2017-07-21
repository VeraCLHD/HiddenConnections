package terms_processing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import io.Reader;
import io.Writer;


public class TermAssembler {
	private static String pathToClusteredTerms = "terms/all_terms_and_variants_with10_filtered_clustered.txt";
	private static Map<String, Set<String>> variants = new HashMap<String, Set<String>>();
	
		// reads the clustered terms
		public void readClusteredTerms(){
			List<String> terms = Reader.readLinesList(pathToClusteredTerms);
			for(String termLine: terms){
				if(!termLine.isEmpty()){
					String[] splitted = termLine.split("\t");
					String term = splitted[0].trim();
					String lemma = splitted[1];
					if(!lemma.equals("-")){
						this.getFoodDiseaseMapping().put(lemma, splitted[2].trim());
					} else{
						this.getFoodDiseaseMapping().put(term, splitted[2].trim());
					}
					
					
				}
			}
			
		}
		// read mesh variants
		public void readMesh(){
			List<String> variations = readFileLinewise("meshVariants.txt");
			for(String variation: variations){
				if( variation != null && !variation.equals("") && !variation.isEmpty()){
					// only if # the term has variations
					String[] oneEntry = variation.split("\t");
					if(oneEntry.length > 2){
						String term = oneEntry[0].trim();
						Set<String> meshVariations = new HashSet<String>(Arrays.asList(oneEntry[2].split(",")));
						// all morphological variations in this list
						
							MeshVariator.getContentOfMeshFile().put(term, meshVariations);
						
						
					}
				}
			
		}
		}
		
		//the main structure allTermsAndVariations gets mesh and cat here; each term gehts mesh
		public void addMeshVariationsToTerms(Map<String, Set<String>> contentOfMeshFile){
			// mesh variations
			for(Term term : InitialRelationsManager.getTerms()){
				// we use the lemma to check in mesh
				String lemma = term.getLemma();
				
				if(contentOfMeshFile.containsKey(term.getOriginalTerm())){
					Set<String> list = contentOfMeshFile.get(term.getOriginalTerm());
					if(list !=null && !list.isEmpty()){
						term.getCatAndMesh().addAll(contentOfMeshFile.get(term.getOriginalTerm()));
						
					}
					
				}
				else if(!term.getOriginalTerm().contains(" ") && contentOfMeshFile.containsKey(lemma)){
					Set<String> list = contentOfMeshFile.get(lemma);
					if(list !=null && !list.isEmpty()){
						term.getCatAndMesh().addAll(contentOfMeshFile.get(term.getLemma()));
						InitialRelationsManager.allTermsAndVariations.add(term.getLemma());
					}
					
				} 
				
				else{
					for(Entry<String,Set<String>> variation: contentOfMeshFile.entrySet()){
						if(variation.getValue().contains(term.getOriginalTerm()) || (!term.getOriginalTerm().contains(" ") && variation.getValue().contains(lemma))){
							Set<String> vars = new HashSet<String>();
							vars.add(variation.getKey());
							vars.addAll(variation.getValue());
							term.getCatAndMesh().addAll(vars);
							break;
						}
					}
				}
				
				
				// this structure only for checking if a string contains them later
				InitialRelationsManager.allTermsAndVariations.addAll(term.getCatAndMesh());
				InitialRelationsManager.allTermsAndVariations.add(term.getOriginalTerm());
				
		
		}
		
		public static List<String> readFileLinewise(String file){
			ArrayList<String> termsOverall = Reader.readLinesList(file);
			return termsOverall;
		}

	public static void main(String[] args) {
		// 1. read clustered, 2. read mesh, combine (+ write), put in map
		// unabhängig: 2 lemmas -> get all variants; for each variant combination -> search in eval corpus

	}

}
