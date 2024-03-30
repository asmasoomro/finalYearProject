package com.example.fypapp;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.fypapp.R;
import com.example.fypapp.ActivitiesWorked;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ActivitiesWorkedTest {

    @Rule
    public ActivityScenarioRule<ActivitiesWorked> activityRule = new ActivityScenarioRule<>(ActivitiesWorked.class);

    @Test
    public void testActivitiesWorkedUI() {
        // Wait for the activity to be launched and displayed
        ActivityScenario<ActivitiesWorked> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            // Check if the list view is displayed
            Espresso.onView(ViewMatchers.withId(R.id.listViewActivities))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            // Optionally, you can also check specific items in the list view if needed
            // For example, check if a specific item with text "Activity Title" is displayed
            Espresso.onView(ViewMatchers.withText("Activity Title"))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        });
    }
}
