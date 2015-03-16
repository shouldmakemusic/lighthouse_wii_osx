package net.hirschauer.yaas.lighthouse.bluetooth;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wiigee.control.WiimoteWiigee; 
import org.wiigee.device.Wiimote;
import org.wiigee.event.AccelerationEvent;
import org.wiigee.event.AccelerationListener;
import org.wiigee.event.ButtonListener;
import org.wiigee.event.ButtonPressedEvent;
import org.wiigee.event.ButtonReleasedEvent;
import org.wiigee.event.GestureEvent;
import org.wiigee.event.GestureListener;
import org.wiigee.event.MotionStartEvent;
import org.wiigee.event.MotionStopEvent;
import org.wiigee.event.RotationEvent;
import org.wiigee.event.RotationListener;
import org.wiigee.event.RotationSpeedEvent;
import org.wiigee.filter.HighPassFilter;
import org.wiigee.filter.RotationThresholdFilter;
import org.wiigee.util.Log;

import de.sciss.net.OSCClient;
import de.sciss.net.OSCMessage;


public class LightHouseWii implements AccelerationListener, ButtonListener, RotationListener, GestureListener {
	
	private Wiimote wiimote;
	private WiimoteWiigee wiigee;
	private OSCClient client;
	private Logger logger = LoggerFactory.getLogger(LightHouseWii.class);
	
	public LightHouseWii() {
		wiigee = new WiimoteWiigee();
        try {
            Wiimote wm = this.wiigee.getDevice();
            if (wm != null) {
                System.out.println("Found a Wiimote!");
                this.wiimote = wm;
                this.setupWiimote();
            }
            wiigee.setTrainButton(100);
            wiigee.setRecognitionButton(101);
            wiigee.setCloseGestureButton(103);;
            
            client = OSCClient.newUsing(OSCClient.UDP);
            client.setTarget(new InetSocketAddress("localhost", 9050));
            client.start();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}
	
	private void setupWiimote() {
	    try {
	        this.wiimote.setAccelerationEnabled(true);
	        // this.wiimote.setInfraredCameraEnabled(true);
	        // this.wiimote.setWiiMotionPlusEnabled(true);
	        this.wiimote.addAccelerationFilter(new HighPassFilter());
	        this.wiimote.addRotationFilter(new RotationThresholdFilter(0.5));
	        
	    } catch(Exception e) {
            Log.write("Error in: setupWiimote()");
            e.printStackTrace();
	    }
	
	    this.wiimote.addAccelerationListener(this);
	    this.wiimote.addButtonListener(this);
	    this.wiimote.addRotationListener(this);
	    this.wiimote.addGestureListener(this);
//	    this.wiimote.addInfraredListener(this);
	}

	public static void main(String[] args) {
		new LightHouseWii();

	}

	@Override
	public void gestureReceived(GestureEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rotationReceived(RotationEvent event) {
		OSCMessage m = new OSCMessage("/wii/1/accel/pry", new Double[] {event.getPitch(), event.getRoll(), event.getYaw()} );
		try {
			client.send(m);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void rotationSpeedReceived(RotationSpeedEvent event) {
//		event.getPhi()
	}

	@Override
	public void buttonPressReceived(ButtonPressedEvent event) {
		System.out.println("Button " + event.getButton());
		String button = "";
		switch (event.getButton()) {
		case ButtonPressedEvent.BUTTON_1:
			button = "1";
			break;
		case ButtonPressedEvent.BUTTON_2:
			button = "2";
			break;
		case ButtonPressedEvent.BUTTON_A:
			button = "A";
			break;
		case ButtonPressedEvent.BUTTON_B:
			button = "B";
			break;
		case ButtonPressedEvent.BUTTON_DOWN:
			button = "Down";
			break;
		case ButtonPressedEvent.BUTTON_HOME:
			button = "Home";
			break;
		case ButtonPressedEvent.BUTTON_LEFT:
			button = "Left";
			break;
		case ButtonPressedEvent.BUTTON_MINUS:
			button = "Minus";
			break;
		case ButtonPressedEvent.BUTTON_PLUS:
			button = "Plus";
			break;
		case ButtonPressedEvent.BUTTON_RIGHT:
			button = "Right";
			break;
		case ButtonPressedEvent.BUTTON_UP:
			button = "Up";
			break;
		}
		OSCMessage m = new OSCMessage("/wii/1/button/" + button);
		try {
			client.send(m);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void buttonReleaseReceived(ButtonReleasedEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void accelerationReceived(AccelerationEvent event) {
		OSCMessage m = new OSCMessage("/wii/1/accel/xyz", new Double[] {event.getX(), event.getY(), event.getZ()} );
		try {
			client.send(m);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void motionStartReceived(MotionStartEvent event) {
	}

	@Override
	public void motionStopReceived(MotionStopEvent event) {
	}
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		client.stop();
		wiimote.disconnect();
	}
}
