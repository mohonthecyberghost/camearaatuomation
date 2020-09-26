package com.waltonbd;




import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
//import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.AssertJUnit;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.TestRunner;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.beust.testng.TestNG;
import com.google.gson.JsonArray;

import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;


@Listeners(value=ReportGenerator.class)

/**
 * Camera Automation
 * @author Sazzad Ahmmed Mohon
 *
 */
public class CameraHALTest {
	//AndroidDriver driver;
	
	private static AndroidDriver<AndroidElement> driver;
	
	
	public static JsonArray imageResultOutput = new JsonArray();
	public static JsonArray videoResultOutput = new JsonArray();
	
	public static int test_count = 0;
	public static int passed_count = 0;
	public static int failed_count = 0;
	public static int skipped_count = 0;
	public static String mobile_model = "";
	
	public static String start_time = null;
	public static String end_time = null;
	
	
	private PhotoFunction photoFunction;
	private VideoFunction videoFunction;
	
	//private static String deviceID;
	private static String deviceID = "HI7LDUDMJJ49ZPVO";
	private static String IP="127.0.0.1";
	//private static String IP="192.168.5.175";
	private static String PORT="4723";
	
	public static boolean isAndroidGo = false; 
	public static int timeFactor = 1;					// Time Factor Dependent to Android GO
	
	private WebDriverWait wait;
	
	public static String OUTPUT_DIRETCORY="";
	
	
	public static String chipset = "mtk";
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args != null && args.length > 0) {
            if (args[0].equals("-s"))
            	deviceID = args[1];
            if (args[2].equals("-port"))
            	PORT = args[3];
            if (args.length>4) {
            	if(args[4].equals("-go")) 
            		if(Integer.parseInt(args[5])==1) isAndroidGo = true;
            	
            }
            if (args[6].equals("-chipset"))
            	chipset = args[7];

        }
		
		System.out.println("\r\n Starting Walton Test Module : \r\n");
		System.out.println("\r\n ============================= \r\n");
		
		//System.out.println("Is Android Go :"+isAndroidGo+" args[4] : "+args[4]+" args[5] :"+args[5]);
		
		TestListenerAdapter tla = new TestListenerAdapter();
		TestNG testng = new TestNG();
		//testng.setDefaultSuiteName("Camera-Suite");
		testng.setTestClasses(new Class[] { CameraHALTest.class });
		testng.addListener(tla);
		testng.run();
		
	}
	
	
	@BeforeTest
	public void setup(ITestContext ctx) {
		start_time = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss").format(new Date());
		
	    TestRunner runner = (TestRunner) ctx;
	    String folderName = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss").format(new Date());
	    
	    OUTPUT_DIRETCORY = Paths.get("").toAbsolutePath().toString()+"\\test-output\\OUT_"+folderName+"\\";
	    runner.setOutputDirectory(OUTPUT_DIRETCORY);
	   // runner.setTestName("Camera-Suite");
	}
	
	@BeforeClass
	public void setUp() throws MalformedURLException, InterruptedException{
		
		System.out.println("\r\n.......... Initializing Device ..............\r\n");
		
		//Set up desired capabilities and pass the Android app-activity and app-package to Appium
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("platformName", "Android");
		capabilities.setCapability("platformVersion", "8.1.0"); 
		
		//capabilities.setCapability("deviceName","GM3+");
		//capabilities.setCapability("udid","HI7LDUDMJJ49ZPVO");
		
		//capabilities.setCapability("deviceName","GM3");
		//capabilities.setCapability("udid","A6OBCEMRSGFMA64D");
		
		capabilities.setCapability("deviceName","Testing Device");
		capabilities.setCapability("udid",deviceID);
		
		capabilities.setCapability("automationName","UiAutomator2");
		
		capabilities.setCapability("autoAcceptAlerts", true);
		capabilities.setCapability("autoGrantPermissions", true);
	 
		if(chipset.equals("mtk")) {
			capabilities.setCapability("appPackage", "com.mediatek.camera");
			// This package name of your app (you can get it from apk info app)
			capabilities.setCapability("appActivity","com.mediatek.camera.CameraLauncher"); // This is Launcher activity of your app (you can get it from apk info app)
		}
		
		else if(chipset.equals("sprd")) {
			capabilities.setCapability("appPackage", "com.android.camera2");
			capabilities.setCapability("appActivity","com.android.camera.CameraLauncher"); // This is Launcher activity of your app (you can get it from apk info app)
		}
		
		
		System.out.println(".......... Please Wait .............. ");
		
		ProgressBarRotating cmdProgress = new ProgressBarRotating();
		//cmdProgress.run();
		
		new Thread(){
	        public void run(){
	        	cmdProgress.run();
	        }
	      }.start();
		
		driver = new AndroidDriver<AndroidElement>(new URL("http://"+IP+":"+PORT+"/wd/hub"), capabilities);
	   	//driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"),capabilities);
	   	//driver = new AndroidDriver(new URL("http://192.168.5.175:4723/wd/hub"),capabilities);
	   	//driver.startActivity(new Activity("com.android.camera", "Camera"));
	   	//driver = (WebDriver) new SwipeableWebDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
		
		if(isAndroidGo) timeFactor = 2;
		
		
		
		wait = new WebDriverWait(driver, 30);
		
		cmdProgress.showProgress = false;
		System.out.println("\r\n .......... Initializing Platform .............. \r\n");
		
		photoFunction = new PhotoFunction(driver, wait);
		videoFunction = new VideoFunction(driver, wait);
		
		System.out.println("\r\n ============================= \r\n");
		System.out.println("\r\n Starting Camera Test Module : Image Operation \r\n");
		System.out.println("\r\n ============================= \r\n");
	   
	   
		AssertJUnit.assertTrue(true);
		
		if(!driver.findElements(By.id("com.android.camera2:id/dream_welcome")).isEmpty()) {
			WebElement cameraWelcome = driver.findElement(By.id("com.android.camera2:id/dream_welcome"));
			List<WebElement> welcomeItems = cameraWelcome.findElements(By.className("android.widget.Button"));
			WebElement startCaptureBtn = welcomeItems.get(0);
			startCaptureBtn.click();
		}
		
		photoFunction.setFlashMode("off");
	   	
	}
	
	
	
	
	/**
	 * ######################## ######################## ######################## 
	 * ######################## IMAGE TEST SECTION ######################
	 * ######################## ######################## ######################## 
	 */
	
	
	/**
	 * Read Camera 13M Mega Pixel (16:9) Size Photo
	 * @throws Exception
	 */
	
	@Test (priority=2)
	public void backCamera_PictureSize_Process() throws Exception {
		
		System.out.println("\r\nStarting backCamera_PictureSize_Process : \r\n");
		
		photoFunction.switchCameraDirection("back");
		photoFunction.startBackCameraProcess(imageResultOutput);
		
	}
	
	
	
	/**
	 * Read Camera HDR with 13 Mega Pixel (16:9) Size Photo
	 * @throws Exception
	 */
	@Test (priority=11)
	public void backCamera_HDR_Process() throws Exception {
		
		System.out.println("\r\nStarting backCamera_HDR_Process : \r\n");
		//photoFunction.changeCameraMode("Picture");
		photoFunction.changeCameraTopMode("HDR","back");
		photoFunction.cameraClickGetInfo(imageResultOutput, "HDR 13M(16:9) - Rear", true, false);
		
	}
		
	/**
	 * Read Camera Location Details Picture
	 * @throws Exception
	 */
	//@Test (priority=12)
	public void backCameraLocationProcess() throws Exception {
		
		System.out.println("\r\nStarting backCameraLocationProcess : \r\n");
		photoFunction.changeCameraLocationOn();
		photoFunction.cameraClickGetInfo(imageResultOutput, "Location - Rear", false,true);
		
	}
	
	/**
	 * Read Camera Color Effects Picture
	 * @throws Exception
	 */
	//@Test (priority=13)
	public void backCameraColorEffectProcess() throws Exception {
		
		System.out.println("\r\nStarting backCameraColorEffectProcess : \r\n");
		//photoFunction.changeCameraSizeMegaPixel("13M(16:9)",1);
		photoFunction.changeCameraColorEffectsOn(imageResultOutput,"back");
		//photoFunction.cameraClickGetInfo(imageResultOutput, "Location - Rear", false,true);
		
	}
	
	
	/**
	 * Read Camera Scene Modes Picture
	 * @throws Exception
	 */
	//@Test (priority=14)
	public void backCameraSceneModeProcess() throws Exception {
		
		System.out.println("\r\nStarting backCameraSceneModeProcess : \r\n");
		photoFunction.changeCameraSizeMegaPixel("13M(16:9)",1);
		photoFunction.changeCameraSceneModesOn(imageResultOutput,"back");
		
	}
	
	
	/**
	 * Read Camera Scene Modes Picture
	 * @throws Exception
	 */
	//@Test (priority=15)
	public void backCameraProModeProcess() throws Exception {
		
		System.out.println("\r\nStarting backCameraProModeProcess : \r\n");
		photoFunction.changeCameraProModesOn(imageResultOutput,"back");
		
	}
	
	
	/**
	 * Read Camera Scene Modes Picture
	 * @throws Exception
	 */
	//@Test (priority=16)
	public void backCameraAntiFlickerModeProcess() throws Exception {
		
		System.out.println("\r\nStarting backCameraAntiFlickerModeProcess : \r\n");
		//photoFunction.changeCameraProModesOn(imageResultOutput);
		photoFunction.changeCameraAntiFlickerOn(imageResultOutput);
		
	}
	
	
	/**
	 * Read Camera Face Detection Picture
	 * @throws Exception
	 */
	//@Test (priority=17)
	public void backCameraFaceDetectionProcess() throws Exception {
		
		System.out.println("\r\nStarting backCameraFaceDetectionProcess : \r\n");
		photoFunction.changeFaceDetectionOn();
		photoFunction.cameraClickGetInfo(imageResultOutput, "Location - Rear", false,true);
		
	}
	
	//@Test (priority=18)
	public void backCamera_Night_Process() throws Exception {
		
		System.out.println("\r\nStarting backCamera_Night_Process : \r\n");
		//photoFunction.changeCameraMode("Picture");
		photoFunction.changeCameraTopMode("Night","back");
		photoFunction.cameraClickGetInfo(imageResultOutput, "Night 13M(16:9) - Rear", true, false);
		
	}
	
	
	/**
	 * ######################## ######################## ######################## 
	 * ######################## Front Camera TEST SECTION ######################
	 * ######################## ######################## ######################## 
	 */
	
	@Test (priority=21)
	public void frontCamera_PictureSize_Process() throws Exception {
		
		System.out.println("\r\nStarting frontCamera_PictureSize_Process : \r\n");
		
		photoFunction.switchCameraDirection("front");
		photoFunction.startFrontCameraProcess(imageResultOutput);
		
		
	}
	
	/**
	 * Read Camera HDR with 13 Mega Pixel (16:9) Size Photo
	 * @throws Exception
	 */
	@Test (priority=22)
	public void frontCamera_HDR_Process() throws Exception {
		
		System.out.println("\r\nStarting frontCamera_HDR_Process : \r\n");
		photoFunction.switchCameraDirection("front");
		photoFunction.changeCameraTopMode("HDR","front");
		photoFunction.cameraClickGetInfo(imageResultOutput, "HDR 13M(16:9) - Front", true, false);
		
	}
	
	
	/**
	 * Read Camera Location Details Picture
	 * @throws Exception
	 */
	//@Test (priority=23)
	public void frontCameraLocationProcess() throws Exception {
		
		System.out.println("\r\nStarting frontCameraLocationProcess : \r\n");
		photoFunction.changeCameraLocationOn();
		photoFunction.cameraClickGetInfo(imageResultOutput, "Location - Front", false,true);
		
	}
	
	/**
	 * Read Camera Color Effects Picture
	 * @throws Exception
	 */
	//@Test (priority=24)
	public void frontCameraColorEffectProcess() throws Exception {
		
		System.out.println("\r\nStarting frontCameraColorEffectProcess : \r\n");
		photoFunction.switchCameraDirection("front");
		//photoFunction.changeCameraSizeMegaPixel("13M(16:9)",1);
		photoFunction.changeCameraColorEffectsOn(imageResultOutput,"front");
		//photoFunction.cameraClickGetInfo(imageResultOutput, "Location - Rear", false,true);
		
	}
	
	
	
	
	/**
	 * Read Front Camera Scene Modes Picture
	 * @throws Exception
	 */
	//@Test (priority=25)
	public void frontCameraSceneModeProcess() throws Exception {
		
		System.out.println("\r\nStarting frontCameraSceneModeProcess : \r\n");
		//photoFunction.changeCameraSizeMegaPixel("13M(16:9)",1);
		photoFunction.changeCameraSceneModesOn(imageResultOutput,"front");
		
	}
	
	/**
	 * Read Front Camera Anti Flicker Mode Picture
	 * @throws Exception
	 */
	//@Test (priority=26)
	public void frontCameraAntiFlickerModeProcess() throws Exception {
		
		System.out.println("\r\nStarting backCameraAntiFlickerModeProcess : \r\n");
		photoFunction.switchCameraDirection("front");
		photoFunction.changeCameraAntiFlickerOn(imageResultOutput);
		
	}
	
	/**
	 * ######################## ######################## ######################## 
	 * ######################## VIDEO TEST SECTION ######################
	 * ######################## ######################## ######################## 
	 */
	
	
	
	/**
	 * Read Back Camera Video Details
	 * @throws Exception
	 */
	//@Test (priority=71)
	public void backCameraVideoProcess() throws Exception {
		
		photoFunction.switchCameraDirection("back");
		System.out.println("\r\nStarting backCameraVideoProcess : \r\n");
		videoFunction.changeToVideoMode();
		
		videoFunction.startVideoProcess(videoResultOutput,"Back");
		//videoFunction.videoAction(videoResultOutput, "1080P");
		
	}
	
	/**
	 * Read Front Camera Video Details
	 * @throws Exception
	 */
	//@Test (priority=72)
	public void frontCameraVideoProcess() throws Exception {
		
		photoFunction.switchCameraDirection("front");
		System.out.println("\r\nStartingfrontCameraVideoProcess : \r\n");
		
		videoFunction.startVideoProcess(videoResultOutput,"Front");
		
	}
	
	
	
	
	
	
	
	/**
	 * ######################## ######################## ######################## 
	 * ######################## WRITING DOCX SECTION ######################
	 * ######################## ######################## ######################## 
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws MalformedTemplateNameException 
	 * @throws TemplateNotFoundException 
	 * @throws InvalidFormatException 
	 * @throws TemplateException 
	 */
	
	private void createHtmlOutputFile() throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
		ReportWriteInHtml htmlReport = new ReportWriteInHtml();
		htmlReport.writeHtml();
	}
	
	
	
	private void createWordOutputFile() throws IOException, InvalidFormatException, TemplateException {
		ReportWriteInWord wordReport = new ReportWriteInWord(driver, "CAM");
		wordReport.writeJson(imageResultOutput);
		wordReport.writeJson(videoResultOutput);
		wordReport.writeFile();
        
	}
	
	
	
	@AfterMethod
	public void fetchMostRecentTestResult(ITestResult result) {

	    int status = result.getStatus();
	    
	    test_count++;

	    switch (status) {
	        case ITestResult.SUCCESS:
	            passed_count++;
	            break;
	        case ITestResult.FAILURE:
	            failed_count++;
	            break;
	        case ITestResult.SKIP:
	            skipped_count++;
	            break;
	        default:
	            throw new RuntimeException("Invalid status");
	    }
	}
	
	
	@AfterClass
	public void teardown() throws Exception{
		end_time = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss").format(new Date());
		
		this.createHtmlOutputFile();
		this.createWordOutputFile();
		//close the app
		driver.quit();
	}

}
