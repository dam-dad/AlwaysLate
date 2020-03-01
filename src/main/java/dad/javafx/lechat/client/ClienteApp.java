package dad.javafx.lechat.client;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ClienteApp extends Application {

	public static Stage stage;
	public static ChatScene chatscene;
	public static LoginScene loginscene;

	@Override
	public void start(Stage stage) throws Exception {

		ClienteApp.stage = stage;

		chatscene = new ChatScene();
		loginscene = new LoginScene();

		stage.setTitle("leChat v1.0.0 Client APP");
		stage.setOnCloseRequest(c -> {
			System.exit(0);
		});
		stage.getIcons().add(new Image(getClass().getResource("/img/chatIco.jpg").toExternalForm()));

		// carga de las escenas

		stage.setScene(loginscene.getScene());

		stage.show();

		stage.setOnCloseRequest(c -> {
			System.exit(0);
		});

	}

	public static void main(String[] args) {
		launch(args);
	}

}
