package com.example.fypapp;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.fypapp.R;
import com.example.fypapp.WeeklyMood;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class WeeklyMoodTest {

    @Rule
    public ActivityScenarioRule<WeeklyMood> activityRule = new ActivityScenarioRule<>(WeeklyMood.class);

    @Test
    public void testWeeklyMoodUI() {
        // Wait for the activity to be launched and displayed
        ActivityScenario<WeeklyMood> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            // Check if the line chart is displayed
            Espresso.onView(ViewMatchers.withId(R.id.lineChart))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            // Check if the message TextView is displayed and has the correct text
            Espresso.onView(ViewMatchers.withId(R.id.textViewMessage))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                    .check(ViewAssertions.matches(ViewMatchers.withText("Weekly Mood Chart displayed")));
        });
    }
}
