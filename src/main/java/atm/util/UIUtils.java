package atm.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class UIUtils {

    public static void typewriterAnimation(Label label, String text, Runnable onFinished) {
        final Timeline timeline = new Timeline();
        label.setGraphic(null); 
        
        for (int i = 0; i <= text.length(); i++) {
            final int index = i;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(i * 30), event -> {
                label.setText(text.substring(0, index));
            });
            timeline.getKeyFrames().add(keyFrame);
        }

        if (onFinished != null) {
            timeline.setOnFinished(event -> onFinished.run());
        }

        timeline.play();
    }
}