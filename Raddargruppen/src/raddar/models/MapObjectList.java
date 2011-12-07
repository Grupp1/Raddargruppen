package raddar.models;

import java.util.ArrayList;

import raddar.controllers.SessionController;
import raddar.enums.ResourceStatus;
import raddar.enums.SituationPriority;
import raddar.views.MainView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.EditText;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MapObjectList extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	private EditText input;
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

	public ArrayList<OverlayItem> getOverlays(){
		return mOverlays;
	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		this.populate();
	}

	public void updateOverlay(int index, MapObject o){
		mOverlays.set(index, o);
		this.populate();
	}

	/**
	 * Vad som h�nder n�r man trycker p� en situation
	 */

	protected boolean onTap(final int index) {

		final CharSequence [] situationPriority = {SituationPriority.HIGH.toString(), SituationPriority.NORMAL.toString(), SituationPriority.LOW.toString()};
		final CharSequence [] resourceStatus = {ResourceStatus.BUSY.toString(), ResourceStatus.FREE.toString()};

		item = (MapObject) mOverlays.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getDescription());

		AlertDialog alert = dialog.create();
		if(!(item instanceof You && item.getId()!=SessionController.getUser())){
			alert.setButton("�ndra beskrivning", new DialogInterface.OnClickListener() {

				//alert.setBackground(R.drawable.rounded_button);
				//setBackgroundResource(R.drawable.rounded_button);
				public void onClick(DialogInterface dialog, int whichButton) {
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

					alertDialog.setTitle("�ndra beskrivning");
					alertDialog.setMessage("Beskrivning");

					input = new EditText(mContext);
					alertDialog.setView(input);
					input.setText(item.getSnippet());
					if(!(item instanceof You && item.getId()!=SessionController.getUser())){
						alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								item.setSnippet(input.getText().toString());
								MainView.mapCont.updateObject(item,true);		
							}
						});

						alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {

							}
						});
					}
					alertDialog.show();
				}

			});

		}

		/*
		 * Ta bort fr�n kartan. G�r ej att ta bort sig sj�lv
		 */

		if(!(item instanceof You)){
			alert.setButton2("Ta bort", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

					alertDialog.setTitle("�r du s�ker p� att du vill ta bort objektet?");


					alertDialog.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							mOverlays.remove(item);

							MainView.mapCont.removeObject(item,true);
							setLastFocusedIndex(-1);
							populate();
						}

					});

					alertDialog.setNegativeButton("Nej", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
						
						}
					});
					alertDialog.show();
				}
			});
		}
	
		// Ringa och skicka meddelande till instanser av You som inte �r dig sj�lv
		if (item instanceof You && !(item.getId().equals(SessionController.getUser()))){
			alert.setButton("Ring", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					MainView.mapCont.callUser(item.getAddedBy());
				}
			});
			alert.setButton2("Skicka bildmeddelande", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					MainView.mapCont.sendImageMessage(item.getAddedBy());
				}
			});
			alert.setButton3("Skicka textmeddelande", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					MainView.mapCont.sendTextMessage(item.getAddedBy());
				}
			});
		}
		
		
		
		//		/*
		//		 * �ndra prioritet p� situation p� kartan
		//		 */
		//
		//							}
		//						});
		//						alertDialog.show();
		//					}
		//				});
		//			}


		/*
		 * �ndra prioritet p� situation p� kartan
		 */

		if(item instanceof Situation){
			alert.setButton3("�ndra prioritet", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
					builder.setTitle("Prioritet");
					builder.setItems(situationPriority, new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int i) {

							whichItem = i;

							AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

							alertDialog.setTitle("�r du s�ker p� att du vill �ndra prioritet?");


							alertDialog.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {

									if(whichItem == 0){
										((Situation) item).setPriority(SituationPriority.HIGH);
										MainView.mapCont.updateObject(item,true);
									}
									if(whichItem == 1){
										((Situation) item).setPriority(SituationPriority.NORMAL);
										MainView.mapCont.updateObject(item,true);
									}
									if(whichItem == 2){
										((Situation) item).setPriority(SituationPriority.LOW);
										MainView.mapCont.updateObject(item,true);
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
		 * �ndra Status p� resurs p� kartan.
		 */
		else{
			if(!(item instanceof You && item.getId()!=SessionController.getUser())){
				alert.setButton3("�ndra status", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
						builder.setTitle("Status");
						builder.setItems(resourceStatus, new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int i) {

								whichItem = i;

								AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

								alertDialog.setTitle("�r du s�ker p� att du vill �ndra status?");


								alertDialog.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {

										if(whichItem == 0){
											((Resource) item).setStatus(ResourceStatus.BUSY);
											MainView.mapCont.updateObject(item,true);
										}
										if(whichItem == 1){
											((Resource) item).setStatus(ResourceStatus.FREE);
											MainView.mapCont.updateObject(item,true);
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
			}
			alert.show();
		}
			
			return true;

		}

	
			

	public void addMapObject(MapObject o){
		String oId = o.getId();
		for(OverlayItem mo:mOverlays){
			if(((MapObject)mo).getId().equals(oId))
				return;;
		}
		mOverlays.add(o);
		populate();
	}

	public void removeMapObject(final MapObject o) {
		((Activity)mContext).runOnUiThread(new Runnable(){
			public void run() {
				Log.d("MAPOBJECTLIST", "REMOVE");
				String oId = o.getId();
				for(int i = 0; i < mOverlays.size(); i++){
					if(((MapObject)mOverlays.get(i)).getId().equals(oId)){
						Log.d("MAPOBJECTLIST","REMOVE  "+mOverlays.remove(mOverlays.remove(i)));
						return;
					}
				}
				setLastFocusedIndex(-1);
				populate();
			}
		});
	}

	public void addUpdateMapObject(final MapObject o) {
		((Activity)mContext).runOnUiThread(new Runnable(){
			public void run() {
				String oId = o.getId();
				for(OverlayItem mo:mOverlays){
					if(((MapObject)mo).getId().equals(oId)){
						mOverlays.remove(mo);
						mOverlays.add(o);
						setLastFocusedIndex(-1);
						populate();
						return;
					}
				}
			}

		});
	}
}
