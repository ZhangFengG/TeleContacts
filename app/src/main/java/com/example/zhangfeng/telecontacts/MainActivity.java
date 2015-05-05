package com.example.zhangfeng.telecontacts;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhangfeng.telecontacts.bean.Contact;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * 内容提供者获取联系人信息
     * 声明变量
     */
    private static ContentResolver resolver;
    private static final String URI_CONTACTS_RAW = "content://com.android.contacts/raw_contacts";
    private static final String URI_CONTACTS_DATA="content://com.android.contacts/data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        resolver = getContentResolver();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true); //显示应用标题
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView)rootView.findViewById(R.id.section_label);
            /*
             *获取联系人对象
             */
            List<Contact> contacts = getContacats();
            Iterator<Contact> iterator = contacts.iterator();
            Contact contact = new Contact();
            if(iterator.hasNext()){
                contact = iterator.next();

            }
            textView.setText("test"+contact.getName());

            Button button = (Button)rootView.findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(rootView.getContext(),"TEST",Toast.LENGTH_SHORT).show();
                }
            });

            return rootView;
        }

        @Override
        public void onAttach(final Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));

        }
    }

    /**
     *
     */
    private static List<Contact> getContacats(){
        List<Contact> contacts = new ArrayList<Contact>();
        String id = "TEST";
        String dataSelect = " length(data1)<7 and length(data1)>5 and mimetype_id=5 ";  //联系人数据查询条件
        String nameSelect = " _id=? "; //  联系人姓名查询条件
        Uri uriRaw = Uri.parse(URI_CONTACTS_RAW);
        Uri uriData = Uri.parse(URI_CONTACTS_DATA);
        //Cursor cursor = resolver.query(uriRaw, null, "display_name='Zhangfeng'",null, null);
        Cursor cursor = resolver.query(uriData,new String[]{"raw_contact_id","data1"},dataSelect,null,null);
        //读取游标中的值
//        while(cursor.moveToNext()){
//            id = cursor.getString(cursor.getColumnIndex("contact_id"));
//            Log.i("TEST",id);
//        }
        while(cursor.moveToNext()){
            id = cursor.getString(cursor.getColumnIndex("raw_contact_id"));
            String phoneNum = cursor.getString(cursor.getColumnIndex("data1"));
            Contact contact = new Contact();
            //Log.i("TEST",id);
            contact.setId(id);
            contact.setPhoneNum(phoneNum);
            Cursor cursorForName = resolver.query(uriRaw,new String[]{"display_name"},nameSelect,new String[]{id},null);
            if(cursorForName.moveToNext()){
                contact.setName(cursorForName.getString(cursorForName.getColumnIndex("display_name")));
            }
            contacts.add(contact);
            //id = cursor.getString(cursor.getColumnIndex("contact_id"));
        }
//        Log.i("TEST",Integer.toString(cursor.getCount()));
//        Log.i("TEST",id);
        Log.i("TEST",Integer.toString(contacts.size()));
        cursor.close();
        return contacts;
    }
}
