package server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
	
	public int portNumber = 31370;
	public boolean keepRunning = true;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private DataInputStream input;

	public ChatServer() {
	}
	
	public void runServer() {
		serverSocket = null;
		try {
			System.out.println("Server Started");
			serverSocket = new ServerSocket(portNumber);
			clientSocket = serverSocket.accept();
			System.out.println("Accepted connection: " + clientSocket.getLocalPort());
			createStreams();
			while(keepRunning) {
				try {
					String stringRecieved = input.readUTF();
					System.out.println(stringRecieved);
					if(stringRecieved.equals("/exit")) {
						keepRunning = false;
					}
				} catch (IOException e) {
					System.err.println("Error Recieving: " + e.getMessage());
					keepRunning = false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private void createStreams() throws IOException {
		input = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
	}
}
