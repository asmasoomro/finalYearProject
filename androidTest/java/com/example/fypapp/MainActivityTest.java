package com.example.fypapp;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Before
    public void setUp() {
        ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void testSignInWithValidCredentials() {
        Espresso.onView(withId(R.id.email)).perform(ViewActions.typeText("valid@email.com"));
        Espresso.onView(withId(R.id.password)).perform(ViewActions.typeText("password123"));

        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.sign_in)).perform(ViewActions.click());

        // You can add assertions here based on what should happen after a successful sign-in
        Espresso.onView(withText("Login Successful")).check(matches(isDisplayed()));
    }

    @Test
    public void testSignInWithEmptyEmail() {
        Espresso.onView(withId(R.id.password)).perform(ViewActions.typeText("password123"));

        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.sign_in)).perform(ViewActions.click());

        // Check if the "Enter Email" toast is displayed
        Espresso.onView(withText("Enter Email")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    @Test
    public void testSignInWithEmptyPassword() {
        Espresso.onView(withId(R.id.email)).perform(ViewActions.typeText("valid@email.com"));

        Espresso.closeSoftKeyboard();

        Espresso.onView(withId(R.id.sign_in)).perform(ViewActions.click());

        // Check if the "Enter Password" toast is displayed
        Espresso.onView(withText("Enter Password")).inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    // You can add more test cases as needed
}
