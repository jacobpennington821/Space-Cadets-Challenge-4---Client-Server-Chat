package protocol;

import server.ChatServer.ServerThread;


public class ChatProtocol {
	
	boolean server = false;
	
	ServerThread sThread;
	
	public ChatProtocol(ServerThread thread) {
		this.server = true;
		this.sThread = thread;
	}
	
	public ChatProtocol() {
		this.server = false;
	}
	

	public String decodeMessage(String input) {
		StringBuilder output = new StringBuilder("");
		String[] inputArray = input.split(" ");
		if(inputArray[0].equals("COMMAND")) {
			switch(inputArray[1]) {
				case "nick":
					if(server) {
						sThread.setUsername(inputArray[2]);
						output.append("Set username to: " + sThread.getUsername());
					} else {
						output.append("nick command doesn't work on a client");
					}
					break;
				default:
					output.append("command not recognised: " + inputArray[1]);
			}
		} else if(inputArray[0].equals("MESSAGE")){
			for(int i = 1; i < inputArray.length; i++) {
				output.append(inputArray[i] + " ");
			}
		}
		return output.toString();
	}
	
	public String encodeMessage(String input) {
		StringBuilder output = new StringBuilder("");
		String[] inputArray = input.split(" ");
		if(input.startsWith("/")) {
			output.append("COMMAND ");
			output.append(inputArray[0].substring(1, inputArray[0].length()));
			for(int i = 1; i < inputArray.length; i++) {
				output.append(" ");
				output.append(inputArray[i]);
			}
		} else {
			output.append("MESSAGE ");
			output.append(input);
		}
		return output.toString();
	}
}
