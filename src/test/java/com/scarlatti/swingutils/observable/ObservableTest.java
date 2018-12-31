package com.scarlatti.swingutils.observable;

import org.junit.Test;

import java.util.Observable;
import java.util.Observer;

/**
 * @author Alessandro Scarlatti
 * @since Monday, 12/31/2018
 */
public class ObservableTest {

    static class Bird {

    }

    static class ObservableTarget<T> extends Observable {

        T target;

        public ObservableTarget(T target) {
            this.target = target;
        }

        void update() {
            setChanged();
            notifyObservers(target);
        }
    }

    static class Penguin extends Bird {
        ObservableTarget observable = new ObservableTarget<>(this);

        void ork() {
            observable.update();
        }
    }

    static class TargetObserver<T> implements Observer {
        public void update(Observable o, Object arg) {
            update((T) arg);
        }

        public void update(T target) {
            System.out.println("TargetObserver.update() target = [" + target + "]");
        }
    }


    static class CoolWidget {
        TargetObserver<Penguin> penguinObserver = new TargetObserver<>();
    }

    @Test
    public void testObservable() {

        Penguin penguin = new Penguin();
        CoolWidget widget = new CoolWidget();

        penguin.observable.addObserver(widget.penguinObserver);

        penguin.ork();
        penguin.ork();
    }
}
