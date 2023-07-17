package tw.com.staninfo.easyinventory;


import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.Random;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

  @Rule
  public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(
      LoginActivity.class);

  @Test
  public void loginActivityTest() {

    ViewInteraction appCompatButton5 = onView(
        allOf(withId(R.id.btnLogout), withText("登出"),
            childAtPosition(
                childAtPosition(
                    withId(android.R.id.content),
                    0),
                0),
            isDisplayed()));

    appCompatButton5.perform(click());

    ViewInteraction appCompatAutoCompleteTextView = onView(
        allOf(withId(R.id.username),
            childAtPosition(
                childAtPosition(
                    withClassName(is("android.support.design.widget.TextInputLayout")),
                    0),
                0)));
    appCompatAutoCompleteTextView.perform(scrollTo(), click());

    ViewInteraction appCompatAutoCompleteTextView5 = onView(
        allOf(withId(R.id.username), withText(""),
            childAtPosition(
                childAtPosition(
                    withClassName(is("android.support.design.widget.TextInputLayout")),
                    0),
                0)));
    appCompatAutoCompleteTextView5.perform(scrollTo(), replaceText("JEFF"));

    ViewInteraction appCompatAutoCompleteTextView6 = onView(
        allOf(withId(R.id.username), withText("JEFF"),
            childAtPosition(
                childAtPosition(
                    withClassName(is("android.support.design.widget.TextInputLayout")),
                    0),
                0),
            isDisplayed()));
    appCompatAutoCompleteTextView6.perform(closeSoftKeyboard());

    ViewInteraction appCompatEditText = onView(
        allOf(withId(R.id.password),
            childAtPosition(
                childAtPosition(
                    withClassName(is("android.support.design.widget.TextInputLayout")),
                    0),
                0)));
    appCompatEditText.perform(scrollTo(), replaceText("JEFF"), closeSoftKeyboard());

    ViewInteraction appCompatButton = onView(
        allOf(withId(R.id.name_sign_in_button), withText("登入"),
            childAtPosition(
                allOf(withId(R.id.email_login_form),
                    childAtPosition(
                        withId(R.id.login_form),
                        0)),
                2)));
    appCompatButton.perform(scrollTo(), click());

    //TODO
    for (int i = 0;i < 100;i++){
      loopTest();
    }
    appCompatButton5.perform(click());

  }

  private void loopTest(){
    ViewInteraction appCompatButton2 = onView(
        allOf(withId(R.id.location_move_button), withText("庫位移轉"),
            childAtPosition(
                childAtPosition(
                    withId(android.R.id.content),
                    0),
                3),
            isDisplayed()));
    appCompatButton2.perform(click());

    Random random = new Random(System.currentTimeMillis());
    DataInteraction constraintLayout = onData(anything())
        .inAdapterView(allOf(withId(R.id.location_list),
            childAtPosition(
                withClassName(is("android.support.constraint.ConstraintLayout")),
                0)))
        .atPosition(random.nextInt(10));
    constraintLayout.perform(click());

    //TODO add data
    for (int i = 1; i < 5;i++){
      StringBuffer sb = new StringBuffer();
      sb.append(random.nextInt(100000000));
      addData(sb.toString());
    }

    pressBack();

    pressBack();

    ViewInteraction appCompatButton4 = onView(
        allOf(withId(R.id.data_update_button), withText("上傳"),
            childAtPosition(
                childAtPosition(
                    withId(android.R.id.content),
                    0),
                4),
            isDisplayed()));
    appCompatButton4.perform(click());

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    onView(withId(R.id.data_update_button)).check(new ViewAssertion() {
      @Override
      public void check(View view, NoMatchingViewException noViewFoundException) {
        assertEquals(false,view.isEnabled());
      }
    });
  }

  private void addData(String str){
    ViewInteraction appCompatEditText2 = onView(
        allOf(withId(R.id.editTextBarcode),
            childAtPosition(
                childAtPosition(
                    withId(android.R.id.content),
                    0),
                2),
            isDisplayed()));
    appCompatEditText2.perform(replaceText(str), closeSoftKeyboard());

    ViewInteraction appCompatButton3 = onView(
        allOf(withId(R.id.add_button), withText("增加"),
            childAtPosition(
                childAtPosition(
                    withId(android.R.id.content),
                    0),
                3),
            isDisplayed()));
    appCompatButton3.perform(click());
  }

  private static Matcher<View> childAtPosition(
      final Matcher<View> parentMatcher, final int position) {

    return new TypeSafeMatcher<View>() {
      @Override
      public void describeTo(Description description) {
        description.appendText("Child at position " + position + " in parent ");
        parentMatcher.describeTo(description);
      }

      @Override
      public boolean matchesSafely(View view) {
        ViewParent parent = view.getParent();
        return parent instanceof ViewGroup && parentMatcher.matches(parent)
            && view.equals(((ViewGroup) parent).getChildAt(position));
      }
    };
  }
}
