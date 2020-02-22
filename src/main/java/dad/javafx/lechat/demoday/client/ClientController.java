package dad.javafx.lechat.demoday.client;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import dad.javafx.lechat.demoday.login.datos;

public class ClientController implements Initializable {

	public static Thread th;
	Socket sock;
	DataOutputStream dos;
	DataInputStream dis;

	// -- [ lista de ejemplo para los clientes ] ---------------------------
	ObservableList<String> names = FXCollections.observableArrayList("Julia", "Ian", "Sue", "Matthew", "Hannah","Stephan", "Denise");
	ObservableList<String> clientes_nick = FXCollections.observableArrayList();
	
	
	@FXML
	private BorderPane view;

	@FXML
	private TextArea chatArea;

	@FXML
	private TextField mensajeText;

	@FXML
	private Button enviarButton;

	@FXML
	private Button salirButton;

	@FXML
	public ListView<String> listaClientes;

	private String cadena;
	protected ListProperty<String> listProperty = new SimpleListProperty<>();
	protected List<String> usersCurrencyList = new ArrayList<>();

	public ClientController() throws IOException {

		/*
		 * FXMLLoader loader = new
		 * FXMLLoader(getClass().getResource("/fxml/leChatLogin.fxml"));
		 * loader.setController(this); loader.load();
		 */

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/leChat.fxml"));
		loader.setController(this);
		loader.load();

		sock = new Socket(datos.ip, datos.port);
		dos = new DataOutputStream(sock.getOutputStream());
		dis = new DataInputStream(sock.getInputStream());

		dos.writeUTF(datos.nickname);

		th = new Thread(() -> {
			try {

				JSONParser parser = new JSONParser();

				while (true) {
					String newMsgJson = dis.readUTF();

					System.out.println("RE : " + newMsgJson);
					Message newMsg = new Message();

					Object obj = parser.parse(newMsgJson);
					JSONObject msg = (JSONObject) obj;

					newMsg.setName((String) msg.get("name"));
					newMsg.setMessage((String) msg.get("message"));

					cadena += newMsg.getName() + " : " + newMsg.getMessage() + "\n";

					// clientes.add(new String((String) msg.get("name")));

					// -- [ intentando añadir una lista fija en el hilo ] ----

					chatArea.setText(cadena);

					System.out.println("[>] Mi nombre de usuario es: " + datos.nickname);

					if (newMsg.getName().equals("#fill_clients_list")) {

						System.out.println("[!] #fill_clients_list");
						
						clientes_nick.add(newMsg.getMessage());
						//listaClientes.setItems(names);
						listaClientes.getItems().removeAll();
						listaClientes.getItems().addAll(clientes_nick);
						// listaClientes.getItems().add("EY");
					}

					// String cliente = (String) msg.get("name");
					// RellenarListadoClientes(cliente);

					// Thread.sleep(5);
					// listaClientes.getItems().add("cliente");

				}
			} catch (Exception E) {
				E.printStackTrace();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});

		th.start();

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}

	@FXML
	void onenviarButton(ActionEvent event) {
		//listProperty.set(FXCollections.observableArrayList(usersCurrencyList));
		
		//listaClientes.setItems(names);
		
		try {
			String msg = mensajeText.getText();

			// String json = "{" + " 'name' : '" + data.name + "', 'message' : '" + msg +
			// "'" + "}";

			JSONObject js = new JSONObject();
			js.put("name", datos.nickname);
			js.put("message", msg);

			String json = js.toJSONString();

			System.out.println(json);

			dos.writeUTF(json);
			mensajeText.setText("");
			mensajeText.requestFocus();

			// listaClientes.getItems().add("test");

		} catch (IOException E) {
			E.printStackTrace();
		}

	}

	/*
	 * public void buttonPressed(KeyEvent e) {
	 * if(e.getCode().toString().equals("ENTER")) { onenviarButton(); } }
	 */

	@FXML
	void onsalirButton(ActionEvent event) {
		System.exit(0);
	}

	public BorderPane getView() {
		return view;

	}

}
