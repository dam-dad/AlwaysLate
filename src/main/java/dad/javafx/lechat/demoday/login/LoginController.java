package dad.javafx.lechat.demoday.login;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import dad.javafx.lechat.demoday.client.ClientController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class LoginController implements Initializable {
	
	/*
    @FXML
    private BorderPane view;
    */
	
	@FXML
    private AnchorPane view;
    
    @FXML
    private TextField nicknameText;

    @FXML
    private TextField servidorText;

    @FXML
    private TextField portText;
    
    @FXML
    public String tempPort;

    @FXML
    private Button conectarButton;
    
    // -- [ mejora de movilidad de la ventana ] ---------------------------------------------
    
    private double xOffset = 0;
    private double yOffset = 0;
    

    public LoginController() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/leChatLogin.fxml"));
		loader.setController(this);
		loader.load();
	}
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
    @FXML
    void onconectarButtonAction(ActionEvent event) throws IOException {
    	
    	System.out.println("[i] click en conectar");
    	
    	// -- [ data save ] --------------------------
    	datos.nickname = nicknameText.getText();
    	datos.ip = servidorText.getText();
    	
    	this.tempPort = portText.getText();
    	datos.port = Integer.parseInt(tempPort);
    	
    	// -- [ test launch chat ] ------------------------------
    	
        Stage stage;
        stage = (Stage) servidorText.getScene().getWindow();
        
        stage.close();
        stage = new Stage();
    	
        // Parent root = FXMLLoader.load(login.class.getResource("/fxml/leChat.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("/fxml/leChat.fxml"));        
        //Parent root =  FXMLLoader.load(LoginController.class.getResource("/fxml/leChat.fxml"));
        ClientController controller = new ClientController();
        
        stage.setScene(new Scene(controller.getView(), 600, 400));
        //stage.setScene(new Scene(controller.getView()));
        stage.setTitle("Welcome to leChat "+datos.nickname);
        stage.setOnCloseRequest(e-> {
            //e.consume();
            ClientController.th.stop();
            System.exit(0);
        });
        stage.setResizable(true);
        stage.show();
    	
    	
    }

    /*
	public BorderPane getView() {
		return view;
	}
	*/
    
    @FXML
    void CerrarAction(ActionEvent event) {
    	 System.exit(0);
    }

	public AnchorPane getView() {
		return view;
	}
    
    
	

}	
