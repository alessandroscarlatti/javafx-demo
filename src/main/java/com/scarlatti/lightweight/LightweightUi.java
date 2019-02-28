package com.scarlatti.lightweight;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Alessandro Scarlatti
 * @since Wednesday, 2/13/2019
 */
public class LightweightUi {

    public static class SimpleApp {

//        private Map<String, StringProperty> propertiesMap = new HashMap<>();
//        private Map<String, Activity> activitiesMap = new HashMap<>();
//        private Map<String, Page> pagesMap = new HashMap<>();
        private Map<String, Object> context = new HashMap<>();

        void loadProperty(StringProperty property) {
            context.put(property.key, property);
        }

        void loadProperties(Collection<StringProperty> properties) {
            // load the collection of properties into the context
            for (StringProperty property : properties)
                loadProperty(property);
        }

        void loadActivity(Activity activity) {
            // load the activity into the context
            context.put(activity.key, activity);
        }

        void loadPage(Page page) {
            // load the page into the context
            context.put(page.key, page);
        }

        void put(String key, Object object) {
            context.put(key, object);
        }

        <T> T get(String key, Class<T> clazz) {
            return (T) context.get(key);
        }

        void run(Consumer<SimpleApp> runner) {
            runner.accept(this);
        }

        void run(String activity) {
            // run an activity...
            // the only thing we have to diff if asked to re-render
            // is whether or not we already have the frame displayed!
        }
    }

    public static class StringProperty {
        String value;
        String key;
        String name;
        String description;
    }

    public static class Page {
        String key;
        String name;
        String description;
        List<String> properties;
    }

    public static class Activity {
        String key;
        String name;
        String description;
        List<String> pages;
    }

    // a default view bean would be created...
    public static class View {
        String key;
        String activity;
        boolean visible;
    }
}
