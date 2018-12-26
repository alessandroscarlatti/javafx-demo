package com.scarlatti.swingutils.filechooser;

import com.scarlatti.swingutils.grid.FileChooserWidget;
import org.junit.Test;

import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WindowsFileChooserTest {

    @Test
    public void testFileChooserReturnValue() {
        Path selectedFile = new WindowsFileChooser()
            .withFilter("All Files", "*.*")
            .withTitle("Choose Keystore")
            .withInitialFile(Paths.get("build.gradle"))
            .showOpenFileDialog();

        System.out.println("Selected file: " + selectedFile);
    }

    @Test
    public void testChooseWithInitialFile() {
        Path selectedFile = new WindowsFileChooser()
            .withFilter("All Files", "*.*")
            .withTitle("Choose Keystore")
            .withInitialFile(Paths.get("build.gradle"))
            .showOpenFileDialog();

        System.out.println("Selected file: " + selectedFile);
    }

    @Test
    public void testChooseWithInitialDir() {
        Path selectedFile = new WindowsFileChooser()
            .withFilter("All Files", "*.*")
            .withTitle("Choose Keystore")
            .withInitialFile(Paths.get("build"))
            .showOpenFileDialog();

        System.out.println("Selected file: " + selectedFile);
    }

    @Test
    public void canSuggestNonExistingFileForSave() {
        Path selectedFile = new WindowsFileChooser()
            .withFilter("All Files (*.*)", "*.*")
            .withFilter("Text Files (*.txt)", "*.txt")
            .withTitle("Choose Text File")
            .withInitialFile(Paths.get("src/main/somefile.txt"))
            .showSaveFileDialog();

        System.out.println("Selected file: " + selectedFile);
    }

    @Test
    public void canSuggestFileForSaveWithNoFilters() {
        Path selectedFile = new WindowsFileChooser()
            .withTitle("Choose Text File")
            .withInitialFile(Paths.get("src/main/somefile.txt"))
            .showSaveFileDialog();

        System.out.println("Selected file: " + selectedFile);
    }

    @Test
    public void testFileChooserReturnValueWithParent() {
        JFrame jFrame = new JFrame();
        FileChooserWidget fileChooserWidget = new FileChooserWidget(FileChooserWidget.FileChooserMode.OPEN);
        jFrame.setContentPane(fileChooserWidget.getUi());
        jFrame.pack();

        try {
            jFrame.setVisible(true);

            Path selectedFile = new WindowsFileChooser()
                .withFilter("All Files (*.*)", "*")
                .withFilter("Text Files (*.txt)", "*.txt")
                .withTitle("Choose Keystore")
                .withInitialFile(Paths.get("build.gradle"))
                .withGuiParent(jFrame)
                .showOpenFileDialog();

            System.out.println("Selected file: " + selectedFile);
        } finally {
            jFrame.dispose();
        }
    }
}