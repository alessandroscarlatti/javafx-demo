package com.scarlatti.swingutils.decision;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 12/29/2018
 */
public class YesNoCancelWidget {

    private Boolean choice;
    private String title;
    private String message;

    public YesNoCancelWidget(Consumer<YesNoCancelWidget> config) {
        config.accept(this);
    }

    public static Boolean modal(Consumer<YesNoCancelWidget> config) {
        return new YesNoCancelWidget(config).getChoiceInteractive();
    }

    public Boolean getChoiceInteractive() {
        int choiceCode = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_CANCEL_OPTION);
        if (choiceCode == JOptionPane.YES_OPTION) {
            return true;
        } else if (choiceCode == JOptionPane.NO_OPTION) {
            return false;
        } else if (choiceCode == JOptionPane.CANCEL_OPTION) {
            return null;
        } else {
            throw new RuntimeException("Unrecognized response code " + choiceCode);
        }
    }

    public Boolean getChoice() {
        return choice;
    }

    public void setChoice(Boolean choice) {
        this.choice = choice;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
