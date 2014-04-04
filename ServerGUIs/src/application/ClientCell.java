package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

public class ClientCell extends ListCell<String> {
	HBox hbox = new HBox();
	Label label = new Label();
	Pane pane = new Pane();
	Circle statusIndicator = new Circle(8, Color.web("green"));
	Button ban = new Button("Ban IP");
	Button kick = new Button("Kick");
	String lastItem;

	public ClientCell() {
		super();
		statusIndicator.setStrokeType(StrokeType.OUTSIDE);
		statusIndicator.setStroke(Color.web("black"));
		statusIndicator.setStrokeWidth(1);
		label.setStyle("-fx-padding: 0 20 0 0;");
		
		//statusIndicator.setStyle("-fx-fill: green;");
		hbox.getChildren().addAll(label, statusIndicator, pane, ban, kick);
		HBox.setHgrow(pane, Priority.ALWAYS);
		
		ban.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Ban: " + label.getText());
			}
		});
		
		kick.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Kick: " + label.getText());
			}
		});
		
		this.addEventFilter(MouseEvent.MOUSE_CLICKED,
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						if(!kick.isHover() && !ban.isHover())
							System.out.println("Cell text: " + label.getText());
					}
				});
	}
	/**
	 * 
	 * @param status - 0 for green, 1 for yellow, 2 for red
	 */
	public void changeClientStatus(int status){
		String c = "#FFFF00";
		
		switch(status){
		case 0: c = "#00FF00";	break;	// Green
		case 1: c = "#FFFF00"; 	break;	// Yellow
		case 2: c = "#FF0000"; 	break;	// Red
		}
		
		this.statusIndicator.styleProperty().bind(new SimpleStringProperty("-fx-fill: " + c + ";"));
	}

	@Override
	protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		setText(null); // No text in label of super class
		if (empty) {
			lastItem = null;
			setGraphic(null);
		} else {
			lastItem = item;
			label.setText(item != null ? item : "<null>");
			setGraphic(hbox);
		}
	}
}
