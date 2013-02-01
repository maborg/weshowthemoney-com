package com.weshowthemoney.data.consob;
/**
 * Legge il file TXT derivato (via u32) HTML della
 *  consob contenente la lista
 * dei manager  delle società quotate in borsa
 * 
 * 
 * @author marco borgna
 * 5 gennaio 2007
 *
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class OrganiReader {

	public class Carica {
		String Titolo;
		Azienda azienda = null;
	}

	TreeSet<Azienda> aziende = new TreeSet<Azienda>(Azienda.getcomp());
	TreeSet<Azienda> aziendewalked = new TreeSet<Azienda>(Azienda.getcomp());

	SortedMap<Direttore, Direttore> direttori = new TreeMap<Direttore, Direttore>();
	public ArrayList<Direttore> direttoriSorted;
	protected FileWriter filewriter;

	void readFile(String file) throws IOException {
		BufferedReader f = new BufferedReader(new FileReader(file));
		Azienda azienda = null;
		String line = f.readLine();
		while (line != null) {
			if (line.startsWith("*Organi Sociali")) {
				azienda = new Azienda();
				azienda.azienda = line.substring("*Organi Sociali".length())
				.trim();

				azienda.azienda = azienda.azienda.substring(0,azienda.azienda.length()-1);
				aziende.add(azienda);

			} else if (line.contains("\t") && !line.contains("Carica")) {
				String[] linesplits = line.trim().split("\\t");
				if (linesplits.length == 2) {
					Direttore d = new Direttore();
					d.nome = linesplits[1].trim();
					if (direttori.containsKey(d)) {
						d = direttori.get(d);
						Carica c = new Carica();
						c.azienda = azienda;
						c.Titolo = linesplits[0].trim();
						d.cariche.add(c);
					} else {
						Carica c = new Carica();
						c.azienda = azienda;
						c.Titolo = linesplits[0].trim();
						d.cariche.add(c);
						direttori.put(d, d);

					}
				}
			}

			line = f.readLine();
		}

		return;
	}
	protected void writeFile(String fileOUT) throws IOException {
	
		internalWriteFile(fileOUT);
		filewriter.close();
	}
	
	protected void internalWriteFile(String fileOUT) throws IOException {
		filewriter = new FileWriter(fileOUT);

		// calculate azienda weight
		for (Direttore dir : direttori.keySet()) {
			//ignore not linkd director 
			if (dir.cariche.size() > 1)
				for (Carica car : dir.cariche) {
					car.azienda.weight += dir.cariche.size();
					//add dir to his azienda
					car.azienda.direttori.add(dir);
				}
		}
		ArrayList<Azienda> aziendeSorted = new ArrayList<Azienda>();

		Azienda dummy[] = new Azienda[1];
		Collections.addAll(aziendeSorted, aziende.toArray(dummy));
		Collections.sort(aziendeSorted, Azienda.getcomp());

		//clac direttori weight
		for (Azienda az : aziendeSorted) {

			if (az.weight>0){
				for (Direttore dir1 : az.direttori) {
					if (dir1.cariche.size() > 1){
						dir1.weight  += az.weight;
					}
				}
			}
		}

		direttoriSorted = new ArrayList<Direttore>();
		Collections.addAll(direttoriSorted, direttori.keySet().toArray(new Direttore[1]));
		Collections.sort(direttoriSorted, Direttore.getcompweight());

		int i = 0;
		// write azienda node
		for (Azienda az : aziendeSorted) {
			az.ID = i++;
			if (az.weight>0){
				String node = String.format("<node id=\"%s\">\n"
						+ " <data key=\"name\">%s</data>\n"
						+ " <data key=\"weight\">%s</data>\n"  
						+ " <data key=\"type\">SOC</data>\n"
						+ "</node>\n", 
						az.ID,
						az.azienda.replace("&", "E"),
						az.weight);
				filewriter.write(node);
			}
		}
		//write direttori node
		boolean wholemap = true;
		if (wholemap)
			for (Direttore dir : direttoriSorted) {
				dir.ID = i++;
				if (dir.cariche.size() > 1) {
					String node = String.format("<node id=\"%s\"  >\n"
							+ " <data key=\"name\">%s</data>\n"
							+ " <data key=\"weight\">%s</data>\n"  
							+ " <data key=\"type\">DIR</data>\n" 
							+ " </node>\n",
							dir.ID, dir.nome,dir.weight);
					filewriter.write(node);
				}
			}

		if (wholemap) {
			// write links
			for (Direttore dir1 : direttori.keySet()) {
				if (dir1.cariche.size() > 1){
					for (Carica car : dir1.cariche) {
						String edge = String.format(
								"<edge source=\"%s\" target=\"%s\"  />\n",
								dir1.ID, car.azienda.ID
						);
						filewriter.write(edge);

					}
				}
			}
		}
		else{
			for (Azienda azienda2 : aziendeSorted) {
				if (azienda2.weight>0)
					for (Direttore dir3 : azienda2.direttori) {
						if(dir3.cariche.size()>1){
//							String edge = String.format(
//							"<edge source=\"%s\" target=\"%s\" />\n", dir3.ID,
//							azienda2.ID);
//							fw.write(edge);

							for (Carica carica : dir3.cariche) {

								String edge = String.format(
										"<edge source=\"%s\" target=\"%s\" />\n",
										azienda2.ID, carica.azienda.ID);
								filewriter.write(edge);


							}
						}
					}
			}
		}



		
	}

	public static void main(String[] args) throws IOException {
		OrganiReader o = new OrganiReader();
		
		o.readFile(args[0]);
		o.writeFile(args[1]);
		
	}

	static class Azienda implements Comparable<Azienda> {
		public TreeSet<Direttore> direttori = new TreeSet<Direttore>(Direttore.getcomp());

		String azienda = null;

		int ID = -1;

		public int weight = 0;

		public int compareTo(Azienda a1) {

			int diff = weight - a1.weight;
			if (diff != 0)
				return diff;
			else
				return a1.azienda.compareToIgnoreCase(azienda);
		}
		static Comparator<Azienda> getalfacomp() {
			return new Comparator<Azienda>() {

				public int compare(Azienda a1, Azienda a2) {
						return a1.azienda.compareToIgnoreCase(a2.azienda);
				}

			};
		}

		static Comparator<Azienda> getcomp() {
			return new Comparator<Azienda>() {

				public int compare(Azienda a1, Azienda a2) {
					int diff = a2.weight - a1.weight;
					if (diff != 0)
						return diff;
					else
						return a1.azienda.compareToIgnoreCase(a2.azienda);
				}

			};
		}
		@Override
		public boolean equals(Object obj) {
			
			return azienda.equalsIgnoreCase(((Azienda)obj).azienda);
		}
	}

	static class Direttore implements Comparable<Direttore> {
		public int weight = 0;

		String nome = null;

		ArrayList<Carica> cariche = new ArrayList<Carica>();

		public int ID;

		static Comparator<Direttore> getcomp() {
			return new Comparator<Direttore>() {

				public int compare(Direttore d1, Direttore d2) {
					int diff = d2.cariche.size() - d1.cariche.size();
					if (diff != 0)
						return diff;
					else
						return d1.nome.compareToIgnoreCase(d2.nome);
				}

			};
		}
		static Comparator<Direttore> getalfacomp() {
			return new Comparator<Direttore>() {

				public int compare(Direttore d1, Direttore d2) {
						return d1.nome.compareToIgnoreCase(d2.nome);
				}

			};
		}
		static Comparator<Direttore> getcompweight() {
			return new Comparator<Direttore>() {

				public int compare(Direttore d1, Direttore d2) {
					int diff = d2.weight - d1.weight;
					if (diff != 0)
						return diff;
					else
						return d1.nome.compareToIgnoreCase(d2.nome);
				}

			};
		}
		public int compareTo(Direttore d1) {

			return d1.nome.compareToIgnoreCase(nome);
		}
		public boolean equals(Object obj) {
			
			return nome.equalsIgnoreCase(((Direttore)obj).nome);
		}
	}
}
