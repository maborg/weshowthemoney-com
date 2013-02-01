package com.weshowthemoney.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipInputStream;

public class Grabber {

//	private String str;
//
//	private int length;
//
//	private int next = 0;
//
//	private int mark = 0;

	private BufferedReader bufferedreader;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) {

		Grabber g = new Grabber();
		try {
			g.open();

			String line;
			do {
				line = g.readline();

				System.out.print(line);
			} while (line != null);

		} catch (IOException e) {
			
			e.printStackTrace();
		}

	}

	public void open() throws IOException {
		String post = "http://www.consob.it/main/downloadassetti?filter=ors&folder=assetti108";

		URL urlpost = new URL(post);
		URLConnection c = urlpost.openConnection();

		ZipInputStream z = new ZipInputStream(c.getInputStream());
		z.getNextEntry();
		bufferedreader = new BufferedReader(new InputStreamReader(z));

	}

private String readline() throws IOException{
		String line;
		StringBuffer  sb = new StringBuffer();
		StringBuffer  tag = new StringBuffer();
			boolean istext = false;
			line = bufferedreader.readLine();
			for (int i = 0; i < line.length(); i++) {
				char ch = line.charAt(i);
				if (ch == '>') {
					istext = true;
					if (tag.toString().trim().compareToIgnoreCase("/TR")==0)
						sb.append(" \t ");
					tag.setLength(0);		
					continue;
				}else
				if (ch == '<'){
					istext = false;
					
				}
				if (istext)
					sb.append(ch);
				else if (ch != '<')
					tag.append(ch);
			}
			sb.append('\n');
		return sb.toString();
	}}
