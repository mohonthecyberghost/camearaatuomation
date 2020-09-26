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

import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

/**
 * This Class Deals with all types of photo functions
 * in details with information
 * default app: Google Photos
 * @author Sazzad Ahmmed Mohon
 *
 */
public class PhotoFunction {
	
	public AndroidDriver<AndroidElement> driver;
	public WebDriverWait wait;
	
	public int lowTimeDelay = 200; 			// Miliseconds
	public int normalTimeDelay = 700; 		// Miliseconds
	public int highTimeDelay = 1000; 		// Miliseconds
	public int extraHighTimeDelay = 2500; 	// Miliseconds
	
	String[] backPictureSizeItemListMtk = {"13M(16:9)","13M(4:3)","8M(4:3)","6M(16:9)","4M(16:9)","3M(4:3)"};
	String[] backPictureSizeItemListSprd = {"(4:3) 8.0","(4:3) 4.9","(4:3) 3.1","(4:3) 1.9","(4:3) 1.2","(4:3) 0.8","(16:9) 6.0","(16:9) 3.0","(16:9) 2.1","(16:9) 0.9","(16:9) 0.4"};

	String[] frontPictureSizeItemListMtk = {"5M(16:9)","5M(4:3)","3M(16:9)","2M(4:3)","1M(4:3)","1M(16:9)"};
	String[] frontPictureSizeItemListSprd = {"(4:3) 4.9 megapixels","(4:3) 1.9 megapixels","(4:3) 0.8 megapixels","(16:9) 3.0 megapixels","(16:9) 0.9 megapixels","(16:9) 0.4 megapixels"};
	
	String[] antiFlickerItemList = {"Auto","Off","50Hz","60Hz"};

	//String[] sceneItemList = {"off","Auto","Night","Sunset","Party","Portrait","Landscape","Night portrait","Theatre","Beach","Snow","Steady photo","Fireworks","Sports","Candle light"};
	String[] sceneItemList = {"off","Auto","Night","Beach"};
	
	
	
	
	public PhotoFunction(AndroidDriver<AndroidElement> driver, WebDriverWait wait) {
		super();
		this.driver = driver;
		this.wait = wait;
		
		lowTimeDelay*=CameraHALTest.timeFactor;
		normalTimeDelay*=CameraHALTest.timeFactor;
		highTimeDelay*=CameraHALTest.timeFactor;
		extraHighTimeDelay*=CameraHALTest.timeFactor;
		
		//System.out.println("normalTimeDelay :"+normalTimeDelay);
	}
	
	public double[] getWidthHeight(double width, double height, double fixedHeight) {
		
		double ratio = width/height;
		double fixedWidth = ratio*fixedHeight;
		
		double[] fixedValues = new double[2];
		fixedValues[0] = fixedWidth;
		fixedValues[1] = fixedHeight;
		
		return fixedValues;
		
	}
	
	public String getPackageName(int type) {
		String packageName = "";
		
		String[] mtkPackages = {"com.mediatek.camera","com.google.android.apps.photos"};
		String[] sprdPackages = {"com.android.camera2","com.android.gallery3d","com.google.android.apps.photos"};
		
		if(CameraHALTest.chipset.equals("mtk")) 
			packageName = mtkPackages[type];
		else if(CameraHALTest.chipset.equals("sprd"))
			packageName = sprdPackages[type];
		
		return packageName;
	}
	
	
	public WebElement getElementFromScreen(String name,int type) {
		WebElement element = null;
		String packageName = getPackageName(type);
		
		if(name.equals("thumbnail") && CameraHALTest.chipset.equals("sprd"))
			name = "rounded_thumbnail_view";
		else if(name.equals("details_list") && CameraHALTest.chipset.equals("sprd") && type == 1)
			name = "detail_list";
		
		wait.until(ExpectedConditions.elementToBeClickable(By.id(packageName+":id/"+name)));
		element=driver.findElement(By.id(packageName+":id/"+name));
			
		return element;
	}
	
	
	
	


	/**
	 * Getting Last Image Details Information Captured by Shutter Button
	 * @return String After Image Details Parsing
	 * @throws InterruptedException
	 * @throws IOException 
	 */
	public String lastImageInfoRead(JsonObject jsonObject, boolean locationOn) throws InterruptedException, IOError, IOException {
		
		//WebDriverWait wait = new WebDriverWait(driver,20);
		
		String imageInfoDetailsText = "";
		Thread.sleep(this.extraHighTimeDelay);
		Thread.sleep(this.highTimeDelay);
		
		this.getElementFromScreen("thumbnail", 0).click();
		
		
		Thread.sleep(this.highTimeDelay);
		Thread.sleep(this.highTimeDelay);
		//driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		
		//if(!driver.findElements(By.id("com.google.android.apps.photos:id/details")).isEmpty()) 
		{
			
			if(!CameraHALTest.isAndroidGo && CameraHALTest.chipset.equals("sprd"))
				this.getElementFromScreen("details", 2).click();
			else
				this.getElementFromScreen("details", 1).click();
		}
		
		/*
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
		*/
	   
		Thread.sleep(this.lowTimeDelay);
		WebElement infoDetailsList = this.getElementFromScreen("details_list", 2);	//driver.findElement(By.id("com.google.android.apps.photos:id/details_list"));
		if(CameraHALTest.isAndroidGo)
			infoDetailsList = this.getElementFromScreen("details_list", 1);
		
		List<WebElement> infoDetailsListItems = infoDetailsList.findElements(By.className("android.widget.LinearLayout"));
	   
		WebElement imageInfoDetails =  infoDetailsListItems.get(3);
		List<WebElement> imageInfoDetailsRows = imageInfoDetails.findElements(By.className("android.widget.TextView"));
	   
		WebElement imageInfoDetailsRow0 = imageInfoDetailsRows.get(0);
		String imageName = imageInfoDetailsRow0.getAttribute("text");
		
		String imageOriginalName = imageName.substring(imageName.lastIndexOf("/")+1, imageName.length());
		
		jsonObject.addProperty("imageWebPath", "images/"+imageOriginalName);
		
		
		String desktopPath = System.getProperty("user.dir")+"\\images\\"+imageOriginalName;
		//System.out.println("desktopPath: "+desktopPath);
		
		byte[] fileBase64 = driver.pullFile(imageName);
		//System.out.println("Image Base 64 Data: "+fileBase64.toString());
		
		//File outFile = new File(System.getProperty("user.dir")+"\\image\\");
		//outFile.getParentFile().mkdirs();
		
		new File(System.getProperty("user.dir")+"\\images\\").mkdir();

		
		Files.write(fileBase64, new File(desktopPath));
		jsonObject.addProperty("imagePath", desktopPath);
		
		WebElement imageInfoDetailsRow1 = imageInfoDetailsRows.get(1);
		String imageDetails = imageInfoDetailsRow1.getAttribute("text");
		

		String imageType = Files.getFileExtension(imageName);
		String imageTypeOk = (imageType.toLowerCase().equals("jpg"))||(imageType.toLowerCase().equals("jpeg")) ?"ImageType - OK":"ImageType - Not OK";
		imageInfoDetailsText += ("\r\nName: "+imageName+" "+imageType+" "+imageTypeOk+"\r\n");
	   
		//4.9MP 1920x2560 1.42 MB
		//5.3MP 3072 x 1728 1.2 MB
		
		//System.out.println(">>>>:"+imageDetails);
		
		String[] imageSizeValues = null;
				
		
		imageSizeValues = imageDetails.split("    ");
		if(CameraHALTest.chipset.equals("sprd"))
			imageSizeValues = imageDetails.split("   ");
		
		String imageSizeValue = imageSizeValues[1];
		String[] imageWidthHeight = imageSizeValue.split(" x ");
		if(CameraHALTest.chipset.equals("sprd"))
			imageWidthHeight = imageSizeValue.split("x");
		
		double imageWidth= Double.parseDouble(imageWidthHeight[0]);
		double imageHeight= Double.parseDouble(imageWidthHeight[1]);
		double imageOriginalMP = (imageWidth*imageHeight)/Math.pow(10, 6);
		
		double fixedWidthHeight[] = this.getWidthHeight(imageWidth, imageHeight, 200);
		
		jsonObject.addProperty("imageWidth", fixedWidthHeight[0]);
		jsonObject.addProperty("imageHeight", fixedWidthHeight[1]);
	   
		
		
		imageInfoDetailsText += ("Selected MP: "+imageSizeValues[0]+"  Original MP :"+imageOriginalMP+"\r\n");
		imageInfoDetailsText += ("Image Resolution: "+imageSizeValues[1]+" "+"Image Size: "+imageSizeValues[2]+"\r\n");
		
		
		WebElement cameraInfoDetails =  infoDetailsListItems.get(5);
		List<WebElement> cameraInfoDetailsRows = cameraInfoDetails.findElements(By.className("android.widget.TextView"));
		WebElement cameraInfoDetailsRow0 = cameraInfoDetailsRows.get(0);
		imageInfoDetailsText += ("Mobile Model : "+cameraInfoDetailsRow0.getAttribute("text")+"\r\n");
		
		
		
		if(CameraHALTest.mobile_model=="") {
			String imageInfoDetailsTextString = cameraInfoDetailsRow0.getAttribute("text");
			//String[] InfoDetailsValues = imageInfoDetailsTextString.split("    ");
			CameraHALTest.mobile_model = imageInfoDetailsTextString;
		}
		
		if(locationOn==true) {
			WebElement locationInfoDetails =  infoDetailsListItems.get(8);
			List<WebElement> locationInfoDetailsRows = locationInfoDetails.findElements(By.className("android.widget.TextView"));
			WebElement locationInfoDetailsRow0 = locationInfoDetailsRows.get(0);
			WebElement locationInfoDetailsRow1 = locationInfoDetailsRows.get(1);
			
			imageInfoDetailsText += ("Camera Location : "+locationInfoDetailsRow0.getAttribute("text")+"\r\n");
			imageInfoDetailsText += ("Latitude, Longitude : "+locationInfoDetailsRow1.getAttribute("text")+"\r\n");
		}
		
		imageInfoDetailsText += "===========================\r\n";
		//jsonObject.addProperty("imagePath",imageName);
		
		return imageInfoDetailsText;
	}
	
	
	public void changeCameraColorEffectsOn(JsonArray imageResultOutput, String category) throws InterruptedException, IOError, IOException {
		
		if(category.equals("back") && CameraHALTest.chipset.equals("mtk"))
			this.changeCameraSizeMegaPixel(backPictureSizeItemListMtk[0],1);
		else if(category.equals("front") && CameraHALTest.chipset.equals("mtk"))
			this.changeCameraSizeMegaPixel(frontPictureSizeItemListMtk[0],1);
		else if(category.equals("back") && CameraHALTest.chipset.equals("sprd"))
			this.changeCameraSizeMegaPixel(backPictureSizeItemListSprd[0],1);
		else if(category.equals("front") && CameraHALTest.chipset.equals("sprd"))
			this.changeCameraSizeMegaPixel(frontPictureSizeItemListSprd[0],1);
		
		
		Thread.sleep(this.lowTimeDelay);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.mediatek.camera:id/lomo_effect_indicator")));
		WebElement colorSettingsButton=driver.findElement(By.id("com.mediatek.camera:id/lomo_effect_indicator"));
		
		colorSettingsButton.click();
		Thread.sleep(this.normalTimeDelay);
		
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.mediatek.camera:id/lomo_effect_gridview")));
		WebElement effectsGridList = driver.findElement(By.id("com.mediatek.camera:id/lomo_effect_gridview"));
		List<WebElement> effectsGridListItems = effectsGridList.findElements(By.className("android.widget.RelativeLayout"));
	   
		int j=0;
		
		for(int i=1;i<effectsGridListItems.size()+6;i++) {
			
			CameraHALTest.passed_count++;
			
			
			
			WebElement effectsGridListItem = effectsGridListItems.get(i+j);
			List<WebElement> effectTexts = effectsGridListItem.findElements(By.className("android.widget.TextView"));
			WebElement effectText = effectTexts.get(0);
			
			String effectName = effectText.getAttribute("text");
			
			System.out.println("effectName :"+effectName+": \r\n");
			
			effectsGridListItem.click();
			
			Thread.sleep(this.normalTimeDelay);
			
			this.cameraClickGetInfo(imageResultOutput, category+" "+effectName,false,false);
			
			Thread.sleep(this.normalTimeDelay);
			
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.mediatek.camera:id/lomo_effect_indicator")));
			colorSettingsButton=driver.findElement(By.id("com.mediatek.camera:id/lomo_effect_indicator"));
			
			colorSettingsButton.click();
			Thread.sleep(this.normalTimeDelay);
			
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.mediatek.camera:id/lomo_effect_gridview")));
			effectsGridList = driver.findElement(By.id("com.mediatek.camera:id/lomo_effect_gridview"));
			effectsGridListItems = effectsGridList.findElements(By.className("android.widget.RelativeLayout"));
			
			if(effectName.equals("Blackboard") || effectName.equals("Movie")) j=j-3;
		}
		
		
		
		// Now Select None Effect
		Dimension dim = driver.manage().window().getSize();
		int height = dim.getHeight();
		int width = dim.getWidth();
		
		int startY =  (int)(effectsGridList.getLocation().y);
		int endY =  startY;
		int startX = 1;
		int endX = width-1;
		
		int screen = 1;
		
		if(screen==1) {
			startX = width-1;
			endX = 1;
		}
		
		TouchAction action = new TouchAction(driver);
		action.press(PointOption.point(startX,startY));
		action.waitAction(WaitOptions.waitOptions(Duration.ofMillis(300)));
		action.moveTo(PointOption.point(endX,endY)).release().perform();
		Thread.sleep(this.normalTimeDelay);
		
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.mediatek.camera:id/lomo_effect_gridview")));
		effectsGridList = driver.findElement(By.id("com.mediatek.camera:id/lomo_effect_gridview"));
		effectsGridListItems = effectsGridList.findElements(By.className("android.widget.RelativeLayout"));
		WebElement effectsGridListItem = effectsGridListItems.get(0);
		effectsGridListItem.click();
		
	}
	
	
	public void changeCameraProModesOn(JsonArray imageResultOutput, String category) throws InterruptedException, IOError, IOException {
		this.changeCameraTopMode("Pro",category);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.mediatek.camera:id/seek_arc_group")));
		WebElement cameraArcGroup=driver.findElement(By.id("com.mediatek.camera:id/seek_arc_group"));
		
		List<WebElement> arcViewListItems = cameraArcGroup.findElements(By.className("android.view.View"));
		WebElement whiteBalanceArc = arcViewListItems.get(0);
		
		// Now Select None Effect
		Dimension dim = driver.manage().window().getSize();
		int height = dim.getHeight();
		int width = dim.getWidth();
		
		int startY =  250;
		int endY =  startY-40;
		int startX = 1;
		int endX = 150;
		
		TouchAction action = new TouchAction(driver);
		action.press(PointOption.point(startX,startY));
		action.waitAction(WaitOptions.waitOptions(Duration.ofMillis(300)));
		action.moveTo(PointOption.point(endX,endY)).release().perform();
		
	
		Thread.sleep(this.normalTimeDelay);
		
		CameraHALTest.passed_count++;
		
		this.cameraClickGetInfo(imageResultOutput, "Rear Incandescent" ,true,false);
		
		Thread.sleep(this.normalTimeDelay);
	}
	
	
	
	
	public void changeCameraAntiFlickerOn(JsonArray imageResultOutput) throws InterruptedException, IOError, IOException {
		
		CameraHALTest.passed_count--;
		for(int i=1;i<antiFlickerItemList.length;i++) {
			this.changeCameraSettings("Anti flicker","main");
			this.changeCameraSettings(antiFlickerItemList[i],"sub");
			CameraHALTest.passed_count++;
			this.cameraClickGetInfo(imageResultOutput, "Anti flicker : "+antiFlickerItemList[i] ,false,false);
		}
		
		
	}
	
	
	
	
	public boolean changeCameraSettingsOnSpredtrum(String settingsName, String settingsType) throws InterruptedException {
		
		
		Thread.sleep(normalTimeDelay);
		
		if(settingsType.equals("main")) {
			
			Thread.sleep(normalTimeDelay);
			
			MobileActions mobileActions = new MobileActions(driver); 
			mobileActions.horizontalSwipeByPercentage(0.9, 0.5, 0.5);
			
			Thread.sleep(this.normalTimeDelay);
			
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("android:id/list")));
			WebElement settingsList = driver.findElement(By.id("android:id/list"));
			List<WebElement> settingsListItems = settingsList.findElements(By.className("android.widget.TextView"));
			
			for (WebElement settingsListItem : settingsListItems) {
			    if(settingsListItem.getAttribute("text").contains(settingsName)) {
			    	settingsListItem.click();
			    	break;
			    }
			    	
			}
		}
		
		else if(settingsType.equals("sub")) {
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("android:id/select_dialog_listview")));
			WebElement settingsList = driver.findElement(By.id("android:id/select_dialog_listview"));
			List<WebElement> settingsListItems = settingsList.findElements(By.className("android.widget.CheckedTextView"));
			
			for (WebElement settingsListItem : settingsListItems) {
			    if(settingsListItem.getAttribute("text").contains(settingsName)) {
			    	settingsListItem.click();
			    	Thread.sleep(this.lowTimeDelay);
			    	driver.navigate().back();
			    	break;
			    }
			    	
			}
			
			//System.out.println("Time for back");
			
		}
		
				
		
		Thread.sleep(normalTimeDelay);
		
		return true;
	}
	
	
	
	public boolean changeCameraSettings(String settingsName, String settingsType) throws InterruptedException {
		
		if(CameraHALTest.chipset.equals("sprd")) {
			return changeCameraSettingsOnSpredtrum(settingsName,settingsType);
		}
			
		
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
	
	
	public void changeCameraSceneModesOn(JsonArray imageResultOutput, String category) throws InterruptedException, IOError, IOException {
		
		CameraHALTest.passed_count--;
		
		if(category.equals("back") && CameraHALTest.chipset.equals("mtk"))
			this.changeCameraSizeMegaPixel(backPictureSizeItemListMtk[0],1);
		else if(category.equals("front") && CameraHALTest.chipset.equals("mtk"))
			this.changeCameraSizeMegaPixel(frontPictureSizeItemListMtk[0],1);
		else if(category.equals("back") && CameraHALTest.chipset.equals("sprd"))
			this.changeCameraSizeMegaPixel(backPictureSizeItemListSprd[0],1);
		else if(category.equals("front") && CameraHALTest.chipset.equals("sprd"))
			this.changeCameraSizeMegaPixel(frontPictureSizeItemListSprd[0],1);
		
		for(int i=1;i<sceneItemList.length;i++) {
			this.changeCameraSettings("Scene mode","main");
			if(!this.changeCameraSettings(sceneItemList[i],"sub")) continue;
			CameraHALTest.passed_count++;
			this.cameraClickGetInfo(imageResultOutput, "Rear Scene Mode : "+sceneItemList[i] ,false,false);
		}
		
		
	}
	
	public void startBackCameraProcess(JsonArray imageResultOutput) throws InterruptedException, IOError, IOException {
		
		CameraHALTest.passed_count--;
		
		String[] backPictureSizeItemList = null;
		
		if(CameraHALTest.chipset.equals("mtk"))
			backPictureSizeItemList = backPictureSizeItemListMtk;
		else if(CameraHALTest.chipset.equals("sprd"))
			backPictureSizeItemList = backPictureSizeItemListSprd;

		for(int i=0;i<backPictureSizeItemList.length;i++) {
			this.changeCameraSettings("Picture size","main");
			Thread.sleep(this.normalTimeDelay);
			System.out.println(backPictureSizeItemList[i]+": \r\n");
			if(!this.changeCameraSettings(backPictureSizeItemList[i],"sub")) continue;
			CameraHALTest.passed_count++;
			this.cameraClickGetInfo(imageResultOutput, "Back : "+backPictureSizeItemList[i] ,false,false);
		}
		
		
		
	}
	
	public void startFrontCameraProcess(JsonArray imageResultOutput) throws InterruptedException, IOError, IOException {
		
		String[] frontPictureSizeItemList = null;
		
		if(CameraHALTest.chipset.equals("mtk"))
			frontPictureSizeItemList = frontPictureSizeItemListMtk;
		else if(CameraHALTest.chipset.equals("sprd"))
			frontPictureSizeItemList = frontPictureSizeItemListSprd;
		
		for(int i=0;i<frontPictureSizeItemList.length;i++) {
			this.changeCameraSettings("Picture size","main");
			Thread.sleep(this.normalTimeDelay);
			System.out.println(frontPictureSizeItemList[i]+": \r\n");
			if(!this.changeCameraSettings(frontPictureSizeItemList[i],"sub")) continue;
			CameraHALTest.passed_count++;
			this.cameraClickGetInfo(imageResultOutput, "Front : "+frontPictureSizeItemList[i] ,false,false);
		}
		
		
	}
	
	public void changeCameraLocationOn() throws InterruptedException {
		
		this.changeCameraSettings("GPS location", "main");
		driver.navigate().back();
		
	}
	
	
	public void changeFaceDetectionOn() throws InterruptedException {
		
		this.changeCameraSettings("Face", "main");
		driver.navigate().back();
		
	}
	
	
	/**
	 * Change the Camera Size Options to different Megapixels
	 * @param type = 13MP, 8MP, 5MP ....
	 * @param screen = 1 or 2 [2 screens for swipe with options]
	 * @throws InterruptedException
	 */
	public void changeCameraSizeMegaPixel(String type,int screen) throws InterruptedException {
		this.changeCameraSettings("Picture size", "main");
		this.changeCameraSettings(type, "sub");
	}
	
	
	/**
	 * Change the Camera Modes at the Top Screen
	 * @throws InterruptedException 
	 */
	public void changeCameraTopMode(String type, String category) throws InterruptedException {
		Thread.sleep(this.extraHighTimeDelay);
		
		if(CameraHALTest.chipset.equals("mtk")) {
			
			if(category.equals("back"))
				this.changeCameraSizeMegaPixel(backPictureSizeItemListMtk[0],1);
			else if(category.equals("front"))
				this.changeCameraSizeMegaPixel(frontPictureSizeItemListMtk[0],1);
			
			
			Thread.sleep(this.highTimeDelay);
			
			wait.until(ExpectedConditions.presenceOfElementLocated(By.id("com.mediatek.camera:id/mode")));
			WebElement cameraSettingsButton=driver.findElement(By.id("com.mediatek.camera:id/mode"));
			
			cameraSettingsButton.click();
			Thread.sleep(this.normalTimeDelay);
			
			WebElement cameraModeButton=driver.findElement(By.xpath("//android.view.ViewGroup[@content-desc=\""+type+"\"]"));
			
			cameraModeButton.click();
			Thread.sleep(this.normalTimeDelay);
		
		}
			
		else if(CameraHALTest.chipset.equals("sprd")) {
			Thread.sleep(this.highTimeDelay);
			
			if(category.equals("back"))
				this.changeCameraSizeMegaPixel(backPictureSizeItemListSprd[0],1);
			else if(category.equals("front"))
				this.changeCameraSizeMegaPixel(frontPictureSizeItemListSprd[0],1);
			
			if(type.equals("HDR")) {
				this.toggleCameraHDR("on");
			}
		}
			
		
	}
	
	public void toggleCameraHDR(String type) throws InterruptedException {
		
		WebElement hdrBtn = null;
		
		if(CameraHALTest.chipset.equals("sprd")) {
			Thread.sleep(this.lowTimeDelay);
			
			hdrBtn=driver.findElement(By.id("com.android.camera2:id/hdr_toggle_button_dream"));
			
		
			while(true) {
				
				hdrBtn.click();
				
				//System.out.println(" Got : "+flashMode.getAttribute("contentDescription"));
				
				if(hdrBtn.getAttribute("contentDescription").toLowerCase().contains(type))
						break;
				
				hdrBtn=driver.findElement(By.id("com.android.camera2:id/hdr_toggle_button_dream"));
				Thread.sleep(this.lowTimeDelay);
			}
			
		}
			
	}
	
	/**
	 * Camera Photo Action - Step 1: Change Camera Size, Step 2: Shoot, Step 3: Get Info
	 * @param imageResultOutput = Json Array for Image Output Results
	 * @param cameraSubType = Camera Size in MP format
	 * @throws InterruptedException
	 * @throws IOException 
	 * @throws IOError 
	 */
	public void cameraAction(JsonArray imageResultOutput, String cameraSubType,int sizeScreen) throws InterruptedException, IOError, IOException {
		
		this.changeCameraSizeMegaPixel(cameraSubType,sizeScreen);
		Thread.sleep(this.highTimeDelay);
		
		this.cameraClickGetInfo(imageResultOutput, cameraSubType,false,false);
	}
	
	
	
	
	public void cameraClickGetInfo(JsonArray imageResultOutput, String cameraSubType, boolean hdrOn, boolean locationOn) throws InterruptedException, IOError, IOException {
		
		this.getElementFromScreen("shutter_button", 0).click();
		Thread.sleep(this.extraHighTimeDelay);
		
		JsonObject cameraImageOutput = new JsonObject();
		cameraImageOutput.addProperty("title", "Camera "+ cameraSubType);
		cameraImageOutput.addProperty("details", this.lastImageInfoRead(cameraImageOutput,locationOn));
		//cameraImageOutput.addProperty("imagePath","");
		
		/*
		backCamera_13M_16_9_Output += ("13M(16:9) || Image Details here:\r\n=============================\r\n");
		backCamera_13M_16_9_Output += this.lastImageInfoRead();
		System.out.println(backCamera_13M_16_9_Output);
		*/
		
		
		driver.navigate().back();
		Thread.sleep(this.normalTimeDelay);
   
		driver.navigate().back();
		Thread.sleep(this.normalTimeDelay);
		
		//System.out.println("hdrOn ? : "+hdrOn);
		
		if(hdrOn == true) {
			
			Thread.sleep(this.extraHighTimeDelay);
			
			if(CameraHALTest.chipset=="mtk") {
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("com.mediatek.camera:id/mode")));
				WebElement hdrBackBtn=driver.findElement(By.id("com.mediatek.camera:id/mode"));
				hdrBackBtn.click();
			}
			else
				this.toggleCameraHDR("off");
			
			Thread.sleep(this.extraHighTimeDelay);
		}
		
		
		
		System.out.println(cameraImageOutput.get("details").getAsString());
		imageResultOutput.add(cameraImageOutput);
		//AssertJUnit.assertTrue(true);
	}
	
	
	
	/**
	 * Set the Camera Flash Mode
	 * @param type = on|off|auto
	 * @throws InterruptedException
	 */
	public void setFlashMode(String type) throws InterruptedException {
		Thread.sleep(this.normalTimeDelay);
		
		WebElement flashMode = null;
		
		if(CameraHALTest.chipset.equals("mtk"))
			flashMode=driver.findElement(By.id("com.mediatek.camera:id/flash_icon"));
		
		else if(CameraHALTest.chipset.equals("sprd"))
			flashMode=driver.findElement(By.id("com.android.camera2:id/flash_toggle_button_dream"));
		
		while(true) {
			
			flashMode.click();
			
			//System.out.println(" Got : "+flashMode.getAttribute("contentDescription"));
			
			if(flashMode.getAttribute("contentDescription").toLowerCase().equals("flash "+type))
					break;
			
			if(CameraHALTest.chipset.equals("mtk"))
				flashMode=driver.findElement(By.id("com.mediatek.camera:id/flash_icon"));
			
			if(CameraHALTest.chipset.equals("sprd"))
				flashMode=driver.findElement(By.id("com.android.camera2:id/flash_toggle_button_dream"));
			
			Thread.sleep(this.lowTimeDelay);
		}
	}
	
	
	/**
	 * Switch the Camera Front/Back
	 * @param type = on|off|auto
	 * @throws InterruptedException
	 */
	public void switchCameraDirection(String type) throws InterruptedException {
		Thread.sleep(this.normalTimeDelay);
		
		WebElement switchModeIcon = null;
		
		if(CameraHALTest.chipset.equals("mtk"))
			switchModeIcon=driver.findElement(By.id("com.mediatek.camera:id/camera_switcher"));
		
		else if(CameraHALTest.chipset.equals("sprd"))
			switchModeIcon=driver.findElement(By.id("com.android.camera2:id/camera_toggle_button_dream"));
		
		
		
		while(true) {
			
			switchModeIcon.click();
			
			//System.out.println(" Got : "+flashMode.getAttribute("contentDescription"));
			
			if(switchModeIcon.getAttribute("contentDescription").toLowerCase().contains(type))
					break;
			
			if(CameraHALTest.chipset.equals("mtk"))
				switchModeIcon=driver.findElement(By.id("com.mediatek.camera:id/camera_switcher"));
			
			else if(CameraHALTest.chipset.equals("sprd"))
				switchModeIcon=driver.findElement(By.id("com.android.camera2:id/camera_toggle_button_dream"));
			
			Thread.sleep(this.lowTimeDelay);
			
		}
	}
	
	

}
