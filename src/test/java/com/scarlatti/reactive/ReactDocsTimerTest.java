package com.scarlatti.reactive;

import com.scarlatti.swingutils.SwingUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;

/**
 * @author Alessandro Scarlatti
 * @since Friday, 3/1/2019
 */
public class ReactDocsTimerTest {
    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void testTimer() {
        SwingUtils.display(() -> {
            JPanel jPanel = new JPanel();

            Timer timer = new Timer();
            timer._construct();
            timer._render(jPanel);

            return jPanel;
        });
    }

    private static class Timer extends ReactiveComponent {

        private State state;
        private JLabel jLabel;

        private class State {
            int seconds;
        }

        @Override
        public void _construct() {
            this.state = new State();
            this.state.seconds = 0;

            Executors.newSingleThreadExecutor().submit(() -> {
                for (;;) {
                    tick();
                    Thread.sleep(1000);
                }
            });
        }

        private void tick() {
            this._setState((Timer timer) -> {
                timer.state.seconds += 1;
            });
        }

        void _renderFirst(Container container) {
            jLabel = new JLabel();
            jLabel.setText("Seconds: " + state.seconds);
            container.add(jLabel);
        }

        void _renderUpdate(Container container) {
            jLabel.setText("Seconds: " + state.seconds);
        }
    }
}
