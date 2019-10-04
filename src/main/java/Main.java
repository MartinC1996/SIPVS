import java.io.IOException;

import com.fiit.sipvs.XmlZadanie.helpers.Localization;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main  extends Application{



	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle(Localization.getResourceBundle().getString("mainWindowTitle"));

        Parent root = null;
        try {

            root = FXMLLoader.load(getClass().getResource("layout/main_layout.fxml"),
                    Localization.getResourceBundle());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //set scene
        Scene mainScene = new Scene(root, 800, 610);

        stage.setScene(mainScene);
        //stage.setMaximized(true);
        stage.show();
		
	}
	
	
	public static void main(String[] args) {
		Application.launch(args);
	}

}
