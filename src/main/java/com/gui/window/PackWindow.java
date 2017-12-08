package com.gui.window;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.UIManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import com.gui.tools.PackUtils;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class PackWindow {

	private JFrame frmEclipsemyeclipseV;
	private JTextField textField_projectPath;
	private JTextField textField_excelPath;
	private JTextField textField_exportPath;
	private String configFilePath = System.getProperty("user.home") + File.separator + "pack.ini";
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PackWindow window = new PackWindow();
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");//windows风格
					//Font font = new Font("微软雅黑",Font.PLAIN,15);
			        //UIManager.put("Button.font", font); 
					window.frmEclipsemyeclipseV.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public PackWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmEclipsemyeclipseV = new JFrame();
		frmEclipsemyeclipseV.setTitle("Eclipse&MyEclipse增量打包工具 V1.0.0");
		frmEclipsemyeclipseV.setBounds(100, 100, 625, 165);
		frmEclipsemyeclipseV.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmEclipsemyeclipseV.getContentPane().setLayout(null);
		frmEclipsemyeclipseV.setLocationRelativeTo(null);
		frmEclipsemyeclipseV.setResizable(false);
		
		JLabel lblNewLabel = new JLabel("项目路径");
		lblNewLabel.setBounds(10, 10, 54, 15);
		frmEclipsemyeclipseV.getContentPane().add(lblNewLabel);
		
		textField_projectPath = new JTextField();
		textField_projectPath.setBounds(74, 7, 535, 21);
		frmEclipsemyeclipseV.getContentPane().add(textField_projectPath);
		textField_projectPath.setColumns(10);
		
		JLabel lblExcel = new JLabel("Excel路径");
		lblExcel.setBounds(10, 38, 65, 15);
		frmEclipsemyeclipseV.getContentPane().add(lblExcel);
		
		textField_excelPath = new JTextField();
		textField_excelPath.setColumns(10);
		textField_excelPath.setBounds(74, 38, 535, 21);
		frmEclipsemyeclipseV.getContentPane().add(textField_excelPath);
		
		textField_exportPath = new JTextField();
		textField_exportPath.setColumns(10);
		textField_exportPath.setBounds(74, 69, 535, 21);
		frmEclipsemyeclipseV.getContentPane().add(textField_exportPath);
		
		JLabel label = new JLabel("输出路径");
		label.setBounds(10, 69, 54, 15);
		frmEclipsemyeclipseV.getContentPane().add(label);
		
		JButton btnNewButton = new JButton("立即打包");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					PackUtils.projectPath = textField_projectPath.getText().trim();
					PackUtils.excelPath = textField_excelPath.getText().trim();
					PackUtils.exportPath = textField_exportPath.getText().trim();
					String rootPath = PackUtils.exportPath.substring(0, PackUtils.exportPath.indexOf(File.separator) + 1);
					if(rootPath.equals(PackUtils.exportPath)){
						PackUtils.exportPath += "pack";
						textField_exportPath.setText(PackUtils.exportPath);
					}
					//记录路径
				    StringBuffer sb = new StringBuffer();
				    sb.append("PACK_PROJECT_PATH=" + PackUtils.projectPath.replace("\\", "\\\\") + "\r\n");
				    sb.append("PACK_EXCEP_PATH=" + PackUtils.excelPath.replace("\\", "\\\\") + "\r\n");
				    sb.append("PACK_EXPORT_PATH=" + PackUtils.exportPath.replace("\\", "\\\\") + "\r\n");
				    FileUtils.writeStringToFile(new File(configFilePath), sb.toString(), "UTF-8");
					PackUtils.pack();					
					JOptionPane.showMessageDialog(null, "执行完毕！", "消息", JOptionPane.NO_OPTION);
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(null, exception.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnNewButton.setBounds(268, 100, 93, 23);
		frmEclipsemyeclipseV.getContentPane().add(btnNewButton);
		//读取路径
		InputStream in = null;
		try {
			if(new File(configFilePath).exists()){
				Properties properties = new Properties();
				in = new FileInputStream(configFilePath);
				properties.load(new InputStreamReader(in, "UTF-8"));
			    textField_projectPath.setText(properties.getProperty("PACK_PROJECT_PATH"));
			    textField_excelPath.setText(properties.getProperty("PACK_EXCEP_PATH"));
			    textField_exportPath.setText(properties.getProperty("PACK_EXPORT_PATH"));					
			}		
		} catch (Exception exception) {
			JOptionPane.showMessageDialog(null, exception.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}
}
