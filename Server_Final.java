package COMP3015_Project;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Server_Final extends JFrame implements ActionListener {
	int portNum = 8888;
	ServerSocket serverSocket;
	static String dlDirectory;
	String userPassword;
	String kernelPassword;
	boolean execution = true;

	static String taskInfo = "";

	JLabel location = new JLabel("Location: ");
	JLabel userPass = new JLabel("user Password: ");
	JLabel KerPass = new JLabel("kernel Password: ");
	JLabel taskMan = new JLabel("Task Manager: ");

	JTextField locT = new JTextField();
	JPasswordField userPassT = new JPasswordField(20);
	JPasswordField kernelPassT = new JPasswordField(20);

	JButton submit = new JButton("Submit");
	JButton reset = new JButton("Reset");

	JTextArea taskManB = new JTextArea();

	public Server_Final() {
		super("Server");
		Preprocessing();
		try {
			while (execution) {
				System.out.print("");
			}
			serverSocket = new ServerSocket(portNum);
			accept();
		} catch (IOException ex) {
			System.out.println("Connection terminated!");
		} finally {
			disconnect();
		}
	}

	public void Preprocessing() {

		// This part should turn to GUI
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
		// this.setLayout(new FlowLayout());

		locT.setPreferredSize(new Dimension(100, 26));
		taskManB.setEditable(false);

		JScrollPane scrPane = new JScrollPane(taskManB);
		scrPane.setPreferredSize(new Dimension(400, 200));
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);

		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(location)
						.addComponent(userPass).addComponent(KerPass).addComponent(taskMan))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(locT)
						.addComponent(userPassT).addComponent(kernelPassT).addComponent(scrPane))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(location)
						.addComponent(submit).addComponent(reset)));

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(location)
						.addComponent(locT))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(userPass)
						.addComponent(userPassT).addComponent(submit))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(KerPass)
						.addComponent(kernelPassT).addComponent(reset))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(taskMan)
						.addComponent(scrPane)));

		layout.linkSize(SwingConstants.HORIZONTAL, submit, reset);

		panel.setLayout(layout);
		this.add(panel);
		this.pack();
		this.setVisible(true);

		submit.addActionListener(this);
		reset.addActionListener(this);

	}

	public void accept() throws IOException {

		while (true) {

			Socket clientSocket = serverSocket.accept();
			new connectClient(clientSocket, userPassword, kernelPassword, this).start();

		}

	}

	public void disconnect() {
		try {
			serverSocket.close();
			connectClient.serverSocket.close();
		} catch (IOException ex) {
			System.out.println("Something Wrong Happens");
		}

	}

	public static void main(String[] args) {
		new Server_Final();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String cmd = ((JButton) e.getSource()).getText();
		if (cmd.equals("Submit")) {

			String dlDirectory = locT.getText().trim();
			String userPassword = new String(userPassT.getPassword());
			String kernelPassword = new String(kernelPassT.getPassword());

			if (dlDirectory.equals("") || userPassword.equals("") || kernelPassword.equals("")) {
				JOptionPane.showMessageDialog(null, "Please fill in all the blanks!");
				rePaint();
			} else {
				JOptionPane.showMessageDialog(null, "Submit successfully!");

				submit.removeActionListener(this);
				reset.removeActionListener(this);
				Server_Final.dlDirectory = dlDirectory;
				this.userPassword = userPassword;
				this.kernelPassword = kernelPassword;
				execution = false;
			}
		} else {
			rePaint();
		}

	}

	public void rePaint() {
		locT.setText("");
		userPassT.setText("");
		kernelPassT.setText("");
	}

}

class connectClient extends Thread {
	Socket clientSocket;
	String userPassword;
	String kenelPassword;
	String downloadLocation;
	Server_Final server;
	String fileList;
	int fileLength;
	int foundFile = 0;

	static int portNum = 8889;
	static ServerSocket serverSocket;

	public connectClient(Socket clientSocket, String userPassword, String kenelPassword, Server_Final server) {

		this.clientSocket = clientSocket;
		this.userPassword = userPassword;
		this.kenelPassword = kenelPassword;
		this.server = server;
		this.fileList = "";
		this.fileLength = 0;

	}

	static {
		try {
			serverSocket = new ServerSocket(portNum);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void run() {

		try {

			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
			DataInputStream in = new DataInputStream(clientSocket.getInputStream());

			passwordConnection(in, out);
			instruction(in, out);

		} catch (IOException ex) {

			System.out.println(ex);

		}

	}

	public void passwordConnection(DataInputStream in, DataOutputStream out) {

		try {

			String msg = new String(receive(in));

			while (true) {

				if (msg.equals(userPassword)) {
					String tmp = "You have successfully login the user system!";
					send(tmp.length(), tmp.getBytes(), out);
					downloadLocation = Server_Final.dlDirectory;
					break;
				} else if (msg.equals(kenelPassword)) {
					String tmp = "You have successfully login the kenel system!";
					send(tmp.length(), tmp.getBytes(), out);
					downloadLocation = new String(receive(in));
					break;
				} else {

					String tmp = "Please re-enter your password!";
					send(tmp.length(), tmp.getBytes(), out);

					msg = new String(receive(in));

				}
			}
		} catch (IOException ex) {
			System.out.println("Exception occurs in entering password!");
		}

	}

	public void iterationFile(File folder) {
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			if (file.isFile()) {
				double fileSize = file.length();
				String tmp = "FileName: " + file.getName() + "\t FileSize: " + fileSize + " bytes";
				fileList = fileList + tmp + "\n";
				this.fileLength++;
			} else if (file.isDirectory()) {
				iterationFile(file);
			}
		}
	}

	public void downloadFile(File folder) throws IOException {

		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			if (file.isFile()) {

				String fileName = file.getName();
				Socket clientSocket = serverSocket.accept();
				new DownloadFile(fileName, clientSocket, file.getAbsolutePath(), this.server).start();

			} else if (file.isDirectory()) {
				downloadFile(file);
			}
		}

	}

	public void cmdDl(File folder, String token) throws IOException {

		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
			if (file.isFile()) {

				String fileName = file.getName();
				if (fileName.equals(token)) {
					Socket clientSocket = serverSocket.accept();
					new DownloadFile(fileName, clientSocket, file.getAbsolutePath(), this.server).start();
					this.foundFile++;
					return;
				}

			} else if (file.isDirectory()) {
				cmdDl(file, token);
			}
		}

	}

	public void instruction(DataInputStream in, DataOutputStream out) {
		try {

			String msg = new String(receive(in));

			while (true) {

				if (msg.equals("cmd fs")) {

					File folder = new File(downloadLocation);
					this.iterationFile(folder);
					System.out.println(fileList.length());
					send(fileList.length(), fileList.getBytes(), out);

				} else if (msg.equals("cmd dl")) {

					msg = new String(receive(in));
					int fileNum = Integer.parseInt(msg);
					String fileName = new String(receive(in));
					String[] tokens = fileName.split(",");

					File folder = new File(downloadLocation);

					if (tokens.length != fileNum) {
						send("format".length(), "format".getBytes(), out);
					} else {

						for (int i = 0; i < fileNum; i++) {
							cmdDl(folder, tokens[i]);
						}

						if (this.foundFile == 0) {
							send("fileNotFound".length(), "fileNotFound".getBytes(), out);
						} else {
							send("success".length(), "success".getBytes(), out);
						}
						
					}

				} else if (msg.equals("cmd dlall")) {

					File folder = new File(downloadLocation);

					String len = "" + this.fileLength;
					send(len.length(), len.getBytes(), out);

					this.downloadFile(folder);

					send("true".length(), "true".getBytes(), out);

				}

				msg = new String(receive(in));
			}
		} catch (IOException ex) {
			// modify the task manager
			// System.out.println("Loss connection of IP address: " +
			// this.clientSocket.getInetAddress().getHostAddress()
			// + " Port Number: " + this.clientSocket.getPort());
			Server_Final.taskInfo = Server_Final.taskInfo + "Loss connection of IP address: "
					+ this.clientSocket.getInetAddress().getHostAddress() + " Port Number: "
					+ this.clientSocket.getPort() + "\n";

			this.server.taskManB.setText(Server_Final.taskInfo);

		} finally {

			try {
				this.clientSocket.close();
			} catch (IOException e) {
				System.out.println("Exception occurs in closing client!");
			}

		}

	}

	public byte[] receive(DataInputStream in) throws IOException {
		int ran1 = in.readInt();
		int ran2 = in.readInt();

		int size = in.readInt();
		byte[] data = new byte[size];

		do {
			int len = in.read(data, data.length - size, size);
			size = size - len;
		} while (size > 0);

		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) ((data[i] - ran1) + ran2);
		}

		return data;
	}

	public void send(int len, byte[] msg, DataOutputStream out) {

		try {
			Random randomGenerator = new Random();

			int ran1 = randomGenerator.nextInt(100);
			int ran2 = randomGenerator.nextInt(100);

			out.writeInt(ran1);
			out.writeInt(ran2);

			for (int i = 0; i < msg.length; i++) {
				msg[i] = (byte) ((msg[i] + ran1) - ran2);
			}

			out.writeInt(len);
			out.write(msg);
			out.flush();
		} catch (IOException ex) {
			System.out.println("Sending Error!");
		}

	}

}

class DownloadFile extends Thread {
	String fileName;
	Socket clientSocket;
	DataOutputStream out;
	String downloadLocation;
	Server_Final server;

	public DownloadFile(String fileName, Socket clientSocket, String downloadLocation, Server_Final server)
			throws IOException {

		this.fileName = fileName;
		this.clientSocket = clientSocket;
		this.downloadLocation = downloadLocation;
		this.server = server;

		out = new DataOutputStream(this.clientSocket.getOutputStream());

	}

	public void run() {

		try {
			sendFile(out);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public void sendFile(DataOutputStream out) throws IOException {

		send(fileName.length(), fileName.getBytes(), out);
		String directory = downloadLocation;
		File file = new File(directory);
		InputStream fileIn = new FileInputStream(file);
		byte[] buffer = new byte[(int) file.length() + 1];

		fileIn.read(buffer);
		fileIn.close();

		send(buffer.length, buffer, out);

		// modify the task manager
		// System.out.println("sending file " + fileName + " successfully!");
		Server_Final.taskInfo = Server_Final.taskInfo + "sending file " + fileName + " successfully!" + "\n";
		this.server.taskManB.setText(Server_Final.taskInfo);

	}

	public void send(int len, byte[] msg, DataOutputStream out) {

		try {

			Random randomGenerator = new Random();

			int ran1 = randomGenerator.nextInt(100);
			int ran2 = randomGenerator.nextInt(100);

			out.writeInt(ran1);
			out.writeInt(ran2);

			for (int i = 0; i < msg.length; i++) {
				msg[i] = (byte) ((msg[i] + ran1) - ran2);
			}

			out.writeInt(len);
			out.write(msg);

		} catch (IOException ex) {
			System.out.println("Sending Error!");
		}

	}

}
