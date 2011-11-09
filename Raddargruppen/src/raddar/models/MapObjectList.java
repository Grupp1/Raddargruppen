package raddar.models;

import java.util.ArrayList;

import raddar.controllers.MapCont;
import raddar.views.MapUI;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.widget.EditText;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MapObjectList extends ItemizedOverlay<OverlayItem> {


	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	private EditText input;
	private String value;
	private MapObject item;
	
	

	public MapObjectList(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public MapObjectList(Drawable defaultMarker, Context context) {
		this(defaultMarker);
		mContext = context;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		this.populate();
	}

	/**
	 * Vad som händer när man trycker på en situation
	 */
	@Override
	protected boolean onTap(int index) {
		item = (MapObject) mOverlays.get(index);



		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage("Beskrivning: "+item.getSnippet()+"\nAdress: "+item.getAdress()+
				"Koordinater: "+item.getPoint().getLatitudeE6()/1E6+", "+item.getPoint().getLongitudeE6()/1E6);
		// Lägga till String onTouch i MapObject???
	
		dialog.setPositiveButton("Ändra beskrivning", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);


				alertDialog.setTitle("Ändra beskrivning");
				alertDialog.setMessage("Beskrivning");

				input = new EditText(mContext);
				alertDialog.setView(input);

				alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						value = input.getText().toString();
						MapUI.mapCont.updateSnippet(item, value);			
					}
				});
				
				alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});
				alertDialog.show();
			}

		});
		dialog.show();
		return true;
	}
}
