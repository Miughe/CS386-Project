package musicplayer.cs371m.musicplayer

import android.graphics.Color
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers.anyOf
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    // Here is a test to get you started
    @Test
    fun firstSongDarkStar() {
        Thread.sleep(500)

        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.moveToState(Lifecycle.State.STARTED)
        scenario.moveToState(Lifecycle.State.RESUMED)

        onView(withId(R.id.playerCurrentSongText))
            .check(matches(withText("Dark Star")))

        scenario.moveToState(Lifecycle.State.DESTROYED)
        scenario.close()
    }
}


