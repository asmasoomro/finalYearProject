package com.example.fypapp;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.Test;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class JournalTest {

    @Test
    public void testJournalActivity() {
        // Simulate user input in the journal EditText
        Espresso.onView(withId(R.id.journal))
                .perform(ViewActions.typeText("I feel very bad today"));

        // Click on the ButtonAnalysis button
        Espresso.onView(withId(R.id.ButtonAnalysis))
                .perform(ViewActions.click());

        // Check if the txtpositive TextView is displayed
        Espresso.onView(withId(R.id.txtpositive))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Check if the txtnegative TextView is displayed
        Espresso.onView(withId(R.id.txtnegative))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Simulate a scenario where positivePercentage > 70
        // Replace "Positive Activities" with the expected title for your PositiveActivities activity
        suggestActivities(80, 50);

        // Check if PositiveActivities screen is launched after suggesting positive activities
        Espresso.onView(ViewMatchers.withText("Positive Activities"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Simulate a scenario where negativePercentage > 70
        // Replace "Activities" with the expected title for your Activities activity
        suggestActivities(50, 80);

        // Check if Activities screen is launched after suggesting activities to improve mood
        Espresso.onView(ViewMatchers.withText("Activities"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Simulate a scenario where neither positivePercentage nor negativePercentage is > 70
        // Replace "Default Activities" with the expected title for your DefaultActivities activity
        suggestActivities(60, 60);

        // Check if DefaultActivities screen is launched after suggesting default activities
        Espresso.onView(ViewMatchers.withText("Default Activities"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    private void suggestActivities(float positivePercentage, float negativePercentage) {
        // Simulate the suggestActivities method with given percentages
        if (positivePercentage > 70) {
            suggestPositiveActivities();
        } else if (negativePercentage > 70) {
            suggestActivitiesToImproveMood();
        } else {
            suggestDefaultActivities();
        }
    }

    private void suggestPositiveActivities() {
        // Mock method for suggesting positive activities
        // For example, you can launch the PositiveActivities activity directly
    }

    private void suggestActivitiesToImproveMood() {
        // Mock method for suggesting activities to improve mood
        // For example, you can launch the Activities activity directly
    }

    private void suggestDefaultActivities() {
        // Mock method for suggesting default activities
        // For example, you can launch the DefaultActivities activity directly
    }
}
