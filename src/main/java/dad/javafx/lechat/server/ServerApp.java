package dad.javafx.lechat.server;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ServerApp extends Application {

	private static Stage primaryStage;
	private ServerController controller;
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		ServerApp.primaryStage = primaryStage;
		controller = new ServerController();
		
		primaryStage.setTitle("LeChat v1.0.0 SERVER APP");
		primaryStage.setScene(new Scene(controller.getView()));
		primaryStage.getIcons().add(new Image(getClass().getResource("/img/chatIco.jpg").toExternalForm()));
		primaryStage.setOnCloseRequest(c -> {
			controller.stopServer();
			System.exit(0);
		});
		primaryStage.show();
		
	}
	
	public static Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
