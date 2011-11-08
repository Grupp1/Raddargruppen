package raddar.models;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MapObjectList extends ItemizedOverlay<OverlayItem> {

	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	
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
		

	  OverlayItem item = mOverlays.get(index);
	  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	  dialog.setTitle(item.getTitle());
	  dialog.setMessage(item.getSnippet() + item.getPoint().toString());
	  dialog.show();
	  return true;
	}
	
}
