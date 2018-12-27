package com.scarlatti.swingutils.wizard;

import com.scarlatti.swingutils.SwingUtils;
import com.scarlatti.swingutils.filechooser.FileChooserWidget;
import com.scarlatti.swingutils.filechooser.FileChoosers;
import com.scarlatti.swingutils.wizard.WizardWidget;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 12/25/2018
 */
public class WizardWidgetTest {

    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void createWidget() {
        SwingUtils.display(() -> {
//             create "content"
            JPanel wizardContent = new JPanel();
            GroupLayout gl = new GroupLayout(wizardContent);
            wizardContent.setLayout(gl);
//
//            // file chooser
            FileChooserWidget fileChooserWidget1 = FileChoosers.openFileWidget();
            FileChooserWidget fileChooserWidget2 = FileChoosers.saveFileWidget();
            JButton jButton = new JButton("OK");
//
            // configure layout
            gl.setAutoCreateGaps(true);
            gl.setAutoCreateContainerGaps(true);

            GroupLayout.ParallelGroup widgetHGroup = gl
                .createParallelGroup()
                .addComponent(fileChooserWidget1.getUi())
                .addComponent(fileChooserWidget2.getUi())
                .addComponent(jButton);

            GroupLayout.SequentialGroup widgetVGroup = gl
                .createSequentialGroup()
                .addComponent(fileChooserWidget1.getUi())
                .addComponent(fileChooserWidget2.getUi())
                .addComponent(jButton);

            gl.setHorizontalGroup(widgetHGroup);
            gl.setVerticalGroup(widgetVGroup);

            WizardWidget wizardWidget = new WizardWidget(wizard -> {
                wizard.wizardContent = wizardContent;
                wizard.title = "stuff and things";
            });

            return wizardWidget.getUi();
        });
    }
}
