package asdawn.base;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import gnu.io.*;

/**
 * List, read.
 *
 */
public class SerialPorts {

	/**
	 * Get names of valid serial ports.
	 * 
	 * @return a name list if there exists any serial port, or null for nothing.
	 */
	public static synchronized List<String> listSerialPorts() {
		java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
		List<String> serialPortList = new ArrayList<>();
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier portIdentifier = portEnum.nextElement();
			if (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				serialPortList.add(portIdentifier.getName());
			}
		}
		return serialPortList.isEmpty() ? null : serialPortList;
	}

	/**
	 * Open given port.
	 * 
	 * @param name
	 *            port name
	 * @param baudrate
	 *            baudrate
	 * @return {@link SerialPort}, or {@code null} for failure.
	 */
	public static synchronized SerialPort open(String name, int baudrate) {
		if (name == null || name.isEmpty()) {
			return null;
		}
		CommPortIdentifier portIdentifier = null;
		CommPort commPort = null;
		SerialPort port = null;
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(name);
		} catch (NoSuchPortException e) {
			e.printStackTrace();
			return null;
		}
		try {
			commPort = portIdentifier.open(name, 2000);
		} catch (PortInUseException e) {
			e.printStackTrace();
			return null;
		}
		if(commPort instanceof SerialPort){
			port = (SerialPort) commPort;
		}else{
			return null;
		}
		try{
			port.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		}catch ( UnsupportedCommOperationException e) {
			e.printStackTrace();
			return null;
		}
		return port;
	}

	/**
	 * Close given port.
	 * 
	 * @param port
	 *            SerialPort
	 */
	public static synchronized void close(SerialPort port) {
		if (port != null) {
			try {
				port.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Read data from port
	 * @param port {@link SerialPort}
	 * @return data as {@code byte[]}, or {@code null} for failure.
	 */
	public static synchronized byte[] readData(SerialPort port){
		byte[] bytes = null;
		try(InputStream is = port.getInputStream()){
			int length = is.available();
			while(length != 0){
				bytes = new byte[length];
				is.read(bytes);
				length = is.available();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(bytes !=null && bytes.length==0){
			bytes = null;
		}
		return bytes;
	}

	public static void main(String[] args) throws InterruptedException {
		List<String> list = listSerialPorts();
		for (String string : list) {
			System.out.println(string);
		}
		SerialPort port = open(list.get(0), 9600);
		byte[] bytes = null;
		for(int i=0;i<10 && bytes==null;i++){
			Thread.sleep(100);
			bytes = readData(port);
		}
		PMS5003ST.parseData(bytes);
		close(port);
	}

}
