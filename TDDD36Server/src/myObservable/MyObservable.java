package myObservable;

import java.util.Observable;
import java.util.Observer;

public class MyObservable extends Observable {

	MyView view;

	MyModel model;

	public MyObservable() {

		view = new MyView();

		model = new MyModel();
		model.addObserver(view);

	}

	public static void main(String[] av) {
		MyObservable me = new MyObservable();
		me.demo();
	}

	public void demo() {
		model.changeSomething();
	}

	/** The Observer normally maintains a view on the data */
	class MyView implements Observer {
		/** For now, we just print the fact that we got notified. */
		public void update(Observable obs, Object x) {
			System.out.println("update(" + obs + "," + x + ");");
		}
	}

	/** The Observable normally maintains the data */
	class MyModel extends Observable {
		public void changeSomething() {
			// Notify observers of change
			setChanged();
			notifyObservers();
		}
	}

}

