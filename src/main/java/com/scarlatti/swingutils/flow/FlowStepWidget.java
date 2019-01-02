package com.scarlatti.swingutils.flow;

import java.util.function.Consumer;

/**
 * @author Alessandro Scarlatti
 * @since Monday, 12/31/2018
 * <p>
 *
 */
public class FlowStepWidget {
    private String id;
    private String commandName;

    public FlowStepWidget() {
    }

    public FlowStepWidget(Consumer<FlowStepWidget> config) {
        config.accept(this);
    }

    public static FlowStepWidget step() {
        return new FlowStepWidget();
    }

    public static FlowStepWidget step(Consumer<FlowStepWidget> config) {
        return new FlowStepWidget(config);
    }

    /**
     * Get the text to display when asking the user to perform this step.
     * @return text, or null to specify this step wishes to use the default text.
     */
    public String getCommandName() {
        return commandName;
    }

    public boolean canBegin() {
        return true;
    }

    public boolean isComplete() {
        return false;
    }

    public boolean canRevert() {
        return false;
    }

    public void start() {
        // display the page
    }

    public void finish() {
        // may be nothing...
    }

    public void revert() {
        // display the page
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }
}
