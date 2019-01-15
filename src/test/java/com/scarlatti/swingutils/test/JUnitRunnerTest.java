package com.scarlatti.swingutils.test;

import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * @author Alessandro Scarlatti
 * @since Monday, 1/7/2019
 */
public class JUnitRunnerTest {

    @Test
    public void testJUniRunner() {
        JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.addListener(new TextListener(System.out));
        Result result = jUnitCore.run(SmokeTests.class);

        System.out.println(result);
    }
}
