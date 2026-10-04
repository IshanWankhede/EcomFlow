package com.ecomflow.gui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 * Reusable animated linear-gradient background region cycling smoothly
 * through shades derived from --gradient-hero (#1a0533, #6B21E8, #0B0B0F).
 */
public class AnimatedGradientBackground extends Region {
    private final Timeline timeline;
    private final DoubleProperty gradientProgress;

    public AnimatedGradientBackground() {
        this.gradientProgress = new SimpleDoubleProperty(0.0);

        // Update CSS gradient whenever progress changes
        gradientProgress.addListener((obs, oldVal, newVal) -> {
            double t = newVal.doubleValue();

            // Interpolate colors derived from #1a0533 -> #6B21E8 -> #0B0B0F
            int r1 = (int) (15 + (35 - 15) * t);
            int g1 = (int) (2 + (10 - 2) * t);
            int b1 = (int) (35 + (65 - 35) * t);

            int r2 = (int) (65 + (130 - 65) * t);
            int g2 = (int) (15 + (45 - 15) * t);
            int b2 = (int) (180 + (245 - 180) * t);

            int stop1 = (int) (15 + 15 * t);
            int stop2 = (int) (50 + 20 * Math.cos(t * Math.PI));

            String gradStyle = String.format(
                "-fx-background-color: linear-gradient(from 0%% 0%% to 100%% 100%%, #0B0B0F 0%%, rgb(%d,%d,%d) %d%%, rgb(%d,%d,%d) %d%%, #0B0B0F 100%%);",
                r1, g1, b1, stop1,
                r2, g2, b2, stop2
            );
            setStyle(gradStyle);
        });

        timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(gradientProgress, 0.0)),
            new KeyFrame(Duration.seconds(6), new KeyValue(gradientProgress, 1.0))
        );
        timeline.setAutoReverse(true);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void stopAnimation() {
        if (timeline != null) timeline.stop();
    }

    public void playAnimation() {
        if (timeline != null) timeline.play();
    }
}
