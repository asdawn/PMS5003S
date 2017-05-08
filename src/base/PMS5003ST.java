package asdawn.base;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TooManyListenersException;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

/**
 * 简单的读取程序，请安装jre和对应版本的RXTX（
 * http://rxtx.qbang.org/pub/rxtx/rxtx-2.2pre2-bins.zip）
 */
public class PMS5003ST {

	public static void main(String[] args) throws NoSuchPortException, PortInUseException,
			UnsupportedCommOperationException, IOException, TooManyListenersException {
		String COM_PORT = "COM4"; // 注意修改，很可能是COM3!!
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(COM_PORT);
		CommPort commPort = portIdentifier.open(COM_PORT, 2000);
		SerialPort serialPort = (SerialPort) commPort;
		serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		InputStream in = serialPort.getInputStream();
		serialPort.addEventListener(new SerialReader(in));
		serialPort.notifyOnDataAvailable(true);

	}

	public class PMSData {
		/*
		 * 0: 42 1: 4d start 2: 0 3: 24 packet size = 36 byte, infact is it not
		 * right 4: 0 5: 1c pm1.0 cf=1 ug/cm3 6: 0 7: 29 pm2.5 cf=1 8: 0 9: 2d
		 * pm10 cf=1 10: 0 11: 18 pm1.0 12: 0 13: 24 pm2.5 14: 0 15: 2d pm10 16:
		 * 13 17: cb 0.3um cnt / 0.1L 18: 5 19: 8b 0.5um cnt / 0.1L 20: 0 21: fb
		 * 1.0um cnt / 0.1L 22: 0 23: 18 2.5um cnt / 0.1L 24: 0 25: 6 5.0um cnt
		 * / 0.1L 26: 0 27: 1 10um cnt / 0.1L 28: 0 29: 38 HCHO ug/m3 30: 0 31:
		 * e1 temp 0.1degree 32: 1 33: 6 humi % (*0.1) 34: 0 35: 0 chechksum 36:
		 * 91 37: 0 38: 5 39: c7
		 */
	}

	public static void parseData(byte[] data) {
		if(data == null || data.length<2){
			return;
		}
		short head = toShort(data[0], data[1]);
		if (head != 0x424d) {
			return;
		}
		short len = toShort(data[2], data[3]);
		switch (len) {
		case 36:
		case 34:
			System.out.println("湿度：" + toShort(data[32], data[33]) / 10.0 + "%");
		case 32:
			System.out.println("温度：" + toShort(data[30], data[31]) / 10.0 + "摄氏度");
		case 30:
			System.out.println("甲醛：" + toShort(data[28], data[29]) / 1000.0 + "mg/m³");
		case 28:
			System.out.println("10um颗粒数（每0.1L)：" + toShort(data[26], data[27]));
		case 26:
			System.out.println("5um颗粒数（每0.1L)：" + toShort(data[24], data[25]));
		case 24:
			System.out.println("2.5um颗粒数（每0.1L)：" + toShort(data[22], data[23]));
		case 22:
			System.out.println("1um颗粒数（每0.1L)：" + toShort(data[20], data[21]));
		case 20:
			System.out.println("0.5um颗粒数（每0.1L)：" + toShort(data[18], data[19]));
		case 18:
			System.out.println("0.3um颗粒数（每0.1L)：" + toShort(data[16], data[17]));
		case 16:
			System.out.println("PM10（大气环境下）：" + toShort(data[14], data[15]) + "μg/m³");
		case 14:
			System.out.println("PM5（大气环境下）：" + toShort(data[12], data[13]) + "μg/m³");
		case 12:
			System.out.println("PM2.5（大气环境下）：" + toShort(data[10], data[11]) + "μg/m³");
		case 10:
			System.out.println("PM10（CF=1，标准颗粒物）：" + toShort(data[8], data[9]) + "μg/m³");
		case 8:
			System.out.println("PM5（CF=1，标准颗粒物）：" + toShort(data[6], data[7]) + "μg/m³");
		case 6:
			System.out.println("PM2.5（CF=1，标准颗粒物）：" + toShort(data[4], data[5]) + "μg/m³");
		}
	}

	public static Map<String, Double> readData(byte[] data) {
		short head = toShort(data[0], data[1]);
		if (head != 0x424d) {
			return null;
		}
		Map<String, Double> map = new HashMap<>();
		short len = toShort(data[2], data[3]);
		switch (len) {
		case 36:
		case 34:
			map.put("湿度", toShort(data[32], data[33]) / 10.0);
		case 32:
			map.put("温度", toShort(data[30], data[31]) / 10.0);
		case 30:
			map.put("甲醛", toShort(data[28], data[29]) / 1000.0);
			// mg/m³
		case 28:
			map.put("10um颗粒数/L", (double) (toShort(data[26], data[27]) * 10));
		case 26:
			map.put("5um颗粒数/L", (double) (toShort(data[24], data[25]) * 10));
		case 24:
			map.put("2.5um颗粒数/L", (double) (toShort(data[22], data[23]) * 10));
		case 22:
			map.put("1um颗粒数/L", (double) (toShort(data[20], data[21]) * 10));
		case 20:
			map.put("0.5um颗粒数/L", (double) (toShort(data[18], data[19]) * 10));
		case 18:
			map.put("0.3um颗粒数/L", (double) (toShort(data[16], data[17]) * 10));
		case 16:
			map.put("PM10（大气环境下）", (double) (toShort(data[14], data[15])));
			// "μg/m³"
		case 14:
			map.put("PM5（大气环境下）", (double) (toShort(data[12], data[13])));
		case 12:
			map.put("PM2.5（大气环境下）", (double) (toShort(data[10], data[11])));
		case 10:
			map.put("PM10（CF=1，标准颗粒物）", (double) (toShort(data[8], data[9])));
		case 8:
			map.put("PM5（CF=1，标准颗粒物）", (double) (toShort(data[6], data[7])));
		case 6:
			map.put("PM2.5（CF=1，标准颗粒物）", (double) (toShort(data[4], data[5])));
		}
		return map;
	}

	public static short toShort(byte high, byte low) {
		return (short) (((high & 0x00FF) << 8) | (0x00FF & low));
	}

	public static class SerialReader implements SerialPortEventListener {
		private InputStream in;
		private volatile long cnt = 0;
		private byte[] buffer = new byte[64];

		public SerialReader(InputStream in) {
			this.in = in;
		}

		public void serialEvent(SerialPortEvent arg0) {
			int data;
			cnt++;
			if (cnt % 10 == 0) {
				try {
					int len = 0;
					while ((data = in.read()) > -1) {
						if (data == '\n') {
							break;
						}
						buffer[len++] = (byte) data;
					}
					parseData(buffer);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
		}
	}
}
