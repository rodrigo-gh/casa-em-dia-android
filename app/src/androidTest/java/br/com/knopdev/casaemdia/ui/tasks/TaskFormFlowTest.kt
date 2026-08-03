package br.com.knopdev.casaemdia.ui.tasks

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import br.com.knopdev.casaemdia.MainActivity
import br.com.knopdev.casaemdia.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskFormFlowTest {

    @Test
    fun clickingNewTask_opensTaskForm() {
        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.add_task_button))
            .perform(click())

        onView(withText(R.string.new_task))
            .check(matches(isDisplayed()))
    }

    @Test
    fun savingWithoutTitle_showsValidationError() {
        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.add_task_button))
            .perform(click())

        onView(withId(R.id.save_task_button))
            .perform(click())

        onView(withText(R.string.task_title_required))
            .check(matches(isDisplayed()))
    }
}