package pt.iscte.daam.bookcase;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import pt.iscte.daam.bookcase.bo.BookCaseDbHelper;
import pt.iscte.daam.bookcase.bo.GRBook;
import pt.iscte.daam.bookcase.bo.UserProfile;
import pt.iscte.daam.bookcase.goodreads.xml.parsers.BookItemAdapter;

public class BookCaseMainActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private Menu menu;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_case_main);

        // (new BookCaseDbHelper(getApplicationContext())).insertMockBooks();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchBooks.class);
                startActivity(intent);
                return;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ((ViewPager) findViewById(R.id.container)).setAdapter(mSectionsPagerAdapter);

        this.setProfileIcon();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ((ViewPager) findViewById(R.id.container)).setAdapter(mSectionsPagerAdapter);

        this.setProfileIcon();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        this.setProfileIcon();
        return true;
    }

    private void setProfileIcon() {
        if (this.menu == null)
            return;

        try {
            UserProfile profile = UserProfile.getProfile(getApplicationContext());
            Bitmap userPhoto = null;

            if (profile != null)
                userPhoto = profile.getPicture(getApplicationContext());

            if (profile != null && userPhoto != null) {
                BitmapDrawable dra = new BitmapDrawable(getApplicationContext().getResources(), userPhoto);
                this.menu.findItem(R.id.profile_menu).setIcon(dra);
            } else {
                this.menu.findItem(R.id.profile_menu).setIcon(R.mipmap.user_profile_icon);
            }
        } catch (Exception e) {
            Log.e("MAINACTIVITY", "Error setting profile picture. Error:" + e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_case_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.profile_menu) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private ArrayList<GRBook> books = null;
        private ListView listView;
        private View rootView;
        private BookItemAdapter adapter;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        @Override
        public void onResume() {
            super.onResume();
            adapter = getBookItemAdapter();
            listView.setAdapter(adapter);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_book_case_main, container, false);
            listView = (ListView) rootView.findViewById(R.id.listView);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GRBook book = adapter.getItem(position);

                    Intent intent = new Intent(view.getContext(), SelectedBookDetailsActivity.class);

                    Bundle b = new Bundle();
                    b.putString("bookApplicationId", book.getApplicationID());

                    intent.putExtras(b);

                    view.getContext().startActivity(intent);
                }
            });

            return rootView;
        }

        @NonNull
        private BookItemAdapter getBookItemAdapter() {
            BookCaseDbHelper bd = new BookCaseDbHelper(getContext());

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    books = bd.getAvailableBooks();
                    break;
                case 2:
                    books = bd.getLentBooks();
                    break;
                default:
                    books = bd.getBooks();
                    break;
            }

            return new BookItemAdapter(getContext(), books);
        }

        @Override
        public void onSaveInstanceState(Bundle savedInstanceState) {
            savedInstanceState.putInt(ARG_SECTION_NUMBER, getArguments().getInt(ARG_SECTION_NUMBER));
            super.onSaveInstanceState(savedInstanceState);
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            adapter = getBookItemAdapter();
            listView.setAdapter(adapter);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.all_books);
                case 1:
                    return getResources().getString(R.string.available_books);
                case 2:
                    return getResources().getString(R.string.lent_books);
            }
            return null;
        }
    }
}
