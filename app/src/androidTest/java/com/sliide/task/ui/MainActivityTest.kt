package com.sliide.task.ui

import android.os.SystemClock
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sliide.task.R
import com.sliide.task.units.recyclerItemAtPosition
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.rule.ActivityTestRule
import com.sliide.task.units.RecyclerViewMatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.net.HttpURLConnection
import javax.inject.Inject
import javax.inject.Singleton


@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule val hiltRule = HiltAndroidRule(this)

    protected lateinit var mockWebServer: MockWebServer

    @Rule
    @JvmField
    var mMainActivityResult = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start(8080)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun test_all_success() {
        mockNetworkResponse("list", HttpURLConnection.HTTP_OK)

        mMainActivityResult.launchActivity(null)

        //waiting loading
        SystemClock.sleep(2000)

        //Check if item at 0th position is having 0th element in json
        Espresso.onView(withId(R.id.userList))
            .check(
                ViewAssertions.matches(
                    recyclerItemAtPosition(
                        0,
                        ViewMatchers.hasDescendant(ViewMatchers.withText("a@a6.com"))
                    )
                )
            )

        //test with "add"
        mockNetworkResponse("add-success", HttpURLConnection.HTTP_OK)
        //click (+)
        Espresso.onView(withId(R.id.fabAdd)).perform(click())

        Espresso.onView(withHint(R.string.hint_name) ).perform(replaceText("this is name"))
        Espresso.onView(withHint(R.string.hint_email) ).perform(replaceText("email3@email.com"))

        Espresso.onView(withId(android.R.id.button1)).perform(click())

        Espresso.onView(withId(R.id.userList))
            .check(
                ViewAssertions.matches(
                    recyclerItemAtPosition(
                        0,
                        ViewMatchers.hasDescendant(ViewMatchers.withText("email3@email.com"))
                    )
                )
            )

        //delete
        SystemClock.sleep(1000)
        mockNetworkResponse("delete-success", HttpURLConnection.HTTP_OK)
        Espresso.onView(
            RecyclerViewMatcher(R.id.userList)
                .atPositionOnView(0, R.id.sectionItemContainer)
        ).perform(longClick())


        //wait 1 sec to launch the dialog
        SystemClock.sleep(1000)
        Espresso.onView(withId(android.R.id.button1)).perform(click())

        SystemClock.sleep(1000)
    }

    @Test
    fun test_list_and_add_server_fail() {
        mockNetworkResponse("list", HttpURLConnection.HTTP_NOT_FOUND)

        mMainActivityResult.launchActivity(null)

        //error dilaog with error
        SystemClock.sleep(1000)
        Espresso.onView(withId(android.R.id.button1)).perform(click())
        SystemClock.sleep(1000)

        //click (+)
        mockNetworkResponse("add-success", HttpURLConnection.HTTP_NOT_FOUND)
        Espresso.onView(withId(R.id.fabAdd)).perform(click())
        Espresso.onView(withHint(R.string.hint_name) ).perform(replaceText("this is name"))
        Espresso.onView(withHint(R.string.hint_email) ).perform(replaceText("email3@email.com"))
        Espresso.onView(withId(android.R.id.button1)).perform(click())
        SystemClock.sleep(1000)
    }

    @Test
    fun test_error_from_server() {
        mockNetworkResponse("list", HttpURLConnection.HTTP_OK)
        mMainActivityResult.launchActivity(null)

        SystemClock.sleep(2000)

        //click (+)
        mockNetworkResponse("add-fail", HttpURLConnection.HTTP_OK)
        Espresso.onView(withId(R.id.fabAdd)).perform(click())
        //input email dialog
        Espresso.onView(withHint(R.string.hint_name) ).perform(replaceText("this is name"))
        Espresso.onView(withHint(R.string.hint_email) ).perform(replaceText("email3@email.com"))
        Espresso.onView(withId(android.R.id.button1)).perform(click())

        SystemClock.sleep(1000)
        //error dialog
        Espresso.onView(withId(android.R.id.button1)).perform(click())
        SystemClock.sleep(1000)

        //cancel button of input email dialog
        Espresso.onView(withId(android.R.id.button2)).perform(click())

        //test delete fail case from server
        mockNetworkResponse("delete-fail", HttpURLConnection.HTTP_OK)
        Espresso.onView(
            RecyclerViewMatcher(R.id.userList)
                .atPositionOnView(1, R.id.sectionItemContainer)
        ).perform(longClick())

        //are you sure dialog
        SystemClock.sleep(1000)
        Espresso.onView(withId(android.R.id.button1)).perform(click())

        //some error from server
        SystemClock.sleep(2000)
        Espresso.onView(withId(android.R.id.button1)).perform(click())


        SystemClock.sleep(3000)
    }



    @Test
    fun test_error_from_server_401_add() {
        mockNetworkResponse("list", HttpURLConnection.HTTP_OK)
        mMainActivityResult.launchActivity(null)
        SystemClock.sleep(2000)

        //click (+)
        mockNetworkResponse("error-401", HttpURLConnection.HTTP_OK)
        Espresso.onView(withId(R.id.fabAdd)).perform(click())
        //input email dialog
        Espresso.onView(withHint(R.string.hint_name) ).perform(replaceText("this is name"))
        Espresso.onView(withHint(R.string.hint_email) ).perform(replaceText("email3@email.com"))
        Espresso.onView(withId(android.R.id.button1)).perform(click())
        SystemClock.sleep(2000)
    }

    @Test
    fun test_error_from_server_401_delete() {
        mockNetworkResponse("list", HttpURLConnection.HTTP_OK)
        mMainActivityResult.launchActivity(null)
        SystemClock.sleep(2000)

        //test delete fail case from server
        mockNetworkResponse("error-401", HttpURLConnection.HTTP_OK)
        Espresso.onView(
            RecyclerViewMatcher(R.id.userList)
                .atPositionOnView(1, R.id.sectionItemContainer)
        ).perform(longClick())

        //are you sure dialog
        SystemClock.sleep(1000)
        Espresso.onView(withId(android.R.id.button1)).perform(click())

        //some error from server
        SystemClock.sleep(2000)
        Espresso.onView(withId(android.R.id.button1)).perform(click())


        SystemClock.sleep(3000)
    }

    @Test
    fun test_error_from_server_401_list() {
        mockNetworkResponse("error-401", HttpURLConnection.HTTP_OK)
        mMainActivityResult.launchActivity(null)
        SystemClock.sleep(2000)
        //error dialog of 401
        Espresso.onView(withId(android.R.id.button1)).perform(click())


    }

    fun mockNetworkResponse(path: String, responseCode: Int) = mockWebServer.enqueue(
        MockResponse()
            .setResponseCode(responseCode)
            .setBody(getJson(path))
    )

    fun getJson(path: String):String{
        if ("list".equals(path))
            return "{\"code\":200,\"meta\":{\"pagination\":{\"total\":3264,\"pages\":164,\"page\":1,\"limit\":20}},\"data\":[{\"id\":4234,\"name\":\"a\",\"email\":\"a@a6.com\",\"gender\":\"male\",\"status\":\"active\"},{\"id\":4233,\"name\":\"a\",\"email\":\"a@a4.com\",\"gender\":\"male\",\"status\":\"active\"},{\"id\":4226,\"name\":\"a\",\"email\":\"a@a55.com\",\"gender\":\"male\",\"status\":\"active\"},{\"id\":4225,\"name\":\"gh\",\"email\":\"a@a123.com\",\"gender\":\"male\",\"status\":\"active\"},{\"id\":4222,\"name\":\"b\",\"email\":\"b@g.com\",\"gender\":\"male\",\"status\":\"active\"},{\"id\":4221,\"name\":\"a\",\"email\":\"a@a24.com\",\"gender\":\"male\",\"status\":\"active\"},{\"id\":4220,\"name\":\"hh\",\"email\":\"nn@jj.com\",\"gender\":\"male\",\"status\":\"active\"},{\"id\":4219,\"name\":\"a\",\"email\":\"a@a12.com\",\"gender\":\"male\",\"status\":\"active\"},{\"id\":4216,\"name\":\"1\",\"email\":\"q@q.com\",\"gender\":\"male\",\"status\":\"active\"},{\"id\":4215,\"name\":\"a\",\"email\":\"a@1a.com\",\"gender\":\"male\",\"status\":\"active\"},{\"id\":4212,\"name\":\"a\",\"email\":\"a@a2.com\",\"gender\":\"male\",\"status\":\"active\"},{\"id\":4210,\"name\":\"aa\",\"email\":\"a@a1.com\",\"gender\":\"male\",\"status\":\"active\"},{\"id\":4209,\"name\":\"a\",\"email\":\"a1@a.com\",\"gender\":\"male\",\"status\":\"active\"},{\"id\":4203,\"name\":\"a\",\"email\":\"b@a.com\",\"gender\":\"male\",\"status\":\"active\"},{\"id\":4200,\"name\":\"a\",\"email\":\"a@a.com\",\"gender\":\"male\",\"status\":\"active\"},{\"id\":3613,\"name\":\"hihihi\",\"email\":\"hihi@hih123i.com\",\"gender\":\"male\",\"status\":\"active\"},{\"id\":3568,\"name\":\"hihihi\",\"email\":\"hihi@hihi.com\",\"gender\":\"male\",\"status\":\"active\"},{\"id\":3549,\"name\":\"hihihi\",\"email\":\"hihi@hih89999i.com\",\"gender\":\"male\",\"status\":\"active\"},{\"id\":3541,\"name\":\"hihihi\",\"email\":\"hihi@hih44i.com\",\"gender\":\"male\",\"status\":\"active\"},{\"id\":3533,\"name\":\"hihihi\",\"email\":\"hihi@hihi333.com\",\"gender\":\"male\",\"status\":\"active\"}]}"
        else if ("add-success".equals(path))
            return "{\n" +
                    "  \"code\": 201,\n" +
                    "  \"meta\": null,\n" +
                    "  \"data\": {\n" +
                    "    \"id\": 4380,\n" +
                    "    \"name\": \"this is name\",\n" +
                    "    \"email\": \"email3@email.com\",\n" +
                    "    \"gender\": \"male\",\n" +
                    "    \"status\": \"active\"\n" +
                    "  }\n" +
                    "}"
        else if ("add-fail".equals(path))
            return "{\n" +
                    "  \"code\": 422,\n" +
                    "  \"meta\": null,\n" +
                    "  \"data\": [{\n" +
                    "    \"field\": \"email\",\n" +
                    "    \"message\": \"has already been taken\"\n" +
                    "  }]\n" +
                    "}"
        else if ("delete-success".equals(path))
            return "{\n" +
                    "  \"code\": 204,\n" +
                    "  \"meta\": null,\n" +
                    "  \"data\": null \n" +
                    "}"
        else if ("delete-fail".equals(path))
            return "{\n" +
                    "  \"code\": 404,\n" +
                    "  \"meta\": null,\n" +
                    "  \"data\": {\n" +
                    "    \"message\": \"something wrong\"\n" +
                    "  }\n" +
                    "}"
        else if ("error-401".equals(path))
            return "{\n" +
                    "  \"code\": 401,\n" +
                    "  \"meta\": null\n" +
                    "}"
        return ""
    }
}