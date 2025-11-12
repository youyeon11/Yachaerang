package com.yachaerang.backend.global.test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JavaFooTest {
    private final JavaFoo javaFoo = new JavaFoo();

    @Test
    void partiallyCoveredHelloMethodTest() {
        String actual = javaFoo.hello("펭");
        assertEquals(actual, "하");
    }
}