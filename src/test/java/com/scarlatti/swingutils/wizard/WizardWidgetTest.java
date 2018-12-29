package com.scarlatti.swingutils.wizard;

import com.scarlatti.swingutils.SwingUtils;
import com.scarlatti.swingutils.filechooser.FileChooserWidget;
import com.scarlatti.swingutils.filechooser.FileChoosers;
import com.scarlatti.swingutils.grid.RowsWidget;
import com.scarlatti.swingutils.progressbar.ProgressBarWidget;
import com.scarlatti.swingutils.text.MultilineTextWidget;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;

import static com.scarlatti.swingutils.SwingUtils.sleep;

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
            FileChooserWidget fileChooserWidget1 = FileChoosers.openFileWidget();
            FileChooserWidget fileChooserWidget2 = FileChoosers.saveFileWidget();
            JButton jButton = new JButton("OK");

            RowsWidget wizardContent = new RowsWidget(_rowsWidget -> {
                _rowsWidget.addRow(fileChooserWidget1.getUi());
                _rowsWidget.addRow(fileChooserWidget2.getUi());
                _rowsWidget.addRow(jButton);
            });

            WizardWidget wizardWidget = new WizardWidget(wizard -> {
                wizard.wizardContent = wizardContent.getUi();
                wizard.title = "Install Stuff and Things";
//                wizard.icon = SwingUtils.createScaledIconFromResource("/com/scarlatti/javafxdemo/wrench-icon-22.png", ICON_SCALE_63);
//                wizard.icon = SwingUtils.createScaledIconFromResource("/com/scarlatti/javafxdemo/wrench-flat.png", ICON_SCALE_63);
//                wizard.icon = SwingUtils.createScaledIcon(getWizardIconBase64String(), ICON_SCALE_63);
            });

            return wizardWidget.getUi();
        });
    }

    @Test
    public void createWidgetWithText() {
        SwingUtils.display(() -> {

            String text = "This is a bunch of text\n\n" +
                "It is really, really long.\n" +
                "It is really, really long.\n" +
                "It is really, really long.\n" +
                "It is really, really long.\n" +
                "It is really, really long.\n\n" +
                "It is really, really long.\n" +
                "It is really, really long.\n" +
                "It is really, really long.\n" +
                "It is really, really long.\n\n" +
                "It really, really is.";

            RowsWidget wizardContent = new RowsWidget(rowsWidget -> {
                rowsWidget.addRow(new MultilineTextWidget(text).getUi());
                rowsWidget.addRow(new MultilineTextWidget(text).getUi());
                rowsWidget.addRow(FileChoosers.openFileWidget().getUi());
                rowsWidget.addRow(FileChoosers.openFileWidget().getUi());
                rowsWidget.addRow(FileChoosers.openFileWidget().getUi());
                rowsWidget.addRow(FileChoosers.openFileWidget().getUi());
                rowsWidget.addRow(FileChoosers.openFileWidget().getUi());
                rowsWidget.addRow(FileChoosers.openFileWidget().getUi());
                rowsWidget.addRow(new ProgressBarWidget(progressBarWidget -> {
                    progressBarWidget.getProgressBarTemplate().setWork(() -> {
                        sleep(1000);
                    });
                    progressBarWidget.setRepeatable(true);
                }).getUi());
            });

            WizardWidget wizardWidget = new WizardWidget(wizard -> {
                wizard.wizardContent = wizardContent.getUi();
                wizard.title = "Install Stuff and Things";
            });

            return wizardWidget.getUi();
        });
    }

    @Test
    public void theDoThisDangerousThingWizard() {
        SwingUtils.display(() -> {
            return new WizardWidget(wizard -> {
                wizard.wizardContent = new RowsWidget(rowsWidget -> {
                    rowsWidget.addRow(new MultilineTextWidget(
                        "This is a really dangerous task that has a really long description.\n" +
                            "It may take as long to execute this task as it does to read about it."
                    ).getUi());
                    rowsWidget.addRow(new ProgressBarWidget((progressBarWidget -> {
                        progressBarWidget.setRepeatable(true);
                        progressBarWidget.getProgressBarTemplate().setWork(() -> {
                            sleep(3000);
                        });
                    })).getUi());
                }).getUi();
                wizard.title = "Dangerous task.";
            }).getUi();
        });
    }
}
