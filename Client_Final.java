package COMP3015_Project;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class Client_Final {
	String serverAddress = "127.0.0.1";
	int portNum = 8888;
	Socket clientSocket;
	DataInputStream in;
	DataOutputStream out;
	static String dlDirectory;
	int passwordNum = 0;
	String password;

	// GUI component
	JFrame framePass;
	JLabel passwordAttemp = new JLabel("password (5 Attempts):");
	JPasswordField passwordField = new JPasswordField(20);

	JButton password_Submit = new JButton("submit");
	JButton password_Reset = new JButton("reset");

	// GUI component
	JFrame frameLoc;
	JLabel locationLabel = new JLabel("Location (Directory):");
	JTextField locationField = new JTextField();

	JButton location_Submit = new JButton("submit");
	JButton location_Reset = new JButton("reset");

	// GUI component
	JFrame frameDir;
	JLabel directoryLabel = new JLabel("Location (Directory):");
	JTextField directoryField = new JTextField();

	JButton directory_Submit = new JButton("submit");
	JButton directory_Reset = new JButton("reset");

	// GUI component
	JFrame frameDis;

	JTextArea fileContent = new JTextArea();

	JLabel fileName = new JLabel("content: ");
	JButton display_download = new JButton("Download");
	JButton display_downloadAll = new JButton("Download \n All");
	JButton display_quit = new JButton("Quit");

	// GUI component
	JFrame frameDown;
	JLabel label_Num = new JLabel("Number: ");
	JLabel label_fileName = new JLabel("File Name: ");

	JTextField text_Num = new JTextField();
	JTextField text_fileName = new JTextField();

	JLabel label_comment = new JLabel("               Format: XXXX.txt,XXXX.mp3...               ");

	JButton download_submit = new JButton("submit");
	JButton download_reset = new JButton("reset");
	JButton download_quit = new JButton("quit");

	// boolean lock
	boolean passwordLock = true;
	boolean locationLock = true;
	boolean directoryLock = true;
	boolean totalLock = true;

	boolean directoryAccessLock = false;

	int displayCount = 0;

	public Client_Final() throws InterruptedException {

		try {
			connect();
			process();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			disconnect();
		}

	}

	public void framePassword() throws InterruptedException {

		passwordLock = true;
		framePass = new JFrame("Enter Password");
		framePass.setSize(300, 150);
		framePass.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		framePass.setResizable(false);
		framePass.setLayout(new FlowLayout());

		passwordField.setPreferredSize(new Dimension(75, 26));

		framePass.add(passwordAttemp);
		framePass.add(passwordField);
		framePass.add(password_Submit);
		framePass.add(password_Reset);

		framePass.setVisible(true);

		password_Submit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				password = new String(passwordField.getPassword());
				passwordLock = false;

			}

		});

		password_Reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				passwordField.setText("");
			}

		});
	}

	public void frameLocation() {

		locationLock = true;

		frameLoc = new JFrame("Download Directory");
		frameLoc.setSize(300, 150);
		frameLoc.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frameLoc.setResizable(false);
		frameLoc.setLayout(new FlowLayout());

		locationField.setPreferredSize(new Dimension(230, 26));

		frameLoc.add(locationLabel);
		frameLoc.add(locationField);
		frameLoc.add(location_Submit);
		frameLoc.add(location_Reset);

		frameLoc.setVisible(true);

		location_Submit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				dlDirectory = locationField.getText();
				locationLock = false;

			}

		});

		location_Reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				locationField.setText("");
			}

		});

	}

	public void frameDirectory() {

		directoryLock = true;
		directoryAccessLock = true;

		frameDir = new JFrame("Set Server Directory");
		frameDir.setSize(300, 150);
		frameDir.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frameDir.setResizable(false);
		frameDir.setLayout(new FlowLayout());

		directoryField.setPreferredSize(new Dimension(230, 26));

		frameDir.add(directoryLabel);
		frameDir.add(directoryField);
		frameDir.add(directory_Submit);
		frameDir.add(directory_Reset);

		frameDir.setVisible(true);

		directory_Submit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				String directory = directoryField.getText();
				send(directory.length(), directory.getBytes());

				directoryLock = false;

			}

		});

		location_Reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				locationField.setText("");
			}

		});

	}

	public void frameDisplay() throws IOException {

		frameLoc.setVisible(false);
		frameDis = new JFrame("Display Page");
		frameDis.setSize(600, 400);
		frameDis.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frameDis.setResizable(false);
		frameDis.setLayout(new FlowLayout());

		fileContent.setEditable(false);

		JScrollPane scrPane1 = new JScrollPane(fileContent);
		scrPane1.setPreferredSize(new Dimension(550, 300));

		String cmd = "cmd fs";
		send(cmd.length(), cmd.getBytes());
		String content = new String(receive());

		fileContent.setText(content);

		frameDis.add(fileName);
		frameDis.add(scrPane1);
		frameDis.add(display_download);
		frameDis.add(display_downloadAll);
		frameDis.add(display_quit);

		frameDis.setVisible(true);

		display_download.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//frameDis.setVisible(false);
				if (displayCount == 0) {
					frameDownload();
					displayCount++;
				} else {
					frameDown.setVisible(true);
				}
			}

		});

		display_downloadAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String cmd = "cmd dlall";
				send(cmd.length(), cmd.getBytes());

				int fileNum = 0;

				try {
					fileNum = Integer.parseInt(new String(receive()));
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				for (int i = 0; i < fileNum; i++) {
					new ReceiveFile().start();
				}
				
				try {
					receive();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				JOptionPane.showMessageDialog(null, "Download Successfully!");
				
			}

		});

		display_quit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				disconnect();
				System.exit(0);

			}

		});

	}

	public void frameDownload() {

		frameDown = new JFrame("Download Page");
		frameDown.setSize(400, 160);
		frameDown.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frameDown.setResizable(false);
		frameDown.setLayout(new FlowLayout());

		text_Num.setPreferredSize(new Dimension(300, 26));
		text_fileName.setPreferredSize(new Dimension(300, 26));

		frameDown.add(label_Num);
		frameDown.add(text_Num);
		frameDown.add(label_fileName);
		frameDown.add(text_fileName);
		frameDown.add(label_comment);
		frameDown.add(download_submit);
		frameDown.add(download_reset);
		frameDown.add(download_quit);

		frameDown.setVisible(true);
		download_reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				text_Num.setText("");
				text_fileName.setText("");
			}

		});

		download_quit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
				disconnect();
			}

		});

		download_submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				String command = "cmd dl";
				send(command.length(), command.getBytes());

				String fileNum = text_Num.getText();
				send(fileNum.length(), fileNum.getBytes());

				String fileName = text_fileName.getText();
				send(fileName.length(), fileName.getBytes());

				int number = Integer.parseInt(fileNum);

				for (int i = 0; i < number; i++) {
					new ReceiveFile().start();
				}

				
				
				String msg="";
				
				try {
					msg=new String(receive());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				if(msg.equals("success")){
					JOptionPane.showMessageDialog(null, "Download Successfully!");
					frameDown.setVisible(false);
					text_Num.setText("");
					text_fileName.setText("");
					
				}else if(msg.equals("fileNotFound")){
					JOptionPane.showMessageDialog(null, "File not found! Please re-enter the file names!");
					text_Num.setText("");
					text_fileName.setText("");
					
				}else if(msg.equals("format")){
					JOptionPane.showMessageDialog(null, "Format exception! Please re-enter the file names!");
					text_Num.setText("");
					text_fileName.setText("");
				}
				
				
				

				
				//frameDis.setVisible(true);
			}

		});

	}

	public void connect() throws UnknownHostException, IOException, InterruptedException {

		clientSocket = new Socket(serverAddress, portNum);
		in = new DataInputStream(clientSocket.getInputStream());
		out = new DataOutputStream(clientSocket.getOutputStream());
		
		
		framePassword();
		
		while (true) {
			

			while (passwordLock == true) {
				System.out.print("");
			}

			send(password.length(), password.getBytes());



			String msg = new String(receive());

			if (msg.equals("You have successfully login the user system!")) {
				JOptionPane.showMessageDialog(null, "You have successfully login the user system!");
				framePass.setVisible(false);
				break;
			}

			if (msg.equals("You have successfully login the kenel system!")) {
				JOptionPane.showMessageDialog(null, "You have successfully login the kenel system!");
				framePass.setVisible(false);
				frameDirectory();
				while (directoryLock == true) {
					System.out.print("");
				}
				break;
			}
			
			if (passwordNum == 4) {
				JOptionPane.showMessageDialog(null, "Loss connection due to wrong password!");
				framePass.setVisible(false);
				disconnect();
				throw new IOException();
			}

			passwordNum++;

			JOptionPane.showMessageDialog(null, "Wrong password! "+(5-passwordNum)+" times remain.");
			passwordField.setText("");
			passwordLock=true;

		}

	}

	public void process() throws IOException {
		
		if(directoryAccessLock==true){
			frameDir.setVisible(false);
		}

		frameLocation();

		while (locationLock == true) {
			System.out.print("");
		}

		frameDisplay();

		while (totalLock == true) {
			System.out.print("");
		}

	}

	public byte[] receive() throws IOException {
		
		int ran1=in.readInt();
		int ran2=in.readInt();
		
		int size = in.readInt();
		byte[] data = new byte[size];

		do {
			int len = in.read(data, data.length - size, size);
			size = size - len;
		} while (size > 0);
		
		for(int i=0; i<data.length; i++){
			data[i]=(byte)((data[i]-ran1)+ran2);
		}

		return data;
	}

	public void send(int len, byte[] msg) {

		try {
			Random randomGenerator=new Random();
			
			int ran1=randomGenerator.nextInt(100);
			int ran2=randomGenerator.nextInt(100);
			
			out.writeInt(ran1);
			out.writeInt(ran2);
			
			for(int i=0; i<msg.length; i++){
				msg[i]=(byte) ((msg[i]+ran1)-ran2);
			}
			
			out.writeInt(len);
			out.write(msg);
		} catch (IOException ex) {
			System.out.println(ex);
		}

	}

	public void disconnect() {

		try {
			clientSocket.close();
		} catch (IOException ex) {
			System.out.println("unwanted closed in client!");
		}
	}

	public static void main(String[] args) throws InterruptedException {
		new Client_Final();
	}

}

class ReceiveFile extends Thread {

	Socket clientSocket;
	int PortNum = 8889;
	String serverAddress = "127.0.0.1";

	public ReceiveFile() {
		try {
			clientSocket = new Socket(serverAddress, PortNum);
		} catch (IOException ex) {
			System.out.println("IOException occurs when create new clientSocket!");
		}

	}

	public void run() {
		try {
			DataInputStream in = new DataInputStream(clientSocket.getInputStream());
			crFile(in);

		} catch (IOException ex) {
			System.out.println(ex);
		} finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void crFile(DataInputStream in) throws IOException {

		String fileName = new String(receive(in));
		String directory = Client_Final.dlDirectory + "/" + fileName;
		File file = new File(directory);
		file.getParentFile().mkdir();
		file.createNewFile();

		OutputStream outFile = new FileOutputStream(file);
		byte[] buffer = receive(in);
		outFile.write(buffer);
		outFile.flush();
		outFile.close();

	}

	public byte[] receive(DataInputStream in) throws IOException {
		
		int ran1=in.readInt();
		int ran2=in.readInt();
		
		int size = in.readInt();
		byte[] data = new byte[size];

		do {
			int len = in.read(data, data.length - size, size);
			size = size - len;
		} while (size > 0);
		
		for(int i=0; i<data.length; i++){
			data[i]=(byte)((data[i]-ran1)+ran2);
		}

		return data;
	}

}
