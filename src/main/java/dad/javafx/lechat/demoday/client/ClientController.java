package dad.javafx.lechat.demoday.client;

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
	
	
	ObservableList<String> names = FXCollections.observableArrayList(
	          "Julia", "Ian", "Sue", "Matthew", "Hannah", "Stephan", "Denise");
	
	
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
	private static ListView<String> listaClientes;

	private String cadena;
	
	

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
		
		
		
		//clientes.add(datos.nickname);
		
		/*
		 * This Thread let the client recieve the message from the server for any time;
		 */
		
		
		
		
		
		/*ListView<String> listView = new ListView<String>(names);
		
		 listaClientes.getItems().setAll(names);
		*/
		
		
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

					//clientes.add(new String((String) msg.get("name")));
					
					// -- [ intentando añadir una lista fija en el hilo ] ----
					
					
					chatArea.setText(cadena);
					
					
					System.out.println("[>] "+datos.nickname);
					
					if(newMsg.getName().equals("#fill_clients_list")) {
						System.out.println("[!] La idea no es mala");	
						listaClientes.setItems(names);
						//listaClientes.getItems().add("EY");
					}
					
					
					//String cliente = (String) msg.get("name");
					//RellenarListadoClientes(cliente);
					
					//Thread.sleep(5);
					//listaClientes.getItems().add("cliente");
					
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
		listaClientes = new ListView<String>(); 
	}

	@FXML
	void onenviarButton(ActionEvent event) {

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
			
			//listaClientes.getItems().add("test");

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

	// -- [ intento de rellenar listview ] ---------------------------- 
	
	public static void RellenarListadoClientes(String cliente) {

		listaClientes.getItems().add(datos.nickname);
		
		if(!(cliente == null)) {
			
			System.out.println("[i]" + cliente);
			listaClientes.setId(cliente);
		}
		
		

	}
	/*
	 * public ClientController() { FXMLLoader loader = new
	 * FXMLLoader(getClass().getResource("/fxml/leChat.fxml"));
	 * loader.setController(this); loader.load(); }
	 */

}
