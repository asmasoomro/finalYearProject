package com.example.fypapp;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.fypapp.R;
import com.example.fypapp.WeeklySleep;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class WeeklySleepTest {

    @Rule
    public ActivityScenarioRule<WeeklySleep> activityRule = new ActivityScenarioRule<>(WeeklySleep.class);

    @Test
    public void testWeeklySleepUI() {
        // Wait for the activity to be launched and displayed
        ActivityScenario<WeeklySleep> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            // Check if the bar chart is displayed
            Espresso.onView(ViewMatchers.withId(R.id.barChartWeekly))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            // Check if the dateTextView is displayed and has the correct text
            Espresso.onView(ViewMatchers.withId(R.id.dateTextViewWeekly))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                    .check(ViewAssertions.matches(ViewMatchers.withText("Weekly Sleep Log")));
        });
    }
}
