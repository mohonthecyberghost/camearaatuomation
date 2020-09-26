package com.waltonbd;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
 
public class WordGenerator {
 
    //Get lines from text file
    public List<String> getLines (String fileName) throws Exception {
        //ReadFile instance
        ReadFile rf = new ReadFile();
 
        //Read the text
        try {
            List<String> lines = rf.readLines(fileName);
            for (String line : lines) {
                System.out.println(line);
            }
            return lines;
        } catch (IOException e) {
            // Print out the exception that occurred
            System.out.println("Unable to create " + fileName + ": " + e.getMessage());
            throw e;
        }
    }
 
    //Create Word
    public void createWord(List<String> lines) throws IOException {
        for (String line : lines) {
            //Blank Document
            XWPFDocument document = new XWPFDocument();
            //Write the Document in file system
            FileOutputStream out = new FileOutputStream(
                    new File("createdWord" + "_" + line + ".docx"));
 
            //create Paragraph
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText("VK Number (Parameter): " + line + " here you type your text...\n");
            document.write(out);
           
            //Close document
            out.close();
            System.out.println("createdWord" + "_" + line + ".docx" + " written successfully");
        }
    }
}