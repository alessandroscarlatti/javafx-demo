package com.scarlatti.swingutils.decision;

import com.scarlatti.swingutils.SwingUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 12/29/2018
 */
public class YesNoCancelWidgetTest {

    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }


    @Test
    public void showYesNoCancelDialog() {
        Boolean choice = YesNoCancelWidget.modal(yesNoCancelWidget -> {
            yesNoCancelWidget.setTitle("Serious Choice for You to Make");
            yesNoCancelWidget.setMessage("Are you sure you want to continue?");
        });

        System.out.println("Choice: " + (choice == null ? "Cancel" : (choice ? "Yes" : "No")));
    }

}