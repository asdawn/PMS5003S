package asdawn.base;

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
	public static List<String> listSerialPorts() {
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

	public static void main(String[] args) {
		List<String> list = listSerialPorts();
		for (String string : list) {
			System.out.println(string);
		}
	}

}
