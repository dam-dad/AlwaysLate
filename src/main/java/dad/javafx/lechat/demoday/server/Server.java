package dad.javafx.lechat.demoday.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

	public static List<Client> clients;
	public static List<String> clients_name;

	public static DataOutputStream dos;
	DataInputStream dis;

	Server() {

		System.out.println("[i] Server iniciado por el puerto 5555");

		String name;
		Socket client;

		clients = new ArrayList<Client>();
		clients_name = new ArrayList<String>();

		try {
			@SuppressWarnings("resource")
			ServerSocket servSock = new ServerSocket(5555);

			while (true) {
				//clients_name.clear();
				client = servSock.accept();
				dis = new DataInputStream(client.getInputStream());
				dos = new DataOutputStream(client.getOutputStream());

				name = dis.readUTF();
				Client user = new Client(name, dos, dis);
				System.out.println("Connected : " + name);
				clients.add(user);
				clients_name.add(user.name);

				String enter_message = "{ \"name\" : \"" + "[i] Mensaje del servidor > " + "\", \"message\" : \"" + name
						+ " ha entrado en el chat :)" + "\"}";
				System.out.println(enter_message);
				List<Client> entry = Server.clients;

				for (Client cli : entry) {
					DataOutputStream edos = cli.getDos();
					edos.writeUTF(enter_message);
				}

				System.out.println("[i] Cantidad de usuarios conectados: " + Server.clients.size());
				System.out.println("[i] Nombre de usuarios conectados: ");
				for (int i = 0; i < Server.clients_name.size(); i++) {
					System.out.println("\t[>] " +  Server.clients_name.get(i) + " ---> " + Server.clients.get(i));
				}

				if (!Server.clients_name.isEmpty()) {
					for (int i = 0; i < Server.clients_name.size(); i++) {
						
						String test_usuarios = "{ \"name\" : \"" + "#fill_clients_list"
								+ "\", \"message\" \"" + Server.clients_name.get(i)
								+ "\"}";

						for (Client cli : entry) {
							DataOutputStream edos = cli.getDos();
							edos.writeUTF(test_usuarios);
						}
					}

				}
			}
		} catch (IOException E) {
			E.printStackTrace();
		}
	}
}