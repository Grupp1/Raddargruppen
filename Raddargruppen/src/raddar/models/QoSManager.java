package raddar.models;

import raddar.views.MainView;
import android.app.Activity;
import android.view.WindowManager;
import android.widget.Toast;

public class QoSManager {

	/*
	 * En lista med aktiva activities så att man lätt kan komma åt dem när man
	 * behöver ändra ljusstyrka etc (vid låg batterinivå)
	 */
	private static Activity current;
	private static int level = 100;

	/**
	 * Ange vilken aktivitet som befinner sig i förgrunden
	 * @param a Aktiviteten som befinner sig i förgrunden
	 */
	public static void setCurrentActivity(Activity a) {
		current = a;
	}
	
	/**
	 * Ställ in telefonens strömläge beroende på telefonens
	 * redan sparade batterinivå
	 */
	public static void setPowerMode() {
		setPowerMode(level);
	}
	
	/**
	 * Ställ in telefonens strömläge beroende på batterinivå
	 * @param l Batterinivå
	 */
	public static void setPowerMode(int l) {
		displayToastIfChanged(l);
		level = l;
		if (level <= 20) 
			setPowerSaveMode();
		else
			setNormalPowerMode();
	}

	/**
	 * Anropa denna när batterinivån är över 20%
	 */
	public static void setNormalPowerMode() {
		WindowManager.LayoutParams lp = current.getWindow().getAttributes();
		lp.screenBrightness = 0.4f;
		current.getWindow().setAttributes(lp);
		
		MainView.theOne.enableButtons();
	}

	/**
	 * Anropa denna när batterinivån är 20% eller mindre
	 */
	public static void setPowerSaveMode() {
		WindowManager.LayoutParams lp = current.getWindow().getAttributes();
		lp.screenBrightness = 0.05f;
		current.getWindow().setAttributes(lp);
		
		MainView.theOne.disableButtons();
	}
	
	private static void displayToastIfChanged(int l) {
		int old_level = level;
		level = l;
		if (old_level == 21 && level == 20)
			Toast.makeText(current, "Strömsparläge aktiverat", Toast.LENGTH_LONG).show();
		if (old_level == 20 && level == 21)
			Toast.makeText(current, "Normalt strömläge aktiverat", Toast.LENGTH_LONG).show();
	}
}
