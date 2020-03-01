package dad.javafx.lechat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.controlsfx.control.Notifications;
import org.fxmisc.richtext.InlineCssTextArea;

import dad.javafx.lechat.complementos.relojDigital;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;

public class ChatScene extends BorderPane implements Initializable {

	relojDigital digitalClock;
	public ChatClient chatClient;
	ArrayList<VentanaChatPrivada> privateChatWindows = new ArrayList<VentanaChatPrivada>();

	static Map<String, String> map = new TreeMap<String, String>();
	String[] colors = { "ORANGE", "CYAN", "MAGENTA", "GREEN", "BLUE", "BROWN", "ORANGE", "CYAN", "MAGENTA", "GREEN",
			"BLUE", "BROWN", "ORANGE", "CYAN", "MAGENTA", "BLACK", "GREEN", "BLUE", "BROWN", "ORANGE", "CYAN",
			"MAGENTA", "BLACK", "GREEN", "BLUE", "BROWN", "ORANGE", "CYAN", "MAGENTA", "BLACK", "GREEN", "BLUE",
			"BROWN", "ORANGE", "CYAN", "MAGENTA", "BLACK", "GREEN", "BLUE", "BROWN", "ORANGE", "CYAN", "MAGENTA",
			"BLACK", "GREEN", "BLUE", "BROWN", "ORANGE", "CYAN", "MAGENTA", "BLACK", "GREEN", "BLUE", "BROWN" };

	@FXML
	private ListView<String> clientesList;

	@FXML
	private Label userLabel;

	@FXML
	private Button privButton;

	@FXML
	private Label topLabel;

	@FXML
	private Button enviarButton;

	@FXML
	private TextField mensajeText;

	@FXML
	private InlineCssTextArea areaText;

	@FXML
	private GridPane abajoGrid;

	Scene scene;

	/**
	 * Constructor
	 * 
	 * @param senter
	 */
	public ChatScene() {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/leChatChatScene.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		scene = new Scene(this);

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Inits the Client
	 * 
	 * @param userName
	 * @param password
	 * @param type
	 */
	public void initClient(String host, int port, String userName, String password, Type type) {
		chatClient = new ChatClient(host, port, userName, password, type);
	}

	/**
	 * Update the ChatScene with the ClientInfo
	 */
	public void initChatScene() {

		// Clear
		privateChatWindows.clear();
		mensajeText.clear();
		areaText.clear();

		// Resident
		if (chatClient.getType() == Type.ADMIN) {
			// TopLabel
			topLabel.setText("Usuario: [" + chatClient.getUserName() + "] Pass: [" + chatClient.getPassword()
					+ "] Tipo: [ADMIN]");
			// SentPrivateMessage
			privButton.setOnAction(ac -> {

				if (clientesList.getItems().size() != 0 && !clientesList.getSelectionModel().isEmpty()) {
					String selected = clientesList.getSelectionModel().getSelectedItem();

					if (privateChatWindows.stream().anyMatch(window -> {
						if (window.receiver.equals(selected)) {
							window.show();
							return true;
						}
						return false;
					})) {
					} else {
						VentanaChatPrivada window = new VentanaChatPrivada(chatClient.getUserName(), selected);
						privateChatWindows.add(window);
						window.show();
					}

				}

			});
			privButton.setDisable(false);
			digitalClock.setVisible(false);

			// Guest
		} else {
			// TopLabel
			topLabel.setText("Usuario: [" + chatClient.getUserName() + "] Tipo: [INVITADO]");
			// SendPrivateMessage
			privButton.setDisable(true);
			digitalClock.setVisible(true);

		}

		// MessageField
		mensajeText.setOnAction(ac -> {
			String text = mensajeText.getText();
			if (!text.isEmpty()) {
				// -------------------
				if (text.equals("QUIT"))
					chatClient.sentMessage("QUIT");
				else if (text.equals("HELLO"))
					chatClient.sentMessage("HELLO");
				// --------------
				else if (text.equals("ROCO"))
					chatClient.sentMessage("ROCO");
				else if (text.equals("PROOMLS"))
					chatClient.sentMessage("PROOMLS");
				else if (text.equals("GETINFO"))
					chatClient.sentMessage("GETINFO");
				else {
					// emoji
					if (mensajeText.getText().contains(":D")) {
						mensajeText.setText(mensajeText.getText().replace(":D", "😁"));
						text = mensajeText.getText();
						// Print my Message to Me
						int lengthBefore = areaText.getText().length();
						areaText.appendText((text.isEmpty() ? "" : "\n") + "Usted" + "->" + text);
						areaText.setStyle(lengthBefore, (lengthBefore + 6),
								"-fx-font-weight:bold; -fx-font-size:14px; -fx-fill:maroon;");

						// Send it
						chatClient.sentMessage("GMESS" + text);
						
					} else if (mensajeText.getText().contains(":)")) {
						mensajeText.setText(mensajeText.getText().replace(":)", "🙂"));
						
						text = mensajeText.getText();
						// Print my Message to Me
						int lengthBefore = areaText.getText().length();
						areaText.appendText((text.isEmpty() ? "" : "\n") + "Usted" + "->" + text);
						areaText.setStyle(lengthBefore, (lengthBefore + 6),
								"-fx-font-weight:bold; -fx-font-size:14px; -fx-fill:maroon;");

						// Send it
						chatClient.sentMessage("GMESS" + text);
						
						
					} else if (mensajeText.getText().contains(":(")) {
						mensajeText.setText(mensajeText.getText().replace(":(", "☹️"));
						
						text = mensajeText.getText();
						// Print my Message to Me
						int lengthBefore = areaText.getText().length();
						areaText.appendText((text.isEmpty() ? "" : "\n") + "Usted" + "->" + text);
						areaText.setStyle(lengthBefore, (lengthBefore + 6),
								"-fx-font-weight:bold; -fx-font-size:14px; -fx-fill:maroon;");

						// Send it
						chatClient.sentMessage("GMESS" + text);
						
						
					} else if (mensajeText.getText().contains(":@")) {
						mensajeText.setText(mensajeText.getText().replace(":@", "😡"));
						
						text = mensajeText.getText();
						// Print my Message to Me
						int lengthBefore = areaText.getText().length();
						areaText.appendText((text.isEmpty() ? "" : "\n") + "Usted" + "->" + text);
						areaText.setStyle(lengthBefore, (lengthBefore + 6),
								"-fx-font-weight:bold; -fx-font-size:14px; -fx-fill:maroon;");

						// Send it
						chatClient.sentMessage("GMESS" + text);
						
						
					} else {

						
						// Print my Message to Me
						int lengthBefore = areaText.getText().length();
						areaText.appendText((text.isEmpty() ? "" : "\n") + "Usted" + "->" + text);
						areaText.setStyle(lengthBefore, (lengthBefore + 6),
								"-fx-font-weight:bold; -fx-font-size:14px; -fx-fill:maroon;");

						// Send it
						chatClient.sentMessage("GMESS" + text);
						
					}
					
					

				}
				mensajeText.clear();
			}
		});

		mensajeText.setCursor(new ImageCursor(new Image(getClass().getResourceAsStream("/img/cursor.png")), 0, 32));

		clientesList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> list) {
				return new CostumeCell();
			}
		});

		// SentMessage
		enviarButton.setOnAction(mensajeText.getOnAction());

	}

	/**
	 * Costume Cell Class with icon
	 *
	 */
	private static class CostumeCell extends ListCell<String> {
		@Override
		public void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			// If item is empty
			if (empty) {
				setGraphic(null);
				setText(null);
			} else {
				setContentDisplay(ContentDisplay.RIGHT);

				ImageView imageView = new ImageView(
						new Image(getClass().getResourceAsStream("/img/" + map.get(item) + ".png")));
				setGraphic(imageView);

				setText(item);
			}
		}
	}

	/**
	 * Connect the Client to Server
	 * 
	 * @return
	 */
	public boolean connectToServer() {
		return chatClient.connectToServer();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("ChatScene Initialized");

		// TopLabel
		topLabel.setStyle(
				"-fx-background-color:white; -fx-background-radius:10px; -fx-font-size:15px; -fx-font-weight:bold; -fx-text-fill:black;");

		digitalClock = new relojDigital(85, 32);
		abajoGrid.add(digitalClock, 2, 0);
	}

	// TODO ChatClient Class
	public class ChatClient implements Runnable {

		Socket socket;
		Thread thread;
		BufferedReader fromServer;
		PrintWriter toServer;
		private String userName;
		private String password;
		private Type type;
		String host;
		int port;

		/**
		 * Constructor
		 * 
		 * @param userName
		 * @param password
		 */
		public ChatClient(String host, int port, String userName, String password, Type genre) {
			this.host = host;
			this.port = port;
			this.userName = userName;
			this.password = password;
			this.type = genre;

		}

		/**
		 * Connects to the Server
		 */
		public boolean connectToServer() {

			/*********** Initialize the Socket *******/
			try {

				/******* Proxy *********/
				// ....some code here

				/******* Not Proxy *********/
				socket = new Socket(host, port);

				toServer = new PrintWriter(socket.getOutputStream(), true);
				fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				// Start the Thread
				thread = new Thread(this);
				thread.start();

				/////////// Send HELO To Server //////////
				sentMessage("LOGIN" + userName + "><:><" + password + "><:><" + type);
				sentMessage("ROCO");
				sentMessage("GETALLMESS");
			} catch (UnknownHostException e) {
				Platform.runLater(() -> {
					Notifications.create().title("Host desconocido")
							.text("Imposible de encontrar:\n[" + e.getMessage() + "]").showError();
				});
				return false;
			} catch (IOException e) {
				Platform.runLater(() -> {
					Notifications.create().title(e.getMessage())
							.text("No se puede conectar con el servidor:\n[" + host + "]").showError();
				});
				e.printStackTrace();
				return false;
			}
			return true;

		}

		/**
		 * Sends the specific message to server
		 * 
		 * @param message
		 */
		public void sentMessage(String message) {
			toServer.println(message);
		}

		/** Returns userName */
		public String getUserName() {
			return userName;
		}

		/** Returns user's password */
		public String getPassword() {
			return password;
		}

		/** Returns user's Genre(GUEST|RESIDENT) */
		public Type getType() {
			return type;
		}

		private void disconnectFromServer() {
			thread = null;

			// Close toServer
			toServer.close();

			// Close fromServer
			try {
				fromServer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Close the socket
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String message;

		/******** Implements the Thread ****************/
		@Override
		public void run() {

			/////////// Read Server Messages //////////

			while (thread != null) {
				try {
					message = fromServer.readLine();

					// ----------------------- LIST WITH ALL CLIENTS IN ROOM
					if (message.startsWith("ROOMLS")) {
						String message = this.message;
						// Clear the previous List
						Platform.runLater(() -> {
							map.clear();
							clientesList.getItems().clear();
							Arrays.stream(message.substring(6).split(";")).forEach(s -> {
								String[] z = s.split("><:><");
								map.put(z[0], z[1]);
							});
							clientesList.getItems().addAll(map.keySet());
							userLabel.setText("Users in Room " + clientesList.getItems().size() + 1);
						});
						// ----------------------- ADD THIS CLIENT IN LIST
					} else if (message.startsWith("ADD")) {
						String[] array = this.message.substring(3).split("><:><");
						Platform.runLater(() -> {
							map.put(array[0], array[1]);
							clientesList.getItems().add(array[0]);
							userLabel.setText("Users in Room " + (map.size() + 1));
						});

						// ----------------------- REMOVE THIS CLIENT FROM LIST
					} else if (message.startsWith("REMOVE")) {
						String message = this.message.substring(6);
						Platform.runLater(() -> {
							map.remove(message);
							clientesList.getItems().remove(message);
							userLabel.setText("Users in Room " + (map.size() + 1));
						});
						// ----------------------- GOT TOTAL CLIENTS IN ROOM
					} else if (message.startsWith("ROCO")) {
						String message = this.message;
						Platform.runLater(() -> {
							userLabel.setText("Users in Room " + message.substring(4));
						});

						// ----------------------- GOT Total Clients in Room
					} else if (message.startsWith("GMESS")) {
						String[] array = message.substring(5).split("><:><");
						int lengthBefore = areaText.getText().length();

						//
						Platform.runLater(() -> {

							areaText.appendText(
									(areaText.getText().isEmpty() ? "" : "\n") + array[0] + "->" + array[1]);

							areaText.setStyle(lengthBefore, (lengthBefore + array[0].length() + 3),
									"-fx-font-weight:bold; -fx-font-size:14px; -fx-fill:"
											+ colors[new ArrayList<String>(map.keySet()).indexOf(array[0])] + ";");

						});

						// ----------------------- GOT Private Message
					} else if (message.startsWith("PMESS")) {
						String[] array = message.substring(5).split("><:><");

						// Check if i had ever spoke with this person
						if (privateChatWindows.stream().anyMatch(window -> {
							if (window.receiver.equals(array[0])) {
								window.update(array[0] + "->" + array[1]);
								return true;
							}
							return false;
						})) {
							// If i hadn't spoke again create a new Private
							// Window
						} else {
							Platform.runLater(() -> {
								VentanaChatPrivada window = new VentanaChatPrivada(userName, array[0]);
								privateChatWindows.add(window);
								window.update(array[0] + " ->" + array[1]);
							});
						}

						// DISCONNECTED FROM SERVER XOXOXO go cry
					} else if (message.startsWith("DISC")) {
						String message = this.message;
						disconnectFromServer();
						// Login
						Platform.runLater(() -> {
							LoginScene.mediaPlayer.play();
							ClienteApp.stage.setScene(ClienteApp.loginscene.getScene());

							privateChatWindows.forEach(window -> window.stage.close());
							digitalClock.stopClock();
							Notifications.create().title("Desconexion del servidor")
									.text("Has sido desconectado del servidor.\n" + message.substring(4)).showWarning();
						});

						// ----------------------- GOT A TIMELIMIT by Server
					} else if (message.startsWith("TIMELIMIT")) {
						digitalClock.startClock(Integer.parseInt(message.substring(9)));

						// ------------------ GOT INFORMATIONS ABOUT THIS CLIENT
					} else if (message.startsWith("GETINFO")) {
						String[] infos = message.substring(7).split("><:><");
						Platform.runLater(() -> {
							areaText.appendText((areaText.getText().isEmpty() ? "" : "\n") + "LoggedInDate: " + infos[0]
									+ " LoggedInTime: " + infos[1]);
						});
					} else {
						String message = this.message;
						Platform.runLater(() -> {
							areaText.appendText((areaText.getText().isEmpty() ? "" : "\n") + message);
						});
					}

				} catch (IOException e) {
					e.printStackTrace();
					thread = null;
				}
			}
		}

	}

}
