package com.scarlatti.swingutils.filechooser;

import org.junit.Test;

import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WindowsFileChooserTest {

    @Test
    public void testFileChooserReturnValue() {
        WindowsFileChooser fileChooserWidget = new WindowsFileChooser(fileChooser -> {
            fileChooser.addFilter("All Files", "*.*");
            fileChooser.title = "Choose Keystore";
            fileChooser.initialFile = Paths.get("build.gradle");
        });

        Path selectedFile = fileChooserWidget.getFile();

        System.out.println("Selected file: " + selectedFile);
    }

    @Test
    public void testChooseWithInitialFile() {
        WindowsFileChooser fileChooserWidget = new WindowsFileChooser(fileChooser -> {
            fileChooser.addFilter("All Files", "*.*");
            fileChooser.title = "Choose Keystore";
            fileChooser.initialFile = Paths.get("build.gradle");
        });

        Path selectedFile = fileChooserWidget.getFile();

        System.out.println("Selected file: " + selectedFile);
    }

    @Test
    public void testChooseWithInitialDir() {
        WindowsFileChooser fileChooserWidget = new WindowsFileChooser(fileChooser -> {
            fileChooser.addFilter("All Files", "*.*");
            fileChooser.title = "Choose Keystore";
            fileChooser.initialFile = Paths.get("build");
        });

        Path selectedFile = fileChooserWidget.getFile();

        System.out.println("Selected file: " + selectedFile);
    }

    @Test
    public void canSuggestNonExistingFileForSave() {
        WindowsFileChooser fileChooserWidget = new WindowsFileChooser(fileChooser -> {
            fileChooser.addFilter("All Files (*.*)", "*.*");
            fileChooser.addFilter("Text Files (*.txt)", "*.txt");
            fileChooser.title = "Save Text File";
            fileChooser.initialFile = Paths.get("src/main/somefile.txt");
        });

        Path selectedFile = fileChooserWidget.getFile();

        System.out.println("Selected file: " + selectedFile);
    }

    @Test
    public void canSuggestFileForSaveWithNoFilters() {
        WindowsFileChooser fileChooserWidget = new WindowsFileChooser(fileChooser -> {
            fileChooser.title = "Choose Keystore";
            fileChooser.initialFile = Paths.get("src/main/somefile.txt");
        });

        Path selectedFile = fileChooserWidget.getFile();

        System.out.println("Selected file: " + selectedFile);
    }

    @Test
    public void testFileChooserReturnValueWithParent() {
        JFrame jFrame = new JFrame();
        FileChooserWidget fileChooserWidget = new FileChooserWidget();
        jFrame.setContentPane(fileChooserWidget.getUi());
        jFrame.pack();

        try {
            jFrame.setVisible(true);

            WindowsFileChooser windowsFileChooser = new WindowsFileChooser(fileChooser -> {
                fileChooser.addFilter("All Files (*.*)", "*.*");
                fileChooser.addFilter("Text Files (*.txt)", "*.txt");
                fileChooser.title = "Save Text File";
                fileChooser.initialFile = Paths.get("build.gradle");
                fileChooser.setGuiParent(jFrame);
            });

            Path selectedFile = windowsFileChooser.getFile();

            System.out.println("Selected file: " + selectedFile);
        } finally {
            jFrame.dispose();
        }
    }
}