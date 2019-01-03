package com.scarlatti.swingutils.wizard;

import com.scarlatti.swingutils.SwingUtils;
import com.scarlatti.swingutils.decision.YesNoWidget;
import com.scarlatti.swingutils.file.*;
import com.scarlatti.swingutils.flow.FlowWidget;
import com.scarlatti.swingutils.grid.RowsWidget;
import com.scarlatti.swingutils.progressbar.ProgressBarWidget;
import com.scarlatti.swingutils.text.MultilineTextWidget;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;

import java.nio.file.Paths;

import static com.scarlatti.swingutils.SwingUtils.sleep;
import static com.scarlatti.swingutils.decision.YesNoWidget.DisplayMode.CHECKBOX;
import static com.scarlatti.swingutils.file.FileChooserWidget.FileType.DIRECTORY;
import static com.scarlatti.swingutils.file.FileChooserWidget.Mode.SAVE;

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
            FileWidget fileWidget1 = FileChoosers.openFileWidget();
            FileWidget fileWidget2 = FileChoosers.saveFileWidget();
            JButton jButton = new JButton("OK");

            RowsWidget wizardContent = new RowsWidget(_rowsWidget -> {
                _rowsWidget.addRow(fileWidget1.getUi());
                _rowsWidget.addRow(fileWidget2.getUi());
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
//                rowsWidget.addRow(new ProgressBarWidget(progressBarWidget -> {
//                    progressBarWidget.getProgressBarTemplate().setWork(() -> {
//                        sleep(1000);
//                    });
//                    progressBarWidget.setRepeatable(true);
//                }).getUi());
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
//                        progressBarWidget.getProgressBarTemplate().setWork(() -> {
//                            sleep(3000);
//                        });
                    })).getUi());
                }).getUi();
                wizard.title = "Dangerous task.";
            }).getUi();
        });
    }

    @Test
    public void theDoThisDangerousThingWizardWithStaticConstructors() {
        SwingUtils.display(
            WizardWidget.ui(wizard -> {
                wizard.wizardContent = RowsWidget.ui(rowsWidget -> {
                    rowsWidget.addRow(
                        MultilineTextWidget.ui(
                            "This is a really dangerous task that has a really long description.\n" +
                                "It may take as long to execute this task as it does to read about it."
                        )
                    );
                    rowsWidget.addRow(
                        FileWidget.ui(fileChooser -> {
                            fileChooser.setTitle("Gradle Build File");
                            fileChooser.setMessage("Select the Gradle build file for your project.");
                            fileChooser.setFile(Paths.get("build.gradle"));
                        })
                    );
                    rowsWidget.addRow(
                        FileWidget.ui(fileChooser -> {
                            fileChooser.setTitle("Installation Directory");
                            fileChooser.setMessage("Select the directory where we will install the dangerous task.");
                            fileChooser.setFile(Paths.get(""));
                            fileChooser.setFileType(DIRECTORY);
                        })
                    );
                    rowsWidget.addRow(
                        FileWidget.ui(fileChooser -> {
                            fileChooser.setTitle("Backup");
                            fileChooser.setMessage("Be sure to backup your work to this file.");
                            fileChooser.setFile(Paths.get("backup.txt"));
                            fileChooser.getFileChooserWidget().setMode(SAVE);
                        })
                    );
                    rowsWidget.addRow(
                        YesNoWidget.ui(yesNoWidget -> {
                            yesNoWidget.setDisplayMode(CHECKBOX);
                            yesNoWidget.setTitle("Option 1");
                            yesNoWidget.setMessage(null);
                            yesNoWidget.setYesText("Use Option 1.");
                            yesNoWidget.setChoice(false);
                        })
                    );
                    rowsWidget.addRow(
                        YesNoWidget.ui(yesNoWidget -> {
                            yesNoWidget.setDisplayMode(CHECKBOX);
                            yesNoWidget.setTitle("Option 2");
                            yesNoWidget.setMessage("You should think very carefully about this.\nIt can often lead to catastrophe.");
                            yesNoWidget.setYesText("I Understand the Risks.  Go ahead anyway.");
                            yesNoWidget.setChoice(true);
                        })
                    );
                    rowsWidget.addRow(
                        ProgressBarWidget.ui(progressBarWidget -> {
                            progressBarWidget.setRepeatable(false);
//                            progressBarWidget.getProgressBarTemplate().setWork(() -> {
//                                sleep(3000);
//                            });
                            progressBarWidget.setTitle("Dangerous Task");
                            progressBarWidget.setMessage("When you are ready click Start.  It may be very dangerous, so watch out!");
                        })
                    );
                });
                wizard.title = "Dangerous task.";
            })
        );
    }

    @Test
    public void wizardWithFlow() {
        SwingUtils.display(
            WizardWidget.ui(wizard -> {
                wizard.title = "Wizard with Flow";
                wizard.wizardContent = FlowWidget.ui();
            })
        );
    }
}
