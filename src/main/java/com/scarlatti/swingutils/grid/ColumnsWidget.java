package com.scarlatti.swingutils.grid;

import com.scarlatti.swingutils.Widget;
import com.scarlatti.swingutils.layout.RelativeLayout;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;

/**
 * @author Alessandro Scarlatti
 * @since Wednesday, 12/28/2018
 */
public class ColumnsWidget implements Widget {

    private JPanel jPanel;
    private List<Container> components = new ArrayList<>();
    private List<ComponentDefinition> definitions = new ArrayList<>();

    public ColumnsWidget() {
    }

    public ColumnsWidget(Consumer<ColumnsWidget> config) {
        config.accept(this);
    }

    public void addRelativeColumn(Container component) {
        components.add(component);
        definitions.add(new ComponentDefinition(component, true));
    }

    public void addFixedColumn(Container component) {
        components.add(component);
        definitions.add(new ComponentDefinition(component, false));
    }

    @Override
    public Container getUi() {
        if (jPanel == null)
            initUi();

        return jPanel;
    }

    private void initUi() {
        jPanel = new JPanel();
        GroupLayout gl = new GroupLayout(jPanel);
        jPanel.setLayout(gl);

        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup widgetHGroup = gl.createSequentialGroup();
        GroupLayout.ParallelGroup widgetVGroup = gl.createParallelGroup(GroupLayout.Alignment.CENTER);

        for (Container component : components) {

//            JPanel wrapper = new JPanel(new BorderLayout());
//            wrapper.setBorder(new BevelBorder(BevelBorder.RAISED));
//            wrapper.add(component, BorderLayout.CENTER);
//
//            GroupLayout.ParallelGroup componentHGroup = gl.createParallelGroup(GroupLayout.Alignment.CENTER, true)
//                .addComponent(wrapper, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
//            widgetHGroup.addGroup(componentHGroup);
//
//            GroupLayout.ParallelGroup componentVGroup = gl.createParallelGroup(GroupLayout.Alignment.CENTER, true)
//                .addComponent(wrapper);
//            widgetVGroup.addGroup(componentVGroup);

            if (component instanceof JScrollPane) {
                widgetHGroup.addComponent(component, DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, PREFERRED_SIZE);
            } else {
                widgetHGroup.addComponent(component, DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
            }

            widgetVGroup.addComponent(component, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        }

        widgetHGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED);

        gl.setHorizontalGroup(widgetHGroup);
        gl.setVerticalGroup(widgetVGroup);

//        jPanel.setLayout(new RelativeLayout());
//
//        for (ComponentDefinition definition : definitions) {
//            if (definition.relative) {
//                jPanel.add(definition.component, 1f);
//            } else {
//                jPanel.add(definition.component);
//            }
//        }
    }

    private static class ComponentDefinition {
        Container component;
        boolean relative = true;

        public ComponentDefinition() {
        }

        public ComponentDefinition(Container component, boolean relative) {
            this.component = component;
            this.relative = relative;
        }
    }
}
