package com.weshowthemoney.data;

import java.io.FileWriter;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WriteCandidateTopDonors extends Downloader {
	SortedMap<String,SortedMap<String,String>> topContributorList = new TreeMap<String,SortedMap<String,String>>();
	// money 
	SortedMap<String,String> stateCoordMap = new TreeMap<String,String>();

	//SortedMap<String,String> contributorCandidatesList = new TreeMap<String,String>();
	String[] stateCoord = {                          
			"AK","61.3850","-152.2683",
			"AL","32.7990","-86.8073", 
			"AR","34.9513","-92.3809", 
			"AS","14.2417","-170.7197",
			"AZ","33.7712","-111.3877",
			"CA","36.1700","-119.7462",
			"CO","39.0646","-105.3272",
			"CT","41.5834","-72.7622", 
			"DC","38.8964","-77.0262", 
			"DE","39.3498","-75.5148", 
			"FL","27.8333","-81.7170", 
			"GA","32.9866","-83.6487", 
			"HI","21.1098","-157.5311",
			"IA","42.0046","-93.2140 ",
			"ID","44.2394","-114.5103",
			"IL","40.3363","-89.0022 ",
			"IN","39.8647","-86.2604 ",
			"KS","38.5111","-96.8005 ",
			"KY","37.6690","-84.6514 ",
			"LA","31.1801","-91.8749 ",
			"MA","42.2373","-71.5314 ",
			"MD","39.0724","-76.7902 ",
			"ME","44.6074","-69.3977 ",
			"MI","43.3504","-84.5603 ",
			"MN","45.7326","-93.9196 ",
			"MO","38.4623","-92.3020 ",
			"MP","14.8058","145.5505 ",
			"MS","32.7673","-89.6812 ",
			"MT","46.9048","-110.3261",
			"NC","35.6411","-79.8431 ",
			"ND","47.5362","-99.7930 ",
			"NE","41.1289","-98.2883 ",
			"NH","43.4108","-71.5653 ",
			"NJ","40.3140","-74.5089 ",
			"NM","34.8375","-106.2371",
			"NV","38.4199","-117.1219",
			"NY","42.1497","-74.9384 ",
			"OH","40.3736","-82.7755 ",
			"OK","35.5376","-96.9247 ",
			"OR","44.5672","-122.1269",
			"PA","40.5773","-77.2640 ",
			"PR","18.2766","-66.3350 ",
			"RI","41.6772","-71.5101 ",
			"SC","33.8191","-80.9066 ",
			"SD","44.2853","-99.4632 ",
			"TN","35.7449","-86.7489 ",
			"TX","31.1060","-97.6475 ",
			"UT","40.1135","-111.8535",
			"VA","37.7680","-78.2057 ",
			"VI","18.0001","-64.8199 ",
			"VT","44.0407","-72.7093",			
			"WA","47.3917","-121.5708",
			"WI","44.2563","-89.6385 ",
			"WV","38.4680","-80.9696 ",
			"WY","42.7475","-107.2085" 
			};

	private static final String TOPDONORS = "http://www.followthemoney.org/api/candidates.top_contributors.php?key=a23d12bb290fd8492e6a7631ae29e372&imsp_candidate_id="; 

	public WriteCandidateTopDonors() {
		super();

	
	}

	public static void main(String[] args) {
		//test only 
		new WriteCandidateTopDonors().downloadTopDonors("415471");
	}
	
	
	private String donorsurl = "" ;
	private String _candidate = null;
	protected synchronized void downloadTopDonors(String candidate){
		donorsurl = TOPDONORS + candidate;
		_candidate = candidate ;
		start();
	}
	
	@Override
	public String getDowloadURL() {
		
		return donorsurl;
	}

	
	
	@Override
	protected  DefaultHandler getMe() {
		return this;
	}
	
	@Override
	public void startElement(String namespaceURI, String lName, String qName, Attributes attrs) throws SAXException {
		
	
		if (qName.equalsIgnoreCase("top_contributor")){
			String contributor_name = attrs.getValue("contributor_name");
			SortedMap<String, String> clist =null;
			if (topContributorList.containsKey(contributor_name)){
				clist = topContributorList.get(contributor_name);
				System.out.println("FOUND");
			}else
			{
				clist = new TreeMap<String,String>();

			}
			clist.put(_candidate,attrs.getValue("total_dollars"));
			topContributorList.put(contributor_name,clist);

		}
		super.startElement(namespaceURI, lName, qName, attrs);
	}
	
	//top_contributor contributor_name="MALLOY, DANNEL P" business_name="Democratic candidate contributions to own campaign" contribution_ranking="1" total_contribution_records="2" total_dollars="500000" percent_of_total_contribution_records="0" percent_of_total_total_dollars="16.7"
	
	@Override
	public void endDocument() throws SAXException {
	
			System.out.println(topContributorList.size());

		super.endDocument();
	}
	final static String start = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+	
	"<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\">\n"+
	"<graph edgedefault=\"directed\">\n\n"+
	"<key id=\"name\" for=\"node\" attr.name=\"name\" attr.type=\"string\"/>\n"+
	"<key id=\"type\" for=\"node\" attr.name=\"type\" attr.type=\"string\"/>\n\n"+
	"<key id=\"weight\" for=\"node\" attr.name=\"weight\" attr.type=\"string\"/>\n\n"+
	"<key id=\"icon\" for=\"node\" attr.name=\"icon\" attr.type=\"string\"/>\n\n"+
	"<key id=\"perc\" for=\"edge\" attr.name=\"perc\" attr.type=\"string\"/>\n\n";
//	"<key id=\"icon\" for=\"node\" attr.name=\"x\" attr.type=\"double\"/>"+
//	"<key id=\"icon\" for=\"node\" attr.name=\"y\" attr.type=\"double\"/>"+
//	"<key id=\"icon\" for=\"node\" attr.name=\"fixed\" attr.type=\"double\"/>";
	final static String end = "\n</graph>\n</graphml>\n";
	
	public void writeCandidateList(SortedMap<String, String> candidateList) throws IOException
	{
		for (int i = 0; i < stateCoord.length; i+=3) {			
			String coord = "<data key=\"x\">"+stateCoord[1]+"</data>"+"<data key=\"y\">"+stateCoord[2]+"</data>"+"<data key=\"fixed\">true</data>";
			stateCoordMap.put(stateCoord[i],coord);
		}	
		
		FileWriter filewriter = new FileWriter("resources\\candidate_donors");
		filewriter.write(start);
		
			
		for ( String  candidate : candidateList.keySet()) {
			
			String[] cdata = candidateList.get(candidate).split(";");
			//todo: nation mapping
			@SuppressWarnings("unused")
			String coord= "";
			String icon; 
			if (cdata[1].equals("REPUBLICAN"))
				icon = "<data key=\"icon\">resources/rep.jpg</data>\n";
			else
				icon = "<data key=\"icon\">resources/dem.jpg</data>\n";
			if (stateCoordMap.containsKey(cdata[2]))
			{
				coord = stateCoordMap.get(cdata[2]);
			}	
			String node = String.format("<node id=\"%s\">\n"
					+ " <data key=\"name\">%s</data>\n"
					+ " <data key=\"weight\">%s</data>\n"  
					+ " <data key=\"type\">SOC</data>\n"
					+ icon
					//+ coord
					+ "</node>\n", 
					candidate,
					cdata[0].replace("&", "_"),
					"1");
			filewriter.write(node);

		}
		int donorID=0;
		for (String donor : topContributorList.keySet())
		{
			SortedMap<String, String> clist = topContributorList.get(donor);
			donorID++;
			// RICHDONOR if has donated more than 100.000 dollar
			boolean richdonor=false ;
			if(clist.keySet().size()>1){
				richdonor=true;
			}else{
				for (String candidate_id : clist.keySet()){
					if (Integer.parseInt(clist.get(candidate_id))>100000){
						richdonor=true;
						break;
					}
				}
			}
			if (richdonor ){
				// write down the node
				String node = String.format("<node id=\"%s\">\n"
						+ " <data key=\"name\">%s</data>\n"
						+ " <data key=\"weight\">%s</data>\n"  
						+ " <data key=\"type\">DIR</data>\n"
						+ "</node>\n", 
						donorID,
						donor.replace("&","_"),
						clist.keySet().size());
				filewriter.write(node);
				// write down all the edges
				for (String candidate_id : clist.keySet()){

					String edge = String.format(
							"<edge source=\"%s\" target=\"%s\" >\n"
							+ " <data key=\"perc\">%s</data>\n"
							+ "</edge>\n",
							donorID,candidate_id,Integer.parseInt(clist.get(candidate_id))
					);
					filewriter.write(edge);
				}
			}
		}
		
		filewriter.write(end);
		filewriter.close();
	}

}