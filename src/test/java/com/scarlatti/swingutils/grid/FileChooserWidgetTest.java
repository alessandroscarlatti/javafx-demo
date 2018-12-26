package com.scarlatti.swingutils.grid;

import com.scarlatti.swingutils.SwingUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.scarlatti.swingutils.grid.FileChooserWidget.FileChooserMode.OPEN;

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
    public void createWidget() {
        SwingUtils.display(() -> {
            return new FileChooserWidget(OPEN).getUi();
        });
    }
}
