package com.scarlatti.swingutils.filechooser;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

import static javax.swing.JFileChooser.DIRECTORIES_ONLY;
import static javax.swing.JFileChooser.FILES_ONLY;

/**
 * @author Alessandro Scarlatti
 * @since Sunday, 12/30/2018
 */
public class SwingFileChooserWidget extends AbstractFileChooserWidget {
    public SwingFileChooserWidget() {
    }

    public SwingFileChooserWidget(Consumer<SwingFileChooserWidget> config) {
        config.accept(this);
    }

    public static SwingFileChooserWidget dialog(Consumer<SwingFileChooserWidget> config) {
        return new SwingFileChooserWidget(config);
    }

    public static SwingFileChooserWidget dialog() {
        return new SwingFileChooserWidget();
    }

    @Override
    Path doChooseFile() {
        Objects.requireNonNull(fileType, "FileType must not be null.");
        Objects.requireNonNull(mode, "Mode must not be null.");
        return chooseFileInternal();
    }

    private Path chooseFileInternal() {
        final JFileChooser fc = new JFileChooser();

        if (file != null) {
            fc.setCurrentDirectory(file.toFile());
            fc.setSelectedFile(file.toFile());
        }

        fc.setMultiSelectionEnabled(false);

        switch (fileType) {
            case FILE:
                fc.setFileSelectionMode(FILES_ONLY);
                break;
            case DIRECTORY:
                fc.setFileSelectionMode(DIRECTORIES_ONLY);
                break;
        }

        // build filters
//        if (filters.size() > 0) {
//            boolean useAcceptAllFilter = false;
//            for (final String[] spec : filters) {
//                // the "All Files" filter is handled specially by JFileChooser
//                if (spec[1].equals("*")) {
//                    useAcceptAllFilter = true;
//                    continue;
//                }
//                fc.addChoosableFileFilter(new FileNameExtensionFilter(
//                    spec[0], Arrays.copyOfRange(spec, 1, spec.length)));
//            }
//            fc.setAcceptAllFileFilterUsed(useAcceptAllFilter);
//        }

        int result = -1;
        if (mode == Mode.OPEN) {
            result = fc.showOpenDialog(parent);
        }
        else {
            result = fc.showSaveDialog(parent);
        }

        if (result == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile().toPath().toAbsolutePath();
            return file;
        }

        return null;
    }
}
