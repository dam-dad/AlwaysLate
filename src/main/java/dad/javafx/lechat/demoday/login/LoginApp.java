package dad.javafx.lechat.demoday.login;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginApp extends Application {

	private static Stage primaryStage;
	private LoginController controller;

	// define your offsets here
	private double xOffset = 0;
	private double yOffset = 0;

	@Override
	public void start(Stage primaryStage) throws Exception {

		LoginApp.primaryStage = primaryStage;
		controller = new LoginController();

		// -- [ permitir mover la ventana ] ----------------------------------

		// grab your root here
		controller.getView().setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = event.getSceneX();
				yOffset = event.getSceneY();
			}
		});

		// move around here
		controller.getView().setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				primaryStage.setX(event.getScreenX() - xOffset);
				primaryStage.setY(event.getScreenY() - yOffset);
			}
		});

		primaryStage.setTitle("LeChat v1.0.0");
		primaryStage.initStyle(StageStyle.UNDECORATED);
		// primaryStage.setScene(new Scene(controller.getView(), 600,400));
		primaryStage.setScene(new Scene(controller.getView()));
		primaryStage.getIcons().add(new Image(getClass().getResource("/img/chatIco.jpg").toExternalForm()));
		//primaryStage.setIconified(true);
		primaryStage.show();

	}

	public static Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}

}