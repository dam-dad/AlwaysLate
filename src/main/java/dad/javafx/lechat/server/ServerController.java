package dad.javafx.lechat.server;

import java.awt.Desktop;
import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import com.sun.prism.paint.Stop;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ServerController implements Runnable, Initializable {

	// Aitor 26022020 ... 01032020
	// Arreglo de la estructura MVC mas implementaciones varias como chatroom lista
	// de clientes, etc etc... tambien se añade vista basica para el servidor

	/*
	 * /** Definicion inicial
	 * 
	 * ServerSocket serverSocket; Socket socket; Thread hilo; PrintWriter writer;
	 * 
	 * int puertoServer; int invitadosMax = 4; int tiempoMax = 15;
	 * 
	 * /** Definicion de las listas para el almacenamiento de los objetos clientes
	 * asi como los mensajes de sala
	 * 
	 * public static ArrayList<Client> clients; public ArrayList<String>
	 * mensajes_sala;
	 * 
	 * // DECLARACION DE LA VISTA FXML
	 * 
	 * 
	 * 
	 * @FXML private TextField puertoText;
	 * 
	 * @FXML private TextField invitadoText;
	 * 
	 * @FXML public TextField tiempoText;
	 * 
	 * @FXML private Button onButton;
	 * 
	 * @FXML private Button offButton;
	 * 
	 * @FXML private Button backupButton;
	 * 
	 * @FXML private Button apagarButton;
	 * 
	 * @FXML public TextArea areaText;
	 */
	// DEFINICION DEL USO DE LOS 4 BOTONES DE LA VISTA

	@FXML
	private BorderPane view;

	@FXML
	private Button onButton;

	@FXML
	private Button offButton;

	@FXML
	TextArea areaText;

	@FXML
	TextField tiempoText;

	@FXML
	private TextField invitadoText;

	@FXML
	private TextField puertoText;

	@FXML
	void onOffAction(ActionEvent event) {
		stopServer();
	}

	@FXML
	void onOnAction(ActionEvent event) {
		startServer();
	}
	
	// menu bar
	
    @FXML
    void onaboutMenuAction(ActionEvent event) {
		try {
			Desktop desk = Desktop.getDesktop();
			desk.browse(new URI("https://github.com/dam-dad/LeChat/blob/master/README.md"));
		} catch (IOException | URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }

    @FXML
    void onserverMenuAction(ActionEvent event) {
		try {
			Desktop desk = Desktop.getDesktop();
			desk.browse(new URI("https://github.com/dam-dad/LeChat/wiki/Gu%C3%ADa-del-uso-del-servidor"));
		} catch (IOException | URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

    }

    @FXML
    void onuserMenuAction(ActionEvent event) {
		try {
			Desktop desk = Desktop.getDesktop();
			desk.browse(new URI("https://github.com/dam-dad/LeChat/wiki/Gu%C3%ADa-de-uso-del-cliente-de-chat"));
		} catch (IOException | URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

    }
	
	

	// [>] REFERENCIA A BACKUP
	@FXML
	void onActivarButton(ActionEvent event) {
		areaText.setText("BACKUP ON");
	}

	@FXML
	void onApagarButton(ActionEvent event) {
		areaText.setText("BACKUP OFF");

	}

	int serverPort = 5555;
	ServerSocket serverSocket;
	Thread thread;
	Socket socket;
	PrintWriter writer;

	// Clients list
	ArrayList<Client> clients;
	// General messages List
	ArrayList<String> roomMessages;
	// maximum Guests in room
	int maximumGuests = 2;
	// Map Keep History about the Residents
	
	public ServerController() throws IOException {
		// TODO Auto-generated constructor stub
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/leChatServer.fxml"));
		loader.setController(this);
		loader.load();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// System.out.println("Server initialized");

		invitadoText.disableProperty().bind(onButton.disabledProperty());
		puertoText.disableProperty().bind(onButton.disabledProperty());
		tiempoText.disableProperty().bind(onButton.disabledProperty());
		offButton.disableProperty().bind(onButton.disabledProperty().not());

		// StartServer
		onButton.setOnAction(a -> {
			startServer();
		});

		// StopServer
		offButton.setOnAction(a -> {
			stopServer();
		});

		// infoArea
		areaText.setPrefWidth(300);
		areaText.setWrapText(true);
		areaText.setEditable(false);
		areaText.setText("Pulsa empezar para iniciar");

		puertoText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d"))
					puertoText.setText(newValue.replaceAll("\\D", ""));
				if (puertoText.getText().length() > 5)
					puertoText.setText(newValue.substring(0, 5));

				if (puertoText.getText().isEmpty())
					puertoText.setText("4444");

				serverPort = Integer.parseInt(puertoText.getText());

			}
		});

		invitadoText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d"))
					invitadoText.setText(newValue.replaceAll("\\D", ""));
				if (invitadoText.getText().length() > 5)
					invitadoText.setText(newValue.substring(0, 5));

				if (invitadoText.getText().isEmpty())
					invitadoText.setText("10");
				else if (invitadoText.getText().equals("0"))
					invitadoText.setText("1");

				maximumGuests = Integer.parseInt(invitadoText.getText());

			}
		});

		tiempoText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d"))
					tiempoText.setText(newValue.replaceAll("\\D", ""));
				if (tiempoText.getText().length() > 10)
					tiempoText.setText(newValue.substring(0, 10));

				if (tiempoText.getText().isEmpty())
					tiempoText.setText("60");
				else if (tiempoText.getText().equals("0"))
					tiempoText.setText("1");

			}
		});
	}

	@Override
	public void run() {

		/*********
		 * Accepting all the client connections and create a seperate thread
		 ******/
		while (thread != null) {
			try {

				////////// Accepting the Server Connections ///////
				socket = serverSocket.accept();

				////////// Create a Seperate Thread for that each client ///////
				new ComunicacionClienteServidor(this, socket);
				Platform.runLater(() -> {
					areaText.appendText("\n >> Nuevo cliente conectado");
				});

				Thread.sleep(150);
			} catch (InterruptedException | IOException ex) {
				stopServer();
			}
		}
		Platform.runLater(() -> {
			areaText.setText("Server has stopped!");
		});

	}

	/**
	 * Starts the Server
	 */
	private void startServer() {

		////////// Initialize the Server Socket ///////
		try {

			
			//System.out.println("[>] CanonicalHostname:\t\t"+serverSocket.getInetAddress().getCanonicalHostName());
			System.out.println("[>] Inet4Adress:\t\t"+Inet4Address.getLocalHost().getHostAddress());
			
			// found ip by datagram socket
			final DatagramSocket socket = new DatagramSocket();
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			System.out.println("[>] Datagram LocalAddress:\t"+socket.getLocalAddress().getHostAddress());
			
			String serverIp = socket.getLocalAddress().getHostAddress();
			
			serverSocket = new ServerSocket( serverPort, 1, InetAddress.getByName(serverIp) );
			
			System.out.println("Esperando conexiones por: "
					+ "\nIP:\t"+serverSocket.getInetAddress().getLocalHost().getHostAddress()
					+ "\nPuerto:\t"+serverPort);
			
		} catch (IOException e) {
			e.printStackTrace();
			areaText.setText(e.getMessage());
			return;
		}

		////////// Initialize the ArrayLists //////////
		clients = new ArrayList<Client>();
		roomMessages = new ArrayList<String>();

		////////// Initialize the thread //////////
		thread = new Thread(this);
		thread.start();

		////////// Configure the Buttons //////////
		onButton.setDisable(true);
		areaText.setText("Esperando a que se conecten clientes...");
	}

	/**
	 * Stop the Server
	 */
	void stopServer() {

		if (serverSocket != null) {
			// Disconnect All Clients
			if (clients != null)
				clients.stream().forEach(client -> sendMessageToClient(client.getSocket(), "DISC"));

			thread = null;

			////////// Close Server Socket //////////
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				// serverSocket = null;
				clients = null;
				roomMessages = null;
			}

			////////// Configure the Buttons //////////
			onButton.setDisable(false);
		}
	}

	/**
	 * Add this User to usersList
	 * 
	 * @param userName
	 */
	public boolean addUser(Socket socket, String userName, String password, String genre) {

		// No Dublicate names Allowed
		if (clients.stream().anyMatch(client -> client.getUserName().equals(userName))) {
			sendMessageToClient(socket, "DISC" + "Nombre duplicado! Prueba otro nombre de usuario");
			return false;
		}

		// (Guest+guest.size()>maximumGuest)=DISCONNECT
		if (genre.equals("INVITADO")
				&& clients.stream().filter(client -> client.getType().equals("INVITADO")).count() >= maximumGuests) {
			sendMessageToClient(socket, "DISC" + "Numero maximo de invitados permitidos (" + maximumGuests + ")");
			return false;
			// Resident
		} else {

		}

		// IF not he is the first Client in this Room
		if (clients.size() != 0) {
			String roomList = "";

			//////// Prepare the ROOM List //////////////
			for (Client client : clients)
				roomList += client.getUserName() + "><:><" + client.getType() + ";";

			areaText.appendText("\nROOMLS" + roomList);

			//////// Send a Room List To New Client////////
			sendMessageToClient(socket, "ROOMLS" + roomList);

			///////// Send the New Client Detail into All Other Clients
			///////// //////
			clients.stream().forEach(client -> {
				sendMessageToClient(client.getSocket(), "ADD" + userName + "><:><" + genre);
				sendMessageToClient(client.getSocket(), "(" + userName + ")" + "se ha unido a la sala...");
			});

		}
		/***** Add a user in to array list ***/
		clients.add(new Client(socket, userName, password, genre));
		return true;

	}

	/**
	 * Lists all Room Clients except the caller
	 * 
	 * @param socket
	 * @param userName
	 */
	public String listUsers(Socket socket, String userName) {
		String roomList = "";

		//////// Prepare the ROOM List //////////////
		for (Client client : clients)
			if (!client.getUserName().equals(userName))
				roomList += client.getUserName() + ";";

		return roomList;
	}

	String info = null;

	/**
	 * Get client Informations
	 * 
	 * @param userName
	 * @return
	 */
	public String getInfoForClient(String userName) {

		clients.stream().filter(client -> client.getUserName().equals(userName)).forEach(client -> {
			// LocalTime now = LocalTime.now();
			// long z = client.getLastLoggedInHour(). -now;

			info = client.getLastLoggedInDate() + "><:><" + client.getLastLoggedInHour();
		});

		return info;
	}

	/**
	 * Lists all the room messages
	 * 
	 * @return the list with room messages <br>
	 *         Every message has this form userName+"><:><"+message <br>
	 *         and you can get every message doing split by "><++><"
	 */
	public String listMessages() {
		String messList = "";

		//////// Prepare the ROOM List //////////////
		for (String roomMessage : roomMessages)
			messList += roomMessage + "><++><";

		return messList;
	}

	/**
	 * Removes this User from usersList
	 * 
	 * @param userName
	 */
	public void removeUser(Socket socket, String userName) {

		///////// Remove this Client from All other Clients Details//////
		Iterator<Client> iterator = clients.iterator();
		while (iterator.hasNext()) {
			Client client = iterator.next();
			if (client.getUserName().equals(userName)) {
				iterator.remove();
				break;
			}
		}
		sendMessageToClient(socket, "DISC" + "BYE!");

		///////// Notify all the Clients about Removed Client //////
		clients.stream().forEach(client -> {
			sendMessageToClient(client.getSocket(), "REMOVE" + userName);
			sendMessageToClient(client.getSocket(), "(" + userName + ")" + " ha abandonado la sala.");
		});

		areaText.appendText("\nConnected Users Now are: (" + clients.size() + ")");
	}

	/**
	 * Sents a general Message to the Room
	 * 
	 * @param clientSocker
	 * @param userName
	 */
	public void sentGeneralMessage(Socket clientSocket, String userName, String message) {

		// Add it to Array so It can be seen
		// From Future Clients that log in this room
		roomMessages.add(userName + "><:><" + message);

		areaText.appendText("\nGeneral message:(" + message + ")");

		///////// Notify all the Clients about the Message //////
		for (Client client : clients)
			if (!client.getUserName().equals(userName))
				sendMessageToClient(client.getSocket(), "GMESS" + userName + "><:><" + message);

	}

	/**
	 * Sents a private message to a user
	 * 
	 * @param clientSocket
	 * @param fromClient
	 * @param message
	 * @param toClient
	 */
	public void sentPrivateMessage(Socket clientSocket, String fromClient, String message, String toClient) {

		areaText.appendText(
				"\nMensaje privado(from->" + fromClient + " to->" + toClient + ") Mensaje: (" + message + ")");

		///////// Notify all the Clients about the Message //////
		for (Client client : clients)
			if (client.getUserName().equals(toClient))
				sendMessageToClient(client.getSocket(), "PMESS" + fromClient + "><:><" + message + "><:><" + toClient);

	}

	/***** Function To Send a Message to Client **********/
	public void sendMessageToClient(Socket clientSocket, String message) {
		if (clientSocket != null && !clientSocket.isClosed()) {
			try {
				writer = new PrintWriter(clientSocket.getOutputStream(), true);
				writer.println(message);
			} catch (IOException ex) {
				ex.printStackTrace();
				// writer.close();
			}
		}
	}


	public Parent getView() {
		return view;
	}

}