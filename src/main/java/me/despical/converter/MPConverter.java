package me.despical.converter;

import com.sapher.youtubedl.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import me.despical.converter.scene.DownloadScene;
import me.despical.converter.util.FileUtils;
import me.despical.converter.util.Fonts;
import me.despical.converter.util.ValidateUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author Despical
 * <p>
 * Created at 23.01.2022
 */
public class MPConverter extends Application {

	private Stage stage;
	private TextField textField;
	private File downloadPath;
	private String targetType;

	private final GridPane gridPane;
	private final Scene scene;

	public MPConverter() {
		this.scene = new Scene(gridPane = new GridPane(), 365, 275);

		start(new Stage());
	}

	@Override
	public void start(Stage primaryStage) {
		stage = primaryStage;
		stage.setTitle("Video Converter");
		stage.setResizable(false);
		stage.setScene(scene);
		stage.setOnCloseRequest(event -> killTasks());
		scene.getStylesheets().add("style.css");

		this.initPaneSizes();
		this.initTexts();
		this.initButtons();

		FileUtils.initDefaultImage(gridPane);
		YoutubeDL.setExecutablePath("C:/Users/pc/Desktop/youtube-dl"); // testing path

		stage.show();
	}

	private void initPaneSizes() {
		gridPane.setAlignment(Pos.TOP_CENTER);
		gridPane.setVgap(5);
		gridPane.setHgap(10);
		gridPane.setPadding(new Insets(5, 0, 0, 5));
	}

	private void initTexts() {
		String information = "Paste the video address with https:// or http://";

		textField = new TextField(information);
		textField.setOnMouseClicked(event -> {
			if (textField.getText().equals(information)) {
				textField.clear();
			}
		});

		textField.setPrefSize(340, 12);
		textField.setFocusTraversable(false);

		gridPane.add(textField, 0, 1);

		Text text = new Text("* Enter video address to convert");
		text.setFont(Fonts.BOLD);

		gridPane.add(text, 0, 2);
	}

	private void initButtons() {
		MenuButton menuButton = new MenuButton("Select video format");
		MenuItem mp3 = new MenuItem("MP3"), mp4 = new MenuItem("MP4");
		mp3.setOnAction(event -> {
			menuButton.setText("MP3");
			menuButton.getItems().clear();
			menuButton.getItems().add(mp4);

			targetType = "mp3";
		});

		mp4.setOnAction(event -> {
			menuButton.setText("MP4");
			menuButton.getItems().clear();
			menuButton.getItems().add(mp3);

			targetType = "mp4";
		});

		menuButton.getItems().addAll(mp3, mp4);

		Button downloadButton = new Button("Download");
		downloadButton.setVisible(false);
		downloadButton.setOnMouseClicked(event -> {
			if (targetType == null) {
				ValidateUtils.displayError("Converting type can not be null!");
				return;
			}

			String url = textField.getText();

			if (downloadPath == null) {
				DirectoryChooser directoryChooser = new DirectoryChooser();
				directoryChooser.setTitle("Select Path");

				this.downloadPath = directoryChooser.showDialog(stage);
			}

			if (!url.isEmpty()) {
				String output = FileUtils.getTitleWithExtension(url, "." + targetType);
				YoutubeDLRequest request = new YoutubeDLRequest(url, downloadPath.getPath());
				request.setOption("ignore-errors");
				request.setOption("output", output);

				if (targetType.equals("mp3")) {
					request.setOption("audio-format", "mp3");
				}

				DownloadScene downloadScene = new DownloadScene(scene, request, output, url);
				downloadScene.showScene(stage);
			}
		});

		Button convertButton = new Button("Convert");
		convertButton.setOnMouseClicked(event -> {
			String url = textField.getText();
			boolean isURL = !FileUtils.getVideoId(url).isEmpty();

			if (isURL) {
				FileUtils.initImageFromURL(gridPane, FileUtils.getVideoThumbnailURL(url), url, true);
				ValidateUtils.displayAlert("The video has been successfully converted to other formats.", "Converting Successful", Alert.AlertType.CONFIRMATION, ButtonType.OK);
			} else {
				FileUtils.initDefaultImage(gridPane);
				ValidateUtils.displayError("We couldn't see anything here.");
			}

			downloadButton.setVisible(isURL);
		});

		HBox buttons = new HBox(5, menuButton, convertButton, downloadButton);

		gridPane.add(buttons, 0, 3, 2, 1);
	}

	public static void killTasks() {
		try {
			Runtime.getRuntime().exec("TASKKILL /F /IM youtube-dl.exe");
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
}