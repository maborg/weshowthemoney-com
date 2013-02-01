package com.weshowthemoney.data.consob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
/**
 * Legge il file HTML della consob contenente la lista
 * dei principali azionisti delle società quotate in borsa
 * 
 * 
 * @author marco borgna
 * 5 gennaio 2007
 *
 */

public class AzioniReader {
	@SuppressWarnings("serial")
	public class EndFileException extends Exception {

	}
	final static String start = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+	
	"<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\">\n"+
	"<graph edgedefault=\"directed\">\n\n"+
	"<key id=\"name\" for=\"node\" attr.name=\"name\" attr.type=\"string\"/>\n"+
	"<key id=\"type\" for=\"node\" attr.name=\"type\" attr.type=\"string\"/>\n\n"+
	"<key id=\"perc\" for=\"edge\" attr.name=\"perc\" attr.type=\"string\"/>\n\n";
	final static String end = "\n</graph>\n</graphml>\n";
	
	SortedMap<Azionista, Azionista> azionisti = new TreeMap<Azionista, Azionista>();
	ArrayList<Societa> societaList = new ArrayList<Societa>();

	class Societa{
		long ID;
		String name;
		ArrayList<DatiAzionista> datiAzionisti = new ArrayList<DatiAzionista>();
		@Override
		public boolean equals(Object arg0) {
			
			return name.equalsIgnoreCase(((Societa)arg0).name);
		}
		
	}
	class Azionista implements Comparable<Azionista>{
		long ID;
		String name;
		
		boolean quotata=false;
		public int compareTo(Azionista o) {

			return name.compareTo(o.name);
		}
		@Override
		public String toString() {
			
			return name;
		}
	}
	class DatiAzionista {
		String perc = "0.0";
		Azionista az = null;
	}

	public static void main(String[] args) throws IOException {
		AzioniReader ar = new AzioniReader();
		try {
			ar.readFile(new File(args[0]));
		} catch (EndFileException e) {

		}
		ar.writeFile(new File(args[1]));
	}

	protected void writeFile(File file) throws IOException {
		FileWriter fw = new FileWriter(file);
		int i  =0 ;


		fw.write(start);
		for (Azionista az : azionisti.keySet()) {
			az.ID = i++;
			String node = null;
			if (!az.quotata)
				node = String.format("<node id=\"%s\">\n"
						+ " <data key=\"name\">%s</data>\n"
						+ " <data key=\"type\">AZ</data>\n"
						+ "</node>\n", 
						az.ID,
						az.name//.replace("&", "E")
				);
			else
				node = String.format("<node id=\"%s\">\n"
						+ " <data key=\"name\">%s</data>\n"
						+ " <data key=\"type\">SOC,AZ</data>\n"
						+ "</node>\n", 
						az.ID,
						az.name//.replace("&", "E")
				);

			fw.write(node);
		}
		for (Societa s : societaList) {
			Azionista azSoc = new Azionista();
			azSoc.name = s.name; 
			azSoc = azionisti.get(azSoc);
			for (DatiAzionista daz : s.datiAzionisti) {
				String edge = String.format(
						"<edge source=\"%s\" target=\"%s\" >\n"
						+ " <data key=\"perc\">%s</data>\n"
						+ "</edge>\n",
						daz.az.ID,azSoc.ID,daz.perc
				);
				fw.write(edge);

			}
		}
		fw.write(end);
		fw.close();
	}


	Integer ID;
	private Integer rowspan;

	void readFile(File file) throws IOException, EndFileException {
		BufferedReader f = new BufferedReader(new FileReader(file));
		String line = readLine(f);

		int linec = 0;

		while (line != null) {
			if (line.contains("Azionisti rilevanti di "))
			{
				linec=0;
				//System.out.println(line);
				// recupera nome società quotata
				int start = line.indexOf("Azionisti rilevanti di ") + "Azionisti rilevanti di ".length();
				int end = line.indexOf("</strong>");	
				String societa = cleanName(line.substring(start, end));

				System.out.println("\n----------------\n"+societa);
				Societa s = new Societa();
				s.name = societa;
				societaList.add(s);
				// le soc sono anche azionisti
				Azionista az = new Azionista();
				az.name = s.name;
				az.quotata = true;
				if (azionisti.containsKey(az)) {
					az = azionisti.get(az);

				} else {

					azionisti.put(az, az);
				}


				// consuma i tr del titolo
				eatTR(f);
				eatTR(f);
				eatTR(f);
				eatTR(f);
				eatTR(f);
				eatTR(f);
				// ripet nome
				line = readLine(f);
				// 
				// legge righe significative
				readAzionisti(f,s);				
			}
			linec++;
			//System.out.println(""+ linec + line);
			line = readLine(f);
		}

	}

	// legge le righe utili (ora solo azionista e totale)
	private boolean readAzionisti(BufferedReader f, Societa s) throws IOException, EndFileException {

		do
		{
			String azionista = eatTD(f) ;
			if (azionista==null)break;
			azionista = cleanName(azionista);
			Azionista az = new Azionista();
			az.name = azionista;


			System.out.println(azionista);
			Integer r = rowspan;
			for (int i = 0; i < r-1; i++) {
				if (!eatTR(f))break;
			}
			eatTD(f);
			String totale = eatTD(f) ;
			System.out.println("["+totale+"]");

			if (azionisti.containsKey(az)) {
				az = azionisti.get(az);
				DatiAzionista daz = new DatiAzionista();
				daz.az = az;
				daz.perc = totale;
				s.datiAzionisti.add(daz);
			} else {
				DatiAzionista daz = new DatiAzionista();
				daz.az = az;
				daz.perc = totale;
				s.datiAzionisti.add(daz);
				azionisti.put(az, az);
			}

		}while (eatTR(f));

		return false;
	}

	//pulisce i nome eliminando anche i commenti
	private String cleanName(String azionista) {
		return azionista.replace("&#160;", "'").replace("&#39;", "'").replaceAll("\\(([^\\)]*)\\)", "").replace("&amp;", "&").trim();
	}
	// legge una riga TD e sputa fuori solo il testo
	private String eatTD(BufferedReader f) throws IOException, EndFileException {
		//System.out.println("AzioniReader.eatTD()");
		String line = "";
		do{
			line = line + readLine(f);

			if (line.contains("rowspan")){
				int start = line.indexOf("rowspan=\"", 0)+"rowspan=\"".length();
				int end   = line.indexOf("\"", start) ;
				rowspan = Integer.parseInt(line.substring(start, end));
				
			}
			if (line.contains("</table>")){
				return null;
			}
			//System.out.println("eatTD " + line);
		}while ( !line.contains("</td>") );
		line = line.replaceAll("<([^>]*)>", "").trim();
		return line;
	}
	//	 legge fino a un /tr
	private boolean eatTR(BufferedReader f) throws IOException, EndFileException {
		//System.out.println("AzioniReader.eatTR()");
		String line;
		do{
			line = readLine(f);


			if (line.contains("</table>"))
			{
				//System.out.println("table " + line);
				return false;
			}
		}while ( !line.contains("</tr>") );
		return true;
	}


	private String readLine(BufferedReader f) throws IOException, EndFileException {
		String line;
		line = f.readLine();
		//System.out.println(line);
		if (line==null) throw new EndFileException();
		return line;
	}

}
