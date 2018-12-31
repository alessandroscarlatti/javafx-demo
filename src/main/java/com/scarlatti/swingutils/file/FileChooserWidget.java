package com.scarlatti.swingutils.file;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alessandro Scarlatti
 * @since Sunday, 12/30/2018
 * <p>
 * An instance of this interface would likely not be thread safe,
 * since the injections are handled separately from the call to #get()
 */
public interface FileChooserWidget {

    /**
     * Inject the title into this file chooser..
     *
     * @param title
     */
    void setTitle(String title);

    /**
     * Inject the initial file into this file chooser..
     *
     * @param file
     */
    void setFile(Path file);

    /**
     * Inject the parent component into this file chooser..
     *
     * @param component
     */
    void setGuiParent(Object component);

    /**
     * Inject the mode for this file chooser.
     *
     * @param mode
     */
    void setMode(Mode mode);

    /**
     * Inject the type of file that should be selected with this file chooser.
     *
     * @param fileType
     */
    void setFileType(FileType fileType);

    /**
     * Inject the filters that should be available in this file chooser.
     *
     * @param filters
     */
    void setFilters(List<FileExtensionFilter> filters);

    /**
     * Inject the filters that should be available in this file chooser.
     *
     * @param filters
     */
    void setFilters(FileExtensionFilter... filters);

    /**
     * Get the file selection from the user.
     *
     * @return the file selected by the user.
     */
    Path chooseFile();

    enum Mode {
        OPEN,
        SAVE
    }

    enum FileType {
        FILE,
        DIRECTORY
    }


    class FileExtensionFilter {
        private String description;
        private List<String> patterns = new ArrayList<>();

        public FileExtensionFilter() {
        }

        public FileExtensionFilter(String description, List<String> patterns) {
            this.description = description;
            this.patterns = patterns;
        }

        /**
         * create a filter
         *
         * @param filterParams you must pass at least 2 arguments, the first argument
         *                     is the name of this filter and the remaining arguments
         *                     are the file extensions.  For example, to select only .txt files
         *                     you might pass {"Text Files (*.txt)", "txt"}, note the absence of the period
         *                     in the file extensions.
         */
        public static FileExtensionFilter filter(String... filterParams) {
            if (filterParams.length < 2) {
                throw new IllegalArgumentException();
            }

            FileExtensionFilter newFilter = new FileExtensionFilter();
            newFilter.description = filterParams[0];
            newFilter.patterns = Arrays.asList(filterParams).subList(1, filterParams.length);
            return newFilter;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<String> getPatterns() {
            return patterns;
        }

        public void setPatterns(List<String> patterns) {
            this.patterns = patterns;
        }
    }
}
