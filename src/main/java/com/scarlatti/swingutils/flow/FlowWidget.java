package com.scarlatti.swingutils.flow;

import com.scarlatti.swingutils.Widget;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import static javax.swing.GroupLayout.Alignment.CENTER;
import static javax.swing.LayoutStyle.ComponentPlacement.UNRELATED;

/**
 * @author Alessandro Scarlatti
 * @since Monday, 12/31/2018
 */
public class FlowWidget implements Widget {

    private JPanel widgetPanel;
    private JButton backButton;
    private JButton nextButton;
    private JButton cancelButton;
    private JButton finishButton;

    private boolean canGoBack;
    private boolean canGoNext;
    private boolean canCancel;
    private boolean canFinish;
    private boolean hasCancelOption = true;
    private boolean hasFinishOption = true;

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

        backButton = new JButton("< Back");
        nextButton = new JButton("Next >");
        cancelButton = new JButton("Cancel");
        finishButton = new JButton("Finish");

        configureWidgetLayoutByState();
        configureWidgetEvents();
        configureWidgetButtonsByState();
    }

    private void configureWidgetEvents() {
        backButton.addActionListener(e -> goBack());
        nextButton.addActionListener(e -> goNext());
        cancelButton.addActionListener(e -> cancel());
        finishButton.addActionListener(e -> finish());
    }

    private void configureWidgetButtonsByState() {
        backButton.setEnabled(canGoBack);
        nextButton.setEnabled(canGoNext);
        cancelButton.setEnabled(canCancel);
        finishButton.setEnabled(canFinish);
    }

    private void configureWidgetLayoutByState() {
        GroupLayout gl = new GroupLayout(widgetPanel);
        widgetPanel.setLayout(gl);

        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup widgetHGroup = gl.createSequentialGroup();
        GroupLayout.ParallelGroup widgetVGroup = gl.createParallelGroup(CENTER);

        widgetHGroup
            .addComponent(backButton)
            .addComponent(nextButton);

        widgetVGroup
            .addComponent(backButton)
            .addComponent(nextButton);

        if (hasCancelOption || hasFinishOption) {
            widgetHGroup.addPreferredGap(UNRELATED);
        }

        if (hasCancelOption) {
            widgetHGroup.addComponent(cancelButton);
            widgetVGroup.addComponent(cancelButton);
        }

        if (hasFinishOption) {
            widgetHGroup.addComponent(finishButton);
            widgetVGroup.addComponent(finishButton);
        }

        gl.setHorizontalGroup(widgetHGroup);
        gl.setVerticalGroup(widgetVGroup);
    }

    private void goBack() {

    }

    private void goNext() {

    }

    private void cancel() {

    }

    private void finish() {

    }

    public void setCanGoBack(boolean canGoBack) {
        this.canGoBack = canGoBack;
    }

    public void setCanGoNext(boolean canGoNext) {
        this.canGoNext = canGoNext;
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }

    public void setCanFinish(boolean canFinish) {
        this.canFinish = canFinish;
    }

    public void setHasCancelOption(boolean hasCancelOption) {
        this.hasCancelOption = hasCancelOption;
    }

    public void setHasFinishOption(boolean hasFinishOption) {
        this.hasFinishOption = hasFinishOption;
    }
}
