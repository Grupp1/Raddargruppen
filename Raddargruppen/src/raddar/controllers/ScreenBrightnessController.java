package raddar.controllers;


public class ScreenBrightnessController{
	private static float screenBrightnesValue = 1;
	
	public static void setScreenBrightnessValueToNormal(){
		screenBrightnesValue = 0.5f;
	}
	
	public static void setScreenBrightnessValueToPowerSaving(){
		screenBrightnesValue = 0.1f;
	}
	
	public static float getScreenBrightnessValue(){
		return screenBrightnesValue;
	}
}
