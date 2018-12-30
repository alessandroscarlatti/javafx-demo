package com.scarlatti.swingutils.text;

import com.scarlatti.swingutils.Widget;

import javax.swing.*;
import javax.swing.border.StrokeBorder;
import java.awt.*;
import java.util.function.Consumer;

/**
 * @author Alessandro Scarlatti
 * @since Wednesday, 12/26/2018
 */
public class MultilineTextWidget implements Widget {

    private JScrollPane jScrollPane;
    private String text;

    public MultilineTextWidget() {
    }

    public MultilineTextWidget(String text) {
        this.text = text;
    }

    public static Container ui(String text) {
        return new MultilineTextWidget(text).getUi();
    }

    public MultilineTextWidget(Consumer<MultilineTextWidget> config) {
        config.accept(this);
    }

    public static Container ui(Consumer<MultilineTextWidget> config) {
        return new MultilineTextWidget(config).getUi();
    }

    @Override
    public Container getUi() {
        if (jScrollPane == null)
            initUi();

        return jScrollPane;
    }

    private void initUi() {
        JMultilineLabel label = new JMultilineLabel(text);
        jScrollPane = new JScrollPane(label);
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane.setBorder(null);
    }

    public void setText(String text) {
        this.text = text;
    }
}
