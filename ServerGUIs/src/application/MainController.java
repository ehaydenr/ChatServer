package application;

import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Circle;
import javafx.util.Callback;

public class MainController extends AnchorPane {

	@FXML
	private ListView<String> listView;
	@FXML
	private Button play;
	@FXML
	private Circle playCircle;
	@FXML
	private Label clientCapLabel;
	@FXML
	private Slider clientCapSlider;
	@FXML
	private ProgressBar clientProgress;
	
	private boolean inverted;
	private static final String PLAY_NO_HOVER = "-fx-fill: green;";
	private static final String PLAY_HOVER = "-fx-fill: linear-gradient(#009900, #00FF00);";
	private static final String STOP_NO_HOVER = "-fx-fill: red;";
	private static final String STOP_HOVER = "-fx-fill: linear-gradient(#990000, #FF0000);";
	
	private static int clientCapMax = 30;
	private final static int DEFAULT_CLIENT_CAP = 15;
	private static int clientCap = DEFAULT_CLIENT_CAP;
	private static int numClients = 0;

	public MainController() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
				"ServerGUI.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	@FXML
	protected void initialize() {
		ObservableList<String> list = FXCollections.observableArrayList(
				"Client 1", "Client 2", "Client 3", "Client 4");
		listView.setItems(list);
		listView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> param) {
				return new ClientCell();
			}
		});
		
		play.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent arg0) {
				invertPlay();
			}
			
		});
		
		changeClientCapMax(clientCapMax);
		clientCapSlider.setValue(clientCap);
		clientCapLabel.setText(""+DEFAULT_CLIENT_CAP);
		changeClientCapMax(clientCapMax);
		clientCapSlider.valueProperty().addListener(new ChangeListener<Number>(){

			@Override
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) {
				clientCapLabel.setText(""+new_val.intValue());
				clientCap = new_val.intValue();
				clientProgress.setProgress(1-numClients/clientCap);
			}
			
		});
		
		clientProgress.setProgress(1-numClients/clientCap);
		
	}
	
	public void changeClientCapMax(int max) {
		clientCapMax = max;
		clientCapSlider.setMax(max);
		clientProgress.setProgress(1-numClients/clientCap);
	}

	void invertPlay(){
		playCircle.styleProperty().bind(
			      Bindings
			        .when(play.hoverProperty())
			          .then(
			            new SimpleStringProperty(!inverted ? STOP_HOVER : PLAY_HOVER)
			          )
			          .otherwise(
			            new SimpleStringProperty(!inverted ? STOP_NO_HOVER : PLAY_NO_HOVER)
			          )
			    );
		
		inverted = !inverted;
	}
}
