package me.despical.converter.util;

import javafx.geometry.Insets;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Despical
 * <p>
 * Created at 23.01.2022
 */
public class FileUtils {

	private static final String YT_THUMBNAIL_URL = "https://img.youtube.com/vi/<ID>/default.jpg";
	private static final String YT_VIDEO_ID_REGEX = "http(?:s)?://(?:m.)?(?:www\\.)?youtu(?:\\.be/|be\\.com/(?:watch\\?(?:feature=youtu.be&)?v=|v/|embed/|user/(?:[\\w#]+/)+))([^&#?\\n]+)";
	private static final Pattern YT_VIDEO_ID_PATTERN = Pattern.compile(YT_VIDEO_ID_REGEX, Pattern.CASE_INSENSITIVE);

	public static String getVideoId(String url) {
		final Matcher matcher = YT_VIDEO_ID_PATTERN.matcher(url);

		String id = "";

		if (matcher.find()){
			return matcher.group(1);
		}

		return id;
	}

	public static String getPureVideoURL(String url) {
		return "https://www.youtube.com/watch?v=" + getVideoId(url);
	}

	public static String getVideoThumbnailURL(String videoURL) {
		return YT_THUMBNAIL_URL.replace("<ID>", getVideoId(videoURL));
	}

	public static String getTitleQuietly(String url) {
		try {
			return readJsonFromUrl("https://www.youtube.com/oembed?url=" + getPureVideoURL(url) + "&format=json").getString("title");
		} catch (JSONException | IOException ignored) {
			return "";
		}
	}

	public static String getTitleWithExtension(String url, String extension) {
		return getTitleQuietly(url).replace(' ', '_') + extension;
	}

	private static String readAll(Reader reader) throws IOException {
		final StringBuilder builder = new StringBuilder();

		int bytes;

		while ((bytes = reader.read()) != -1) {
			builder.append((char) bytes);
		}

		return builder.toString();
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		try (InputStream stream = new URL(url).openStream()) {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
			final String jsonText = readAll(reader);

			return new JSONObject(jsonText);
		}
	}

	public static void initDefaultImage(GridPane gridPane) {
		initImageFromURL(gridPane, "question_mark.jpg", "", false);
	}

	public static void initImageFromURL(GridPane gridPane, String thumbnailURL, String url, boolean tooltip) {
		final Image image = new Image(thumbnailURL);
		final ImageView imageView = new ImageView();

		imageView.setFitWidth(340);
		imageView.setFitHeight(175);
		imageView.setImage(image);

		if (tooltip) Tooltip.install(imageView, new Tooltip(url.startsWith("http") ? FileUtils.getTitleQuietly(url) : "Unknown video"));

		final HBox borderedImage = new HBox();

		borderedImage.getStyleClass().add("image-border");
		borderedImage.getChildren().add(imageView);

		gridPane.add(borderedImage, 0, 0);

		GridPane.setMargin(imageView, new Insets(0, 0, 5, 0));
	}
}