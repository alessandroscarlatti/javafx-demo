package com.scarlatti.swingutils.filechooser;

import com.scarlatti.swingutils.SwingUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static com.scarlatti.swingutils.filechooser.FileChooserWidget.FileType.DIRECTORY;
import static com.scarlatti.swingutils.filechooser.FileChooserWidget.FileType.FILE;
import static com.scarlatti.swingutils.filechooser.FileChooserWidget.Mode.OPEN;
import static com.scarlatti.swingutils.filechooser.FileChooserWidget.Mode.SAVE;

/**
 * @author Alessandro Scarlatti
 * @since Sunday, 12/30/2018
 */
public class SwingFileChooserWidgetTest {


    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void openFileDialog() {
        SwingFileChooserWidget widget = SwingFileChooserWidget.dialog(fileChooserWidget -> {
            fileChooserWidget.setTitle("Test");
            fileChooserWidget.setFile(Paths.get("build.gradle"));
            fileChooserWidget.setMode(OPEN);
        });

        Path file = widget.chooseFile();
        System.out.println("file: " + file);
    }

    @Test
    public void openDirDialog() {
        SwingFileChooserWidget widget = SwingFileChooserWidget.dialog(fileChooserWidget -> {
            fileChooserWidget.setTitle("Test");
            fileChooserWidget.setFile(Paths.get("build"));
            fileChooserWidget.setMode(OPEN);
            fileChooserWidget.setFileType(DIRECTORY);
        });

        Path file = widget.chooseFile();
        System.out.println("file: " + file);
    }

    @Test
    public void saveFileDialog() {
        SwingFileChooserWidget widget = SwingFileChooserWidget.dialog(fileChooserWidget -> {
            fileChooserWidget.setTitle("Test");
            fileChooserWidget.setFile(Paths.get(".gitignore"));
            fileChooserWidget.setMode(SAVE);
            fileChooserWidget.setFileType(FILE);
        });

        Path file = widget.chooseFile();
        System.out.println("file: " + file);
    }
}
