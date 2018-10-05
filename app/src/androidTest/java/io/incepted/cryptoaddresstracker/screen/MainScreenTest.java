package io.incepted.cryptoaddresstracker.screen;

import androidx.test.InstrumentationRegistry;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.incepted.cryptoaddresstracker.activities.MainActivity;
import io.incepted.cryptoaddresstracker.data.source.AddressLocalDataSource;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.utils.LocalAddressRepositoryInjection;
import io.incepted.cryptoaddresstracker.utils.ViewModelFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.google.common.base.Preconditions.checkArgument;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainScreenTest {

    private final static String TITLE1 = "TITLE1";
    private final static String ADDR_VALUE = "0x645D1dea6cd0470678D39c5B7799e6e19c3Bb95f";
    private final static String TITLE2 = "TITLE2";

    @Rule
    public ActivityTestRule<MainActivity> mTasksActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void resetState() {
        ViewModelFactory.destroyInstance();
        LocalAddressRepositoryInjection.provideAddressRepository(InstrumentationRegistry.getTargetContext())
                .deleteAllAddresses(new AddressLocalDataSource.OnAllAddressDeletedListener() {
                    @Override
                    public void onAddressesDeleted() {
                        /* empty */
                    }

                    @Override
                    public void onDeletionNotAvailable() {
                        /* empty */
                    }
                });
    }

    private Matcher<View> withItemText(final String itemText) {
        checkArgument(!TextUtils.isEmpty(itemText), "itemText cannot be null or empty");
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                return allOf(
                        isDescendantOfA(isAssignableFrom(RecyclerView.class)),
                        withText(itemText)).matches(item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is isDescendantOfA LV with text " + itemText);
            }
        };
    }

    @Test
    public void clickAddAddressButton_OpensNewAddrActivity() {
        onView(withId(R.id.main_add_new_address)).perform(click());
        onView(withId(R.id.new_addr_name_edit_text)).check(matches(isDisplayed()));
    }

    @Test
    public void editAddress() throws Exception {
        createAddress(TITLE1, ADDR_VALUE);

        onView(withText(TITLE1)).perform(click());

        onView(withId(R.id.menu_detail_edit)).perform(click());

        String newTitle = TITLE2;

        onView(withId(R.id.address_name_edit))
                .perform(replaceText(newTitle), closeSoftKeyboard());

        onView(withText("DONE"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(allOf(withId(R.id.detail_appbar_content_name), withText(newTitle)))
                .check(matches(isDisplayed()));

        onView(withItemText(TITLE1)).check(doesNotExist());
    }

    @Test
    public void addAddressToList() throws Exception {
        createAddress(TITLE1, ADDR_VALUE);
        onView(withText(TITLE1)).check(matches(isDisplayed()));
    }

    @Test
    public void addMultipleAddresses() throws Exception {
        createAddress(TITLE1, ADDR_VALUE);
        createAddress(TITLE2, ADDR_VALUE);

        onView(withItemText(TITLE1)).check(matches(isDisplayed()));
        onView(withItemText(TITLE2)).check(matches(isDisplayed()));
    }

    @Test
    public void createOneAddress_deleteTas() {
        createAddress(TITLE1, ADDR_VALUE);
        onView(withText(TITLE1)).perform(click());

        onView(withId(R.id.menu_detail_delete)).perform(click());
        onView(withText("OK"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withText(TITLE1)).check(doesNotExist());
    }



    private void createAddress(String title, String addrValue) {
        onView(withId(R.id.main_add_new_address)).perform(click());
        onView(withId(R.id.new_addr_name_edit_text)).perform(typeText(title),
                closeSoftKeyboard());
        onView(withId(R.id.new_addr_addr_edit_text)).perform(typeText(addrValue),
                closeSoftKeyboard());

        onView(withId(R.id.new_addr_save_button)).perform(click());

    }

}
