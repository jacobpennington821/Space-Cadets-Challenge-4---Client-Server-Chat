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

public class ChatServer {
	
	public int portNumber = 31370;
	public boolean keepRunning = true;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private BufferedReader consoleInput;
	ArrayList<ServerThread> threads = new ArrayList<ServerThread>();

	public ChatServer() {
	}
	
	public void runServer() {
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while(true) {
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
				thread.sendMessage("[" + sendingThread.id + "]: " + message);
			}
		}
	}
	
	private class ServerThread extends Thread{
		
		private Socket socket;
		private int id;
		private DataInputStream input;
		private boolean keepRunning = true;	
		private DataOutputStream output;

		
		public ServerThread(Socket socket, int id) {
			this.socket = socket;
			this.id = id;
			this.start();
		}
		
		public void run() {
			System.out.println("Accepted connection -- Thread ID: " + id);
			try {
				createStreams();
			} catch (IOException e) {
				e.printStackTrace();
			}
			while(keepRunning) {
				try {
					String stringRecieved = input.readUTF();
					System.out.println(stringRecieved);
					broadcast(stringRecieved, this);
					if(stringRecieved.equals("/exit")) {
						keepRunning = false;
					}
				} catch (IOException e) {
					System.err.println("Error Recieving: " + e.getMessage());
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
		
	}
}
