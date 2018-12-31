package com.scarlatti.swingutils.file;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alessandro Scarlatti
 * @since Sunday, 12/30/2018
 */
public abstract class AbstractFileChooserWidget implements FileChooserWidget {

    List<FileExtensionFilter> filters = new ArrayList<>();
    Path file;
    String title;
    Window parent;
    Mode mode = Mode.OPEN;
    FileType fileType = FileType.FILE;

    {
        filters.add(
            FileExtensionFilter.filter("All files (*.*)", "*.*")
        );
    }

    @Override
    public Path chooseFile() {
        this.file = doChooseFile();
        return this.file;
    }

    /**
     * Concrete classes can use any strategy to obtain the file.
     *
     * @return the file obtained from the user.
     */
    abstract Path doChooseFile();

    @Override
    public void setGuiParent(Object guiParent) {
        if (guiParent instanceof Component) {
            parent = SwingUtilities.getWindowAncestor((Component) guiParent);
        } else if (guiParent instanceof Window) {
            parent = (Window) guiParent;
        } else {
            throw new RuntimeException("Invalid parent type " + guiParent);
        }
    }

    public Path getFile() {
        return file;
    }

    /**
     * add a filter to the user-selectable list of file filters
     *
     * @param filterParams
     */
    public void addFilter(String... filterParams) {
        filters.add(FileExtensionFilter.filter(filterParams));
    }

    @Override
    public void setFile(Path file) {
        this.file = file;
    }

    @Override
    public void setFilters(List<FileExtensionFilter> filters) {
        this.filters = filters;
    }

    @Override
    public void setFilters(FileExtensionFilter... filters) {
        setFilters(Arrays.asList(filters));
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }
}
