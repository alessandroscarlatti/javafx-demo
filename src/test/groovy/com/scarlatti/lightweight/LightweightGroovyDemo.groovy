package com.scarlatti.lightweight

import com.scarlatti.swingutils.SwingUtils
import org.junit.BeforeClass
import org.junit.Test

import static com.scarlatti.lightweight.LightweightUi.*

/**
 * @author Alessandro Scarlatti
 * @since Wednesday , 2/13/2019
 */
class LightweightGroovyDemo {

    @BeforeClass
    static void setup() {
        SwingUtils.setSystemLookAndFeel()
    }

    @Test
    void demo() {

        SimpleApp app = new SimpleApp()

        StringProperty prop1 = new StringProperty(
                key: "prop1",
                name: "Name of this Property",
                description: "lots of stuff",
                value: "initialValue"
        )

        StringProperty prop2 = new StringProperty(
                key: "prop2",
                name: "Name of this Property",
                description: "lots of stuff",
                value: "initialValue"
        )

        StringProperty prop3 = new StringProperty(key: "prop1", name: "Name of this Property", description: "lots of stuff", value: "initialValue")

        app.loadProperties([
                prop1,
                prop2,
                prop3
        ])

        Page page1 = new Page(
                key: "page1",
                name: "Page 1",
                description: "This is page 1",
                properties: [
                        "prop1", "prop2", "prop3"
                ]
        )

        Page page2 = new Page(key: "page2", name: "Page 2", description: "This is page 2")

        app.loadPage(page1)
        app.loadPage(page2)

        Activity activity1 = new Activity(
                key: "activity1",
                name: "Activity 1",
                description: "This is Activity 1.",
                pages: [
                        "page1", "page2"
                ]
        )

        app.loadActivity(activity1)

        // we are assuming that this method is implicitly
        // ASKING the runner to consider the context updated.
        // that means we want the runner to make things appear
        // properly.
        app.run("activity1")

    }
}
