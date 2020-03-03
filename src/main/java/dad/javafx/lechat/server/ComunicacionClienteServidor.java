package dad.javafx.lechat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ComunicacionClienteServidor implements Runnable {

	ServerController server;
	Socket clientSocket;
	Thread thread;

	BufferedReader fromClient;
	PrintWriter toClient;
	String text;
	String userName;
	String[] welcomeString = { "Test Entrada" };

	/**
	 * Constructor
	 * 
	 * @param server
	 * @param clientSocket
	 */
	public ComunicacionClienteServidor(ServerController server, Socket clientSocket) {
		this.server = server;
		this.clientSocket = clientSocket;
		try {
			fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			toClient = new PrintWriter(clientSocket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		thread = new Thread(this);
		thread.start();

	}

	int helloCount;

	@Override
	/******** Implement the Thread Interface *********/
	public void run() {
		while (thread != null) {
			try {
				String text = fromClient.readLine();

				// System.out.println(text);

				// if (text != null) {
				// ----------------------- SAY HELLO TO CLIENT
				if (text.startsWith("HELLO")) {
					if (helloCount != welcomeString.length)
						toClient.println("[Server]<>" + welcomeString[helloCount++]);
					else {
						server.sendMessageToClient(clientSocket, "DISC" + "Desconectado");
						disconnectUser();
					}

					// ----------------------- LOGIN CLIENT
				} else if (text.startsWith("LOGIN")) {
					String[] array = text.substring(5).split("><:><");
					userName = array[0];
					if (server.addUser(clientSocket, array[0], array[1], array[2])) {
						if (array[2].equals("INVITADO")) {
							int seconds = Integer.parseInt(server.tiempoText.getText());
							// Send these messages to Client
							toClient.println("[Server]<>Connected,you have " + (seconds / 60) + " minutes left..");
							toClient.println("TIMELIMIT" + seconds);
						} else
							toClient.println("[Server]<>Connected,you have unlimited access..");
					}

					// ----------------------- DISCONNECT FROM SERVER
				} else if (text.startsWith("QUIT")) {
					disconnectUser();

					// ----------------------- RETURN TOTAL CLIENTS IN ROOM
				} else if (text.startsWith("ROCO")) {
					toClient.println("ROCO" + server.clients.size());

					// ----------------------- SENT GENERAL MESSAGE TO ROOM
				} else if (text.startsWith("GMESS")) {
					server.sentGeneralMessage(clientSocket, userName, text.substring(5));

					// ----------------------- SENT PRIVATE MESSAGE TO ROOM
				} else if (text.startsWith("PMESS")) {
					String[] array = text.substring(5).split("><:><");
					server.sentPrivateMessage(clientSocket, array[0], array[1], array[2]);
					// ----------------------- SENT INFO ABOUT DATE AND TIME
					// CONNECTED
				} else if (text.startsWith("GETINFO")) {
					toClient.println("GETINFO" + server.getInfoForClient(userName));
				}
				// }

			} catch (IOException e) {
				e.printStackTrace();
				disconnectUser();
			}
		}

		server.areaText.appendText("\nCommunication stopped for Client:(" + userName + ")");
	}

	/**
	 * Disconnects the User
	 */
	public void disconnectUser() {
		server.areaText.appendText("\nDisconnecting Client: (" + userName + ")...");
		server.removeUser(clientSocket, userName);
		stopConnection();
		server.areaText.appendText("\nClient: (" + userName + ") has been disconnected!");
	}

	/**
	 * Stops the connection with client
	 */
	public void stopConnection() {

		thread = null;

		// Close toClient
		toClient.close();

		// Close fromClient
		try {
			fromClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Close the socket
		try {
			clientSocket.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}
}
