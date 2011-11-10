package raddar.models;

import java.util.ArrayList;


import raddar.enums.ResourceStatus;
import raddar.enums.SituationPriority;
import raddar.gruppen.R;
import raddar.views.MapUI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MapObjectList extends ItemizedOverlay<OverlayItem> {


	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	private EditText input;
	private String value;
	private MapObject item;
	private int whichItem;

	/*
	 * I en MapObjectList ligger MapObjects 
	 */

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

	//	public void updateOverlay(int index, MapObject o){
	//		mOverlays.set(index, o);
	//		this.populate();
	//	}

	/**
	 * Vad som händer när man trycker på en situation
	 */

	@Override
	protected boolean onTap(int index) {

		final CharSequence [] situationPriority = {"Hög", "Mellan", "Låg"};
		final CharSequence [] resourceStatus = {"Upptagen", "Ledig"};

		item = (MapObject) mOverlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getDescription());
		// Lägga till String onTouch i MapObject???

		AlertDialog alert = dialog.create();

		alert.setButton("Ändra beskrivning", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

				alertDialog.setTitle("Ändra beskrivning");
				alertDialog.setMessage("Beskrivning");

				input = new EditText(mContext);
				alertDialog.setView(input);

				alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						value = input.getText().toString();
						MapUI.mapCont.updateObject(item, value);		
					}
				});

				alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});
				alertDialog.show();
			}

		});

		/*
		 * Ta bort från kartan. Går ej att ta bort sig själv
		 */

		if(!(item instanceof You)){
			alert.setButton2("Ta bort", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					mOverlays.remove(item);
					//MapUI.mapCont.updateObject(item);
				}

			});
		}

		/*
		 * Ändra prioritet på situation på kartan
		 */

		if(item instanceof Situation){
			alert.setButton3("Ändra prioritet", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
					builder.setTitle("Prioritet");
					builder.setItems(situationPriority, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int i) {

							whichItem = i;

							AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

							alertDialog.setTitle("Är du säker på att du vill ändra prioritet?");


							alertDialog.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {

									if(whichItem == 0){
										((Situation) item).setPriority(SituationPriority.HIGH);
										MapUI.mapCont.updateObject(item);
									}
									if(whichItem == 1){
										((Situation) item).setPriority(SituationPriority.NORMAL);
										MapUI.mapCont.updateObject(item);
									}
									if(whichItem == 2){
										((Situation) item).setPriority(SituationPriority.LOW);
										MapUI.mapCont.updateObject(item);
									}

								}
							});
							alertDialog.setNegativeButton("Nej", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {

								}
							});
							alertDialog.show();
						}

					});
					AlertDialog alert1 = builder.create();
					alert1.show();
				}
			});

			alert.show();

		}
		/*
		 * Ändra Status på resurs på kartan.
		 */
		else{
			alert.setButton3("Ändra status", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
					builder.setTitle("Status");
					builder.setItems(resourceStatus, new DialogInterface.OnClickListener() {




						public void onClick(DialogInterface dialog, int i) {

							whichItem = i;
							
							AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

							alertDialog.setTitle("Är du säker på att du vill ändra status?");


							alertDialog.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {

									if(whichItem == 0){
										((Resource) item).setStatus(ResourceStatus.BUSY);
										MapUI.mapCont.updateObject(item);
									}
									if(whichItem == 1){
										((Resource) item).setStatus(ResourceStatus.FREE);
										MapUI.mapCont.updateObject(item);
									}
								}
							});
							alertDialog.setNegativeButton("Nej", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {

								}
							});
							alertDialog.show();
						}

					});
					AlertDialog alert1 = builder.create();
					alert1.show();
				}
			});

			alert.show();

		}
		return true;
	}
}
