package com.scarlatti.swingutils.grid;

import com.scarlatti.swingutils.Widget;

import javax.swing.*;
import java.awt.*;

/**
 * @author Alessandro Scarlatti
 * @since Thursday, 12/27/2018
 */
public class MarginWidget implements Widget {

    private JPanel jPanel;
    private Container wrappedComponent;

    public MarginWidget(Container wrappedComponent) {
        this.wrappedComponent = wrappedComponent;
    }

    @Override
    public Container getUi() {
        if (jPanel == null)
            initUi();

        return jPanel;
    }

    private void initUi() {
        jPanel = new JPanel();
        GroupLayout gl = new GroupLayout(jPanel);
        jPanel.setLayout(gl);

        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup widgetHGroup = gl.createSequentialGroup()
            .addComponent(wrappedComponent);

        GroupLayout.SequentialGroup widgetVGroup = gl.createSequentialGroup()
            .addComponent(wrappedComponent);

        gl.setHorizontalGroup(widgetHGroup);
        gl.setVerticalGroup(widgetVGroup);
    }
}
