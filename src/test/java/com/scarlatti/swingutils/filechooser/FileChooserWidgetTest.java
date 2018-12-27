package com.scarlatti.swingutils.filechooser;

import com.scarlatti.swingutils.SwingUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static com.scarlatti.swingutils.filechooser.WindowsFileChooser.WindowsFileChooserProps.Mode.SAVE;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 12/25/2018
 */
public class FileChooserWidgetTest {

    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void createWidgetForOpening() {
        SwingUtils.display(() -> {
            FileChooserWidget fileChooserWidget = new FileChooserWidget(fileChooser -> {
                fileChooser.initialFile = Paths.get("build.gradle");
                fileChooser.fileChoiceStrategy = () -> {
                    WindowsFileChooser windowsFileChooser = new WindowsFileChooser(wFileChooser -> {
                        wFileChooser.title = "Choose gradle build";
                        wFileChooser.initialFile = fileChooser.self.getState().path;
                        wFileChooser.addFilter("All files (*.*)", "*.*");
                        wFileChooser.addFilter("Gradle files (*.gradle)", "*.gradle");
                    });

                    return windowsFileChooser.getFile();
                };
            });

            return fileChooserWidget.getUi();
        });
    }

    @Test
    public void createWidgetForSaving() {
        SwingUtils.display(() -> {
            FileChooserWidget fileChooser = new FileChooserWidget(_fileChooser -> {
                _fileChooser.initialFile = Paths.get("build.gradle");
                _fileChooser.fileChoiceStrategy = () -> {
                    WindowsFileChooser wFileChooser = new WindowsFileChooser(_wFileChooser -> {
                        _wFileChooser.title = "Choose gradle build";
                        _wFileChooser.mode = SAVE;
                        _wFileChooser.initialFile = _fileChooser.self.getState().path;
                        _wFileChooser.addFilter("All files (*.*)", "*.*");
                        _wFileChooser.addFilter("Gradle files (*.gradle)", "*.gradle");
                    });

                    return wFileChooser.getFile();
                };
            });

            return fileChooser.getUi();
        });
    }
}
