package com.waltonbd;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;


/**
 * Generate and Write Word Report file
 * Customized docx format
 * @author Sazzad Ahmmed Mohon
 *
 */
public class ReportWriteInWord {
	
	XWPFDocument document;
    XWPFTable table;
	String fileName;
	XWPFParagraph paragraph;
	XWPFRun paragraphOneRunOne;
	FileOutputStream out;
	
	public AppiumDriver<AndroidElement> driver;

	public ReportWriteInWord(AndroidDriver<AndroidElement> driver,String filename) throws FileNotFoundException {
		//Blank Document
		driver = driver;
		
		document= new XWPFDocument(); 
		
		
	
		fileName = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss").format(new Date());
		
		fileName = filename+"_"+fileName+".docx";
		//Write the Document in file system
		out = new FileOutputStream(new File(fileName));
	
		//create paragraph
		
	}
	
	/**
	 * Write String with New Lines separated by \n
	 * @param paragraphOneRunOne XWPFRun 
	 * @param text StringText
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws InvalidFormatException 
	 */
	public void writeJson(JsonArray resultOutput) throws InvalidFormatException, FileNotFoundException, IOException {
		
		String text = "";
		
		
		XWPFDocument document1= new XWPFDocument(); 
		
		for(int j=0;j<resultOutput.size();j++) {
			
			table = document.createTable();
			
			XWPFTableRow tableRowOne = table.getRow(0);
			
			//paragraph = document1.createParagraph();
			
			//paragraphOneRunOne = paragraph.createRun();
			
			paragraphOneRunOne =  table.getRow(0).getCell(0).addParagraph().createRun();
			
			JsonObject outPutJsonObject = resultOutput.get(j).getAsJsonObject();
			String title = outPutJsonObject.get("title").getAsString();
			String details = outPutJsonObject.get("details").getAsString();
		
			//paragraphOneRunOne = paragraph.createRun();
			//paragraphOneRunOne.addBreak();
			//paragraphOneRunOne.addBreak();
			paragraphOneRunOne.setFontSize(14);
			paragraphOneRunOne.setBold(true);
			paragraphOneRunOne.setText(title +" Test: \r\n");
			//paragraphOneRunOne = paragraph.createRun();
			
			paragraphOneRunOne =  table.getRow(0).getCell(0).addParagraph().createRun();
			paragraphOneRunOne.setFontSize(12);
			paragraphOneRunOne.setBold(false);
			
			
			
			text = details;
			if (text.contains("\n")) {
	            String[] lines = text.split("\n");
	            paragraphOneRunOne.setText(lines[0], 0); // set first line into XWPFRun
	            for(int i=0;i<lines.length;i++){
	                // add break and insert new text
	            	paragraphOneRunOne.addBreak();
	            	lines[i] = lines[i].replaceAll("[^a-zA-Z0-9= /:|._]", "");
	            	paragraphOneRunOne.setText(lines[i]);
	            }
	        } else {
	        	paragraphOneRunOne.setText(text, 0);
	        }
			
			
			//tableRowOne.getCell(0).setParagraph(paragraph);
			
			/*
			if(outPutJsonObject.get("imagePath") != null) {
				
				//paragraph = document.createParagraph();
				   
				//paragraphOneRunOne = paragraph.createRun();
				
				paragraphOneRunOne = table.getRow(0).addNewTableCell().addParagraph().createRun();
				//paragraphOneRunOne =  table.getRow(0).getCell(0).addParagraph().createRun();
				
				String imgFile=outPutJsonObject.get("imagePath").getAsString();
				System.out.println("Image Path: "+imgFile);
				
				double imgWidth = outPutJsonObject.get("imageWidth").getAsDouble();
				double imgHeight = outPutJsonObject.get("imageHeight").getAsDouble();
				
				paragraphOneRunOne.addBreak();
				InputStream writeInputStream = new FileInputStream(imgFile);
				paragraphOneRunOne.addPicture(writeInputStream, XWPFDocument.PICTURE_TYPE_JPEG, imgFile,  Units.toEMU(imgWidth), Units.toEMU(imgHeight));
				writeInputStream.close();
				paragraphOneRunOne.addBreak();
				
				//tableRowOne.getCell(1).setParagraph(paragraph);
				
			
				//byte[] fileBase64 = driver.pullFile(imgFile);
				//paragraphOneRunOne.addPicture(new FileInputStream(imgFile), XWPFDocument.PICTURE_TYPE_JPEG, imgFile, 300, 300);
				//document.addPicture(new FileInputStream(imgFile), XWPFDocument.PICTURE_TYPE_JPEG);
				//System.out.println(driver.pullFile(imgFile)+"");
				
				//document.addPicture(fileBase64, XWPFDocument.PICTURE_TYPE_JPEG);
			}
			*/
		}
		
		
	}
	
	public void writeFile() throws IOException {
		document.write(out);
		out.close();
		
		//File wordFile = new File(fileName);
        //Desktop.getDesktop().open(wordFile);
        
		System.out.println(fileName+" written successully");
	}
	
	
}
