package com.scarlatti.reactive;

import com.scarlatti.swingutils.SwingUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Alessandro Scarlatti
 * @since Friday, 3/1/2019
 */
public class BlueRedTest {

    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void blueRedTest() {
        SwingUtils.display(() -> {
            JPanel jPanel = new JPanel(new BorderLayout());

            BlueRedComponent blueRedComponent = new BlueRedComponent();
            blueRedComponent._construct();
            blueRedComponent._render(jPanel);

            return jPanel;
        });
    }

    private static class BlueRedComponent extends ReactiveComponent {

        private State state;
        private JPanel jPanel;

        private static class State {
            Color color;
        }

        @Override
        public void _construct() {
            this.state = new State();
            this.state.color = Color.BLUE;
        }

        private void onClick() {
            this._setState((BlueRedComponent comp) -> {
                if (comp.state.color == Color.BLUE) {
                    comp.state.color = Color.RED;
                } else if (comp.state.color == Color.RED) {
                    comp.state.color = Color.BLUE;
                }
            });
        }

        void _renderFirst(Container container) {
            JPanel jPanel = new JPanel();
            jPanel.setBackground(this.state.color);
            jPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    BlueRedComponent.this.onClick();
                }
            });
            this.jPanel = jPanel;
            // mount first
            container.add(jPanel);
        }

        void _renderUpdate(Container container) {
            jPanel.setBackground(this.state.color);

            // no mount necessary
        }
    }
}
