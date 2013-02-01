package com.weshowthemoney.data.consob;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import com.weshowthemoney.data.consob.AzioniReader.Azionista;
import com.weshowthemoney.data.consob.AzioniReader.DatiAzionista;
import com.weshowthemoney.data.consob.AzioniReader.EndFileException;
import com.weshowthemoney.data.consob.AzioniReader.Societa;

public class FullFileWriter extends OrganiReader {

	final static String start = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+	
	"<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\">\n"+
	"<graph edgedefault=\"directed\">\n\n"+
	"<key id=\"name\" for=\"node\" attr.name=\"name\" attr.type=\"string\"/>\n"+
	"<key id=\"type\" for=\"node\" attr.name=\"type\" attr.type=\"string\"/>\n\n"+
	"<key id=\"weight\" for=\"node\" attr.name=\"weight\" attr.type=\"string\"/>\n\n"+
	"<key id=\"icon\" for=\"node\" attr.name=\"icon\" attr.type=\"string\"/>\n\n"+
	"<key id=\"perc\" for=\"edge\" attr.name=\"perc\" attr.type=\"string\"/>\n\n";
	final static String end = "\n</graph>\n</graphml>\n";
	
	
	private static AzioniReader azionireader;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		azionireader = new AzioniReader();
		try {
			azionireader.readFile(new File(args[0]));
		} catch (EndFileException e) {
			// safely ignored
		}
		FullFileWriter fr = new FullFileWriter();
		fr.readFile(args[1]);

		fr.writeFile(args[2]);
	}

	@Override
	protected void writeFile(String fileOUT) throws IOException {
		
		filewriter = new FileWriter(fileOUT);
		filewriter.write(start);

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
		Collections.sort(aziendeSorted, Azienda.getalfacomp());

		//calc direttori weight
		for (Azienda az : aziendeSorted) {
			if (az.weight>0){
				for (Direttore dir1 : az.direttori) {
					if (dir1.cariche.size() > 1){
						dir1.weight  += az.weight;
					}
				}
			}
		}

		// Sort admin by weight
		direttoriSorted = new ArrayList<Direttore>();
		Collections.addAll(direttoriSorted, direttori.keySet().toArray(new Direttore[1]));
		Collections.sort(direttoriSorted, Direttore.getalfacomp());

		int i = 0;
		// write every azienda node
		for (Azienda az : aziendeSorted) {
			az.ID = i++;
			Azionista a = azionireader.new Azionista();
			a.name = az.azienda.replace("&", "E");
			String type;
			if (azionireader.azionisti.containsKey(a))
				type="SOC,AZ";
			else
				type="SOC";
			//if (az.weight>0){ // <- commented out 20070208 (no more sel on corporation)
			String node = String.format("<node id=\"%s\">\n"
					+ " <data key=\"name\">%s</data>\n"
					+ " <data key=\"weight\">%s</data>\n"  
					+ " <data key=\"type\">"+type+"</data>\n"
					+ "</node>\n", 
					az.ID,
					az.azienda.replace("&", "E"),
					az.weight);
			filewriter.write(node);
			//}
		}
		//write direttori node
		boolean wholemap = true;
		if (wholemap)
			for (Direttore dir : direttoriSorted) {
				dir.ID = i++;
				Azionista a = azionireader.new Azionista();
				a.name = dir.nome;
				String type;
				if (azionireader.azionisti.containsKey(a))
					type="AZ,DIR";
				else
					type="DIR";

				if (dir.cariche.size() > 0) { // dummy check (can be used to get smaller map)
					String node = String.format("<node id=\"%s\"  >\n"
							+ " <data key=\"name\">%s</data>\n"
							+ " <data key=\"weight\">%s</data>\n"  
							+ " <data key=\"type\">"+type+"</data>\n" 
							+ " <data key=\"icon\">resources/manager.gif</data>\n" 

							+ " </node>\n",
							dir.ID, dir.nome,dir.weight);
					filewriter.write(node);
				}
			}

		if (wholemap) {
			// write links
			for (Direttore dir1 : direttori.keySet()) {
				if (dir1.cariche.size() > 0){ 
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
			// this branch was used to get a lighter graph
			for (Azienda azienda2 : aziendeSorted) {
				if (azienda2.weight>0)
					for (Direttore dir3 : azienda2.direttori) {
						if(dir3.cariche.size()>0){
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

		for (Azionista az : azionireader.azionisti.keySet()) {
			az.ID = i++;
			String node = null;
			if (!az.quotata){
				// maybe it's also a director
				Direttore dir = new Direttore();
				dir.nome = az.name;
				int index  =direttoriSorted.indexOf(dir);
				if (index>=0)
				{
					az.ID = direttoriSorted.get(index).ID;
					continue;
				}
				
				node = String.format("<node id=\"%s\">\n"
						+ " <data key=\"name\">%s</data>\n"
						+ " <data key=\"type\">AZ</data>\n"
						+ "</node>\n", 
						az.ID,
						az.name.replace("&", "E"));
						filewriter.write(node);

			}
			else
			{
				//calculate new index 
				Azienda azienda = new Azienda();
				azienda.azienda = az.name;
				int index  =aziendeSorted.indexOf(azienda);
				if (index<0)
				{
					System.err.println("Quoted not found! name:" + az.name);
					continue;
				}
				az.ID = aziendeSorted.get(index).ID;
			}

			
		}
		for (Azienda s : aziendeSorted) {
			Societa soc = azionireader.new Societa();
			
			soc.name = s.azienda; 
			//azionireader.azionisti.keySet() PIRELLI & C. SPA //PIRELLI & C. SPA
			int index = azionireader.societaList.indexOf(soc);
			if (index==-1) continue;
			Societa s1 = azionireader.societaList.get(index);
			
			for (DatiAzionista daz : s1.datiAzionisti) {
				String edge = String.format(
						"<edge source=\"%s\" target=\"%s\" >\n"
						+ " <data key=\"perc\">%s</data>\n"
						+ "</edge>\n",
						daz.az.ID,s.ID,daz.perc
				);
				filewriter.write(edge);

			}
		}

		filewriter.write(end);
		
		filewriter.close();

	
	}

}
