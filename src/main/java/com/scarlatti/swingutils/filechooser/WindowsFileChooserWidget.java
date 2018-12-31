package com.scarlatti.swingutils.filechooser;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Alessandro Scarlatti
 * @since Sunday, 12/30/2018
 */
public class WindowsFileChooserWidget extends AbstractFileChooserWidget {

    public WindowsFileChooserWidget() {
    }

    public WindowsFileChooserWidget(Consumer<WindowsFileChooserWidget> config) {
        config.accept(this);
    }

    public static WindowsFileChooserWidget dialog(Consumer<WindowsFileChooserWidget> config) {
        return new WindowsFileChooserWidget(config);
    }

    public static WindowsFileChooserWidget dialog() {
        return new WindowsFileChooserWidget();
    }

    @Override
    Path doChooseFile() {
        Objects.requireNonNull(fileType, "Mode must not be null.");
        switch (fileType) {
            case FILE:
                return chooseFileInternal();
            case DIRECTORY:
                return chooseFolderInternal();
            default:
                throw new RuntimeException("Unrecognized file type: " + fileType);
        }
    }

    private Path chooseFileInternal() {
        WindowsFileChooser fileChooser = new WindowsFileChooser();
        fileChooser.setTitle(title);
        fileChooser.setFile(file);
        fileChooser.setFilters(filters);
        fileChooser.setMode(mode);
        fileChooser.setParent(parent);

        return fileChooser.chooseFile();
    }

    private Path chooseFolderInternal() {
        WindowsFolderBrowser folderBrowser = new WindowsFolderBrowser();
        folderBrowser.setTitle(title);
        folderBrowser.setDirectory(file);
        folderBrowser.setParent(parent);

        return folderBrowser.chooseDirectory();
    }
}
