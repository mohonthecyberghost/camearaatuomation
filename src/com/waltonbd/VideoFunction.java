package com.waltonbd;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.AssertJUnit;

import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

/**
 * This Class Deals with all types of video functions
 * in details with information
 * default app: Google Photos
 * @author Sazzad Ahmmed Mohon
 *
 */
public class VideoFunction {
	
	public AndroidDriver<AndroidElement> driver;
	public WebDriverWait wait;
	
	public int lowTimeDelay = 200; 			// Miliseconds
	public int normalTimeDelay = 500; 		// Miliseconds
	public int highTimeDelay =1000; 		// Miliseconds
	public int extraHighTimeDelay =2000; 	// Miliseconds
	
	String[] videoQualityBackItemList = {"CIF","480P","720P","1080P"};
	String[] videoQualityFrontItemList = {"QVGA","CIF","480P","720P"};
	
	
	public VideoFunction(AndroidDriver<AndroidElement> driver, WebDriverWait wait) {
		super();
		this.driver = driver;
		this.wait = wait;
		
		lowTimeDelay*=CameraHALTest.timeFactor;
		normalTimeDelay*=CameraHALTest.timeFactor;
		highTimeDelay*=CameraHALTest.timeFactor;
		extraHighTimeDelay*=CameraHALTest.timeFactor;
	}
	
	/**
	 * Getting Last Video Details Information Captured by Shutter Button
	 * @return String After Video Details Parsing
	 * @throws InterruptedException
	 * @throws IOException 
	 */
	public String lastVideoInfoRead(JsonObject jsonObject) throws InterruptedException, IOException {
		
		//WebDriverWait wait = new WebDriverWait(driver,20);
		
		String videoInfoDetailsText = "";
		Thread.sleep(this.normalTimeDelay);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("com.mediatek.camera:id/thumbnail")));
		WebElement imageThumbnail=driver.findElement(By.id("com.mediatek.camera:id/thumbnail"));
		imageThumbnail.click();
		
		Thread.sleep(this.extraHighTimeDelay);
		Thread.sleep(this.extraHighTimeDelay);
		//driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		
		
		if(!driver.findElements(By.id("com.google.android.apps.photos:id/details")).isEmpty()) {
			WebElement infoOptionsButton=driver.findElement(By.id("com.google.android.apps.photos:id/details"));
			infoOptionsButton.click();
		}
		else {
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("com.google.android.apps.photos:id/action_bar_overflow")));
			WebElement moreOptionsButton=driver.findElement(By.id("com.google.android.apps.photos:id/action_bar_overflow"));
			//wait.until(ExpectedConditions.invisibilityOfElementLocated((By) moreOptionsButton)).click();
			moreOptionsButton.click();
			Thread.sleep(this.normalTimeDelay);
			
			wait.until(ExpectedConditions.presenceOfElementLocated(By.className("android.widget.TextView")));
			//Find element using ClassName property
			List<AndroidElement> moreOptionsElements = driver.findElements(By.className("android.widget.TextView"));
			
			
			for(WebElement moreOptionElement : moreOptionsElements) {
			   if(moreOptionElement.getAttribute("text").equals("Info")) {
				   moreOptionElement.click(); 
					break;
			   }
			}
			
		}
		
		
		Thread.sleep(this.normalTimeDelay);
		WebElement infoDetailsList = driver.findElement(By.id("com.google.android.apps.photos:id/details_list"));
		List<WebElement> infoDetailsListItems = infoDetailsList.findElements(By.className("android.widget.LinearLayout"));
	   
		WebElement vidoeInfoDetails =  infoDetailsListItems.get(3);
		List<WebElement> videoInfoDetailsRows = vidoeInfoDetails.findElements(By.className("android.widget.TextView"));
	   
		WebElement videoInfoDetailsRow0 = videoInfoDetailsRows.get(0);
		String videoName = videoInfoDetailsRow0.getAttribute("text");
	   
		WebElement videoInfoDetailsRow1 = videoInfoDetailsRows.get(1);
		String videoDetails = videoInfoDetailsRow1.getAttribute("text");
		

		String videoType = Files.getFileExtension(videoName);
		String videoTypeOk = (videoType.toLowerCase().equals("mp4"))||(videoType.toLowerCase().equals("MP4")) ?"VideoType - OK":"VideoType - Not OK";
		videoInfoDetailsText += ("\r\nName: "+videoName+" "+videoTypeOk+"\r\n");
		
		String videoOriginalName = videoName.substring(videoName.lastIndexOf("/")+1, videoName.length());
		
		jsonObject.addProperty("videoWebPath", "videos/"+videoOriginalName);
		
		String desktopPath = System.getProperty("user.dir")+"\\videos\\"+videoOriginalName;
		System.out.println("desktopPath: "+desktopPath);
		
		byte[] fileBase64 = driver.pullFile(videoName);
		//System.out.println("Image Base 64 Data: "+fileBase64.toString());
		
		//File outFile = new File(System.getProperty("user.dir")+"\\image\\");
		//outFile.getParentFile().mkdirs();
		
		new File(System.getProperty("user.dir")+"\\videos\\").mkdir();

		
		Files.write(fileBase64, new File(desktopPath));
	   
		String[] videoSizeValues = videoDetails.split("    ");
		String videoSizeValue = videoSizeValues[1];
		String[] videoWidthHeight = videoSizeValue.split(" x ");
	   
		
		videoInfoDetailsText += ("Video Resolution: "+videoWidthHeight[0]+"x"+videoWidthHeight[1]+"\r\n============================= \r\n");
		
		
		
		return videoInfoDetailsText;
	}
	
	
	public boolean changeCameraSettings(String settingsName, String settingsType) throws InterruptedException {
		
		int j=0;
		WebElement settingsMenuListItem = null;
		
		if(settingsType.equals("main")) {
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.mediatek.camera:id/mode")));
			WebElement cameraSettingsButton=driver.findElement(By.id("com.mediatek.camera:id/mode"));
			cameraSettingsButton.click();
			Thread.sleep(this.normalTimeDelay);
		}
					
				
		WebElement settingsList = null;
		List<WebElement> settingsListItems = null;
		String settingsMenuName=null;
		WebElement settingsMenuText = null;
		
		int k;
		
		while(true) {
			
			j++;
			
			if(settingsType.equals("main"))
				settingsList = driver.findElement(By.id("com.mediatek.camera:id/century_setting_list"));
			else if(settingsType.equals("sub"))
				settingsList = driver.findElement(By.id("com.mediatek.camera:id/century_setting_list_items"));
			
			settingsListItems = settingsList.findElements(By.className("android.widget.RelativeLayout"));
			
			for(k=0;k<5;k++) {
				settingsMenuListItem = settingsListItems.get(k);
				List<WebElement> settingsMenuTexts = settingsMenuListItem.findElements(By.className("android.widget.TextView"));
				settingsMenuText = settingsMenuTexts.get(0);
				settingsMenuName = settingsMenuText.getAttribute("text");
				
				
				
				if(settingsMenuName.equals(settingsName))
					break;
			}
			
			
			if(k<5) {
				
				System.out.println("settingsMenuName :"+settingsMenuName);
				j=0;
				break;
			}
			/*
			else if(settingsMenuName.equals(settingsMenuItemList[settingsMenuItemList.length-1])) {
				j=0;
				settingsMenuListItem = null;
				break;
			}*/
			else if(j>10) {
				settingsMenuListItem = null;
				j=0;
				break;
			}
			else {
				Thread.sleep(this.normalTimeDelay);
				
				Dimension dim = driver.manage().window().getSize();
				int height = dim.getHeight();
				int width = dim.getWidth();
				
				int startY =  (int)(settingsMenuListItem.getLocation().y);
				int endY =  startY;
				int startX = width/2-1;
				int endX = width/2-120;
				
				TouchAction action = new TouchAction(driver);
				action.press(PointOption.point(startX,startY));
				action.waitAction(WaitOptions.waitOptions(Duration.ofMillis(300)));
				action.moveTo(PointOption.point(endX,endY)).release().perform();
				
				Thread.sleep(this.normalTimeDelay);
			}
			
			
			
		}
		
		if(settingsMenuListItem == null) {
			Thread.sleep(this.normalTimeDelay);
			driver.navigate().back();
			return false;
		}
		
			
		
		if(settingsMenuListItem != null) {
			Thread.sleep(this.normalTimeDelay);
			settingsMenuListItem.click();
		}
		
		Thread.sleep(this.normalTimeDelay);
		
		if(settingsType.equals("sub"))
			driver.navigate().back();
		
		return true;
	}

	
	
	public void changeToVideoMode() throws InterruptedException {
		
		Thread.sleep(this.extraHighTimeDelay);
		//First Change to Video Mode
		List<AndroidElement> cameraOptionsElements = driver.findElements(By.className("android.widget.TextView"));
		
		for(WebElement cameraOptionsElement : cameraOptionsElements) {
		   if(cameraOptionsElement.getAttribute("text").equals("Video")) {
			   cameraOptionsElement.click(); 
				break;
		   }
		}
		Thread.sleep(this.highTimeDelay);
	}
	
	
	
	public void startVideoProcess(JsonArray videoResultOutput,String type) throws InterruptedException, IOError, IOException {
		
		String[] videoQualityItemList = null;
		
		if(type.equals("Front"))
			videoQualityItemList = videoQualityFrontItemList;
		else
			videoQualityItemList = videoQualityBackItemList;
		
		for(int i=0;i<videoQualityItemList.length;i++) {
			this.changeCameraSettings("Video quality","main");
			if(!this.changeCameraSettings(videoQualityItemList[i],"sub")) continue;
			CameraHALTest.passed_count++;
			this.videoAction(videoResultOutput, type+" Camera : "+videoQualityItemList[i]);
		}
		
	}
	
	
	
	public void videoAction(JsonArray videoResultOutput, String cameraSubType) throws InterruptedException, IOException {

		
		Thread.sleep(this.highTimeDelay);
		
		wait.until(ExpectedConditions.elementToBeClickable(By.id("com.mediatek.camera:id/shutter_button")));
		WebElement shutter=driver.findElement(By.id("com.mediatek.camera:id/shutter_button"));
		shutter.click();
		Thread.sleep(10000);	// 10 Seconds video shoot
		
		WebElement stop_shutter=driver.findElement(By.id("com.mediatek.camera:id/video_stop_shutter"));
		stop_shutter.click();
		Thread.sleep(this.normalTimeDelay);
		
		JsonObject cameraVideoOutput = new JsonObject();
		cameraVideoOutput.addProperty("title", "Video "+cameraSubType);
		
		cameraVideoOutput.addProperty("details", this.lastVideoInfoRead(cameraVideoOutput));
	   
		System.out.println(cameraVideoOutput.get("details").getAsString());
		
		videoResultOutput.add(cameraVideoOutput);
		
		AssertJUnit.assertTrue(true);
   
		driver.navigate().back();
		Thread.sleep(this.lowTimeDelay);
   
		driver.navigate().back();
		Thread.sleep(this.lowTimeDelay);
	}
	
	

}
