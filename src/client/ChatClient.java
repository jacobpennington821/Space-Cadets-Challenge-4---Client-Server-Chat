package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {
	
	public int portNumber = 31370;
	private boolean keepRunning = true;
	
	private BufferedReader input;
	private DataOutputStream output;
	private Socket connection;
	
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
			//BufferedReader recieve = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		String stringToSend = "";
		String stringRecieved = "";
			
		while(!stringToSend.equals("/exit")) {
			try {
				stringToSend = input.readLine();
				output.writeUTF(stringToSend);
			} catch (IOException e) {
				System.err.println("Couldn't send message: " + e.getMessage());
			}
		}
		
		cleanup();
	}
	
	private void createStreams() throws IOException{
		input = new BufferedReader(new InputStreamReader(System.in));
		output = new DataOutputStream(connection.getOutputStream());
	}
	
	private void cleanup() {
		try {
			if(input != null) {
				input.close();
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
}
