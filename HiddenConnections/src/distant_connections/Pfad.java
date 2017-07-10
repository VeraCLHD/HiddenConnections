package distant_connections;

import java.util.HashSet;
import java.util.Vector;


public class Pfad {
	public Vector<Knoten> knoten;
	public Vector<Kante> kanten;
	public Knoten start_knoten;
	public Knoten aktueller_knoten;
	public HashSet<Knoten> gesehene_knoten;
	public HashSet<String> gesehene_kantentypen;
	
	public Pfad(Knoten sk){
		this.aktueller_knoten = sk;
		this.start_knoten = sk;
		this.knoten = new Vector<Knoten>();
		this.kanten = new Vector<Kante>();
		this.gesehene_knoten = new HashSet<Knoten>();
		this.gesehene_knoten.add(sk);
		this.gesehene_kantentypen = new HashSet<String>();
	}
	
	public Pfad(Knoten start_knoten, Vector<Knoten> knoten, Vector<Kante> kanten, HashSet<Knoten> geseheneKnoten, HashSet<String> geseheneKanten){
		this.start_knoten = start_knoten;
		this.knoten=knoten;
		this.kanten=kanten;
		this.gesehene_knoten=geseheneKnoten;
		this.gesehene_kantentypen=geseheneKanten;
		this.aktueller_knoten = knoten.lastElement();
	}
	
	public String toString(){
		String result_string = this.start_knoten.name;
		if (knoten.size()==0) return result_string;
		for (int i=0; i<knoten.size(); i++){
			result_string += " " + kanten.get(i).type + " "+ knoten.get(i).name;
		}
		return result_string;
	}
	
	public Pfad expandPfad(Kante kant, Knoten knot){
		Vector<Knoten> new_knoten = new Vector<Knoten>();
		for (int i=0; i<this.knoten.size(); i++){
			new_knoten.add(this.knoten.get(i));
		}
		new_knoten.add(knot);
		Vector<Kante> new_kanten = new Vector<Kante>();
		for (int i=0; i<this.kanten.size(); i++){
			new_kanten.add(this.kanten.get(i));
		}
		new_kanten.add(kant);
		HashSet<Knoten> new_gesehene_knoten = new HashSet<Knoten>();
		new_gesehene_knoten.addAll(this.gesehene_knoten);
		new_gesehene_knoten.add(knot);
		HashSet<String> new_gesehene_kantentypen = new HashSet<String>();
		new_gesehene_kantentypen.addAll(this.gesehene_kantentypen);
		new_gesehene_kantentypen.add(kant.type);
		return new Pfad(this.start_knoten, new_knoten, new_kanten, new_gesehene_knoten, new_gesehene_kantentypen);
	}
}

