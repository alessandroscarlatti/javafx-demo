package com.scarlatti.swingutils.file;

import org.junit.Test;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static com.scarlatti.swingutils.file.FileChooserWidget.FileExtensionFilter.filter;

public class WindowsFileChooserTest {

    @Test
    public void testFileChooserReturnValue() {
        WindowsFileChooser fileChooserWidget = new WindowsFileChooser(fileChooser -> {
            fileChooser.setFilters(Arrays.asList(
                filter("All Files", "*.*")
            ));
            fileChooser.setTitle("Choose Keystore");
            fileChooser.setFile(Paths.get("build.gradle"));
        });

        Path selectedFile = fileChooserWidget.chooseFile();

        System.out.println("Selected file: " + selectedFile);
    }

    @Test
    public void testChooseWithInitialFile() {
        WindowsFileChooser fileChooserWidget = new WindowsFileChooser(fileChooser -> {
            fileChooser.setFilters(Arrays.asList(
                filter("All Files", "*.*")
            ));
            fileChooser.setTitle("Choose Keystore");
            fileChooser.setFile(Paths.get("build.gradle"));
        });

        Path selectedFile = fileChooserWidget.chooseFile();

        System.out.println("Selected file: " + selectedFile);
    }

    @Test
    public void testChooseWithInitialDir() {
        WindowsFileChooser fileChooserWidget = new WindowsFileChooser(fileChooser -> {
            fileChooser.setFilters(Arrays.asList(
                filter("All Files", "*.*")
            ));
            fileChooser.setTitle("Choose Keystore");
            fileChooser.setFile(Paths.get("build"));
        });

        Path selectedFile = fileChooserWidget.chooseFile();

        System.out.println("Selected file: " + selectedFile);
    }

    @Test
    public void canSuggestNonExistingFileForSave() {
        WindowsFileChooser fileChooserWidget = new WindowsFileChooser(fileChooser -> {
            fileChooser.setFilters(Arrays.asList(
                filter("All Files", "*.*")
            ));
            fileChooser.setTitle("Save Text file");
            fileChooser.setFile(Paths.get("src/main/somefile.txt"));
        });

        Path selectedFile = fileChooserWidget.chooseFile();

        System.out.println("Selected file: " + selectedFile);
    }

    @Test
    public void canSuggestFileForSaveWithNoFilters() {
        WindowsFileChooser fileChooserWidget = new WindowsFileChooser(fileChooser -> {
            fileChooser.setTitle("Choose Keystore");
            fileChooser.setFile(Paths.get("src/main/somefile.txt"));
        });

        Path selectedFile = fileChooserWidget.chooseFile();

        System.out.println("Selected file: " + selectedFile);
    }

    @Test
    public void testFileChooserReturnValueWithParent() {
        JFrame jFrame = new JFrame();
        FileWidget fileWidget = new FileWidget();
        jFrame.setContentPane(fileWidget.getUi());
        jFrame.pack();

        try {
            jFrame.setVisible(true);

            WindowsFileChooser windowsFileChooser = new WindowsFileChooser(fileChooser -> {
                fileChooser.setFilters(Arrays.asList(
                    filter("All Files", "*.*")
                ));
                fileChooser.setTitle("Save Text file");
                fileChooser.setFile(Paths.get("build.gradle"));
                fileChooser.setParent(jFrame);
            });

            Path selectedFile = windowsFileChooser.chooseFile();

            System.out.println("Selected file: " + selectedFile);
        } finally {
            jFrame.dispose();
        }
    }

    @Test
    public void testNativeDirectoryChooser() {
//        BrowseInfoAStructure params = new BrowseInfoAStructure();
////        params.pszDisplayName = new Memory(4 * 260 + 1);
////        params.ulFlags = BrowseInfoAStructure.BIF_EDITBOX;
////
////        WindowsFileChooser.Shell32Silly.SHBrowseForFolderW(params);
////
////        System.out.println(params.pszDisplayName.getString(0, true));

        JFrame jFrame = new JFrame();
        FileWidget fileWidget = new FileWidget();
        jFrame.setContentPane(fileWidget.getUi());
        jFrame.pack();

        try {
            jFrame.setVisible(true);

            WindowsFolderBrowser windowsFolderBrowser = new WindowsFolderBrowser();
            File file = windowsFolderBrowser.showDialog(jFrame);

            System.out.println("file: " + file);
        } finally {
            jFrame.dispose();
        }



    }
}