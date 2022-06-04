package me.despical.converter;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

import java.awt.*;
import java.net.URL;

/**
 * @author Despical
 * <p>
 * Created at 4.06.2022
 */
public class ControlMenu {

	private final Menu fileMenu, helpMenu;
	private final MenuBar menuBar;

	public ControlMenu(TextField textField) {
		this.menuBar = new MenuBar();
		this.menuBar.getMenus().addAll(fileMenu = new Menu("File"), helpMenu = new Menu("Help"));

		final MenuItem exitItem = new MenuItem("Exit");
		exitItem.setOnAction(event -> System.exit(0));

		final MenuItem clearItem = new MenuItem("Clear Text");
		clearItem.setOnAction(event -> textField.clear());

		String[][] links = {{"GitHub", "http://www.github.com/Despical/Video-Converter"}, {"Issue Tracker", "http://www.github.com/Despical/Video-Converter/issues"}};

		for (int i = 0; i < links.length; i++) {
			MenuItem item = new MenuItem(links[i][0]);
			int temp = i;

			item.setOnAction(event -> {
				try {
					Desktop.getDesktop().browse(new URL(links[temp][1]).toURI());
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			});

			this.helpMenu.getItems().add(item);
		}

		this.fileMenu.getItems().addAll(exitItem, clearItem);
	}

	public MenuBar getMenuBar() {
		return menuBar;
	}
}