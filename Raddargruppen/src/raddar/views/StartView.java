package raddar.views;

import raddar.enums.MessageType;
import raddar.gruppen.R;
import raddar.models.TextMessage;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class StartView extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        
        //TextMessage tm = new TextMessage(MessageType.TEXT, "Borche", "Alice");
        //tm.setMessage("Hejsan. Detta är ett INTEFFE testmeddelande!");
        //TextView tv = (TextView) findViewById(R.id.textView1);
        //tv.setText(tm.getFormattedMessage());
    }

}
