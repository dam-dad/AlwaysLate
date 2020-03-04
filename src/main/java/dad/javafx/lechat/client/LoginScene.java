package dad.javafx.lechat.client;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

import org.controlsfx.control.Notifications;

import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

public class LoginScene extends StackPane implements Initializable {

	
	// vista

	@FXML
	private MediaView mediaView;

	@FXML
	private TextField nombreText;

	@FXML
	private PasswordField passText;

	@FXML
	private Button invitadoButton;

	@FXML
	private Button adminButton;

	@FXML
	private TextField ipText;

	@FXML
	private TextField portText;

	@FXML
	private Button infoButton;

	@FXML
	private Button volOnButton;

	@FXML
	private Button volOffButton;

	Scene scene;

	public LoginScene() {

		this.connected = false;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/leChatLoginScene.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		scene = new Scene(this);

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private boolean connected;
	public static MediaPlayer mediaPlayer;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		System.out.println("[!] Login Scene cargada");

		Media media = null;
		Media music = null;

		try {
			media = new Media(getClass().getResource("/video/chat.mp4").toURI().toString());
			music = new Media(getClass().getResource("/music/sample.mp3").toURI().toString());
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		MediaPlayer musicPlay = new MediaPlayer(music);
		musicPlay.play();
		musicPlay.setCycleCount(MediaPlayer.INDEFINITE);

		/*
		 * Platform.runLater(() -> {
		 * Notifications.create().text("Cliente leChat cargado con éxito").
		 * showInformation();
		 * 
		 * });
		 */

		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setMute(true);
		mediaPlayer.play();
		mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		mediaView.setMediaPlayer(mediaPlayer);

		// redimencionamiento automatico del video a la scene
		widthProperty().addListener(l -> {
			mediaView.setFitWidth(this.getWidth());
			mediaView.setFitHeight(this.getHeight());
		});

		heightProperty().addListener(l -> {
			mediaView.setFitWidth(this.getWidth());
			mediaView.setFitHeight(this.getHeight());
		});

		mediaPlayer.setOnError(() -> System.out.println("[ERROR] Media error:" + mediaPlayer.getError().toString()));

		// info
		infoButton.setOnAction(e -> {
			
			
			try {
				Desktop desk = Desktop.getDesktop();
				desk.browse(new URI("https://github.com/dam-dad/LeChat/blob/master/README.md"));
			} catch (IOException | URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			/*
			try {
				ayudita = new Ayudita();
				Stage secondcontroller = new Stage();
				secondcontroller.setScene(new Scene(ayudita.getView(), 800, 600));
				secondcontroller.setTitle("LeChat v1.0.0 ## Guía para el usuario ");
				secondcontroller.getIcons().add(new Image(getClass().getResource("/img/chatIco.png").toExternalForm()));
				secondcontroller.show();
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			*/
			
		});
		

		// volumen On
		volOnButton.setOnAction(e -> {
			musicPlay.setMute(false);
		});

		// volumen Off
		volOffButton.setOnAction(e -> {
			musicPlay.setMute(true);
		});

		// login como admin
		adminButton.setOnAction(a -> {

			musicPlay.stop();
			String nombre = nombreText.getText();
			String pass = passText.getText();

			// validacion primer paso
			if (!nombre.isEmpty() && !pass.isEmpty()) {
				tryLogin(Type.ADMIN);

			} else if (nombre.isEmpty()) {
				Platform.runLater(() -> {
					Notifications.create().text("El nombre de usuario no puede estar vacio").showWarning();
				});
			} else if (pass.isEmpty()) {
				Platform.runLater(() -> {
					Notifications.create().text("La contraseña para admin no puede estar vacia").showWarning();
				});

			}

		});

		// login como invitado
		invitadoButton.setOnAction(a -> {
			musicPlay.stop();
			String nombre = nombreText.getText();

			// validacion primer paso
			if (!nombre.isEmpty()) {
				tryLogin(Type.INVITADO);

			} else if (nombre.isEmpty()) {
				Platform.runLater(() -> {
					Notifications.create().text("El nombre de usuario no puede estar vacio").showWarning();
				});
			}
		});

		// si nuestra ip de servidor esta vacia que nos la ponga directamente como ::1

		ipText.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (ipText.getText().isEmpty())
					ipText.setText("::1");

			}

		});

		// misma idea para el puerto del servidor, usando regex, nos lo ponga a 5555

		portText.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d"))
					portText.setText(newValue.replaceAll("\\D", ""));
				if (portText.getText().length() > 5)
					portText.setText(newValue.substring(0, 5));

				if (portText.getText().isEmpty())
					portText.setText("5555");

			}
		});

	}

	/**
	 * Trys a Login
	 * 
	 * @param type
	 */
	private void tryLogin(Type type) {

		// indicator.setVisible(true);
		CountDownLatch latch = new CountDownLatch(1);
		connected = false;

		adminButton.setDisable(true);
		invitadoButton.setDisable(true);

		// -------------- Servidor
		new Thread(() -> {
			/*
			ClienteApp.chatscene.initClient(ipText.getText().replace(".", ""), Integer.parseInt(portText.getText()),
					nombreText.getText(), passText.getText(), type);
			connected = ClienteApp.chatscene.connectToServer();
			latch.countDown();
			*/
			ClienteApp.chatscene.initClient(ipText.getText(), Integer.parseInt(portText.getText()),
					nombreText.getText(), passText.getText(), type);
			connected = ClienteApp.chatscene.connectToServer();
			latch.countDown();
			
			
		}).start();

		// --------------- Wait above| Thread to finish
		new Thread(() -> {

			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Platform.runLater(() -> {
				// --------------------- Server
				if (connected) {
					ClienteApp.chatscene.initChatScene();
					ClienteApp.stage.setScene(ClienteApp.chatscene.getScene());
					// indicator.setVisible(false);
					LoginScene.mediaPlayer.pause();
				} else {
					// Notifications.create().title("Error").text("Can't Connect to
					// Server!").showError();
					// indicator.setVisible(false);
				}
				adminButton.setDisable(false);
				invitadoButton.setDisable(false);
			});
		}).start();
	}

}
