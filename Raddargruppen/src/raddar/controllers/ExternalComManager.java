
package raddar.controllers;

import java.util.Observable;
import java.util.Observer;

import raddar.models.Message;


/*
 * Sk�ter kommunikationen som kommer utifr�n (g�r via servern)
 */
public class ExternalComManager implements Observer {

	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
		
	}
	
	public void inData(Message msg){
		
	}

	public void sendData(String data, String reciever){
		
	}
	
}
