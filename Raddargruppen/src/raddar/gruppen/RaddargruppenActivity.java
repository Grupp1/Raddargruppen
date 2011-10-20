package raddar.gruppen;

import raddar.enums.MessageType;
import raddar.models.TextMessage;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class RaddargruppenActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TextMessage tm = new TextMessage(MessageType.TEXT, "Borche", "Alice");
        tm.setMessage("Hejsan. Detta �r ett testmeddelande!");
        
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setText(tm.getFormattedMessage());
    }
}