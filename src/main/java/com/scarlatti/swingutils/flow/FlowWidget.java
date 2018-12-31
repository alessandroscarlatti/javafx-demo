package com.scarlatti.swingutils.flow;

import com.scarlatti.swingutils.Widget;
import com.scarlatti.swingutils.grid.RowsWidget;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import static javax.swing.GroupLayout.Alignment.CENTER;
import static javax.swing.GroupLayout.Alignment.TRAILING;
import static javax.swing.LayoutStyle.ComponentPlacement.RELATED;
import static javax.swing.LayoutStyle.ComponentPlacement.UNRELATED;

/**
 * @author Alessandro Scarlatti
 * @since Monday, 12/31/2018
 */
public class FlowWidget implements Widget {

    private JPanel widgetPanel;
    private JPanel contentPanel;
    private JButton backButton;
    private JButton nextButton;
    private JButton terminalButton;

    public FlowWidget() {
    }

    public FlowWidget(Consumer<FlowWidget> config) {
        config.accept(this);
    }

    public static Container ui() {
        return new FlowWidget().getUi();
    }

    public static Container ui(Consumer<FlowWidget> config) {
        return new FlowWidget(config).getUi();
    }

    @Override
    public Container getUi() {
        if (widgetPanel == null)
            initUi();

        return widgetPanel;
    }

    private void initUi() {
        widgetPanel = new JPanel();
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.GREEN);

        backButton = new JButton("Back");
        nextButton = new JButton("Next");
        terminalButton = new JButton("Cancel");

        GroupLayout gl = new GroupLayout(widgetPanel);
        widgetPanel.setLayout(gl);

        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        GroupLayout.ParallelGroup widgetHGroup = gl.createParallelGroup(CENTER);
        GroupLayout.SequentialGroup widgetVGroup = gl.createSequentialGroup();
        GroupLayout.SequentialGroup flowNavbarHGroup = gl.createSequentialGroup();
        GroupLayout.ParallelGroup flowNavbarVGroup = gl.createParallelGroup(TRAILING);

        flowNavbarHGroup
            .addPreferredGap(RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(backButton)
            .addComponent(nextButton)
            .addPreferredGap(UNRELATED)
            .addComponent(terminalButton);

        widgetHGroup
            .addComponent(contentPanel)
            .addGroup(flowNavbarHGroup);

        flowNavbarVGroup
            .addComponent(backButton)
            .addComponent(nextButton)
            .addComponent(terminalButton);

        widgetVGroup
            .addComponent(contentPanel)
            .addGroup(flowNavbarVGroup);

        gl.setHorizontalGroup(widgetHGroup);
        gl.setVerticalGroup(widgetVGroup);
    }
}
