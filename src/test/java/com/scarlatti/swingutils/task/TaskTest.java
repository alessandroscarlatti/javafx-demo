package com.scarlatti.swingutils.task;

import com.scarlatti.swingutils.SwingUtils;
import com.scarlatti.swingutils.decision.YesNoWidget;
import com.scarlatti.swingutils.grid.RowsWidget;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.scarlatti.swingutils.decision.YesNoWidget.DisplayMode.CHECKBOX;

/**
 * @author Alessandro Scarlatti
 * @since Thursday, 1/3/2019
 */
public class TaskTest {


    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void canDetermineDoneWithTwoCheckboxes() {

        System.out.println("Only print this when both checkboxes are filled out.");

        SwingUtils.display(
            RowsWidget.ui(rowsWidget -> {
                rowsWidget.addRow(
                    YesNoWidget.ui(yesNoWidget -> {
                        yesNoWidget.setTitle("Option 1");
                        yesNoWidget.setDisplayMode(CHECKBOX);
                        yesNoWidget.setYesText("Choose Option 1");
                    })
                );
                rowsWidget.addRow(
                    YesNoWidget.ui(yesNoWidget -> {
                        yesNoWidget.setTitle("Option 2");
                        yesNoWidget.setDisplayMode(CHECKBOX);
                        yesNoWidget.setYesText("Choose Option 2");
                    })
                );
            })
        );
    }
}
