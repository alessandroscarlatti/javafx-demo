package com.scarlatti.swingutils.filechooser;

import com.scarlatti.swingutils.SwingUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static com.scarlatti.swingutils.filechooser.FileChooserWidget.FileExtensionFilter.filter;
import static com.scarlatti.swingutils.filechooser.FileChooserWidget.Mode.SAVE;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 12/25/2018
 */
public class FileWidgetTest {

    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void createWidgetForOpening() {
        SwingUtils.display(
            FileWidget.ui(fileChooser -> {
                fileChooser.setFile(Paths.get("build.gradle"));
                fileChooser.setFileChooserWidget(
                    WindowsFileChooser.dialog(wFileChooser -> {
                        wFileChooser.setTitle("Choose gradle build");
                        wFileChooser.addFilter("All files (*.*)", "*.*");
                        wFileChooser.addFilter("Gradle files (*.gradle)", "*.gradle");
                    })
                );
            })
        );
    }

    @Test
    public void createWidgetWithTitleAndMessage() {
        SwingUtils.display(
            FileWidget.ui(fileChooserWidget -> {
                fileChooserWidget.setTitle("Configuration File");
                fileChooserWidget.setMessage("This file should hold all your configurations");
                fileChooserWidget.setFileChooserWidget(null);  // todo fix this
            })
        );
    }

    @Test
    public void createWidgetForSaving() {
        SwingUtils.display(FileWidget.ui(fileChooser -> {
                fileChooser.setTitle("Choose gradle build");
                fileChooser.setFile(Paths.get("build.gradle"));
                fileChooser.getFileChooserWidget().setMode(SAVE);
                fileChooser.getFileChooserWidget().setFilters(
                    filter("Gradle files (*.gradle)", "*.gradle"),
                    filter("All files (*.*)", "*.*")
                );
            })
        );
    }
}
