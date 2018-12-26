package com.scarlatti.swingutils.grid;

import com.scarlatti.swingutils.Widget;

import javax.swing.*;
import java.awt.*;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Tuesday, 12/25/2018
 */
public class WizardWidget implements Widget {

    @Override
    public Container getUi() {
        return null;
    }

    Container createUi() {
        JPanel widget = new JPanel();

        GroupLayout gl = new GroupLayout(widget);
        widget.setLayout(gl);

        // configure layout
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        return widget;
    }
}
