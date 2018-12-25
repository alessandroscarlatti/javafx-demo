package com.scarlatti.swingutils.grid;

import com.scarlatti.swingutils.Widget;
import com.scarlatti.swingutils.layout.RelativeLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Monday, 12/24/2018
 * <p>
 * Ala bootstrap row.
 */
public class RowWidget implements Widget {

    private List<Column> columns = new ArrayList<>();

    private JPanel rowJPanel;

    /**
     * Add onto the existing ui components
     *
     * @param container the ui component to add
     * @param width     the width for this column
     */
    public void addColumn(Container container, int width) {
        columns.add(new Column(container, width));
    }

    @Override
    public Container getUi() {
        return rowJPanel == null ? buildUiFirstTimeBox() : rowJPanel;
    }

    Container buildUiFirstTimeBox() {
        rowJPanel = new JPanel();
        rowJPanel.setBackground(Color.GREEN);
        rowJPanel.setLayout(new BoxLayout(rowJPanel, BoxLayout.X_AXIS));

        for (Column column : columns) {
            rowJPanel.add(column.container);
        }

        return rowJPanel;
    }

    Container buildUiFirstTimeGridBag() {
        rowJPanel = new JPanel();
        rowJPanel.setBackground(Color.GREEN);
        rowJPanel.setLayout(new GridBagLayout());

        int gridxCount = 0;

        for (int i = 0; i < 12; i++) {
            rowJPanel.add(Box.createRigidArea(new Dimension(40, 40)));
        }

        for (Column column : columns) {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridy = 0;
            constraints.gridx = gridxCount;
            constraints.gridwidth = column.width;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.weightx = 1;
            constraints.weighty = 1;
            gridxCount += column.width;

            rowJPanel.add(column.container, constraints);
        }

        return rowJPanel;
    }

    Container buildUiFirstTimeWithRelativeLayout() {
        rowJPanel = new JPanel();
        rowJPanel.setBackground(Color.GREEN);
        rowJPanel.setLayout(new RelativeLayout(RelativeLayout.X_AXIS));

        for (Column column : columns) {
            rowJPanel.add(column.container, (float) column.width);
        }

        return rowJPanel;
    }

    public static class Column {
        Container container;
        int width;

        public Column(Container container, int width) {
            this.container = container;
            this.width = width;
        }
    }
}
