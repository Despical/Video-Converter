package me.despical.converter;

import com.sun.javafx.application.PlatformImpl;
import javafx.embed.swing.JFXPanel;

/**
 * @author Despical
 * <p>
 * Created at 23.01.2022
 */
public class Main {

	public static void main(String[] args) {
		new JFXPanel();

		PlatformImpl.runAndWait(MPConverter::new);
	}
}