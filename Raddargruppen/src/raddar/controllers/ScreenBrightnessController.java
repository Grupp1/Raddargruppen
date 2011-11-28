package raddar.controllers;

import android.app.Activity;
import android.view.WindowManager;

public class ScreenBrightnessController{
	private static float screenBrightnesValue = 1;
	
	public static void setScreenBrightnessValueToNormal(){
		screenBrightnesValue = 1;
	}
	
	public static void setScreenBrightnessValueToPowerSaving(){
		screenBrightnesValue = 0.3f;
	}
	
	public static float getScreenBrightnessValue(){
		return screenBrightnesValue;
	}
}
