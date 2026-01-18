package software.ulpgc.imageviewer.application.gui;

import software.ulpgc.imageviewer.architecture.presenter.ImagePresenter.Animation;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.System.currentTimeMillis;

public class SwingAnimation implements Animation {

    private Timer timer;

    @Override
    public void animate(long duration, Consumer<Double> animationStep, Runnable onEnd) {
        if (timer != null) timer.stop();
        timer = new Timer(10, new Step(currentTimeMillis(), duration, animationStep, onEnd));
        timer.start();
    }

    private class Step implements ActionListener {

        private final long start;
        private final long duration;
        private final Consumer<Double> animationStep;
        private final Runnable onEnd;

        private Step(long start, long duration, Consumer<Double> animationStep, Runnable onEnd) {
            this.start = start;
            this.duration = duration;
            this.animationStep = animationStep;
            this.onEnd = onEnd;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            performActionWith(animationProgress());
        }

        private void performActionWith(double progress) {
            animationStep.accept(1 - pow(1 - progress, 3));
            if (progress >= 1.0) finishAnimation();
        }

        private double animationProgress() {
            return min(1.0, (double) (currentTimeMillis() - start) / duration);
        }

        private void finishAnimation() {
            timer.stop();
            onEnd.run();
        }
    }
}