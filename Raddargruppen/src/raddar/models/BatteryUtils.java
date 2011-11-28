package raddar.models;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

public class BatteryUtils extends Activity {
	
	private TextView tvBatteryLevel;
	private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
				int level = intent.getIntExtra("level", 0);
				int scale = intent.getIntExtra("scale", 100);
				tvBatteryLevel.setText("Battery level: "
						+ String.valueOf(level * 100 / scale) + "%");
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(mBatteryInfoReceiver, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(mBatteryInfoReceiver);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		tvBatteryLevel = (TextView) findViewById(R.id.tvBatteryLevel);
	}
}
