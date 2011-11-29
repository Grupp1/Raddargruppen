package raddar.views;

import raddar.gruppen.R;
import raddar.models.QoSManager;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsView extends Activity {

	private RadioButton normal;
	private RadioButton low;
	private RadioButton automatic;

	private static int index = R.id.automatic_power_radio_button;
	private static boolean automatic_power = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		normal = (RadioButton) findViewById(R.id.normal_power_radio_button);
		low = (RadioButton) findViewById(R.id.low_power_radio_button);
		automatic = (RadioButton) findViewById(R.id.automatic_power_radio_button);

		RadioGroup rg = (RadioGroup) findViewById(R.id.power_program_radio_group);
		rg.check(index);

		OnClickListener ocl = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v == normal) {
					unregisterBatteryReceiver();
					QoSManager.setPowerMode(100);
					index = R.id.normal_power_radio_button;
				} else if (v == low) {
					unregisterBatteryReceiver();
					QoSManager.setPowerMode(18);
					index = R.id.low_power_radio_button;
				} else if (v == automatic) {
					registerBatteryReceiver();
					index = R.id.automatic_power_radio_button;
				}
			}
		};

		normal.setOnClickListener(ocl);
		low.setOnClickListener(ocl);
		automatic.setOnClickListener(ocl);
	}

	private void unregisterBatteryReceiver() {
		if (automatic_power) {
			MainView.theOne
					.unregisterReceiver(MainView.theOne.mBatteryInfoReceiver);
			automatic_power = false;
		}
	}

	private void registerBatteryReceiver() {
		MainView.theOne.registerReceiver(MainView.theOne.mBatteryInfoReceiver,
				new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		automatic_power = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		QoSManager.setCurrentActivity(this);
		QoSManager.setPowerMode();
	}

	public static boolean powerIsAutomatic() {
		return automatic_power;
	}
}
