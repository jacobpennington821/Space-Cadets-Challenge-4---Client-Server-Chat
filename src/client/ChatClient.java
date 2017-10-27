package client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {
	
	public int portNumber = 31370;
	
	private BufferedReader consoleInput;
	private DataOutputStream output;
	private Socket connection;
	private ClientRecieveThread recieve;
	
	public ChatClient() {
		try {
			System.out.println("Client Started");
			connection = new Socket("localhost", portNumber);
			createStreams();
		} catch (UnknownHostException e) {
			System.err.println("Cant find host: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("IO error: " + e.getMessage());
		}

		String stringToSend = "";
		
		recieve = new ClientRecieveThread();
		recieve.start();
		
		while(!stringToSend.equals("/exit")) {
			try {
				stringToSend = consoleInput.readLine();
				output.writeUTF(stringToSend);
			} catch (IOException e) {
				System.err.println("Couldn't send message: " + e.getMessage());
			}
		}
		
		cleanup();
	}
	
	private void createStreams() throws IOException{
		consoleInput = new BufferedReader(new InputStreamReader(System.in));
		output = new DataOutputStream(connection.getOutputStream());
	}
	
	private void cleanup() {
		try {
			if(consoleInput != null) {
				consoleInput.close();
			}
			if(output != null) {
				output.close();
			}
			if(connection != null) {
				connection.close();
			}
		} catch (IOException e) {
			System.err.println("Error shutting down: " + e.getMessage());
		}
	}
	
	private class ClientRecieveThread extends Thread{
		
		private DataInputStream recieve;

		public ClientRecieveThread() {
			
		}
		
		public void run() {
			try {
				recieve = new DataInputStream(new BufferedInputStream(connection.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			while(true) {
				try {
					System.out.println(recieve.readUTF());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
