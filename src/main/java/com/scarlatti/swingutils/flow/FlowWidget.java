package com.scarlatti.swingutils.flow;

import com.scarlatti.swingutils.Widget;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static javax.swing.GroupLayout.Alignment.CENTER;
import static javax.swing.LayoutStyle.ComponentPlacement.UNRELATED;

/**
 * @author Alessandro Scarlatti
 * @since Monday, 12/31/2018
 * <p>
 * Invoke flow steps at the right time based on interaction with the steps.
 * <p>
 * This widget will contain a list of flow steps.
 * The final flow step will be considered to be the "Finish" step.
 * It should trigger whatever needs to be done on "Finish".
 * This may or may not be a "display" type task.
 * <p>
 * The flow step work is understood to be asynchronous.
 * The results will be communicated by the steps notifying the
 * flow widget via a callback.
 * <p>
 * The assumption is that a flow step work begins automatically.
 * If work like "Install" is being done immediately upon clicking the "Next" button,
 * that means that an installation progress bar page would reasonably be the next "step."
 * <p>
 * Whether or not we can go back depends on:
 * - whether or not we can abort and revert
 * the current step
 * - whether or not we can repeat the previous step.
 * <p>
 * ===============
 * What does it mean to "go back"?
 * ===============
 * <p>
 * Suppose we have the following sequence of steps:
 * <p>
 * - ConfigPage1
 * - InstallerTask1
 * - ConfigPage2
 * - InstallerTask2
 * - FinishedPage
 * <p>
 * Widget automatically runs ConfigPage1.
 *
 * Widget is notified that Next is available.
 * Widget enables Next.
 *
 * User clicks Next.
 * Widget automatically runs InstallerTask1.
 *
 * Widget is notified that Next is available.
 * Widget enables Next.
 *
 * User clicks Next.
 * Widget automatically runs ConfigPage2.
 * Widget asks if Back is available.
 * InstallerTask1 says Yes.
 * Widget enables Back.
 *
 * Widget is notified that Next is available.
 * Widget enables Next.
 *
 * User clicks Back.
 * Widget does ???WHAT???
 *
 *
 * <p>
 * <p>
 * ===============
 * <p>
 * The work for a configuration page step
 * would be defined as follows:
 * "Run a method that returns a
 * <p>
 * <p>
 * The "Next >" text must be configurable per step.
 * <p>
 * For example:
 * <p>
 * Step1 -> Step2 = "Next >"
 * Step2 -> Step3 = "Next >"
 * Step3 -> Step4 = "Install"
 * Step4 -> Step5 = "Finish"
 */
public class FlowWidget implements Widget {

    private JPanel widgetPanel;
    private JButton backButton;
    private JButton nextButton;
    private JButton cancelButton;

    private boolean canGoBack;
    private boolean canGoNext;
    private boolean canCancel;
    private boolean canFinish;
    private boolean hasBackOrNextOptions = true;
    private boolean hasCancelOption = true;
    private boolean hasFinishOption = true;

    private List<FlowStepWidget> steps = new ArrayList<>();
    private int currentStepIndex = -1;

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

        configureWidgetLayoutByState();
        configureWidgetEvents();
        configureWidgetButtonsByState();
    }

    private void configureWidgetEvents() {
        backButton.addActionListener(e -> goBack());
        nextButton.addActionListener(e -> goNext());
        cancelButton.addActionListener(e -> cancel());
    }

    private void configureWidgetButtonsByState() {
        backButton.setEnabled(canGoBack);
        nextButton.setEnabled(canGoNext);
        cancelButton.setEnabled(canCancel);

        // set next button text
        if (getCurrentStep() == null) {
            backButton.setEnabled(false);
            nextButton.setEnabled(false);
            nextButton.setText("Next >");
            cancelButton.setEnabled(true);
        } else {
            FlowStepWidget previousStep = getPreviousStep();
            if (previousStep == null) {
                backButton.setEnabled(false);
            } else {
                backButton.setEnabled(previousStep.canRevert());
            }

            FlowStepWidget nextStep = getNextStep();
            if (nextStep == null) {
                nextButton.setEnabled(false);
                nextButton.setText("Next >");
            } else {
                nextButton.setEnabled(true);
                nextButton.setText(getGoNextTextForStep(nextStep));
            }
        }
    }

    private String getGoNextTextForStep(FlowStepWidget step) {
        String goNextText = step.getCommandName();
        if (goNextText == null) {
            return "Next >";
        } else {
            return goNextText;
        }
    }

    private void configureWidgetLayoutByState() {
        GroupLayout gl = new GroupLayout(widgetPanel);
        widgetPanel.setLayout(gl);

        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup widgetHGroup = gl.createSequentialGroup();
        GroupLayout.ParallelGroup widgetVGroup = gl.createParallelGroup(CENTER);

        if (hasBackOrNextOptions) {
            widgetHGroup
                .addComponent(backButton)
                .addComponent(nextButton);

            widgetVGroup
                .addComponent(backButton)
                .addComponent(nextButton);
        }

        if (hasCancelOption) {
            widgetHGroup.addComponent(cancelButton);
            widgetVGroup.addComponent(cancelButton);
        }

        if (hasBackOrNextOptions && (hasCancelOption || hasFinishOption)) {
            widgetHGroup.addPreferredGap(UNRELATED);
        }

        gl.setHorizontalGroup(widgetHGroup);
        gl.setVerticalGroup(widgetVGroup);
    }

    public void start() {
        if (steps.size() > 0)
            currentStepIndex = 0;
    }

    private void goBack() {
        setCurrentStep(currentStepIndex - 1);
    }

    private void goNext() {
        setCurrentStep(currentStepIndex + 1);
    }

    private void cancel() {
        setCurrentStep(-1);
    }

    private void setCurrentStep(int newCurrentStepIndex) {
        currentStepIndex = newCurrentStepIndex;
    }

    private FlowStepWidget getCurrentStep() {
        if (currentStepIndex >= 0)
            return steps.get(currentStepIndex);

        return null;
    }

    private FlowStepWidget getPreviousStep() {
        if (currentStepIndex > 0)
            return steps.get(currentStepIndex - 1);

        return null;
    }

    private FlowStepWidget getNextStep() {
        if (currentStepIndex < steps.size() - 1)
            return steps.get(currentStepIndex + 1);

        return null;
    }

    public void setHasCancelOption(boolean hasCancelOption) {
        this.hasCancelOption = hasCancelOption;
    }

    public void setHasFinishOption(boolean hasFinishOption) {
        this.hasFinishOption = hasFinishOption;
    }

    public void addStep(FlowStepWidget step) {
        steps.add(step);
    }

    public void setSteps(List<FlowStepWidget> steps) {
        this.steps = steps;
    }

    public static class FlowManager {

    }
}
