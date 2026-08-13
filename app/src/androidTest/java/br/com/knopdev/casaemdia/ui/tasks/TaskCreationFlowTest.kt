package br.com.knopdev.casaemdia.ui.tasks

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import br.com.knopdev.casaemdia.CasaEmDiaApplication
import br.com.knopdev.casaemdia.MainActivity
import br.com.knopdev.casaemdia.R
import br.com.knopdev.casaemdia.data.repository.NoOpTaskOperationTracker
import br.com.knopdev.casaemdia.testing.EspressoTaskOperationTracker
import org.junit.After
import org.junit.Before
import org.junit.Test

class TaskCreationFlowTest {

    private lateinit var application: CasaEmDiaApplication

    @Before
    fun registerIdlingResource() {
        application = InstrumentationRegistry.getInstrumentation()
            .targetContext
            .applicationContext as CasaEmDiaApplication
        application.taskOperationTracker = EspressoTaskOperationTracker

        IdlingRegistry.getInstance().register(
            EspressoTaskOperationTracker.idlingResource
        )
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(
            EspressoTaskOperationTracker.idlingResource
        )
        application.taskOperationTracker = NoOpTaskOperationTracker
    }

    @Test
    fun savingTask_displaysItInTaskList() {
        val title = "Tarefa Espresso ${System.currentTimeMillis()}"

        ActivityScenario.launch(MainActivity::class.java).use {

            onView(withId(R.id.add_task_button))
                .perform(click())

            onView(withId(R.id.task_title_input))
                .perform(typeText(title))

            onView(withId(R.id.task_title_input))
                .perform(closeSoftKeyboard())

            onView(withId(R.id.save_task_button))
                .perform(click())

            onView(withText(title))
                .check(matches(isDisplayed()))
        }
    }
}
