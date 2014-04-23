package com.fbksoft.engine.fonts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FntGenerator {
	
	public FntGenerator(String fontName, int charWidth, int charHeight, String... mapping) throws IOException {
		File outputFile = new File("data/input/font/"+fontName+".fnt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		
		String EOL = "\n";
				
		writer.write("info face=\""+fontName+"\" size="+charWidth+" bold=0 italic=0 charset=\"\" unicode=1 stretchH=100 smooth=1 aa=1 padding=0,0,0,0 spacing=1,1 outline=0"+EOL);
		writer.write("common lineHeight=32 base=25 scaleW=256 scaleH=256 pages=1 packed=0 alphaChnl=1 redChnl=0 greenChnl=0 blueChnl=0"+EOL);		
		writer.write("page id=0 file=\""+fontName+".png\""+EOL);
		
		writer.write("chars count="+count(mapping)+EOL);
		
		for(int line=0; line < mapping.length; line++) {
			for (int i = 0; i < mapping[line].length(); i++) {
				int character = (int)mapping[line].charAt(i);
				writer.write(String.format("char id=%-5d   x=%-6d    y=%-6d     width=%d    height=%d xoffset=0     yoffset=0     xadvance=%d    page=0  chnl=0"+EOL, 
						(int)character,
						i*charWidth, line*charHeight, charWidth, charWidth, charWidth));			
			}		
		}
		
		writer.flush();
		writer.close();
	}

	private int count(String[] mapping) {
		int count = 0;
		for (String string : mapping) {
			count+=string.length();
		}
		return count;
	}

	private static void generate(String fontName, int charWidth, int charHeight, String... mapping) throws IOException {
		new FntGenerator(fontName, charWidth, charHeight, mapping);
	}
	

	public static void main(String[] args) throws IOException {		                                             		
		FntGenerator.generate("fullfont", 8, 8, "ABCDEFGHIJKLMNOP","QRSTUVWXYZ", "abcdefghijklmop", "qrstuvwxyz", ".!?:;,'\"()`/\\+-=", "#@_$", "0123456789");
	}
	
}

