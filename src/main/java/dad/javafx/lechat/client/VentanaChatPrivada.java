package dad.javafx.lechat.client;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class VentanaChatPrivada extends BorderPane implements Initializable{

	@FXML
	private TextArea messagesArea;

	@FXML
	private TextField messageField;

	@FXML
	private Button sentMessage;

	@FXML
	private Label label;

	String senter;
	String receiver;
	Stage stage;

	/**
	 * Constructor
	 * 
	 * @param owner
	 * @param receiver
	 * @throws IOException 
	 */
	public VentanaChatPrivada(String senter, String receiver)  {
		
		this.senter = senter;
		this.receiver = receiver;
		this.stage = new Stage();

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/leChatPrivateChat.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();

			stage.setAlwaysOnTop(true);
			stage.initStyle(StageStyle.UNIFIED);
			stage.setScene(new Scene(this));
			//stage.getScene().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
			stage.show();
		} catch (Exception e) {
			System.out.println("[i] Problema en la carga del chat privado");
			}

	}

	/**
	 * Updates the Text of MessagesArea
	 * 
	 * @param text
	 */
	public void update(String text) {
		Platform.runLater(() -> {
			messagesArea.appendText((messagesArea.getText().isEmpty() ? "" : "\n") + text);
			stage.show();
		});

	}

	/**
	 * Shows the private chat Window
	 */
	public void show() {
		stage.show();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Private Chat Window Initialized....");

		label.setText("CHAT PRIVADO [" + receiver +" ]");
		stage.setTitle(label.getText());

		messageField.setOnAction(ac -> {
			if (!messageField.getText().isEmpty()) {
				ClienteApp.chatscene.chatClient
						.sentMessage("PMESS" + senter + "><:><" + messageField.getText() + "><:><" + receiver);
				update("Usted" + "->" + messageField.getText());
				messageField.clear();
			}
		});

		sentMessage.setOnAction(messageField.getOnAction());
	}

}
