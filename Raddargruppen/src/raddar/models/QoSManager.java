package raddar.models;

import raddar.views.MainView;
import android.app.Activity;
import android.view.WindowManager;
import android.widget.Toast;

public class QoSManager {

	/*
	 * En lista med aktiva activities s� att man l�tt kan komma �t dem n�r man
	 * beh�ver �ndra ljusstyrka etc (vid l�g batteriniv�)
	 */
	private static Activity current;
	private static int level = 100;

	/**
	 * Ange vilken aktivitet som befinner sig i f�rgrunden
	 * @param a Aktiviteten som befinner sig i f�rgrunden
	 */
	public static void setCurrentActivity(Activity a) {
		current = a;
	}
	public static Activity getCurrentActivity(){
		return current;
	}
	
	/**
	 * St�ll in telefonens str�ml�ge beroende p� telefonens
	 * redan sparade batteriniv�
	 */
	public static void setPowerMode() {
		setPowerMode(level);
	}
	
	/**
	 * St�ll in telefonens str�ml�ge beroende p� batteriniv�
	 * @param l Batteriniv�
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
	 * Anropa denna n�r batteriniv�n �r �ver 20%
	 */
	public static void setNormalPowerMode() {
		WindowManager.LayoutParams lp = current.getWindow().getAttributes();
		lp.screenBrightness = 0.4f;
		current.getWindow().setAttributes(lp);
		
		if (current == MainView.theOne)
			MainView.theOne.enableButtons();
	}

	/**
	 * Anropa denna n�r batteriniv�n �r 20% eller mindre
	 */
	public static void setPowerSaveMode() {
		WindowManager.LayoutParams lp = current.getWindow().getAttributes();
		lp.screenBrightness = 0.05f;
		current.getWindow().setAttributes(lp);
		
		if (current == MainView.theOne)
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
