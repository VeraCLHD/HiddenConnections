package distant_connections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.Reader;
import io.Writer;

public class RelationsTraversal {
	private static int run = 1;
	private static Set<Quadruple<String>> newlyEmerged = new HashSet<Quadruple<String>>();
	// a variable to check if there were newly emerged in the last method call (method for identification is recursive)
	private static int newlyEmergedCount = 0;
	private static String pathToInstances = "SEEDS/CONCATENATED/ALL_RELATIONS_FINAL - Copy.txt";
	
	// key:term lemma first term, value: string second + pattern + CAUSE;
	private Map<String, Set<String>> relations = new HashMap<String, Set<String>>();
	
	
	public void initialiseMapForTraversal(PreparationForIndirectConnections prep){
		Set<Quadruple<String>> quad = prep.getAllConnections();
		for(Quadruple<String> q: quad){
			String first = q.first;
			String second = q.second;
			//from now on we work only with lemmas but save the variants for a lemma;
			// in lemmatized we would find the variants for evaluation later
			if(!first.contains(" ")){
				String lemmatized = prep.getLemmatized().get(first);
				if(lemmatized !=null){
					first = lemmatized;
				}
			} 
			
			if(!second.contains(" ")){
				String lemmatizeds = prep.getLemmatized().get(second);
				if(lemmatizeds !=null){
					second = lemmatizeds;
				}
			}
			
			
			if(!first.equals(second)){
				Set<String> listConnectedForFirst = this.getRelations().get(first);
				
				if(listConnectedForFirst !=null){
					
					listConnectedForFirst.add(second + "\t" + q.third + "\t" + q.forth);
					this.getRelations().put(first, listConnectedForFirst);
					
					
					
				} else{
					Set<String> listForGen = new HashSet<String>();
					listForGen.add(second + "\t" + q.third + "\t" + q.forth);
					
					this.getRelations().put(first, listForGen);
				}
			}
			

		}
	}
	
	public void traverseRelations(PreparationForIndirectConnections prep){
		this.initialiseMapForTraversal(prep);
		System.out.println(this.getRelations());
		for(String first: this.getRelations().keySet()){
			String typeOfTerm = prep.getFoodDiseaseMapping().get(first);
			if(typeOfTerm != null && typeOfTerm.equals("DISEASE")){
				int countSteps = 0;
				//travelThroughRelationsRecursive(first, prep);
			}
			
			
		}

	}
	
	private void travelThroughRelationsRecursive(String term, PreparationForIndirectConnections prep, int countSteps) {
		Set<String> connected = this.getRelations().get(term);
		if(connected !=null && !connected.isEmpty()){
			Double f = freq + concrete.size();
			this.setFreq(f);
			for(String cterm: concrete){
				travelThroughRelationsRecursive(cterm, prep);
			}
		}
	
	}
	
	//____________________________________________________________________________
		// unnecessary duplicates arise: x-z, z-y -> x-y, y-x (not necessary when IS-A)
		public static void traverseAndFindHidden(Collection<Quadruple<String>> collection){
			RelationsTraversal.setNewlyEmergedCount(0);
			RelationsTraversal.run +=1; 
			List<Quadruple<String>> list1 = new ArrayList<Quadruple<String>>(collection);
			List<Quadruple<String>> list2 = new ArrayList<Quadruple<String>>(collection);
			
			for(int i= 0; i< list1.size(); i++){
				for(int j= i; j< list2.size(); j++){
					
				}
			}
			
				
					
					/*if(!list1.get(i).first.equals(list2.get(j).second)){
						if(list1.get(i).second.equals(list2.get(j).first)){
							//Quadruple<String> newPair = new Quadruple<String>(list1.get(i).first,list2.get(j).second);
							// the relation should go into one consistent direction
							if(!newPair.first.equals(newPair.second)){
								IndirectConnectionsFinder2.newlyEmerged.add(newPair);
								IndirectConnectionsFinder2.newlyEmergedCount +=1;
							}
							
						} /*else if(list1.get(i).first.equals(list2.get(j).second)){
							Pair<String> newPair = new Pair<String>(list1.get(i).second, list2.get(j).first);
							
							if(allConnectionsCopy.add(newPair)){
								IndirectConnectionsFinder.newlyEmerged.add(newPair);
								IndirectConnectionsFinder.newlyEmergedCount +=1;
							}
						}
					}
				}
			} 
			allConnections.addAll(allConnectionsCopy);
			if( IndirectConnectionsFinder2.run <= 2 && IndirectConnectionsFinder2.newlyEmergedCount > 0){
				traverseAndFindHidden(collection);
			}*/

		}
		
		/**
		 * A method to identify which relations are newly emerged.
		 */
		/*public static void filter(){
			newlyEmerged.remove(RelationsTraversal.getAllConnections());
			Set<Quadruple<String>> fin = newlyEmerged;
			for(Quadruple<String> pair: fin){
				Writer.appendLineToFile(pair.first + "\t" + pair.second, "evaluation/toEvaluate_ISA.txt");
			}
		}*/
		
		public static int getNewlyEmergedCount() {
			return newlyEmergedCount;
		}

		public static void setNewlyEmergedCount(int newlyEmergedCount) {
			RelationsTraversal.newlyEmergedCount = newlyEmergedCount;
		}
		
	public static void main(String[] args) {
		PreparationForIndirectConnections prep = new PreparationForIndirectConnections();
		//prep.rewriteResultsInSameDirection();
		
		// setzt voraus, dass information content file already there
		prep.readInformationContentFile();
		//Writer.overwriteFile("", "evaluation/toEvaluate_all.txt");
		prep.readAllConnections();
		
		prep.readClusteredTerms();
		RelationsTraversal rt = new RelationsTraversal();
		rt.traverseRelations( prep);

	}

	public Map<String, Set<String>> getRelations() {
		return relations;
	}

	public void setRelations(Map<String, Set<String>> relations) {
		this.relations = relations;
	}

}
