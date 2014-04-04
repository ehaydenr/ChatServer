package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Main extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		// Fire up GUI
		MainController mainControl = new MainController();
		Scene scene = new Scene(mainControl);

		stage.setTitle("Chat Room Server");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
