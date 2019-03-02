package com.scarlatti.reactive;

import com.scarlatti.swingutils.SwingUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author Alessandro Scarlatti
 * @since Friday, 3/1/2019
 */
public class ReactDocsTodoTest {

    @BeforeClass
    public static void setup() {
        SwingUtils.setSystemLookAndFeel();
    }

    @Test
    public void todoTest() {
        SwingUtils.display(() -> {
            JPanel jPanel = new JPanel();

            TodoApp todoApp = new TodoApp();
            todoApp._construct();
            todoApp._render(jPanel);

            return jPanel;
        });
    }

    private static class TodoApp extends ReactiveComponent {

        private State state;
        private JButton button;
        private TodoList todoList;
        private JTextField textField;
        private String text;

        private static class State {
            List<Item> items;
        }

        private static class Item {
            String text;
            int id;
        }

        @Override
        public void _construct() {
            state = new State();
            state.items = new ArrayList<>();
            text = "";
        }

        private void handleChange(JTextField textField) {
            text = textField.getText();
        }

        private void handleSubmit(ActionEvent event) {
            if (text.isEmpty())
                return;

            this._setState((TodoApp todoApp) -> {
                Item item = new Item();
                item.id = new Random(new Date().getTime()).nextInt();
                item.text = text;

                todoApp.state.items.add(item);
                text = "";
            });
        }

        @Override
        void _renderFirst(Container container) {
            // need a title
            JPanel jPanel = new JPanel();
            jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

            JLabel title = new JLabel("TODO");
            jPanel.add(title);

            todoList = new TodoList();
            todoList.items = this.state.items;

            todoList._construct();
            todoList._render(jPanel);

            JLabel prompt = new JLabel("What needs to be done?");
            jPanel.add(prompt);

            textField = new JTextField();
            textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    TodoApp.this.handleChange(textField);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    TodoApp.this.handleChange(textField);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    TodoApp.this.handleChange(textField);
                }
            });
            jPanel.add(textField);

            button = new JButton("Add #" + (state.items.size() + 1));
            button.addActionListener(this::handleSubmit);
            jPanel.add(button);
            container.add(jPanel);
        }

        @Override
        void _renderUpdate(Container container) {
            button.setText("Add #" + (state.items.size() + 1));

            todoList.items = this.state.items;
            todoList._render(container);
            textField.setText(this.text);
        }
    }

    private static class TodoList extends ReactiveComponent {

        private JPanel jPanel;
        private List<TodoApp.Item> items;
        private int previousItemsLength;

        @Override
        void _renderFirst(Container container) {
            jPanel = new JPanel();
            jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

            for (TodoApp.Item item : items) {
                JLabel jLabel = new JLabel("- " + item.text);
                jPanel.add(jLabel);
            }

            previousItemsLength = items.size();
            container.add(jPanel);
        }

        @Override
        void _renderUpdate(Container container) {
            if (previousItemsLength < items.size()) {
                for (int i = previousItemsLength; i < items.size(); i++) {
                    JLabel jLabel = new JLabel("- " + items.get(i).text);
                    jPanel.add(jLabel);
                }
                previousItemsLength = items.size();
            }
        }
    }
}
