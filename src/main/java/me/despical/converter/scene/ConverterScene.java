package me.despical.converter.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public abstract class ConverterScene implements Restorable {

	protected final GridPane gridPane;
	protected final Scene previous, mainScene;

	public ConverterScene(Scene previous) {
		this.previous = previous;
		this.mainScene = new Scene(gridPane = new GridPane(), previous.getWidth(), previous.getHeight());
		this.mainScene.getStylesheets().add("style.css");
	}

	public abstract void initialize();

	public void showScene(Stage stage) {
		initPaneSizes();
		initialize();

		stage.setScene(mainScene);
	}

	private void initPaneSizes() {
		gridPane.setAlignment(Pos.TOP_CENTER);
		gridPane.setVgap(5);
		gridPane.setHgap(10);
		gridPane.setPadding(new Insets(5, 0, 0, 0));
	}

	// make sure to run this method on FX APP thread
	@Override
	public void restoreScene(Stage stage) {
		stage.setScene(previous);
	}
}