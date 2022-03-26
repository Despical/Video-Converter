package me.despical.converter.scene;

import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import me.despical.converter.MPConverter;
import me.despical.converter.util.FileUtils;
import me.despical.converter.util.Fonts;
import me.despical.converter.util.ValidateUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Despical
 * <p>
 * Created at 19.03.2022
 */
public class DownloadScene extends ConverterScene {

	private Text text;
	private Stage stage;
	private Thread downloadThread;

	private volatile boolean stopped;

	private final String url, output;
	private final YoutubeDLRequest request;

	public DownloadScene(Scene previous, YoutubeDLRequest request, String output, String url) {
		super (previous);

		this.request = request;
		this.url = url;
		this.output = output.replace('|', '#');
	}

	@Override
	public void initialize() {
		initTexts();
		initButtons();

		FileUtils.initImageFromURL(gridPane, FileUtils.getVideoThumbnailURL(url), url, true);
	}

	@Override
	public void showScene(Stage stage) {
		this.stage = stage;

		downloadThread = new Thread(() -> {
			try {
				YoutubeDL.execute(request, (progress, etaInSeconds) -> {
					System.out.println("Progress: " + progress);

					text.setText("* Downloading... %" + progress);

					if (progress == 100) {
						restoreScene(stage);
					}
				});
			} catch (YoutubeDLException e) {
				Platform.runLater(() -> {
					super.restoreScene(stage);

					ValidateUtils.displayError("HTTP Error 403: Forbidden / Download progress " + (stopped ? "manually stopped" : "interrupted"));
				});
			}
		});

		downloadThread.start();

		super.showScene(stage);
	}

	@Override
	public void restoreScene(Stage stage) {
		Platform.runLater(() -> {
			super.restoreScene(stage);

			ValidateUtils.displayAlert("Successfully downloaded video with selected format.", "Download Successful", Alert.AlertType.CONFIRMATION, ButtonType.CLOSE);
		});
	}

	private void initTexts() {
		text = new Text("* Downloading...");
		text.setFont(Fonts.BOLD);

		Text titleText = new Text("* " + output.replace('_', ' ').replace('#', '|').replace(".mp3", ""));
		titleText.setFont(Fonts.BOLD);

		gridPane.add(text, 0, 1);
		gridPane.add(titleText, 0, 2);
	}

	private void initButtons() {
		Button cancelButton = new Button("Cancel Download");
		cancelButton.setOnMouseClicked(event -> {
			System.out.println("Cancelling download progress...");

			text.setText("* Cancelling download progress...");

			stopDownload();
			cancelButton.setDisable(true);
		});

		gridPane.add(cancelButton, 0, 3);
	}

	private void stopDownload() {
		if (downloadThread.isAlive()) {
			try {
				this.stopped = true;

				downloadThread.interrupt();
				MPConverter.killTasks();

				System.out.println("Stopped download progress.");
			} catch (Exception exception) {
				System.out.println("Download progress couldn't be cancelled.");
			}
		}

		System.out.println("Restoring stage back to previous scene and deleting cache files.");

		try {
			Thread.sleep(1000); // wait task manager to kill youtube-dl.exe
			Files.deleteIfExists(Paths.get(request.getDirectory() + File.separator + output + ".part"));

			System.out.println("Deletion successful.");
		} catch (IOException | InterruptedException e) {
			System.out.println("File is still in use.");
		}

		super.restoreScene(stage);
	}
}