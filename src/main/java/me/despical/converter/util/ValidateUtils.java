package me.despical.converter.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;

/**
 * @author Despical
 * <p>
 * Created at 12.03.2022
 */
public class ValidateUtils {

	public static boolean isURL(String url) {
		try {
			new URL(url);
			return true;
		} catch (Exception ignored) {
			return false;
		}
	}

	public static void displayError(String message) {
		displayAlert(message, "Hata", Alert.AlertType.ERROR, ButtonType.CLOSE);
	}

	public static void displayAlert(String message, String title, Alert.AlertType alertType, ButtonType buttonType) {
		Alert alert = new Alert(alertType, null, buttonType);
		alert.getDialogPane().setContent(new VBox(5, new Text(message)));
		alert.setHeaderText(null);
		alert.setTitle(title);
		alert.setGraphic(null);
		alert.showAndWait();
	}
}
