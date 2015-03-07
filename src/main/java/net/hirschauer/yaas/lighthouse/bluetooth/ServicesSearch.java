package net.hirschauer.yaas.lighthouse.bluetooth;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.bluetooth.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Minimal Services Search example.
 */
public class ServicesSearch {

    
    private Logger logger = LoggerFactory.getLogger(ServicesSearch.class);

    public static final Vector/*<String>*/ serviceFound = new Vector();

    public static void main(String[] args) throws IOException, InterruptedException {
    	new ServicesSearch();
    }

    public ServicesSearch() throws IOException, InterruptedException {

        // First run RemoteDeviceDiscovery and use discoved device
        new RemoteDeviceDiscovery();

        serviceFound.clear();

        final Object serviceSearchCompletedEvent = new Object();

        DiscoveryListener listener = new DiscoveryListener() {

            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
            }

            public void inquiryCompleted(int discType) {
            }

            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
                for (int i = 0; i < servRecord.length; i++) {
                    String url = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                    if (url == null) {
                        continue;
                    }
                    serviceFound.add(url);
                    DataElement serviceName = servRecord[i].getAttributeValue(0x0100);
                    if (serviceName != null) {
                        logger.info("service " + serviceName.getValue() + " found " + url);
                    } else {
                    	logger.info("service found " + url);
                    }
                }
            }

            public void serviceSearchCompleted(int transID, int respCode) {
            	logger.info("service search completed!");
                synchronized(serviceSearchCompletedEvent){
                    serviceSearchCompletedEvent.notifyAll();
                }
            }

        };
        
    	UUID OBEX_OBJECT_PUSH = new UUID(0x1105);
        UUID OBEX_FILE_TRANSFER = new UUID(0x1106);

        UUID serviceUUID = OBEX_OBJECT_PUSH;
        UUID[] searchUuidSet = new UUID[] { serviceUUID };
        int[] attrIDs =  new int[] {
                0x0100 // Service name
        };

        for(Enumeration en = RemoteDeviceDiscovery.devicesDiscovered.elements(); en.hasMoreElements(); ) {
            RemoteDevice btDevice = (RemoteDevice)en.nextElement();

            synchronized(serviceSearchCompletedEvent) {
            	logger.info("search services on " + btDevice.getBluetoothAddress() + " " + btDevice.getFriendlyName(false));
                LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, searchUuidSet, btDevice, listener);
                serviceSearchCompletedEvent.wait();
            }
        }

    }

}