package com.example.fypapp;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.fypapp.R;
import com.example.fypapp.Favorite;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FavoriteTest {

    @Rule
    public ActivityScenarioRule<Favorite> activityRule = new ActivityScenarioRule<>(Favorite.class);

    @Test
    public void testFavoriteUI() {
        // Wait for the activity to be launched and displayed
        ActivityScenario<Favorite> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            // Check if the progress bar is not visible
            Espresso.onView(ViewMatchers.withId(R.id.progressBar))
                    .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

            // Check if the recycler view is displayed
            Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            // Optionally, you can also check specific items in the recycler view if needed
            // For example, check if a specific item with text "Activity Title" is displayed
            Espresso.onView(ViewMatchers.withText("Activity Title"))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        });
    }
}
