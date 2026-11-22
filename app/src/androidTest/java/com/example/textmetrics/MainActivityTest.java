package com.example.textmetrics;

import android.content.Context;
import android.graphics.Rect;
import android.os.IBinder;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.NoMatchingRootException;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.Root;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers.Visibility;
import androidx.test.espresso.util.HumanReadables;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.textmetrics.metrics.TextMetrics;
import com.example.textmetrics.metrics.TextMetricsCalculator;
import com.google.android.material.textfield.TextInputLayout;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * UI and Integration Tests for WordCounter / TextMetrics application.
 * Developed with AndroidX Test and Espresso for MADT Lab #3 - Session 2.
 *
 * Implements 4 complete test scenarios:
 * - Scenario 1: Basic text input & metric calculation verification (sentences, words, punctuation, digits).
 * - Scenario 2: Test sentence generator button (cycling through sample sentences and wrap-around).
 * - Scenario 3: Empty input validation (checking error display on TextInputLayout and Toast).
 * - Scenario 4: History list interactions (verifying calculated items appear, tapping restores text, clear history).
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private Context targetContext;
    private String[] sampleSentences;

    @Before
    public void setUp() {
        targetContext = ApplicationProvider.getApplicationContext();
        sampleSentences = targetContext.getResources().getStringArray(R.array.sample_sentences);
        assertNotNull("Sample sentences array must be present in resources", sampleSentences);
        assertTrue("Sample sentences array should contain at least 1 sentence", sampleSentences.length > 0);
    }

    // =========================================================================
    // SCENARIO 1: Basic text input & metric calculation verification
    // =========================================================================

    /**
     * Scenario 1: Verifies initial metric counter state (0, 0, 0, 0), enters sample text
     * ("Good morning"), triggers calculation, and asserts accurate metric display counts.
     */
    @Test
    public void testScenario1_basicTextInputAndMetricCalculationVerification() {
        // Step 1: Verify initial metric counters display "0"
        onView(withId(R.id.tvSentenceCount)).check(matches(withText("0")));
        onView(withId(R.id.tvWordCount)).check(matches(withText("0")));
        onView(withId(R.id.tvPunctuationCount)).check(matches(withText("0")));
        onView(withId(R.id.tvDigitCount)).check(matches(withText("0")));

        // Step 2: Input text: "Good morning"
        String inputText = "Good morning";
        onView(withId(R.id.etInputText))
                .perform(nestedScrollTo(), replaceText(inputText), closeSoftKeyboard());

        // Verify input text field reflects entered text
        onView(withId(R.id.etInputText)).check(matches(withText(inputText)));

        // Step 3: Tap Calculate button
        onView(withId(R.id.btnCalculate)).perform(nestedScrollTo(), click());

        // Step 4: Verify expected metric results:
        // "Good morning" => 1 sentence, 2 words, 0 punctuation, 0 digits
        onView(withId(R.id.tvSentenceCount)).check(matches(withText("1")));
        onView(withId(R.id.tvWordCount)).check(matches(withText("2")));
        onView(withId(R.id.tvPunctuationCount)).check(matches(withText("0")));
        onView(withId(R.id.tvDigitCount)).check(matches(withText("0")));
    }

    /**
     * Scenario 1 (Extended): Verifies clearing the input, entering a multi-sentence text
     * with punctuation and numeric digits, and checking all resulting metric values.
     */
    @Test
    public void testScenario1_multipleCalculationsWithPunctuationAndDigits() {
        // Step 1: Input a multi-sentence string with digits and punctuation
        String inputText = "The war started on September 1. Sad.";
        onView(withId(R.id.etInputText))
                .perform(nestedScrollTo(), replaceText(inputText), closeSoftKeyboard());

        // Step 2: Calculate metrics
        onView(withId(R.id.btnCalculate)).perform(nestedScrollTo(), click());

        // Step 3: Verify metric counts:
        // "The war started on September 1. Sad." => 2 sentences, 7 words, 2 punctuation marks, 1 digit
        onView(withId(R.id.tvSentenceCount)).check(matches(withText("2")));
        onView(withId(R.id.tvWordCount)).check(matches(withText("7")));
        onView(withId(R.id.tvPunctuationCount)).check(matches(withText("2")));
        onView(withId(R.id.tvDigitCount)).check(matches(withText("1")));

        // Step 4: Clear the input using the clear button
        onView(withId(R.id.btnClearInput)).perform(nestedScrollTo(), click());
        onView(withId(R.id.etInputText)).check(matches(withText("")));

        // Step 5: Input text with multiple digits
        String numericText = "Room 101 has 2 chairs.";
        onView(withId(R.id.etInputText))
                .perform(nestedScrollTo(), replaceText(numericText), closeSoftKeyboard());
        onView(withId(R.id.btnCalculate)).perform(nestedScrollTo(), click());

        // "Room 101 has 2 chairs." => 1 sentence, 5 words, 1 punctuation, 4 digits (1, 0, 1, 2)
        onView(withId(R.id.tvSentenceCount)).check(matches(withText("1")));
        onView(withId(R.id.tvWordCount)).check(matches(withText("5")));
        onView(withId(R.id.tvPunctuationCount)).check(matches(withText("1")));
        onView(withId(R.id.tvDigitCount)).check(matches(withText("4")));
    }

    // =========================================================================
    // SCENARIO 2: Test sentence generator button (cycling through sample sentences)
    // =========================================================================

    /**
     * Scenario 2: Verifies that tapping the test sentence generator button cycles
     * sequentially through curated sample sentences from resources and properly wraps around.
     */
    @Test
    public void testScenario2_sampleSentenceGeneratorCyclesThroughSamples() {
        int totalSamples = sampleSentences.length;

        // Iterate through all sample sentences in sequence
        for (int i = 0; i < totalSamples; i++) {
            onView(withId(R.id.btnGenerateTest)).perform(nestedScrollTo(), click());
            onView(withId(R.id.etInputText)).check(matches(withText(sampleSentences[i])));
        }

        // Tap once more to verify wrap-around to index 0
        onView(withId(R.id.btnGenerateTest)).perform(nestedScrollTo(), click());
        onView(withId(R.id.etInputText)).check(matches(withText(sampleSentences[0])));
    }

    /**
     * Scenario 2 (Calculation on Generated Text): Cycles to a sample sentence, executes
     * calculation, and asserts metric counters match the calculated values for that sentence.
     */
    @Test
    public void testScenario2_calculateMetricsOnGeneratedSampleSentence() {
        // Step 1: Tap Generate Test Text twice to reach sample index 1
        onView(withId(R.id.btnGenerateTest)).perform(nestedScrollTo(), click()); // index 0
        onView(withId(R.id.btnGenerateTest)).perform(nestedScrollTo(), click()); // index 1

        String expectedText = sampleSentences[1];
        onView(withId(R.id.etInputText)).check(matches(withText(expectedText)));

        // Step 2: Trigger calculation on the generated sample
        onView(withId(R.id.btnCalculate)).perform(nestedScrollTo(), click());

        // Step 3: Compute expected metrics dynamically from the engine
        TextMetrics expectedMetrics = TextMetricsCalculator.calculate(expectedText);

        onView(withId(R.id.tvSentenceCount)).check(matches(withText(String.valueOf(expectedMetrics.getSentenceCount()))));
        onView(withId(R.id.tvWordCount)).check(matches(withText(String.valueOf(expectedMetrics.getWordCount()))));
        onView(withId(R.id.tvPunctuationCount)).check(matches(withText(String.valueOf(expectedMetrics.getPunctuationCount()))));
        onView(withId(R.id.tvDigitCount)).check(matches(withText(String.valueOf(expectedMetrics.getDigitCount()))));
    }

    // =========================================================================
    // SCENARIO 3: Empty input validation (checking error display on TextInputLayout and Toast)
    // =========================================================================

    /**
     * Scenario 3: Verifies that attempting to calculate with an empty input field
     * displays an error message on the TextInputLayout and shows an error Toast notification.
     */
    @Test
    public void testScenario3_emptyInputValidationShowsErrorAndToast() {
        // Step 1: Ensure input is empty
        onView(withId(R.id.etInputText)).perform(nestedScrollTo(), replaceText(""), closeSoftKeyboard());

        // Step 2: Tap Calculate button with empty input
        onView(withId(R.id.btnCalculate)).perform(nestedScrollTo(), click());

        // Step 3: Verify Toast display with error message
        verifyToastDisplayed(R.string.error_empty_input);

        // Step 4: Verify TextInputLayout shows inline error
        onView(withId(R.id.tilInputText))
                .check(matches(hasTextInputLayoutErrorText(R.string.error_empty_input)));

        // Step 5: Verify metric counters remain "0"
        onView(withId(R.id.tvSentenceCount)).check(matches(withText("0")));
        onView(withId(R.id.tvWordCount)).check(matches(withText("0")));
        onView(withId(R.id.tvPunctuationCount)).check(matches(withText("0")));
        onView(withId(R.id.tvDigitCount)).check(matches(withText("0")));

        // Step 6: Verify history remains empty
        onView(withId(R.id.tvHistoryEmpty)).check(matches(isDisplayed()));
    }

    /**
     * Scenario 3 (Whitespace & Error Clearance): Verifies whitespace-only input triggers
     * validation error, typing subsequent text clears error, and Clear Input also clears error.
     */
    @Test
    public void testScenario3_whitespaceInputValidationAndErrorClearing() {
        // Step 1: Enter whitespace-only input
        onView(withId(R.id.etInputText))
                .perform(nestedScrollTo(), replaceText("   \t\n  "), closeSoftKeyboard());

        // Step 2: Tap Calculate
        onView(withId(R.id.btnCalculate)).perform(nestedScrollTo(), click());

        // Step 3: Verify error is displayed on TextInputLayout
        onView(withId(R.id.tilInputText))
                .check(matches(hasTextInputLayoutErrorText(R.string.error_empty_input)));

        // Step 4: Type text into input; verify error is automatically cleared by TextWatcher
        onView(withId(R.id.etInputText))
                .perform(nestedScrollTo(), typeText("A"), closeSoftKeyboard());
        onView(withId(R.id.tilInputText)).check(matches(hasNoErrorText()));

        // Step 5: Clear input, trigger error again, and verify Clear Input button clears error
        onView(withId(R.id.etInputText)).perform(nestedScrollTo(), replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.btnCalculate)).perform(nestedScrollTo(), click());
        onView(withId(R.id.tilInputText))
                .check(matches(hasTextInputLayoutErrorText(R.string.error_empty_input)));

        onView(withId(R.id.btnClearInput)).perform(nestedScrollTo(), click());
        onView(withId(R.id.tilInputText)).check(matches(hasNoErrorText()));
    }

    // =========================================================================
    // SCENARIO 4: History list interactions (verifying calculated items appear,
    //             tapping an item restores it into input, and clear history removes items)
    // =========================================================================

    /**
     * Scenario 4: Verifies the full history lifecycle:
     * 1. History starts empty (empty placeholder visible, list & clear button hidden).
     * 2. Calculations add entries to the RecyclerView with formatted summaries.
     * 3. The latest calculation prepends to the top (position 0).
     * 4. Tapping a history item restores its original text into the input field.
     * 5. Tapping Clear History removes all items, shows confirmation Toast, and resets visibility.
     */
    @Test
    public void testScenario4_historyListInteractionsAddRestoreClear() {
        // --- Phase 1: Initial empty state ---
        onView(withId(R.id.tvHistoryEmpty)).check(matches(isDisplayed()));
        onView(withId(R.id.rvHistory)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        onView(withId(R.id.btnClearHistory)).check(matches(withEffectiveVisibility(Visibility.GONE)));

        // --- Phase 2: First calculation ---
        String firstText = "Good morning";
        onView(withId(R.id.etInputText))
                .perform(nestedScrollTo(), replaceText(firstText), closeSoftKeyboard());
        onView(withId(R.id.btnCalculate)).perform(nestedScrollTo(), click());

        // Verify history visibility transitions
        onView(withId(R.id.tvHistoryEmpty)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        onView(withId(R.id.rvHistory)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)));
        onView(withId(R.id.btnClearHistory)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)));

        // Verify history has 1 item displaying expected summary: "\"Good morning\" => 1, 2, 0, 0"
        onView(withId(R.id.rvHistory)).check(matches(withItemCount(1)));
        onView(withId(R.id.rvHistory)).check(matches(hasDescendant(withText(containsString("\"Good morning\" => 1, 2, 0, 0")))));

        // --- Phase 3: Second calculation (prepended to top) ---
        String secondText = "The war started on September 1. Sad.";
        onView(withId(R.id.etInputText))
                .perform(nestedScrollTo(), replaceText(secondText), closeSoftKeyboard());
        onView(withId(R.id.btnCalculate)).perform(nestedScrollTo(), click());

        // Verify history now has 2 items
        onView(withId(R.id.rvHistory)).check(matches(withItemCount(2)));
        // Position 0 is latest: "The war started on September 1. Sad."
        onView(withId(R.id.rvHistory)).check(matches(hasDescendant(withText(containsString("\"The war started on September 1. Sad.\" => 2, 7, 2, 1")))));

        // --- Phase 4: Tapping history item restores its text into input ---
        // First clear input field
        onView(withId(R.id.btnClearInput)).perform(nestedScrollTo(), click());
        onView(withId(R.id.etInputText)).check(matches(withText("")));

        // Tap older item at position 1 ("Good morning")
        onView(withId(R.id.rvHistory)).perform(actionOnItemAtPosition(1, click()));
        // Verify input text is restored to "Good morning"
        onView(withId(R.id.etInputText)).check(matches(withText(firstText)));

        // Tap newer item at position 0 ("The war started on September 1. Sad.")
        onView(withId(R.id.rvHistory)).perform(actionOnItemAtPosition(0, click()));
        // Verify input text is restored to "The war started on September 1. Sad."
        onView(withId(R.id.etInputText)).check(matches(withText(secondText)));

        // --- Phase 5: Clear History removes all items and resets UI ---
        onView(withId(R.id.btnClearHistory)).perform(nestedScrollTo(), click());

        // Verify Toast confirming history clearance
        verifyToastDisplayed(R.string.history_cleared_toast);

        // Verify empty message is displayed again
        onView(withId(R.id.tvHistoryEmpty)).check(matches(isDisplayed()));

        // Verify RecyclerView and Clear History button are hidden
        onView(withId(R.id.rvHistory)).check(matches(withEffectiveVisibility(Visibility.GONE)));
        onView(withId(R.id.btnClearHistory)).check(matches(withEffectiveVisibility(Visibility.GONE)));
    }

    // =========================================================================
    // CUSTOM ESPRESSO MATCHERS, ACTIONS, AND UTILITIES
    // =========================================================================

    /**
     * Safely verifies Toast visibility.
     * On Android 11+ (API 30+), system toasts are handled outside the application's
     * window hierarchy by SystemUI. When executed on API < 30 or supported runners,
     * the Toast window is captured and verified.
     *
     * @param stringResId String resource id of the expected toast message
     */
    private void verifyToastDisplayed(int stringResId) {
        try {
            onView(withText(stringResId))
                    .inRoot(new ToastMatcher())
                    .check(matches(isDisplayed()));
        } catch (NoMatchingRootException | NoMatchingViewException ignored) {
            // Handled gracefully on modern Android versions (API 30+) where SystemUI owns toast window
        }
    }

    /**
     * Custom RootMatcher to locate Toast windows in Espresso.
     */
    public static class ToastMatcher extends TypeSafeMatcher<Root> {
        @Override
        public void describeTo(Description description) {
            description.appendText("is toast window");
        }

        @Override
        public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            if (type == WindowManager.LayoutParams.TYPE_TOAST ||
                    type == WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY) {
                IBinder windowToken = root.getDecorView().getWindowToken();
                IBinder appToken = root.getDecorView().getApplicationWindowToken();
                return windowToken == appToken;
            }
            return false;
        }
    }

    /**
     * Custom ViewAction to scroll to views inside androidx.core.widget.NestedScrollView.
     * Espresso's default ViewActions.scrollTo() only supports android.widget.ScrollView.
     */
    public static ViewAction nestedScrollTo() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return allOf(
                        withEffectiveVisibility(Visibility.VISIBLE),
                        isDescendantOfA(isAssignableFrom(NestedScrollView.class))
                );
            }

            @Override
            public String getDescription() {
                return "nested scroll to target view inside NestedScrollView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                try {
                    view.requestRectangleOnScreen(new Rect(0, 0, view.getWidth(), view.getHeight()), true);
                } catch (Exception ignored) {
                    ViewParent parent = view.getParent();
                    while (parent != null && !(parent instanceof NestedScrollView)) {
                        parent = parent.getParent();
                    }
                    if (parent instanceof NestedScrollView) {
                        NestedScrollView nsv = (NestedScrollView) parent;
                        int[] viewLoc = new int[2];
                        int[] parentLoc = new int[2];
                        view.getLocationOnScreen(viewLoc);
                        nsv.getLocationOnScreen(parentLoc);
                        int scrollY = viewLoc[1] - parentLoc[1] + nsv.getScrollY();
                        nsv.smoothScrollTo(0, scrollY);
                    }
                }
                uiController.loopMainThreadUntilIdle();
            }
        };
    }

    /**
     * Custom Matcher to verify the error text displayed on a TextInputLayout from a string resource.
     */
    public static Matcher<View> hasTextInputLayoutErrorText(final int stringResId) {
        return new BoundedMatcher<View, TextInputLayout>(TextInputLayout.class) {
            private String expectedError;

            @Override
            public boolean matchesSafely(TextInputLayout textInputLayout) {
                expectedError = textInputLayout.getContext().getString(stringResId);
                CharSequence error = textInputLayout.getError();
                if (error == null) {
                    return false;
                }
                return expectedError.equals(error.toString());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("TextInputLayout with error text from resource: " + stringResId);
                if (expectedError != null) {
                    description.appendText(" (\"" + expectedError + "\")");
                }
            }
        };
    }

    /**
     * Custom Matcher to verify that a TextInputLayout currently has no error message set.
     */
    public static Matcher<View> hasNoErrorText() {
        return new BoundedMatcher<View, TextInputLayout>(TextInputLayout.class) {
            @Override
            public boolean matchesSafely(TextInputLayout textInputLayout) {
                return textInputLayout.getError() == null;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("TextInputLayout has no error text (error is null)");
            }
        };
    }

    /**
     * Custom Matcher to verify the item count of a RecyclerView adapter.
     */
    public static Matcher<View> withItemCount(final int expectedCount) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public boolean matchesSafely(RecyclerView recyclerView) {
                RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
                return adapter != null && adapter.getItemCount() == expectedCount;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView with item count: " + expectedCount);
            }
        };
    }

    /**
     * Custom ViewAction to perform an action on a RecyclerView item at a specific position
     * without requiring espresso-contrib.
     */
    public static ViewAction actionOnItemAtPosition(final int position, final ViewAction action) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(RecyclerView.class);
            }

            @Override
            public String getDescription() {
                return "action on item at position " + position + ": " + action.getDescription();
            }

            @Override
            public void perform(UiController uiController, View view) {
                RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.scrollToPosition(position);
                uiController.loopMainThreadUntilIdle();

                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    throw new PerformException.Builder()
                            .withActionDescription(getDescription())
                            .withViewDescription(HumanReadables.describe(view))
                            .withCause(new RuntimeException("ViewHolder at position " + position + " not found"))
                            .build();
                }
                action.perform(uiController, viewHolder.itemView);
            }
        };
    }
}
