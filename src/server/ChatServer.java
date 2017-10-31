package server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import protocol.ChatProtocol;

public class ChatServer {
	
	public int portNumber = 31370;
	public boolean keepServerRunning = true;
	private ServerSocket serverSocket;
	private BufferedReader consoleInput;
	ArrayList<ServerThread> threads = new ArrayList<ServerThread>();

	public ChatServer() {
	}
	
	public void runServer() {
		try {
			serverSocket = new ServerSocket(portNumber);
			System.out.println("Created server listening on port " + portNumber);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while(keepServerRunning) {
			try {
				Socket tempSocket = serverSocket.accept();
				threads.add(new ServerThread(tempSocket, threads.size()));
			} catch(IOException e) {
				System.err.println("Error creating thread: " + e.getMessage());
			}
		}
	}
	
	public synchronized void broadcast(String message, ServerThread sendingThread) {
		for(ServerThread thread : threads) {
			if(thread != sendingThread) {
				if(sendingThread.username != null) {
					thread.sendMessage("[" + sendingThread.username + "]: " + message);
				} else {
					thread.sendMessage("[" + sendingThread.socketId + "]: " + message);
				}
			}
		}
	}
	
	public class ServerThread extends Thread{
		
		private Socket socket;
		private int socketId;
		private DataInputStream input;
		private boolean keepRunning = true;	
		private DataOutputStream output;
		private ChatProtocol protocol;
		private String username = null;
		
		public ServerThread(Socket socket, int id) {
			this.socket = socket;
			this.socketId = id;
			this.start();
		}
		
		public void run() {
			protocol = new ChatProtocol(this);

			System.out.println("Accepted connection -- Thread ID: " + socketId);
			try {
				createStreams();
			} catch (IOException e) {
				e.printStackTrace();
			}
			while(keepRunning) {
				try {
					String stringRecieved = input.readUTF();
					System.out.println(stringRecieved + " --> " + protocol.decodeMessage(stringRecieved));
					broadcast(protocol.decodeMessage(stringRecieved), this);
					if(stringRecieved.equals("/exit")) {
						keepRunning = false;
					}
				} catch (IOException e) {
					System.err.println("Error Recieving: " + e.getMessage() + " on thread " + socketId);
					keepRunning = false;
				}
			}
		}
		
		private void createStreams() throws IOException {
			input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			output = new DataOutputStream(socket.getOutputStream());
		}
		
		public void sendMessage(String message) {
			try {
				output.writeUTF(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void disconnect() {
			try {
				socket.close();
			} catch (IOException e) {
				System.err.println("Error closing socket: " + e.getMessage());
			}
		}
		
		public void setUsername(String username) {
			this.username = username;
		}
		
		public String getUsername() {
			return username;
		}

		public int getSocketId() {
			return socketId;
		}
		
	}
}
