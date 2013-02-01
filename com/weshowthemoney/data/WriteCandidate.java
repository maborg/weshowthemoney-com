package com.weshowthemoney.data;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WriteCandidate extends Downloader {
	protected SortedMap<String,String> candidateList = new TreeMap<String,String>();

	public static void main(String[] args) {
		new WriteCandidate().start();
	}
	
	@Override
	protected  DefaultHandler getMe() {
		
		return new WriteCandidate();
	}
	
	@Override
	public void startElement(String namespaceURI, String lName, String qName, Attributes attrs) throws SAXException {
		
		if (qName.equalsIgnoreCase("candidate")){
			String imsp_candidate_id = attrs.getValue("imsp_candidate_id");
			String party = attrs.getValue("party");
			String state_postal_code = attrs.getValue("state_postal_code");
			candidateList.put(imsp_candidate_id,attrs.getValue("candidate_name")+";"+party+";"+state_postal_code);
		}
		super.startElement(namespaceURI, lName, qName, attrs);
	}
	
	
	@Override
	public void endDocument() throws SAXException {
		WriteCandidateTopDonors wctd = new WriteCandidateTopDonors();
		int row=0;
		int size =  candidateList.size();
		for (String id : candidateList.keySet())
		{
			row++;
			System.out.println("candidate #" + row +" of " + size );
			//if (row>2)break;
//			 for each candidate download top 100 donors
			wctd.downloadTopDonors(id);

			try {
				//sleep for 10 sec
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		
		// write down the xml file
		try {
			wctd.writeCandidateList(candidateList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.endDocument();
		
	}
}