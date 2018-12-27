package com.scarlatti.swingutils.filechooser;

import static com.scarlatti.swingutils.filechooser.WindowsFileChooser.WindowsFileChooserProps.Mode.SAVE;

/**
 * @author Alessandro Scarlatti
 * @since Wednesday, 12/26/2018
 *
 * Convenience methods to create common file choosers.
 */
public class FileChoosers {

    public static WindowsFileChooser openFileChooser() {
        return new WindowsFileChooser(wFileChooser -> {
            wFileChooser.title = "Open File";
            wFileChooser.initialFile = null;
            wFileChooser.addFilter("All files (*.*)", "*.*");
        });
    }

    public static FileChooserWidget openFileWidget() {
        return new FileChooserWidget(fileChooser -> {
            fileChooser.fileChoiceStrategy = () -> {
                WindowsFileChooser windowsFileChooser = new WindowsFileChooser(wFileChooser -> {
                    wFileChooser.title = "Open File";
                    wFileChooser.initialFile = fileChooser.self.getState().path;
                    wFileChooser.addFilter("All files (*.*)", "*.*");
                });

                return windowsFileChooser.getFile();
            };
        });
    }

    public static FileChooserWidget saveFileWidget() {
        return new FileChooserWidget(fileChooser -> {
            fileChooser.fileChoiceStrategy = () -> {
                WindowsFileChooser windowsFileChooser = new WindowsFileChooser(wFileChooser -> {
                    wFileChooser.title = "Save File";
                    wFileChooser.mode = SAVE;
                    wFileChooser.initialFile = fileChooser.self.getState().path;
                    wFileChooser.addFilter("All files (*.*)", "*.*");
                });

                return windowsFileChooser.getFile();
            };
        });
    }
}
