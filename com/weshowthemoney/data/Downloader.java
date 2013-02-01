package com.weshowthemoney.data;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;

import javax.xml.parsers.SAXParser;

public class Downloader extends DefaultHandler
{
	private  static final String CANDIDATE = "http://www.followthemoney.org/api/candidates.list.php?key=a23d12bb290fd8492e6a7631ae29e372%20&office=GOVERNOR&year=2006&candidate_status=won";

	private  String dowloadURL = CANDIDATE ;
	
	public static void main(String argv[])
	{
			new Downloader().start();
	}
	
	
	public void start(){
		String candidateURL = new String(getDowloadURL());

		
		// Use an instance of ourselves as the SAX event handler
		DefaultHandler handler =  getMe();
		// Use the default (non-validating) parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			// Set up output stream
			out = new OutputStreamWriter(System.out, "UTF8");
			URL urlFTM = new URL(candidateURL);
			URLConnection uc = urlFTM.openConnection();
			// Parse the input
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse( uc.getInputStream() , handler);
			
		} catch (Throwable t) {
			t.printStackTrace();
		}
		//System.exit(0);
	}
	protected  DefaultHandler getMe() {
		
		return new Downloader();
	}

	static private Writer  out;
	
	// ===========================================================
	// SAX DocumentHandler methods
	// ===========================================================
	
	public void startDocument()
	throws SAXException
	{
		emit("<?xml version='1.0' encoding='UTF-8'?>");
		nl();
	}
	
	public void endDocument()
	throws SAXException
	{
		try {
			nl();
			out.flush();
		} catch (IOException e) {
			throw new SAXException("I/O error", e);
		}
	}
	
	public void startElement(String namespaceURI,
			String lName, // local name
			String qName, // qualified name
			Attributes attrs)
	throws SAXException
	{
		String eName = lName; // element name
		if ("".equals(eName)) eName = qName; // namespaceAware =
		// false
		emit("<"+eName);
		if (attrs != null) {
			for (int i = 0; i < attrs.getLength(); i++) {
				String aName = attrs.getLocalName(i); // Attr name
				if ("".equals(aName)) aName = attrs.getQName(i);
				emit(" ");
				emit(aName+"=\""+attrs.getValue(i)+"\"");
			}
		}
		emit(">");
	}
	
	public void endElement(String namespaceURI,
			String sName, // simple name
			String qName  // qualified name
	)
	throws SAXException
	{
		emit("</"+sName+">");
	}
	
	public void characters(char buf[], int offset, int len)
	throws SAXException
	{
		String s = new String(buf, offset, len);
		emit(s);
	}
	
	// ===========================================================
	// Utility Methods ...
	// ===========================================================
	
	// Wrap I/O exceptions in SAX exceptions, to
	// suit handler signature requirements
	private void emit(String s)
	throws SAXException
	{
		try {
			out.write(s);
			out.flush();
		} catch (IOException e) {
			throw new SAXException("I/O error", e);
		}
	}
	
	// Start a new line
	private void nl()
	throws SAXException
	{
		String lineEnd =  System.getProperty("line.separator");
		try {
			out.write(lineEnd);
		} catch (IOException e) {
			throw new SAXException("I/O error", e);
		}
	}


	public String getDowloadURL() {
		return dowloadURL;
	}

	public void setDowloadURL(String dowloadURL) {
		this.dowloadURL = dowloadURL;
	}
}