package com.scarlatti.swingutils.decision;

import com.scarlatti.swingutils.Widget;
import com.scarlatti.swingutils.grid.ColumnsWidget;
import com.scarlatti.swingutils.grid.RowsWidget;
import com.scarlatti.swingutils.text.MultilineTextWidget;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 12/29/2018
 */
public class YesNoWidget implements Widget {

    private Container widgetPanel;
    private JButton yesButton;
    private JButton noButton;
    private JRadioButton yesOption;
    private JRadioButton noOption;
    private JCheckBox choiceCheckBox;
    private Font originalFont;
    private boolean choice;
    private String message;
    private String title = "Choice";
    private String yesText = "Yes";
    private String noText = "No";
    private DisplayMode displayMode = DisplayMode.RADIO_BUTTONS;

    public YesNoWidget(Consumer<YesNoWidget> config) {
        config.accept(this);
    }

    public static Container ui(Consumer<YesNoWidget> config) {
        return new YesNoWidget(config).getUi();
    }

    public static boolean modal(Consumer<YesNoWidget> config) {
        return new YesNoWidget(config).getChoiceInteractive();
    }

    @Override
    public Container getUi() {
        if (widgetPanel == null)
            initUi();

        return widgetPanel;
    }

    public boolean getChoiceInteractive() {
        int choiceCode = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
        if (choiceCode == JOptionPane.YES_OPTION) {
            return true;
        } else if (choiceCode == JOptionPane.NO_OPTION) {
            return false;
        } else {
            throw new RuntimeException("Unrecognized response code " + choiceCode);
        }
    }

    private void initUi() {
        switch (displayMode) {
            case BUTTONS:
                initUiButtons();
                break;
            case RADIO_BUTTONS:
                initUiRadioButtons();
                break;
            case CHECKBOX:
                initUiCheckBox();
                break;
        }
    }

    private void initUiButtons() {
        yesButton = new JButton(yesText);
        noButton = new JButton(noText);
        originalFont = yesButton.getFont();

        yesButton.addActionListener(e -> {
            configureUiButtonsByChoice(true);
        });

        noButton.addActionListener(e -> {
            configureUiButtonsByChoice(false);
        });

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(makeBold(titleLabel.getFont()));

        configureUiButtonsByChoice(choice);

        widgetPanel = RowsWidget.ui(rowsWidget -> {
            // optional title
            if (title != null)
                rowsWidget.addRow(titleLabel);

            // optional message
            if (message != null)
                rowsWidget.addRow(MultilineTextWidget.ui(message));

            // always display choice
            rowsWidget.addRow(
                ColumnsWidget.ui(columnsWidget -> {
                    columnsWidget.addFixedColumn(noButton);
                    columnsWidget.addFixedColumn(yesButton);
                })
            );
        });
    }

    private void initUiRadioButtons() {
        yesOption = new JRadioButton(yesText);
        noOption = new JRadioButton(noText);

        yesOption.addActionListener(e -> {
            choice = true;
        });

        noOption.addActionListener(e -> {
            choice = false;
        });

        if (choice) {
            yesOption.setSelected(true);
        } else {
            noOption.setSelected(true);
        }

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(makeBold(titleLabel.getFont()));

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(yesOption);
        buttonGroup.add(noOption);

        widgetPanel = RowsWidget.ui(rowsWidget -> {
            // optional title
            if (title != null)
                rowsWidget.addRow(titleLabel);

            // optional message
            if (message != null)
                rowsWidget.addRow(MultilineTextWidget.ui(message));

            // always display choice
            rowsWidget.addRow(yesOption);
            rowsWidget.addRow(noOption);
        });
    }

    private void initUiCheckBox() {

        choiceCheckBox = new JCheckBox(yesText);
        choiceCheckBox.setSelected(choice);

        choiceCheckBox.addActionListener(e -> {
            choice = choiceCheckBox.isSelected();
        });

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(makeBold(titleLabel.getFont()));

        widgetPanel = RowsWidget.ui(rowsWidget -> {
            // optional title
            if (title != null)
                rowsWidget.addRow(titleLabel);

            // optional message
            if (message != null)
                rowsWidget.addRow(MultilineTextWidget.ui(message));

            // always display choice
            rowsWidget.addRow(choiceCheckBox);
        });
    }

    private static Font makeBold(Font font) {
        return font.deriveFont(Font.BOLD, font.getSize());
    }

    private void configureUiButtonsByChoice(boolean choice) {

        yesButton.setFont(originalFont);
        noButton.setFont(originalFont);

        if (choice) {
            yesButton.setFont(makeBold(originalFont));
        } else {
            noButton.setFont(makeBold(originalFont));
        }
    }

    public enum DisplayMode {
        BUTTONS,
        RADIO_BUTTONS,
        CHECKBOX
    }

    public boolean getChoice() {
        return choice;
    }

    public void setChoice(boolean choice) {
        this.choice = choice;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYesText(String yesText) {
        this.yesText = yesText;
    }

    public void setNoText(String noText) {
        this.noText = noText;
    }

    public void setDisplayMode(DisplayMode displayMode) {
        this.displayMode = displayMode;
    }
}
