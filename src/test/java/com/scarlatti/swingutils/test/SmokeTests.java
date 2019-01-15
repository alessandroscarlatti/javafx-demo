package com.scarlatti.swingutils.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Alessandro Scarlatti
 * @since Monday, 1/7/2019
 */
public class SmokeTests {

    @Test
    public void smokeTest() {
        System.out.println("running a smoke test");
        assertEquals(2, 2);
    }

    @Test
    public void smokeTest2() {
        System.out.println("running a smoke test");
        assertEquals(1, 2);
    }
}
