package com.scarlatti.swingutils.decision;

import com.scarlatti.swingutils.SwingUtils;
import com.scarlatti.swingutils.grid.RowsWidget;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;

import static com.scarlatti.swingutils.decision.YesNoWidget.DisplayMode.BUTTONS;
import static com.scarlatti.swingutils.decision.YesNoWidget.DisplayMode.CHECKBOX;
import static com.scarlatti.swingutils.decision.YesNoWidget.DisplayMode.RADIO_BUTTONS;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 12/29/2018
 */
public class YesNoWidgetTest {

    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }


    @Test
    public void showYesNoDialog() {
        boolean choice = YesNoWidget.modal(yesNoWidget -> {
            yesNoWidget.setMessage("Are you sure?");
            yesNoWidget.setChoice(true);
        });

        System.out.println("Choice: " + (choice ? "Yes" : "No"));
    }

    @Test
    public void choiceWithButtons() {
        SwingUtils.display(YesNoWidget.ui(yesNoWidget -> {
            yesNoWidget.setDisplayMode(BUTTONS);
            yesNoWidget.setMessage("Are you sure?");
            yesNoWidget.setChoice(false);
        }));
    }

    @Test
    public void choiceWithRadioButtons() {
        SwingUtils.display(YesNoWidget.ui(yesNoWidget -> {
            yesNoWidget.setDisplayMode(RADIO_BUTTONS);
            yesNoWidget.setMessage("Are you sure?");
            yesNoWidget.setChoice(false);
        }));
    }

    @Test
    public void choiceWithCheckBox() {
        SwingUtils.display(YesNoWidget.ui(yesNoWidget -> {
            yesNoWidget.setDisplayMode(CHECKBOX);
            yesNoWidget.setTitle(null);
            yesNoWidget.setMessage(null);
            yesNoWidget.setYesText("Subscribe me to the spam email list.");
            yesNoWidget.setChoice(true);
        }));
    }

    @Test
    public void showSeveralChoicesWithCheckBox() {
        SwingUtils.display(
            RowsWidget.ui(rowsWidget -> {
                rowsWidget.addRow(
                    YesNoWidget.ui(yesNoWidget -> {
                        yesNoWidget.setDisplayMode(CHECKBOX);
                        yesNoWidget.setTitle(null);
                        yesNoWidget.setMessage(null);
                        yesNoWidget.setYesText("Subscribe me to the spam email list.");
                        yesNoWidget.setChoice(false);
                    })
                );
                rowsWidget.addRow(
                    YesNoWidget.ui(yesNoWidget -> {
                        yesNoWidget.setDisplayMode(CHECKBOX);
                        yesNoWidget.setTitle(null);
                        yesNoWidget.setMessage(null);
                        yesNoWidget.setYesText("Subscribe me to the nice email list.");
                        yesNoWidget.setChoice(true);
                    })
                );
            })
        );
    }

    @Test
    public void showSeveralChoices() {
        SwingUtils.display(
            RowsWidget.ui(rowsWidget -> {
                rowsWidget.addRow(
                    YesNoWidget.ui(yesNoWidget -> {
                        yesNoWidget.setTitle("Install Feature 1");
                        yesNoWidget.setMessage("This feature will do lots of dangerous things");
                        yesNoWidget.setChoice(false);
                        yesNoWidget.setYesText("Yes, Install Feature 1");
                        yesNoWidget.setNoText("No, Don't Install");
                    })
                );
                rowsWidget.addRow(
                    YesNoWidget.ui(yesNoWidget -> {
                        yesNoWidget.setTitle("Install Feature 2");
                        yesNoWidget.setMessage("This feature will do lots of dangerous things");
                        yesNoWidget.setChoice(true);
                        yesNoWidget.setYesText("Yes, Install Feature 2");
                        yesNoWidget.setNoText("No, Don't Install");
                    })
                );
            })
        );
    }

    @Test
    public void testJOptionPane() {
        JOptionPane.showConfirmDialog(null, "asdf");
    }

    @Test
    public void testJButtonGroup() {
        SwingUtils.display(() -> {

            JRadioButton option1 = new JRadioButton("Option 1");
            JRadioButton option2 = new JRadioButton("Option 2");
            JRadioButton option3 = new JRadioButton("Option 3");


            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add(option1);
            buttonGroup.add(option2);
            buttonGroup.add(option3);

            return RowsWidget.ui(rowsWidget -> {
                rowsWidget.addRow(option1);
                rowsWidget.addRow(option2);
                rowsWidget.addRow(option3);
            });
        });
    }
}