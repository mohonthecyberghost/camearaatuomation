package com.waltonbd;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateNotFoundException;
import freemarker.template.Version;

public class ReportWriteInHtml {
	
	
	public ReportWriteInHtml() {
		
	}
	
	public void writeHtml() throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
		Configuration cfg = new Configuration();
		
		// Where do we load the templates from:
        cfg.setClassForTemplateLoading(CameraHALTest.class, "templates");

        // Some other recommended settings:
        cfg.setIncompatibleImprovements(new Version(2, 3, 20));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setLocale(Locale.US);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        
        Map<String, Object> input = new HashMap<String, Object>();

        input.put("title", "Walton Camera Test Output");
        
        input.put("start_time", CameraHALTest.start_time);
        input.put("end_time", CameraHALTest.end_time);
        
        input.put("mobile_model", CameraHALTest.mobile_model);
        
        input.put("tests_passed", CameraHALTest.passed_count);
        input.put("tests_failed", CameraHALTest.failed_count);

        //input.put("exampleObject", new FreemarkerObject("Camera test", "Mohon"));

        List<FreemarkerObject> htmlValues = new ArrayList<FreemarkerObject>();
        
        
        for(int j=0;j<CameraHALTest.imageResultOutput.size();j++) {
        	
        	JsonObject outPutJsonObject = CameraHALTest.imageResultOutput.get(j).getAsJsonObject();
			String title = outPutJsonObject.get("title").getAsString();
			String details = outPutJsonObject.get("details").getAsString();
			
			
			
			String imgFile = null;
			if(outPutJsonObject.get("imageWebPath") != null) 
			{
				imgFile=outPutJsonObject.get("imageWebPath").getAsString();
				System.out.println("Image Path: "+imgFile);
				
				imgFile = "<img src='"+imgFile+"' />";
				htmlValues.add(new FreemarkerObject(title, details, imgFile));
			}
			
			
			
        }
        
        
        for(int j=0;j<CameraHALTest.videoResultOutput.size();j++) {
        	
        	JsonObject outPutJsonObject = CameraHALTest.videoResultOutput.get(j).getAsJsonObject();
			String title = outPutJsonObject.get("title").getAsString();
			String details = outPutJsonObject.get("details").getAsString();
			
			
			
			String videoFile = null;
			if(outPutJsonObject.get("videoWebPath") != null) 
			{
				videoFile=outPutJsonObject.get("videoWebPath").getAsString();
				System.out.println("Video Path: "+videoFile);
				
				videoFile = "<video width='300' controls><source src='"+videoFile+"' type='video/mp4'></video>";
				
				htmlValues.add(new FreemarkerObject(title, details, videoFile));
				
			}
			
			
			
        }
        
        
        
        input.put("htmlValues", htmlValues);

        // Get the template

        Template template = cfg.getTemplate("camera.ftl");

        // Generate the output

        // Write output to the console
        //Writer consoleWriter = new OutputStreamWriter(System.out);
        //template.process(input, consoleWriter);
        
        String fileName = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss").format(new Date());

        // For the sake of example, also write output into a file:
        Writer fileWriter = new FileWriter(new File("output_"+fileName+".html"));
        try {
            template.process(input, fileWriter);
        } finally {
            fileWriter.close();
        }
        
        File htmlFile = new File("output_"+fileName+".html");
        Desktop.getDesktop().browse(htmlFile.toURI());
        
	}

}
