package com.scarlatti.reactive;

import com.scarlatti.swingutils.SwingUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

/**
 * @author Alessandro Scarlatti
 * @since Friday, 3/1/2019
 */
public class ReactDocsTodoTest2 {

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

        private void handleDelete(ActionEvent event) {
            if (state.items.size() > 0) {
                this._setState((TodoApp todoApp) -> {
                    todoApp.state.items.remove(todoApp.state.items.size() - 1);
                });
            }
        }

        private void handleSort(ActionEvent event) {
            this._setState((TodoApp todoApp) -> {
                todoApp.state.items.sort(Comparator.comparing(item -> item.text));
            });
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
                    ReactDocsTodoTest2.TodoApp.this.handleChange(textField);
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    ReactDocsTodoTest2.TodoApp.this.handleChange(textField);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    ReactDocsTodoTest2.TodoApp.this.handleChange(textField);
                }
            });
            jPanel.add(textField);

            button = new JButton("Add #" + (state.items.size() + 1));
            button.addActionListener(this::handleSubmit);
            jPanel.add(button);

            JButton buttonDelete = new JButton("Delete Last");
            buttonDelete.addActionListener(this::handleDelete);
            jPanel.add(buttonDelete);

            JButton buttonSort = new JButton("Sort");
            buttonSort.addActionListener(this::handleSort);
            jPanel.add(buttonSort);

            container.add(jPanel);
        }

        @Override
        void _renderUpdate(Container container) {
            todoList.items = this.state.items;
            todoList._render(container);
            textField.setText(this.text);
            button.setText("Add #" + (state.items.size() + 1));
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
            // not perfect yet because items will not be updated properly.
            if (previousItemsLength < items.size()) {
                for (int i = previousItemsLength; i < items.size(); i++) {
                    JLabel jLabel = new JLabel("- " + items.get(i).text);
                    jPanel.add(jLabel);
                }
                previousItemsLength = items.size();
            } else if (previousItemsLength > items.size()) {
                // an item has been removed
                for (int i = items.size(); i < previousItemsLength; i++) {
                    jPanel.remove(jPanel.getComponents().length - 1);
                }
                previousItemsLength = items.size();
            } else {
                // lists are equal in length...we need to check for order!
                // not quite perfect...components are continuing to be instantiated
                // fresh every time.  This happens to be OK in this instance, but it's
                // not ideal.
                // ...can we abstract the notion of maintaining components
                // in some order from the specific manner of implementation
                // so that it will be more straightforward how to implement it?
                jPanel.removeAll();
                for (TodoApp.Item item : items) {
                    JLabel jLabel = new JLabel("- " + item.text);
                    jPanel.add(jLabel);
                }
                jPanel.revalidate();
                jPanel.repaint();
            }
        }
    }
}
