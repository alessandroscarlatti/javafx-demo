package com.scarlatti.reactive;

import com.scarlatti.swingutils.SwingUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.Reference;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Alessandro Scarlatti
 * @since Thursday, 2/28/2019
 */
public class ComponentReplacementTest {

    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void canReplaceComponentBeforeAndAfterAndRemainFocused() {
        SwingUtils.display(() -> {
            JPanel jPanel = new JPanel();

            JButton button1 = new JButton("Button1");
            JButton button2 = new JButton("Button2");
            JButton button3 = new JButton("Button3");
            JButton button4 = new JButton("Button4");

            jPanel.add(button1);
            jPanel.add(button2);
            jPanel.add(button3);
            jPanel.add(button4);

            AtomicReference<JButton> button1Ref = new AtomicReference<>(button1);
            AtomicReference<JButton> button4Ref = new AtomicReference<>(button4);

            // replace component BEFORE
            // replace button1 with button 1.1, then 1.2, etc.
            AtomicReference<Integer> count = new AtomicReference<>(1);
            button2.addActionListener(e -> {
                JButton button1a = new JButton("Button1." + count.get());
                jPanel.remove(0);
                jPanel.add(button1a, 0);

                button1Ref.set(button1a);

                jPanel.revalidate();
                jPanel.repaint();

                count.set(count.get() + 1);
            });

            // replace component AFTER
            button3.addActionListener(e -> {
                JButton button4a = new JButton("Button4." + count.get());
                jPanel.remove(3);
                jPanel.add(button4a, 3);

                button1Ref.set(button4a);

                jPanel.revalidate();
                jPanel.repaint();

                count.set(count.get() + 1);
            });

            // replace same component


            return jPanel;
        });
    }

    /**
     * By default, Swing moves the focus downstream to the nearest sibling component.
     * However, Chrome removes focus from any specific control.
     * However, it is AS IF the previous sibling is focused.
     * The previous sibling is NOT focused, but advancing the focus focuses the original position again.
     */
    @Test
    public void canReplaceComponentExactlyAndRemainFocused() {
        SwingUtils.display(() -> {
            JPanel jPanel = new JPanel();

            JButton button1 = new JButton("Button1");
            JButton button2 = new JButton("Button2");
            JButton button3 = new JButton("Button3");
            JButton button4 = new JButton("Button4");

            jPanel.add(button1);
            jPanel.add(button2);
            jPanel.add(button3);
            jPanel.add(button4);

            AtomicReference<JButton> button2Ref = new AtomicReference<>(button1);

            // replace component EXACTLY
            // replace button2 with button 2.1, then 2.2, etc.
            AtomicReference<Integer> count = new AtomicReference<>(1);

            ActionListener actionListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton button2a = new JButton("Button2." + count.get());
                    button2a.addActionListener(this);
                    jPanel.remove(1);
                    jPanel.add(button2a, 1);

                    button2Ref.set(button2a);

                    jPanel.revalidate();
                    jPanel.repaint();

                    count.set(count.get() + 1);
                }
            };

            button2.addActionListener(actionListener);

            return jPanel;
        });
    }

    @Test
    public void replaceComponentInGroupLayout() {
        SwingUtils.display(() -> {
            JPanel jPanel = new JPanel();

            GroupLayout gl = new GroupLayout(jPanel);
            jPanel.setLayout(gl);

            JButton button1 = new JButton("Button1");
            JButton button2 = new JButton("Button2");
            JButton button3 = new JButton("Button3");
            JButton button4 = new JButton("Button4");


            GroupLayout.SequentialGroup hGroup = gl.createSequentialGroup()
                .addComponent(button1)
                .addComponent(button2)
                .addComponent(button3)
                .addComponent(button4);

            GroupLayout.ParallelGroup vGroup = gl.createParallelGroup()
                .addComponent(button1)
                .addComponent(button2)
                .addComponent(button3)
                .addComponent(button4);

            gl.setHorizontalGroup(hGroup);
            gl.setVerticalGroup(vGroup);

            AtomicReference<JButton> button2Ref = new AtomicReference<>(button2);

            // replace component EXACTLY
            // replace button2 with button 2.1, then 2.2, etc.
            AtomicReference<Integer> count = new AtomicReference<>(1);

//            System.out.println(findPrevFocus().getName());

            ActionListener actionListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton button2a = new JButton("Button2." + count.get());
                    button2a.addActionListener(this);
                    button2Ref.get().setFocusable(false);

                    button2.revalidate();

                    gl.replace(button2Ref.get(), button2a);

                    button2Ref.set(button2a);

                    button2a.requestFocus();

                    jPanel.revalidate();
                    jPanel.repaint();

                    count.set(count.get() + 1);
                }
            };

            button2.addActionListener(actionListener);

            return jPanel;
        });
    }

    public static Component findPrevFocus() {
        Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        Container root = c.getFocusCycleRootAncestor();

        FocusTraversalPolicy policy = root.getFocusTraversalPolicy();
        Component prevFocus = policy.getComponentBefore(root, c);
        if (prevFocus == null) {
            prevFocus = policy.getDefaultComponent(root);
        }
        return prevFocus;
    }

}
