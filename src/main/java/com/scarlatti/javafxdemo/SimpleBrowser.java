package com.scarlatti.javafxdemo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.PromptData;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.nio.file.Paths;
import java.util.Optional;


public class SimpleBrowser extends Application {

    private Scene scene;

    @Override
    public void start(Stage stage) {
        stage.setTitle("App");
        scene = new Scene(new Browser(), 750, 500, Color.web("#666970"));
        stage.setScene(scene);
        stage.show();
        stage.getIcons().addAll(JavaFxUtils.scaledImages(getClass().getResourceAsStream("/com/scarlatti/javafxdemo/utility-icon.png")));
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class Browser extends Region {

        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();
        // the interface needs to be a class field
        // so that garbage collection doesn't get rid of it
        JavaApp javaApp = new JavaApp();

        public Browser() {
            //apply the styles
            getStyleClass().add("browser");

            webEngine.setOnAlert(event -> showAlert(event.getData()));
            webEngine.setConfirmHandler(this::showConfirm);
            webEngine.setPromptHandler(this::showPrompt);

            // process page loading
            webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    if (newState == State.SUCCEEDED) {
                        JSObject win = (JSObject) webEngine.executeScript("window");
                        win.setMember("app", javaApp);
                        webEngine.executeScript("console.log = function(message) { app.log(message); }");
                    }
                    if (webEngine.getLoadWorker().getException() != null && newState == State.FAILED) {
                        System.out.println(webEngine.getLoadWorker().getException().toString());
                    }
                }
            );

            // load the home page
            try {
                webEngine.load(Paths.get("src/main/resources/com/scarlatti/javafxdemo/test.html").toUri().toURL().toExternalForm());
            } catch (Exception e) {
                throw new RuntimeException("Error loading webpage.", e);
            }

            //add components
            getChildren().add(browser);
        }

        private void showAlert(String message) {
            Dialog<Void> alert = new Dialog<>();
            alert.getDialogPane().setContentText(message);
            alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
            alert.showAndWait();
        }

        private boolean showConfirm(String message) {
            Dialog<ButtonType> confirm = new Dialog<>();
            confirm.getDialogPane().setContentText(message);
            confirm.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
            boolean result = confirm.showAndWait().filter(ButtonType.YES::equals).isPresent();

            // for debugging:
            System.out.println(result);

            return result ;
        }

        private String showPrompt(PromptData promptData) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Provide Input");
            dialog.setHeaderText(null);
            dialog.setContentText(promptData.getMessage());
            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                System.out.println(result);
                return result.get();
            }

            return null;
        }

        // JavaScript interface object
        public class JavaApp {

            public void exit() {
                Platform.exit();
            }

            public void doSomething() {
                System.out.println("Do Something...");
            }

            public void log(String message) {
                System.out.println(message);
            }
        }

        @Override
        protected void layoutChildren() {
            double w = getWidth();
            double h = getHeight();
            layoutInArea(browser, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
        }
    }
}