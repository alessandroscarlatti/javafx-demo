package com.scarlatti.swingutils.filechooser;

import com.scarlatti.swingutils.SwingUtils;
import com.scarlatti.swingutils.Widget;
import com.scarlatti.swingutils.text.MultilineTextWidget;
import com.sun.jna.Platform;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.scarlatti.swingutils.SwingUtils.IconScale.ICON_SCALE_15;
import static com.scarlatti.swingutils.SwingUtils.makeBold;
import static com.scarlatti.swingutils.filechooser.FileChooserWidget.FileExtensionFilter.filter;
import static com.scarlatti.swingutils.filechooser.FileChooserWidget.FileType;

/**
 * @author Alessandro Scarlatti
 * @since Tuesday, 12/25/2018
 */
public class FileWidget implements Widget {
    private JPanel widgetPanel;
    private JTextField fileTextField;
    private JButton fileChooserButton;
    private JPopupMenu popupMenu;
    private JMenuItem fallbackMenuItem;

    private Supplier<FileChooserWidget> fileChooserWidgetFactory = this::getDefaultFileChooserWidget;
    private FileChooserWidget fileChooserWidget = fileChooserWidgetFactory.get();
    private String title;
    private String message;

    /**
     * The currently selected file,
     * essentially the text that is in the box,
     * whether it was pasted or typed into the box,
     * or put there by the popout file chooser window.
     */
    private Path file;
    private FileType fileType = FileType.FILE;

    public FileWidget() {
    }

    public FileWidget(Consumer<FileWidget> config) {
        config.accept(this);
    }

    public static Container ui(Consumer<FileWidget> config) {
        return new FileWidget(config).getUi();
    }

    @Override
    public Container getUi() {
        if (widgetPanel == null) {
            initUi();
            initEvents();
        }

        return widgetPanel;
    }

    private Container initUi() {
        widgetPanel = new JPanel();
        GroupLayout gl = new GroupLayout(widgetPanel);
        widgetPanel.setLayout(gl);

        // build file text field
        fileTextField = new JTextField();
        if (file != null)
            fileTextField.setText(file.toAbsolutePath().toString());

        // build file chooser button
        fileChooserButton = new JButton();
        Icon popoutIcon = SwingUtils.createScaledIcon(getPopoutIconBase64String(), ICON_SCALE_15);
        fileChooserButton.setIcon(popoutIcon);

        // build title and message
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(makeBold(titleLabel.getFont()));
        Container messageComponent = MultilineTextWidget.ui(message);

        // build fallback context menu
        popupMenu = new JPopupMenu();
        fallbackMenuItem = new JMenuItem("Open basic file chooser");
        Icon fallbackIcon = SwingUtils.createScaledIcon(getFallbackIconBase64String(), ICON_SCALE_15);
        fallbackMenuItem.setIcon(fallbackIcon);
        popupMenu.add(fallbackMenuItem);

        // configure layout
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        GroupLayout.ParallelGroup widgetHGroup = gl.createParallelGroup(GroupLayout.Alignment.LEADING);
        GroupLayout.SequentialGroup widgetVGroup = gl.createSequentialGroup();
        GroupLayout.SequentialGroup fileChooserHGroup = gl.createSequentialGroup();
        GroupLayout.ParallelGroup fileChooserVGroup = gl.createParallelGroup(GroupLayout.Alignment.CENTER, false);

        if (title != null) {
            widgetHGroup.addComponent(titleLabel);
            widgetVGroup.addComponent(titleLabel);
        }
        if (message != null) {
            widgetHGroup.addComponent(messageComponent);
            widgetVGroup.addComponent(messageComponent);
        }

        fileChooserHGroup
            .addComponent(fileTextField)
            .addComponent(fileChooserButton);

        fileChooserVGroup
            .addComponent(fileTextField)
            .addComponent(fileChooserButton);

        widgetHGroup.addGroup(fileChooserHGroup);
        widgetVGroup.addGroup(fileChooserVGroup);

        gl.setHorizontalGroup(widgetHGroup);
        gl.setVerticalGroup(widgetVGroup);

        return widgetPanel;
    }

    private String getPopoutIconBase64String() {
        return "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAQAAADZc7J/AAAA30lEQVR4Ae3QNULGQBiE4RfXBqenwq3G5RTcI6nhBHCEVDhUuN6CG9AhHf4bvvplG3SmzTybXcQpZ5OMoUw5OkYZ72nl0ARkHL2lgY9p5jAdcE09n9PEdhrgjgbUzNqAA1a0JtQq84h7G9CBPxGP9jcYFMyfXr59ZI5TMWCYR0AbZ2LAMAcY4FwEKPOY90xx6QdKickop79n2A9MkFFOV+IDqph3zSVvUMwCd7a57BHLmQQBIE4t9YWWhQIJ14WOhQKrL5vpUGDlZTP1D3w3oAd5lkzADIPiHqhAaH8HkAV9YrL0B802bAAAAABJRU5ErkJggg==";
    }

    private String getFallbackIconBase64String() {
        return "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAACXBIWXMAAA7EAAAOxAGVKw4bAAACLElEQVRYhcXWP2jVUBQG8F9LKahFKxRRECIVHFwsHbrUwQgKgoMgQsVFRLHBrVI7S5Ui0kEHIwgKDtXFVrCDuMRZBEsRdJOIFIeiHRxKkeKQ9yTEZ315fb73QbjJuQe+L/f8uYc2o6OMcxilnTiIQezDjsrWD3zCAhaTOFhvqoAwSgdxCaew+x/uy5jFgyQO3m5KQBilQ5jC0XqE1sArjCdxsPhXAWGUjiZxcL9AvB3TuIDOBsmrWMNN3KgVmk5MhVF6LUc+gHe42ARy6MZ1vAijdGtxsyOM0u/oxQQ+4gn+cGwSXuNEEgerVUP+D29h7j+SwxE8CqP0t6F4xI0c+XrlqRcjsvA2TLiKpziL/diGLdiJQ7giO+qNRN0Oo7QPukqSz8jKaqnG3hpWsIh7lWS+heM1fHsxjol8Em6En7icxMHDMmorsR7FHVk15LGCPfUIWMeZJA5my5AXhJzEsxoiTteTA50YzmduWSRxMC8r8yKO1ZuEY5jejAjcxZuCbaBMFYzJsrch9kobniyY93ZhWLly7JZlfCN4Kbst+yrfPaXmgWYgjNI52bUOX5tx2ZTF59z7UjsE5MP3vh0C8t03aYeA6ki3hvl2CBiqrM+TOFhuqYAwSg+gX9bep2jOyFUG5yrr4yQOFloqIIzSbtmQ+wVXq/ay88BmcB67ECZx8K1qbEknDKO0Bx8wkcTBTH6vVSGYxGSRvCUIo/RwGKUjLSfOCehvG3k9+AWnY5MQvgDiqwAAAABJRU5ErkJggg==";
    }

    private void initEvents() {
        // when the text changes fire pathChanged event
        fileTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                pathChanged();
            }

            public void removeUpdate(DocumentEvent e) {
                pathChanged();
            }

            public void insertUpdate(DocumentEvent e) {
                pathChanged();
            }
        });

        // when the button is clicked open a file chooser dialog
        // with the file to the text
        fileChooserButton.addActionListener(e -> {
            chooseFile();
        });

        fileChooserButton.setComponentPopupMenu(popupMenu);

        fallbackMenuItem.addActionListener(e -> {
            fileChooserWidget = new SwingFileChooserWidget();
            chooseFile();
        });
    }

    private FileChooserWidget getDefaultFileChooserWidget() {
        if (Platform.isWindows()) {
            return new WindowsFileChooserWidget();
        } else {
            return new SwingFileChooserWidget();
        }
    }

    private void chooseFile() {
        // we can invert control here, and delegate to a prop
        // that would be the choose file strategy.
        // this is a hard dependency now on the Windows File Chooser...

        if (fileChooserWidget == null)
            return;

        Path selectedFile = getFileFromFileChooserWidget();

        // this is where we would probably rebuild the ui...
        if (selectedFile != null) {
            if (Files.exists(selectedFile)) {
                file = selectedFile;
                fileTextField.setText(selectedFile.toString());
            } else {
                throw new RuntimeException("File does not exist " + selectedFile);
            }
        }
    }

    private Path getFileFromFileChooserWidget() {
        // inject dependencies first
        fileChooserWidget.setGuiParent(widgetPanel);
        fileChooserWidget.setTitle(title);
        fileChooserWidget.setFile(file);
        fileChooserWidget.setFileType(fileType);

        // now invoke the dialog.
        return fileChooserWidget.chooseFile();
    }

    private void pathChanged() {
        // we do not care if the file is invalid.
        file = Paths.get(fileTextField.getText());
    }

    public Path getFile() {
        return file;
    }

    public void setFile(Path file) {
        this.file = file;
    }

    public FileChooserWidget getFileChooserWidget() {
        return fileChooserWidget;
    }

    public void setFileChooserWidget(FileChooserWidget fileChooserWidget) {
        this.fileChooserWidget = fileChooserWidget;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public void setFileChooserWidgetFactory(Supplier<FileChooserWidget> fileChooserWidgetFactory) {
        this.fileChooserWidgetFactory = fileChooserWidgetFactory;
    }
}
