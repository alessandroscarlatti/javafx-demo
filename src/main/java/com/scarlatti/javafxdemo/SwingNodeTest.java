//package com.scarlatti.javafxdemo;
//
//import javax.swing.*;
//import java.awt.*;
//import java.util.function.Consumer;
//
///**
// * ______    __                         __           ____             __     __  __  _
// * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
// * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
// * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
// * Thursday, 12/20/2018
// */
//public class SwingNodeTest {
//
//    private <T extends JComponent> void addNode(T component, Consumer<SwingNode<T>> config) {
//        SwingNode<T> swingNode = new SwingNode<>(component);
//        config.accept(swingNode);
//    }
//
//    public static void main(String[] args) {
//        SwingNodeTest test = new SwingNodeTest();
//
//        test.addNode(new JPanel(), panel -> {
//            panel.addNode(new JButton(), jButton -> {
//                jButton.configure(jButton1 -> {
//                    jButton1.setText("asdf");
//                });
//            });
//
//            panel.configure(jPanel -> {
//                jPanel.setToolTipText("asdf");
//                jPanel.addPropertyChangeListener();
//                jPanel.add()
//            });a
//        });
//    }
//
//    private static class SwingNode<T extends JComponent> {
//        private T target;
//
//        public SwingNode(T target) {
//            this.target = target;
//        }
//
//        private <E extends JComponent> void addNode(E component, Consumer<SwingNode<E>> config) {
//            SwingNode<E> swingNode = new SwingNode<>(component);
//            config.accept(swingNode);
//
//        }
//
//        private void configure(Consumer<T> config) {
//            config.accept(target);
//        }
//    }
//
//}
