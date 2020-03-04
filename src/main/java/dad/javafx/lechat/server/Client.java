package dad.javafx.lechat.server;

import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;

public class Client {

	private Socket socket;
	private String userName;
	private String password;
	private String type;
	private LocalDate lastLoggedInDate;
	private LocalTime lastLoggedInHour;

	/**
	 * ADMINISTRADOR
	 * 
	 * @param socket
	 * @param userName
	 * @param password
	 * @param type
	 */
	public Client(Socket socket, String userName, String password, String type) {
		setSocket(socket);
		setUserName(userName);
		setPassword(password);
		setType(type);
		setLastLoggedInDate(LocalDate.now());
		setLastLoggedInHour(LocalTime.now());
	}

	/**
	 * INVITADO
	 * 
	 * @param socket
	 * @param userName
	 * @param type
	 */
	public Client(Socket socket, String userName, String type) {
		setSocket(socket);
		setUserName(userName);
		setType(type);
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public LocalDate getLastLoggedInDate() {
		return lastLoggedInDate;
	}

	public void setLastLoggedInDate(LocalDate lastLoggedInDate) {
		this.lastLoggedInDate = lastLoggedInDate;
	}

	public LocalTime getLastLoggedInHour() {
		return lastLoggedInHour;
	}

	public void setLastLoggedInHour(LocalTime lastLoggedInHour) {
		this.lastLoggedInHour = lastLoggedInHour;
	}

	@Override
	public String toString() {
		return userName;
	}

}