package com.scarlatti.swingutils.wizard;

import com.scarlatti.swingutils.SwingUtils;
import com.scarlatti.swingutils.Widget;
import com.scarlatti.swingutils.grid.ScrollablePanel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.function.Consumer;

import static com.scarlatti.swingutils.SwingUtils.IconScale.ICON_SCALE_127;
import static com.scarlatti.swingutils.SwingUtils.IconScale.ICON_SCALE_31;
import static com.scarlatti.swingutils.SwingUtils.IconScale.ICON_SCALE_63;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Tuesday, 12/25/2018
 */
public class WizardWidget implements Widget {

    private WizardWidgetProps props = new WizardWidgetProps();
    private JPanel widgetPanel;
    private JLabel wizardIcon;
    private Container wizardTitle;

    public WizardWidget() {
    }

    public WizardWidget(WizardWidgetProps props) {
        this.props = props;
    }

    public WizardWidget(Consumer<WizardWidgetProps> config) {
        config.accept(props);
    }

    public static Container ui(Consumer<WizardWidgetProps> config) {
        return new WizardWidget(config).getUi();
    }

    @Override
    public Container getUi() {
        if (widgetPanel == null) {
            initUi();
        }

        return widgetPanel;
    }

    Container initUi() {
        widgetPanel = new JPanel();
//        widgetPanel.setBackground(Color.GREEN);

        GroupLayout gl = new GroupLayout(widgetPanel);
        widgetPanel.setLayout(gl);

        // create the icon
        initIcon();

        // create the title
        initTitle();

        // create the content wrapper
        JPanel wizardContentWrapper = new ScrollablePanel();
        wizardContentWrapper.setBackground(Color.BLUE);
        wizardContentWrapper.setLayout(new BorderLayout());
        wizardContentWrapper.add(props.wizardContent, BorderLayout.CENTER);

        JScrollPane wizardContentWrapperScrollPane = new JScrollPane(wizardContentWrapper);
        configureWizardContentWrapperScrollPane(wizardContentWrapperScrollPane);

        // configureWizardContentWrapperScrollPane layout
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        JPanel separator = createSeparator();

        GroupLayout.ParallelGroup wizardSectionHGroup = gl
            .createParallelGroup()
            .addComponent(wizardTitle)
            .addComponent(wizardContentWrapperScrollPane);

        GroupLayout.SequentialGroup wizardSectionVGroup = gl
            .createSequentialGroup()
            .addComponent(wizardTitle)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(wizardContentWrapperScrollPane);

        GroupLayout.SequentialGroup widgetHGroup = gl
            .createSequentialGroup()
            .addComponent(wizardIcon)
            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)

            // don't allow to stretch the separator...
            .addGroup(
                gl
                    .createParallelGroup(GroupLayout.Alignment.CENTER, false)
                    .addComponent(separator)
            )

            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(wizardSectionHGroup);

        GroupLayout.ParallelGroup widgetVGroup = gl
            .createParallelGroup()
            .addComponent(wizardIcon)
            .addComponent(separator)
            .addGroup(wizardSectionVGroup);

        gl.setHorizontalGroup(widgetHGroup);
        gl.setVerticalGroup(widgetVGroup);

        return widgetPanel;
    }

    private void initIcon() {
        wizardIcon = new JLabel();
        Icon icon = props.icon;
        wizardIcon.setIcon(icon);
    }

    private void initTitle() {
        JLabel jLabel = new JLabel(props.title);
        jLabel.setOpaque(false);
        jLabel.setFont(jLabel.getFont().deriveFont(Font.PLAIN, 24f));

        wizardTitle = jLabel;
    }

    private JPanel createSeparator() {
        JPanel jPanel = new JPanel(new BorderLayout());
        JSeparator jSeparator = new JSeparator(JSeparator.VERTICAL);
        jPanel.add(jSeparator, BorderLayout.CENTER);

        return jPanel;
    }

    private void configureWizardContentWrapperScrollPane(JScrollPane scrollPane) {
        // no border
        scrollPane.setBorder(null);

        // scroll X
        switch (props.scrollX) {
            case ALWAYS:
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                break;
            case NEVER:
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                break;
            case AUTO:
            default:
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }

        // scroll Y
        switch (props.scrollY) {
            case ALWAYS:
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                break;
            case NEVER:
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                break;
            case AUTO:
            default:
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        }
    }

    public static class WizardWidgetProps {
        String title;
        Icon icon = SwingUtils.createScaledIconFromResource("/com/scarlatti/javafxdemo/wrench-flat.png", ICON_SCALE_63);
        Container wizardContent;
        ScrollPolicy scrollX = ScrollPolicy.NEVER;
        ScrollPolicy scrollY = ScrollPolicy.AUTO;

        public enum ScrollPolicy {
            ALWAYS,
            AUTO,
            NEVER
        }
    }
}
