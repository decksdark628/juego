package Controlador;

import Modelo.ImagePaths;
import javafx.scene.Scene;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;

public class Cursor {
    private static ImageCursor customCursor;

    public static void applyCustomCursor(Scene scene) {
        if (customCursor == null) {
            Image img = new Image(
                Cursor.class.getResourceAsStream(ImagePaths.CURSOR)
            );
            double hotspotX = img.getWidth() / 2.0;
            double hotspotY = img.getHeight() / 2.0;
            customCursor = new ImageCursor(img, hotspotX, hotspotY);
        }
        scene.setCursor(customCursor);
    }
}

