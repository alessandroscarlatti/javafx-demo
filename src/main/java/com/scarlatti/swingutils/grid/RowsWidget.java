package com.scarlatti.swingutils.grid;

import com.scarlatti.swingutils.Widget;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Monday, 12/24/2018
 * <p>
 * Ala bootstrap row.
 */
public class RowsWidget implements Widget {

    private JPanel jPanel;
    private List<Container> components = new ArrayList<>();

    public RowsWidget() {
    }

    public RowsWidget(Consumer<RowsWidget> config) {
        config.accept(this);
    }

    public void addRow(Container component) {
        components.add(component);
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

        // configure horizontal layout
        GroupLayout.ParallelGroup widgetHGroup = gl.createParallelGroup();

        for (Container component : components) {
            widgetHGroup.addComponent(component);
        }

        // configure vertical layout
        GroupLayout.SequentialGroup widgetVGroup = gl.createSequentialGroup();

        for (Container component : components) {
            widgetVGroup.addComponent(component);
        }

        gl.setHorizontalGroup(widgetHGroup);
        gl.setVerticalGroup(widgetVGroup);
    }
}
