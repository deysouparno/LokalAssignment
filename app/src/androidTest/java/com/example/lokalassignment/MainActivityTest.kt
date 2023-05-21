package com.example.lokalassignment

import android.content.Intent
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Lifecycle
import androidx.paging.PagingData
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matcher
import org.hamcrest.Matchers.any
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val intentsTestRule = IntentsTestRule(MainActivity::class.java)

    companion object {
        val catObj = Cat(
            breeds = arrayListOf(
                Breed(
                    weight = Weight(imperial = "7 - 14", metric = "3 - 6"),
                    id = "hima",
                    name = "Himalayan",
                    cfaUrl = "http://cfa.org/Breeds/BreedsKthruR/NorwegianForestCat.aspx",
                    vetstreetUrl = "http://www.vetstreet.com/cats/himalayan",
                    vcahospitalsUrl = "https://vcahospitals.com/know-your-pet/cat-breeds/himalayan",
                    temperament = "Dependent, Gentle, Intelligent, Quiet, Social",
                    origin = "United States",
                    countryCodes = "US",
                    countryCode = "US",
                    description = "Calm and devoted, Himalayans make excellent companions, though they prefer a quieter home. They are playful in a sedate kind of way and enjoy having an assortment of toys. The Himalayan will stretch out next to you, sleep in your bed and even sit on your lap when she is in the mood.",
                    lifeSpan = "9 - 15",
                    indoor = 0,
                    lap = 1,
                    altNames = "Himalayan Persian, Colourpoint Persian, Longhaired Colourpoint, Himmy",
                    adaptability = 5,
                    affectionLevel = 5,
                    childFriendly = 2,
                    dogFriendly = 2,
                    energyLevel = 1,
                    grooming = 5,
                    healthIssues = 3,
                    intelligence = 3,
                    sheddingLevel = 4,
                    socialNeeds = 4,
                    strangerFriendly = 2,
                    vocalisation = 1,
                    experimental = 0,
                    hairless = 0,
                    natural = 0,
                    rare = 0,
                    rex = 0,
                    suppressedTail = 0,
                    shortLegs = 0,
                    wikipediaUrl = "https://en.wikipedia.org/wiki/Himalayan_(cat)",
                    hypoallergenic = 0,
                    referenceImageId = "CDhOtM-Ig",
                )
            ),
            id = "ZR9dCROV8",
            url = "https://cdn2.thecatapi.com/images/ZR9dCROV8.jpg",
            width = 774,
            height = 1024

        )
    }


    private lateinit var scenario: ActivityScenario<MainActivity>
//    private val mainViewModel: MainViewModel = mockk(relaxed = true)

    @Before
    fun setUp() {
//        startKoin {
//            modules(
//                module { viewModelFactory { mainViewModel } }
//            )
//        }
        scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.moveToState(Lifecycle.State.RESUMED)
    }


    @Test
    fun assert_UI_According_The_Loading_State() {
        var isConnectedToNetwork = true
        scenario.onActivity {
            it.refresh()
            isConnectedToNetwork = it.getNetworkType() != null
        }

        if (isConnectedToNetwork) onView(withId(R.id.pbMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        else {
            onView(withId(R.id.pbMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
            onView(withId(R.id.tvRetry)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
            onView(withId(R.id.tvError)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        }
    }

    @Test
    fun assert_UI_in_Empty_State() {
        scenario.onActivity {
            it.loadData(PagingData.empty())
        }
        onView(withId(R.id.pbMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.tvRetry)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.tvError)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.tvError)).check(matches(withText("No Data")))
    }

    @Test
    fun assert_UI_in_Success_State() {
        scenario.onActivity {
            it.loadData(PagingData.from(listOf(catObj)))
        }
        onView(withId(R.id.pbMain)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.tvRetry)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.tvError)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun test_opening_wiki_in_browser() {
        scenario.onActivity {
            it.loadData(PagingData.from(listOf(catObj)))
        }
        onView(withId(R.id.rvCats)).perform(
            RecyclerViewActions.actionOnItemAtPosition<CatViewHolder>(
                0,
                recyclerChildAction<AppCompatImageView>(R.id.ivWiki) {
                    this.performClick()
                }
            )
        )
        intended(hasAction(Intent.ACTION_VIEW))
    }


    private fun <T : View> recyclerChildAction(@IdRes id: Int, block: T.() -> Unit): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return any(View::class.java)
            }

            override fun getDescription(): String {
                return "Performing action on RecyclerView child item"
            }

            override fun perform(
                uiController: UiController,
                view: View
            ) {
                view.findViewById<T>(id).block()
            }
        }

    }
}