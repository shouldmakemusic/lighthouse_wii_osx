package net.hirschauer.yaas.lighthouse.bluetooth;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

import de.sciss.net.OSCClient;
import de.sciss.net.OSCMessage;

public class Logger2OSC extends WriterAppender {

	private OSCClient client;
	
	public Logger2OSC() throws IOException {
        client = OSCClient.newUsing(OSCClient.UDP);
        client.setTarget(new InetSocketAddress("localhost", 9050));
        client.start();		
	}
	
	@Override
	public void append(final LoggingEvent loggingEvent) {
		
//		final String message = this.layout.format(loggingEvent);
		
		try {
			OSCMessage m = new OSCMessage("/wii_exec", new String[] {loggingEvent.getMessage().toString()} );
			client.send(m);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
