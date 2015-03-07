package net.hirschauer.yaas.lighthouse.bluetooth;

import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Minimal Device Discovery example.
 */
public class RemoteDeviceDiscovery {

    public static final Vector/*<RemoteDevice>*/ devicesDiscovered = new Vector();
    private static final Logger logger = LoggerFactory.getLogger(RemoteDeviceDiscovery.class);

    public RemoteDeviceDiscovery() throws IOException, InterruptedException {

        final Object inquiryCompletedEvent = new Object();

        devicesDiscovered.clear();

        DiscoveryListener listener = new DiscoveryListener() {

            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                logger.info("Device " + btDevice.getBluetoothAddress() + " found");
                devicesDiscovered.addElement(btDevice);
                try {
                    logger.info("     name " + btDevice.getFriendlyName(false));
                } catch (IOException cantGetDeviceName) {
                	logger.error("Can't get device name", cantGetDeviceName);
                }
            }

            public void inquiryCompleted(int discType) {
                logger.info("Device Inquiry completed!");
                synchronized(inquiryCompletedEvent){
                    inquiryCompletedEvent.notifyAll();
                }
            }

            public void serviceSearchCompleted(int transID, int respCode) {
            	logger.debug("serviceSearchCompleted");
            }

            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
            	logger.debug("servicesDiscovered");
            }
        };

        synchronized(inquiryCompletedEvent) {
            boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
            if (started) {
                logger.debug("wait for device inquiry to complete...");
                inquiryCompletedEvent.wait();
                logger.debug(devicesDiscovered.size() +  " device(s) found");
            }
        }
    }

}
