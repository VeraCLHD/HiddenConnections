package distant_connections;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import io.Writer;

public class Knoten {
	private static final String OUTPUT_OF_PROJECT = "distant connections/FINAL.txt";
	private static String pathToClusteredTerms = "terms/all_terms_and_variants_with10_filtered_clustered.txt";
	private static final String DISTANT_CONNECTIONS_FINAL_INPUT = "distant connections/ALL_RELATIONS_WITH_RELEVANT_INFO.txt";
	String name;
	String type;
	HashMap<Kante,Knoten> neighbours = new HashMap<Kante,Knoten>();
	
	static HashSet<Knoten> food = new HashSet<Knoten>();
	static HashMap<String,Knoten> knoten = new HashMap<String,Knoten>();
	
	public Knoten(String name, String type){
		this.name = name;
		this.type = type;
		knoten.put(name, this);
		if (type=="FOOD"){
			food.add(this);
		}
	}
	
	public static Knoten getKnotenforName(String name, String type){
		if (knoten.containsKey(name)){
			return knoten.get(name);
		}
		Knoten k = new Knoten(name, type);
		knoten.put(name, k);
		return k;
	}
	
	public void setNeighbour(String kantentyp, Knoten knoten){
		Kante kante = new Kante(kantentyp);
		neighbours.put(kante, knoten);
		// ungerichtet: knoten.neighbours.put(kante, this);
	}
	
	public ArrayList<String> BFS(int depth){
		HashSet<Pfad> pfade = new HashSet<Pfad>();
		HashSet<Pfad> temp_pfade = new HashSet<Pfad>();
		HashMap<Kante,Knoten> aktuelle_nachbarn;
		ArrayList<String> results = new ArrayList<String>();
		pfade.add(new Pfad(this));
		for (int i = 0; i<depth; i++){
			for (Pfad p: pfade){
				aktuelle_nachbarn = p.aktueller_knoten.neighbours;
				for (Kante nachbar_kante: aktuelle_nachbarn.keySet()){
					if (!(p.gesehene_kantentypen.contains(nachbar_kante.type))&&!(p.gesehene_knoten.contains(aktuelle_nachbarn.get(nachbar_kante)))&&!(aktuelle_nachbarn.get(nachbar_kante).type.equals("FOOD"))){
						if (aktuelle_nachbarn.get(nachbar_kante).type=="DISEASE"){
							//Zielpfad gefunden
							Pfad neuer_pfad = p.expandPfad(nachbar_kante, aktuelle_nachbarn.get(nachbar_kante));
							results.add(neuer_pfad.toString());
						}
						else{
							//gueltigen Pfad gefunden
							Pfad neuer_pfad = p.expandPfad(nachbar_kante, aktuelle_nachbarn.get(nachbar_kante));
							temp_pfade.add(neuer_pfad);
						}
					}
				}
			}
			pfade.clear();
			pfade.addAll(temp_pfade);
			temp_pfade.clear();
		}
		return results;
	}
	
	public static ArrayList<String> getAllPaths(int depth){
		ArrayList<String> results = new ArrayList<String>();
		for (Knoten k: food){
			results.addAll(k.BFS(depth));
		}
		return results;
	}
	
	public static void createGraph(File primaries, File relations) throws IOException{
		BufferedReader br;
		br = new BufferedReader(new FileReader(primaries));
		String line;
		String[] split_line;
		
		while ((line=br.readLine())!=null){
			split_line = line.split("\t");
			if (split_line.length==3){
				
				String lemma = split_line[1];
				String term = split_line[0];
				if(lemma.equals("-")){
					lemma = term;
				}
				if (split_line[2].equals("DISEASE")){
					Knoten k = new Knoten(lemma, "DISEASE");
				}
				if (split_line[2].equals("FOOD")){
					Knoten k = new Knoten(lemma, "FOOD");
				}
			}
		}
		br.close();
		br = new BufferedReader(new FileReader(relations));
		while ((line=br.readLine())!=null){
			split_line = line.split("\t");
			if (split_line.length==8){
				Knoten a = getKnotenforName(split_line[1], "else");
				Knoten b = getKnotenforName(split_line[4], "else");
				a.setNeighbour(split_line[7], b);
			}
		}
		br.close();
	}
	
	public static void main(String[] args) throws IOException{
		//beispiel graph:
//		Knoten a = new Knoten("A", "FOOD");
//		Knoten b = new Knoten("B", "1");
//		Knoten c = new Knoten("C", "2");
//		Knoten d = new Knoten("D", "DISEASE");
//		Knoten e = new Knoten("E", "DISEASE");
//		Knoten f = new Knoten("F", "FOOD");
//		Knoten g = new Knoten("G", "3");
//		Knoten h = new Knoten("H", "DISEASE");
//		a.setNeighbour("x", b);
//		b.setNeighbour("y", c);
//		c.setNeighbour("z", d);
//		c.setNeighbour("y", e);
//		c.setNeighbour("xx",f);
//		c.setNeighbour("xy", g);
//		g.setNeighbour("xz", h);		
//		System.out.println(getAllPaths(3));
		
		createGraph(new File(pathToClusteredTerms), new File(DISTANT_CONNECTIONS_FINAL_INPUT));
		ArrayList<String> all_paths = getAllPaths(4);
		Set<String> results = new HashSet<String>();
		Writer.overwriteFile("", OUTPUT_OF_PROJECT);
		for (String s: all_paths){
			results.add(s);
		}
		
		for(String result: results){
			if(result.split("\t").length == 3){
				continue;
			}
			Writer.appendLineToFile(result, OUTPUT_OF_PROJECT);
		}

	}
}
