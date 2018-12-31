package com.scarlatti.swingutils.file;

import com.scarlatti.swingutils.SwingUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Arrays;

import static com.scarlatti.swingutils.file.FileChooserWidget.FileExtensionFilter.filter;
import static com.scarlatti.swingutils.file.FileChooserWidget.FileType.DIRECTORY;
import static com.scarlatti.swingutils.file.FileChooserWidget.Mode.SAVE;

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
                    WindowsFileChooserWidget.dialog(wFileChooser -> {
                        wFileChooser.setFilters(Arrays.asList(
                            filter("All Files", "*.*")
                        ));
                        wFileChooser.setTitle("Choose gradle build");
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
            })
        );
    }

    @Test
    public void createWidgetForDirWithTitleAndMessage() {
        SwingUtils.display(
            FileWidget.ui(fileChooserWidget -> {
                fileChooserWidget.setTitle("Configuration File");
                fileChooserWidget.setMessage("This file should hold all your configurations");
                fileChooserWidget.setFileType(DIRECTORY);
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

    @Test
    public void createSwingFileChooser() {
        SwingUtils.display(
            FileWidget.ui(fileChooserWidget -> {
                fileChooserWidget.setTitle("Configuration File");
                fileChooserWidget.setMessage("This file should hold all your configurations");
                fileChooserWidget.setFileChooserWidget(
                    new SwingFileChooserWidget()
                );
            })
        );
    }
}
