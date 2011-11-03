package einar.db;

import java.util.ArrayList;
 
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
 
public class DatabaseExampleActivity extends Activity
{
	// the text fields that users input new data into
	EditText 	textFieldOne, textFieldTwo,
				idField, 
				updateIDField, updateTextFieldOne, updateTextFieldTwo;
 
	// the buttons that listen for the user to select an action
	Button 		addButton, deleteButton, retrieveButton, updateButton;
 
	// the table that displays the data
	TableLayout dataTable;
 
	// the class that opens or creates the database and makes sql calls to it
	AABDatabaseManager db;
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	// this try catch block returns better error reporting to the log
    	try
    	{
	        // Android OS specific calls
    		super.onCreate(savedInstanceState);
	        setContentView(R.layout.main);
 
            // TODO complete the setup operations
    	}
    	catch (Exception e)
    	{
    		Log.e("ERROR", e.toString());
    		e.printStackTrace();
    	}
 
    	
		// TODO create the rest of the methods
    }
    /**
     * creates references and listeners for the GUI interface
     */
    private void setupViews()
    {
    	// THE DATA TABLE
    	dataTable=	 		(TableLayout)findViewById(R.id.data_table);
     
    	// THE DATA FORM FIELDS
    	textFieldOne= 		(EditText)findViewById(R.id.text_field_one);
    	textFieldTwo= 		(EditText)findViewById(R.id.text_field_two);
    	idField= 			(EditText)findViewById(R.id.id_field);
    	updateIDField=		(EditText)findViewById(R.id.update_id_field);
    	updateTextFieldOne=	(EditText)findViewById(R.id.update_text_field_one);
    	updateTextFieldTwo=	(EditText)findViewById(R.id.update_text_field_two);
     
    	// THE BUTTONS
    	addButton = 		(Button)findViewById(R.id.add_button);
    	deleteButton = 		(Button)findViewById(R.id.delete_button);
    	retrieveButton =	(Button)findViewById(R.id.retrieve_button);
    	updateButton = 		(Button)findViewById(R.id.update_button);
    }
    
    /**
     * adds listeners to each of the buttons and sets them to call relevant methods
     */
    private void addButtonListeners()
    {
    	addButton.setOnClickListener
    	(
    		new View.OnClickListener()
    		{
    			@Override public void onClick(View v) {addRow();}
    		}
    	);
     
    	deleteButton.setOnClickListener
    	(
    		new View.OnClickListener()
    		{
    			@Override public void onClick(View v) {deleteRow();}
    		}
    	);
     
    	updateButton.setOnClickListener
    	(
    		new View.OnClickListener()
    		{
    			@Override public void onClick(View v) {updateRow();}
    		}
    	);
     
    	retrieveButton.setOnClickListener
    	(
    		new View.OnClickListener()
    		{
    			@Override public void onClick(View v) {retrieveRow();}
    		}
    	);
     
    }
    
    /**
     * adds a row to the database based on information contained in the
     * add row form fields.
     */
    private void addRow()
    {
    	try
    	{
    		// ask the database manager to add a row given the two strings
    		// this is addRow() in the activity calling addRow() in the database object
    		db.addRow
    		(
    				textFieldOne.getText().toString(),
    				textFieldTwo.getText().toString()
    		);
     
    		// request the table be updated
    		updateTable();
     
    		// remove all user input from the Activity
    		emptyFormFields();
    	}
    	catch (Exception e)
    	{
    		Log.e("Add Error", e.toString());
    		e.printStackTrace();
    	}
    }
    
    /**
     * deletes a row from the database with the id number in the corresponding 
     * user entry field
     */
    private void deleteRow()
    {
    	try
    	{
    		// ask the database manager to delete the row with the give rowID.
    		db.deleteRow(Long.parseLong(idField.getText().toString()));
     
    		// request the table be updated
    		updateTable();
     
    		// remove all user input from the Activity
    		emptyFormFields();
    	}
    	catch (Exception e)
    	{
    		Log.e("Delete Error", e.toString());
    		e.printStackTrace();
    	}
    }
    /**
     * retrieves a row from the database with the id number in the corresponding
     * user entry field
     */
    private void retrieveRow()
    {
    	try
    	{
    		// The ArrayList that holds the row data
    		ArrayList<Object> row;
    		// ask the database manager to retrieve the row with the given rowID
    		row = db.getRowAsArray(Long.parseLong(updateIDField.getText().toString()));
     
    		// update the form fields to hold the retrieved data
    		updateTextFieldOne.setText((String)row.get(1));
    		updateTextFieldTwo.setText((String)row.get(2));
    	}
    	catch (Exception e)
    	{
    		Log.e("Retrieve Error", e.toString());
    		e.printStackTrace();
    	}
    }
    
    /**
     * updates a row with the given information in the corresponding user entry
     * fields
     */
    private void updateRow()
    {
    	try
    	{
    		// ask the database manager to update the row based on the information
    		// found in the corresponding user entry fields
    		db.updateRow
    		(
    			Long.parseLong(updateIDField.getText().toString()),
    			updateTextFieldOne.getText().toString(),
    			updateTextFieldTwo.getText().toString()
    		);
     
    		// request the table be updated
    		updateTable();
     
    		// remove all user input from the Activity
    		emptyFormFields();
    	}
    	catch (Exception e)
    	{
    		Log.e("Update Error", e.toString());
    		e.printStackTrace();
    	}
    }
    
    
}