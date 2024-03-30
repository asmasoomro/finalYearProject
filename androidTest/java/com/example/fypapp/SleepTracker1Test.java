package com.example.fypapp;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.fypapp.SleepTracker1;
import com.example.fypapp.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class SleepTracker1Test {

    @Rule
    public ActivityScenarioRule<SleepTracker1> activityRule = new ActivityScenarioRule<>(SleepTracker1.class);

    @Test
    public void testSleepTrackerUI() {
        // Wait for the activity to be launched and displayed
        ActivityScenario<SleepTracker1> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            // Test dateTextView is displayed with today's date
            ViewInteraction dateTextView = Espresso.onView(withId(R.id.dateTextView));
            dateTextView.check(matches(isDisplayed()));
            dateTextView.check(matches(withText("Today's Date: yyyy-MM-dd"))); // Update the date format as needed

            // Simulate clicking on the analysis button
            ViewInteraction analysisButton = Espresso.onView(withId(R.id.ButtonAnalysis));
            analysisButton.perform(click());

            // Verify that the efficiencyTextView and qualityStatusTextView are displayed after analysis
            ViewInteraction efficiencyTextView = Espresso.onView(withId(R.id.efficiencyTextView));
            efficiencyTextView.check(matches(isDisplayed()));

            ViewInteraction qualityStatusTextView = Espresso.onView(withId(R.id.qualityStatusTextView));
            qualityStatusTextView.check(matches(isDisplayed()));
        });
    }
}
