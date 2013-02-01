package com.weshowthemoney;

import prefuse.util.ColorLib;

public class Config {

	public String getGraphFilename() {
		
		return "resources/file1_2008.txt";
		//"resources/candidate_donors"); //$NON-NLS-1$;
	}

	public double getPERCEDGE_DIV() {
		
		return 10.0;
	}

	
	public int getC1() {

		return ColorLib.rgb(200, 200, 255);
	}

	public int getC2() {
	
		return ColorLib.rgb(255, 100,100);
	}

	public int getPlaincolor() {
		
		return ColorLib.rgb(200, 200,200);
	}

	public double getCOLORMAPRANGE() {
		
		return 100.0;
	}



}
