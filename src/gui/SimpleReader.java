package asdawn.gui;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;

import asdawn.base.PMS5003ST;
import asdawn.base.SerialPorts;
import gnu.io.SerialPort;

/**
 * 简易读取器，自带GUI界面，每秒更新
 * 
 * @author Lin DONG
 *
 */
public class SimpleReader extends JFrame implements AutoCloseable {

	private SerialPort port;
	private static final long serialVersionUID = -2909239943601010953L;
	private Map<String, Double> dataMap;
	private JTextPane panel = null;

	public SimpleReader() {
		super();
		initWindow();
		initDevice();
		this.setVisible(true);
	}

	private void initDevice() {
		List<String> ports = SerialPorts.listSerialPorts();
		if (ports == null || ports.isEmpty()) {
			JOptionPane.showMessageDialog(this, "无可用端口！程序退出。");
			System.exit(0);
		}
		String portName = ports.get(0);
		this.setTitle("PMS5003ST 读取程序<打开端口：" + portName + ">");
		this.port = SerialPorts.open(portName, 9600);
		if (this.port == null) {
			JOptionPane.showMessageDialog(this, portName + "端口打开失败！程序退出。");
			System.exit(0);
		}
	}

	private void initWindow() {
		this.setSize(400, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("PMS5003ST 读取程序");
		this.setLayout(new BorderLayout());
		this.panel = getPanel();
		//this.panel.setCharacterAttributes(, true);
		this.add(panel, BorderLayout.CENTER);
	}

	private JTextPane getPanel() {
		return new JTextPane();
	}

	@Override
	public void close() throws Exception {
		if (this.port != null) {
			SerialPorts.close(port);
		}
	}

	public void update() {
		byte[] data = SerialPorts.readData(this.port);
		if (data == null) {
			return;
		}
		String content = "";
		this.dataMap = PMS5003ST.readData(data);
		if (this.dataMap == null) {
			return;
		}
		for (String attribute : dataMap.keySet()) {
			double value = dataMap.get(attribute);
			content+= String.format("%40s\t%f\n", attribute,value);
		}
		this.panel.setText(content);
	}

	public static void main(String[] args) throws InterruptedException {
		SimpleReader reader = new SimpleReader();
		while (true) {
			Thread.sleep(1000);
			reader.update();
		}
		
	}

}
