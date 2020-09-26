/**
 * 
 */
package com.waltonbd;

import org.testng.TestListenerAdapter;

import com.beust.testng.TestNG;

/**
 * @author Hp
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestListenerAdapter tla = new TestListenerAdapter();
		TestNG testng = new TestNG();
		testng.setTestClasses(new Class[] { CameraHALTest.class });
		testng.addListener(tla);
		testng.run(); 
	}
	

}
