package com.scarlatti.swingutils.text;

import com.scarlatti.swingutils.Widget;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Alessandro Scarlatti
 * @since Saturday, 1/19/2019
 */
public class HtmlTextWidget implements Widget {

    private JEditorPane widgetJEditorPane;
    private String html;
    private Map<String, Object> linkActions = new HashMap<>();

    public HtmlTextWidget(Consumer<HtmlTextWidget> config) {
        config.accept(this);
    }

    public static Container ui(Consumer<HtmlTextWidget> config) {
        return new HtmlTextWidget(config).getUi();
    }

    @Override
    public Container getUi() {
        if (widgetJEditorPane == null)
            initUi();

        return widgetJEditorPane;
    }

    private void initUi() {
        widgetJEditorPane = new JEditorPane();
        widgetJEditorPane.setContentType("text/html");
        widgetJEditorPane.setEditable(false);
        widgetJEditorPane.setOpaque(false);
        widgetJEditorPane.setText(html);
        widgetJEditorPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    Object action = linkActions.get(e.getDescription());

                    if (action instanceof Runnable) {
                        ((Runnable) action).run();
                    } else if (action instanceof GuiClickAction) {
                        // may need to inject a value if runnable is instance of
                        // a special interface that can accept a Swing component
                        // with the intention being that that instance will use
                        // the component handed to it as a parent for
                        // gui operations.
                        ((GuiClickAction) action).accept(widgetJEditorPane);
                    } else {
                        throw new RuntimeException("Unrecognized action type: " + action.getClass());
                    }
                } catch (Exception e1) {
                    throw new RuntimeException(e1);
                }
            }
        });
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public void onClick(String linkHref, Runnable action) {
        linkActions.put(linkHref, action);
    }

    public void onClick(String linkHref, GuiClickAction action) {
        linkActions.put(linkHref, action);
    }

    public interface GuiClickAction extends Consumer<Container> {
    }
}
