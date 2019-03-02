package com.scarlatti.reactive;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Alessandro Scarlatti
 * @since Thursday, 2/28/2019
 *
 * An instance is a component in the virtual component tree.
 * This is only "data".
 * An instance may or may not contain any actual Swing instances.
 */
public class ReactiveComponent {

    private List<ReactiveComponent> children;
    private Container _previousContainer;
    private boolean hasRenderedFirst;

    /**
     * Analogous to React.Component#construct()
     */
    public void _construct() {

    }

    /**
     * Analogous to React.Component#setState().
     * The component should update after the state is changed.
     * In this model this should happen synchronously.
     */
    <T extends ReactiveComponent> void _setState(Consumer<T> stateModification) {
        stateModification.accept((T) this);
        if (hasRenderedFirst) {
            _renderUpdate(_previousContainer);
        } else {
            _renderFirst(_previousContainer);
            hasRenderedFirst = true;
        }
    }

    /**
     * Somewhat analogous to React.Component#render().
     * Difference is that this will be much more technical.
     * React components return HTML which is still declarative.
     * Swing is imperative.
     *
     * There will be contextual parameters provided
     * so that this component can properly render itself.
     * @param container
     */
    public void _render(/*contextual parameters*/Container container) {
        _previousContainer = container;
        if (hasRenderedFirst) {
            _renderUpdate(container);
        } else {
            _renderFirst(container);
            hasRenderedFirst = true;
        }
    }

    void _renderFirst(Container container) {

    }

    void _renderUpdate(Container container) {

    }

    /**
     * This will be called when it is determined that this
     * particular component instance will update its GUI components.
     *
     * It is up to the implementation whether it wants to take
     * a declarative or imperative approach to resolving the differences,
     * or whether to update at all!
     */
    private void _update(ReactiveComponent nextDefinition) {

    }
}
