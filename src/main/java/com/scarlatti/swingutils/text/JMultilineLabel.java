package com.scarlatti.swingutils.text;

import javax.swing.*;

public class JMultilineLabel extends JTextArea {

    {
        setEditable(false);
        setCursor(null);
        setOpaque(false);
        setFocusable(true);
        setFont(UIManager.getFont("Label.font"));
        setWrapStyleWord(true);
        setLineWrap(true);
    }

    public JMultilineLabel() {
    }

    public JMultilineLabel(String text) {
        super(text);
    }
} 