package atm;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * Утилитарный класс для вспомогательных функций интерфейса.
 */
public class UIUtils {

    /**
     * Анимирует появление текста в Label по одной букве (эффект "пишущей машинки").
     * @param label Лейбл, в котором будет анимирован текст.
     * @param text Текст для анимации.
     * @param onFinished Действие, которое нужно выполнить после завершения анимации.
     */
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