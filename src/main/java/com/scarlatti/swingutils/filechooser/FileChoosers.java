package com.scarlatti.swingutils.filechooser;

/**
 * @author Alessandro Scarlatti
 * @since Wednesday, 12/26/2018
 * <p>
 * Convenience methods to create common file choosers.
 */
public class FileChoosers {

    // todo simpler file choosers...
    // we do want the file chooser to be a simple Supplier<Path> instance.
    // However, we want by default, for it to be easy to specify
    // an "all-files" file chooser that is linked to the text box.
    // Thus, it makes sense to build that functionality into another class.
    //
    // We could use an interface that has setter methods to inject
    // the currently selected file and the gui parent window.
    //
    // the FileWidget can check to see if the strategy is an instance
    // of this special interface, and if so, inject the values.

    public static WindowsFileChooser openFileChooser() {
        return new WindowsFileChooser(fileChooser -> {
            fileChooser.setTitle("Open File");
            fileChooser.setFile(null);
        });
    }

    public static FileWidget openFileWidget() {
        return new FileWidget(fileChooser -> {
            fileChooser.setTitle("Open File");
        });
    }

    public static FileWidget saveFileWidget() {
        return new FileWidget(fileChooser -> {
            fileChooser.setTitle("Save File");
            fileChooser.getFileChooserWidget().setMode(FileChooserWidget.Mode.SAVE);
        });
    }
}
