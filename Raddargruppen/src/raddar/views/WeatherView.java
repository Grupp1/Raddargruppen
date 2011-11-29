package raddar.views;

import java.text.SimpleDateFormat;
import java.util.Date;

import raddar.models.QoSManager;
import raddar.models.XMLFetcher;
import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherView extends ExpandableListActivity {
	
	ExpandableListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	mAdapter = new MyExpandableListAdapter();
        setListAdapter(mAdapter);
        registerForContextMenu(getExpandableListView());
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Sample menu");
        menu.add(0, 0, 0, "HEJ HEJ");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item.getMenuInfo();

        String title = ((TextView) info.targetView).getText().toString();

        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition); 
            int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition); 
            Toast.makeText(this, title + ": Child " + childPos + " clicked in group " + groupPos,
                    Toast.LENGTH_SHORT).show();
            return true;
        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition); 
            Toast.makeText(this, title + ": Group " + groupPos + " clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }
    
    public class MyExpandableListAdapter extends BaseExpandableListAdapter {
        // Sample data set.  children[i] contains the children (String[]) for groups[i].
    	
    	private XMLFetcher xml = new XMLFetcher();
    	
    	public MyExpandableListAdapter() {
    		int n = xml.list.get(0).blocks.size();
    		for (int i=0; i<4; i++)
    			children[0][i] = "";
    		
    		for (int i=0; i<n; i++)
    			children[0][i] = xml.list.get(0).blocks.get(i).from + "-" + xml.list.get(0).blocks.get(i).to + "\n" + xml.list.get(0).blocks.get(i).temp + "  " + xml.list.get(0).blocks.get(i).speed + " " + xml.list.get(0).blocks.get(i).direction;
    		
    		for (int j=1; j < 9; j++) {
    			for (int i=0; i < 4; i++) {
    				children[j][i] = xml.list.get(j).blocks.get(i).from + "-" + xml.list.get(j).blocks.get(i).to + "\n" + xml.list.get(j).blocks.get(i).temp + "  " + xml.list.get(j).blocks.get(i).speed + " " + xml.list.get(j).blocks.get(i).direction;
    			}
    		}
    	}
    	
        private String[] groups = { 
        		"    Today - sunrise " + xml.sunrise + ", sunset " + xml.sunset, 
        		"    Tomorrow", 
        		"    "+xml.list.get(2).date, 
        		"    "+xml.list.get(3).date,
        		"    "+xml.list.get(4).date,
        		"    "+xml.list.get(5).date,
        		"    "+xml.list.get(6).date,
        		"    "+xml.list.get(7).date,
        		"    "+xml.list.get(8).date
        		};
        private String[][] children = new String[9][4];

        public Object getChild(int groupPosition, int childPosition) {
            return children[groupPosition][childPosition];
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
        	if (groupPosition == 0) return xml.list.get(0).blocks.size();
            return children[groupPosition].length;
        }

        public TextView getGenericView() {
            // Layout parameters for the ExpandableListView
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 64);

            TextView textView = new TextView(WeatherView.this);
            textView.setLayoutParams(lp);
            // Center the text vertically
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
            // Set the text starting position
            textView.setPadding(36, 0, 0, 0);
            return textView;
        }

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                View convertView, ViewGroup parent) {
            TextView textView = getGenericView();
            textView.setText(getChild(groupPosition, childPosition).toString());
            return textView;
        }

        public Object getGroup(int groupPosition) {
            return groups[groupPosition];
        }

        public int getGroupCount() {
            return groups.length;
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                ViewGroup parent) {
            TextView textView = getGenericView();
            textView.setText(getGroup(groupPosition).toString());
            return textView;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }
    }
    
    @Override
	public void onResume() {
		super.onResume();
		QoSManager.setCurrentActivity(this);
		QoSManager.setPowerMode();
	}
}
