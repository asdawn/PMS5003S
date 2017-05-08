package asdawn.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import asdawn.base.PMS5003ST;
import asdawn.base.SerialPorts;
import gnu.io.SerialPort;

/**
 * 简易读取器，自带GUI界面，每秒更新并写入制定目录（仅保留关键指标）
 * 
 * @author Lin DONG
 *
 */
public class WebGUI extends JFrame implements AutoCloseable {

	private SerialPort port;
	private static final long serialVersionUID = -2909239943601010953L;
	private Map<String, Double> dataMap;
	private JTextPane panel = null;
	final static String[] KEYS = { "温度", "湿度", "甲醛", "PM2.5（大气环境下）", "PM5（大气环境下）", "PM10（大气环境下）" };
	private String path = null;

	/**
	 * 创建WebGUI对象
	 * 
	 * @param webFile
	 *            HTML文件路径，直接写入，偷懒做法
	 */
	public WebGUI(String webFile) {
		super();
		this.path = webFile;
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
		// 用于小屏幕显示
		this.setSize(320, 180);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("PMS5003ST 读取程序");
		this.setLayout(new BorderLayout());
		this.panel = getPanel();
		this.panel.setFont(new Font("宋体", Font.PLAIN, 16));
		this.panel.setForeground(Color.GREEN);
		this.panel.setBackground(Color.BLACK);
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
		for (String attribute : KEYS) {
			Double value = dataMap.get(attribute);
			if (value != null) {
				content += String.format("%s\t%f\n", attribute.replace("（大气环境下）", ""), value);
			} else {
				content += String.format("%s\t%s\n", attribute.replace("（大气环境下）", ""), "NaN");
			}
		}
		this.panel.setText(content);
		try {
			if (this.path != null) {
				FileWriter fw = new FileWriter(this.path);
				fw.write("<!doctype html><html><head><meta charset=\"utf-8\"><title>空气质量监测</title></head><body><pre><h2>");
				fw.write(content);
				fw.write("</h2></pre></body></html>");
				fw.flush();
				fw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		//替换到tomcat里边！
		WebGUI reader = new WebGUI("c:/temp/aa.html");
		while (true) {
			Thread.sleep(1000);
			reader.update();
		}

	}

}
