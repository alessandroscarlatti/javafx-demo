package com.scarlatti.swingutils.grid;

import com.scarlatti.swingutils.SwingUtils;
import com.scarlatti.swingutils.TestUtils;
import com.scarlatti.swingutils.layout.RelativeLayout;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Monday, 12/24/2018
 */
public class RowWidgetTest {

    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

//    /**
//     * A row is divided into columns, but the columns can have width.
//     */
//    @Test
//    public void rowCanDisplayTwoColumnsEquallySized() {
//        RowWidget rowWidget = new RowWidget();
//
//        JPanel redJPanel = TestUtils.redJPanel();
//        redJPanel.setLayout(new GridBagLayout());
//        redJPanel.setMinimumSize(new Dimension(40, 40));
//        redJPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
//        redJPanel.setPreferredSize(new Dimension(100, 100));
//
//        JPanel blueJPanel = TestUtils.blueJPanel();
//        blueJPanel.setLayout(new GridBagLayout());
//        blueJPanel.setMinimumSize(new Dimension(40, 40));
//        blueJPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
//        blueJPanel.setPreferredSize(new Dimension(100, 100));
//
//        rowWidget.addColumn(redJPanel, 3);
//        rowWidget.addColumn(blueJPanel, 5);
//
//        SwingUtils.display(rowWidget::buildUiFirstTimeBox);
//    }
//
//    @Test
//    public void rowCanDisplayTwoColumnsEquallySizedWithGridBag() {
//        RowWidget rowWidget = new RowWidget();
//
//        JPanel redJPanel = TestUtils.redJPanel();
//        redJPanel.setLayout(new GridBagLayout());
//        redJPanel.setMinimumSize(new Dimension(40, 40));
//        redJPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
//        redJPanel.setPreferredSize(new Dimension(100, 100));
//
//        JPanel blueJPanel = TestUtils.blueJPanel();
//        blueJPanel.setLayout(new GridBagLayout());
//        blueJPanel.setMinimumSize(new Dimension(40, 40));
//        blueJPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
//        blueJPanel.setPreferredSize(new Dimension(100, 100));
//
//        rowWidget.addColumn(redJPanel, 3);
//        rowWidget.addColumn(blueJPanel, 3);
//
//        SwingUtils.display(rowWidget::buildUiFirstTimeGridBag);
//    }
//
//    /**
//     * A row is divided into columns, but the columns can have width.
//     */
//    @Test
//    public void rowCanDisplayTwoColumnsUnequallySized() {
//        RowWidget rowWidget = new RowWidget();
//
//        JPanel redJPanel = TestUtils.redJPanel();
//        redJPanel.setLayout(new GridBagLayout());
//        redJPanel.setMinimumSize(new Dimension(40, 40));
//        redJPanel.setMaximumSize(new Dimension(500, 200));
//        redJPanel.setPreferredSize(new Dimension(100, 100));
//
//        JPanel blueJPanel = TestUtils.blueJPanel();
//        blueJPanel.setLayout(new GridBagLayout());
//        blueJPanel.setMinimumSize(new Dimension(40, 40));
//        blueJPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
//        blueJPanel.setPreferredSize(new Dimension(100, 100));
//
//        rowWidget.addColumn(redJPanel, 3);
//        rowWidget.addColumn(blueJPanel, 5);
//
//        SwingUtils.display(rowWidget::buildUiFirstTimeBox);
//    }
//
//
//    /**
//     * A row is divided into columns, but the columns can have width.
//     */
//    @Test
//    public void rowCanDisplayTwoColumnsUnequallySizedWithRelativeLayout() {
//        RowWidget rowWidget = new RowWidget();
//
//        JPanel redJPanel = TestUtils.redJPanel();
//        redJPanel.setLayout(new GridBagLayout());
//        redJPanel.setMinimumSize(new Dimension(40, 40));
//        redJPanel.setMaximumSize(new Dimension(500, 200));
//        redJPanel.setPreferredSize(new Dimension(100, 100));
//
//        JPanel blueJPanel = TestUtils.blueJPanel();
//        blueJPanel.setLayout(new GridBagLayout());
//        blueJPanel.setMinimumSize(new Dimension(40, 40));
//        blueJPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
//        blueJPanel.setPreferredSize(new Dimension(100, 100));
//
//        rowWidget.addColumn(blueJPanel, 5);
//        rowWidget.addColumn(redJPanel, 3);
//
//        SwingUtils.display(rowWidget::buildUiFirstTimeWithRelativeLayout);
//    }

    @Test
    public void useRelativeLayoutWithSomeFixedWidthPanels() {

        SwingUtils.display(() -> {
            JPanel redJPanel = TestUtils.redJPanel();
            redJPanel.setLayout(new GridBagLayout());
            redJPanel.setMinimumSize(new Dimension(40, 40));
            redJPanel.setMaximumSize(new Dimension(500, 200));
            redJPanel.setPreferredSize(new Dimension(100, 100));

            JPanel blueJPanel = TestUtils.blueJPanel();
            blueJPanel.setLayout(new GridBagLayout());
            blueJPanel.setMinimumSize(new Dimension(40, 40));
            blueJPanel.setMaximumSize(new Dimension(300, 200));
            blueJPanel.setPreferredSize(new Dimension(100, 100));

            JPanel bodyJPanel = new JPanel(new RelativeLayout());
            bodyJPanel.setBackground(Color.GREEN);
            bodyJPanel.add(Box.createGlue(), .5f);
            bodyJPanel.add(redJPanel);  // fixed size
            bodyJPanel.add(blueJPanel, 1f);
            bodyJPanel.add(Box.createGlue(), .5f);

            return bodyJPanel;
        });
    }

    @Test
    public void useRowWidget() {
        SwingUtils.display(() -> {
            JPanel jPanel = new JPanel();
            jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
            jPanel.add(row1().getUi());
            jPanel.add(row2().getUi());
            return jPanel;
        });
    }

    @Test
    public void useRelativeLayoutVertical() {
        SwingUtils.display(() -> {
            JPanel jPanel = new JPanel(new RelativeLayout(RelativeLayout.Y_AXIS));

            JPanel redJPanel = TestUtils.redJPanel();
            redJPanel.setLayout(new GridBagLayout());
            redJPanel.setMinimumSize(new Dimension(40, 40));
            redJPanel.setMaximumSize(new Dimension(500, 200));
            redJPanel.setPreferredSize(new Dimension(100, 100));

            JPanel blueJPanel = TestUtils.blueJPanel();
            blueJPanel.setLayout(new GridBagLayout());
            blueJPanel.setMinimumSize(new Dimension(40, 40));
            blueJPanel.setMaximumSize(new Dimension(300, 200));
            blueJPanel.setPreferredSize(new Dimension(100, 100));


            jPanel.add(redJPanel);
            jPanel.add(blueJPanel);
            jPanel.setBackground(Color.GREEN);
            return jPanel;
        });
    }

    @Test
    public void useRowWidgetForTable() {
        SwingUtils.display(() -> {
            JPanel body = new JPanel(new RelativeLayout());
            body.add(table(), 1f);

            return body;
        });
    }

    private JPanel table() {
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new RelativeLayout(RelativeLayout.Y_AXIS));
        jPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        jPanel.add(row1().getUi(), .5f);
        jPanel.add(row2().getUi(), .5f);
        return jPanel;
    }

    private RowWidget row1() {
        return RowWidget.createRow(row -> {

            JPanel redJPanel = TestUtils.redJPanel();
            redJPanel.setLayout(new GridBagLayout());
            redJPanel.setMinimumSize(new Dimension(40, 40));
            redJPanel.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
            redJPanel.setPreferredSize(new Dimension(100, 100));

            JPanel blueJPanel = TestUtils.blueJPanel();
            blueJPanel.setLayout(new GridBagLayout());
            blueJPanel.setMinimumSize(new Dimension(40, 40));
            blueJPanel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
            blueJPanel.setPreferredSize(new Dimension(100, 100));

            row.setBackground(Color.GREEN);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            row.add(Box.createGlue(), .5f);
            row.add(redJPanel);  // fixed size
            row.add(blueJPanel, 1f);
            row.add(Box.createGlue(), .5f);
        });
    }

    private RowWidget row2() {
        return RowWidget.createRow(row -> {

            JPanel redJPanel = TestUtils.redJPanel();
            redJPanel.setLayout(new GridBagLayout());
            redJPanel.setMinimumSize(new Dimension(40, 40));
            redJPanel.setMaximumSize(new Dimension(500, 200));
            redJPanel.setPreferredSize(new Dimension(100, 100));

            JPanel blueJPanel = TestUtils.blueJPanel();
            blueJPanel.setLayout(new GridBagLayout());
            blueJPanel.setMinimumSize(new Dimension(40, 40));
            blueJPanel.setMaximumSize(new Dimension(300, 200));
            blueJPanel.setPreferredSize(new Dimension(100, 100));

            row.setBackground(Color.ORANGE);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            row.add(Box.createGlue(), .2f);
            row.add(redJPanel);  // fixed size
            row.add(blueJPanel, 1f);
            row.add(Box.createGlue(), .5f);
        });
    }
}
