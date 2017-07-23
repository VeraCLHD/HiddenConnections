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
	private static final String FINAL_VARIANTS_TXT = "terms/finalVariants.txt";
	private static final String MESH_VARIANTS_TXT = "mesh/meshVariants.txt";
	private static String pathToClusteredTerms = "terms/all_terms_and_variants_with10_filtered_clustered.txt";
	// key: lemma, value: all variants, including lemma
	private static Map<String, Set<String>> variants = new HashMap<String, Set<String>>();
	private static Map<String, String> mapping = new HashMap<String, String>();
	private static Map<String, Set<String>> meshVariants = new HashMap<String, Set<String>>();
	
		
		// reads the clustered terms
		public void readClusteredTerms(){
			List<String> terms = Reader.readLinesList(pathToClusteredTerms);
			for(String termLine: terms){
				if(!termLine.isEmpty()){
					String[] splitted = termLine.split("\t");
					String term = splitted[0].trim();
					String lemma = splitted[1];
					if(lemma.equals("-")){
						lemma = term;
					}
					
					Map<String, Set<String>> variantsLemmas = TermAssembler.getVariants();
					if(variantsLemmas.keySet().contains(lemma)){
						Set<String> set = variantsLemmas.get(lemma);
						set.add(term);
						variantsLemmas.put(lemma, set);
					} else{
						Set<String> set = new HashSet<String>();
						set.add(lemma);
						variantsLemmas.put(lemma, set);
						
					}
					TermAssembler.getMapping().put(lemma, splitted[2]);
	
				}
			}
			
		}
		// read mesh variants
		public void readMesh(){
			List<String> variations = readFileLinewise(MESH_VARIANTS_TXT);
			for(String variation: variations){
				if( variation != null && !variation.equals("") && !variation.isEmpty()){
					// only if # the term has variations
					String[] oneEntry = variation.split("\t");
					if(oneEntry.length > 2){
						String term = oneEntry[0].trim();
						Set<String> meshVariations = new HashSet<String>(Arrays.asList(oneEntry[2].split(",")));
						// all morphological variations in this list
						
						TermAssembler.getMeshVariants().put(term, meshVariations);	
					}
				}
			
		}
		}
		
		//the main structure allTermsAndVariations gets mesh and cat here; each term gehts mesh
		public void addMeshVariationsToTerms(){
			// we use the lemma to check in mesh	
			for(String lemma: TermAssembler.getVariants().keySet()){
				if(TermAssembler.getMeshVariants().containsKey(lemma)){
						Set<String> list = TermAssembler.getMeshVariants().get(lemma);
						Set<String> newSet = TermAssembler.getVariants().get(lemma);
						newSet.addAll(list);
						TermAssembler.getVariants().put(lemma, newSet);

						
					}
					
					else{
						for(Entry<String,Set<String>> variation: TermAssembler.getMeshVariants().entrySet()){
							if(variation.getValue().contains(lemma)){
								Set<String> vars = new HashSet<String>();
								vars.add(variation.getKey());
								vars.addAll(variation.getValue());
								Set<String> newSet = TermAssembler.getVariants().get(lemma);
								newSet.addAll(vars);
								TermAssembler.getVariants().put(lemma, newSet);
							}
						}
					}
					
				}		
		}
		
		public static void writeFinalVariants(){
			Writer.overwriteFile("", FINAL_VARIANTS_TXT);
			for(String lemma: TermAssembler.getVariants().keySet()){
				String line = "";
				for(String variant: TermAssembler.getVariants().get(lemma)){
					line += variant + ",";
				}
				line +=  "\t" +TermAssembler.getMapping().get(lemma) ;
				Writer.appendLineToFile(line, FINAL_VARIANTS_TXT);
			}
		}
		
		public static List<String> readFileLinewise(String file){
			ArrayList<String> termsOverall = Reader.readLinesList(file);
			return termsOverall;
		}
		
		public static Map<String, Set<String>> getVariants() {
			return variants;
		}
		public static Map<String, String> getMapping() {
			return mapping;
		}
		
		public static Map<String, Set<String>> getMeshVariants() {
			return meshVariants;
		}
		private static void assembleFinalVariants() {
			TermAssembler ta = new TermAssembler();
			ta.readClusteredTerms();
			ta.readMesh();
			ta.addMeshVariationsToTerms();
			TermAssembler.writeFinalVariants();
		}
		
	public static void main(String[] args) {
		
		assembleFinalVariants();
		
	}


}
