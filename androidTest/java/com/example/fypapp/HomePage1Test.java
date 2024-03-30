package com.example.fypapp;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class HomePage1Test {

    @Test
    public void testHomePage1Launch() {
        ActivityScenario.launch(HomePage1.class);

        // Check if the gridLayout is displayed
        Espresso.onView(withId(R.id.gridLayout)).check(matches(isDisplayed()));

        // You can add more assertions here to check other UI elements as needed
    }

    // Add more tests as needed for specific UI elements and interactions
}
