package com.pokware.engine.fonts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FntGenerator {
	
	public FntGenerator(String fontName, String mapping, int charWidth, int charHeight) throws IOException {
		File outputFile = new File("data/input/font/"+fontName+".fnt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		
		String EOL = "\n";
		
		writer.write("info face=\""+fontName+"\" size="+charWidth+" bold=0 italic=0 charset=\"\" unicode=1 stretchH=100 smooth=1 aa=1 padding=0,0,0,0 spacing=1,1 outline=0"+EOL);
		writer.write("common lineHeight=32 base=25 scaleW=256 scaleH=256 pages=1 packed=0 alphaChnl=1 redChnl=0 greenChnl=0 blueChnl=0"+EOL);		
		writer.write("page id=0 file=\""+fontName+".png\""+EOL);
		
		writer.write("chars count="+mapping.length()+EOL);
		for (int i = 0; i < mapping.length(); i++) {
			int character = (int)mapping.charAt(i);
			writer.write(String.format("char id=%-5d   x=%-6d    y=0     width=%d    height=%d xoffset=0     yoffset=0     xadvance=%d    page=0  chnl=0"+EOL, 
					(int)character,
					i*charWidth, charWidth, charWidth, charWidth));			
		}		
		
		writer.flush();
		writer.close();
	}

	public static void main(String[] args) throws IOException {		                                             
		new FntGenerator("kromasky20", " !\"©♥%⋯'╰╯☺+`-./0123456789:;╭│╮?☻ABCDEFGHIJKLMNOPQRSTUVWXYZ", 20, 20);				
	}                   
	
}

/* 
info face="Consolas" size=32 bold=0 italic=0 charset="" unicode=1 stretchH=100 smooth=1 aa=1 padding=0,0,0,0 spacing=1,1 outline=0
common lineHeight=32 base=25 scaleW=256 scaleH=256 pages=1 packed=0 alphaChnl=1 redChnl=0 greenChnl=0 blueChnl=0
page id=0 file="test_0.tga"
chars count=26
char id=65   x=35    y=0     width=15    height=17    xoffset=0     yoffset=8     xadvance=15    page=0  chnl=15
char id=66   x=177   y=0     width=13    height=17    xoffset=1     yoffset=8     xadvance=15    page=0  chnl=15
char id=67   x=191   y=0     width=13    height=17    xoffset=1     yoffset=8     xadvance=15    page=0  chnl=15
char id=68   x=147   y=0     width=14    height=17    xoffset=1     yoffset=8     xadvance=15    page=0  chnl=15
char id=69   x=83    y=18    width=11    height=17    xoffset=2     yoffset=8     xadvance=15    page=0  chnl=15
char id=70   x=95    y=18    width=11    height=17    xoffset=2     yoffset=8     xadvance=15    page=0  chnl=15
char id=71   x=162   y=0     width=14    height=17    xoffset=0     yoffset=8     xadvance=15    page=0  chnl=15
char id=72   x=205   y=0     width=13    height=17    xoffset=1     yoffset=8     xadvance=15    page=0  chnl=15
char id=73   x=107   y=18    width=11    height=17    xoffset=2     yoffset=8     xadvance=15    page=0  chnl=15
char id=74   x=119   y=18    width=11    height=17    xoffset=2     yoffset=8     xadvance=15    page=0  chnl=15
char id=75   x=219   y=0     width=13    height=17    xoffset=1     yoffset=8     xadvance=15    page=0  chnl=15
char id=76   x=70    y=18    width=12    height=17    xoffset=2     yoffset=8     xadvance=15    page=0  chnl=15
char id=77   x=51    y=0     width=15    height=17    xoffset=0     yoffset=8     xadvance=15    page=0  chnl=15
char id=78   x=233   y=0     width=13    height=17    xoffset=1     yoffset=8     xadvance=15    page=0  chnl=15
char id=79   x=67    y=0     width=15    height=17    xoffset=0     yoffset=8     xadvance=15    page=0  chnl=15
char id=80   x=0     y=23    width=13    height=17    xoffset=1     yoffset=8     xadvance=15    page=0  chnl=15
char id=81   x=0     y=0     width=16    height=22    xoffset=0     yoffset=8     xadvance=15    page=0  chnl=15
char id=82   x=14    y=23    width=13    height=17    xoffset=2     yoffset=8     xadvance=15    page=0  chnl=15
char id=83   x=28    y=18    width=13    height=17    xoffset=1     yoffset=8     xadvance=15    page=0  chnl=15
char id=84   x=83    y=0     width=15    height=17    xoffset=0     yoffset=8     xadvance=15    page=0  chnl=15
char id=85   x=42    y=18    width=13    height=17    xoffset=1     yoffset=8     xadvance=15    page=0  chnl=15
char id=86   x=99    y=0     width=15    height=17    xoffset=0     yoffset=8     xadvance=15    page=0  chnl=15
char id=87   x=115   y=0     width=15    height=17    xoffset=0     yoffset=8     xadvance=15    page=0  chnl=15
char id=88   x=131   y=0     width=15    height=17    xoffset=0     yoffset=8     xadvance=15    page=0  chnl=15
char id=89   x=17    y=0     width=17    height=17    xoffset=-1    yoffset=8     xadvance=15    page=0  chnl=15
char id=90   x=56    y=18    width=13    height=17    xoffset=1     yoffset=8     xadvance=15    page=0  chnl=15
*/