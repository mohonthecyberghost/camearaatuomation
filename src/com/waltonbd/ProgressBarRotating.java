package com.waltonbd;

public class ProgressBarRotating {
	boolean showProgress = true;
	  public void run() {
	    String anim= "|/-\\";
	    int x = 0;
	    while (showProgress) {
	      System.out.print( anim.charAt(x++ % anim.length()));
	      try { Thread.sleep(100); }
	      catch (Exception e) {};
	      
	      System.out.print("\b");
	    }
	  }
}
