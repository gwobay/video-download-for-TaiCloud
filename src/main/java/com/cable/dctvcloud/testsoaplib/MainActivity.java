package com.cable.dctvcloud.testsoaplib;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.OpenableColumns;
import android.support.v4.app.FragmentActivity;
import android.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.provider.MediaStore;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import android.database.Cursor;
import android.widget.Toast;


public class MainActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    boolean fromSavedInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fromSavedInstance=(savedInstanceState != null);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    private class AsyncSoapConnect extends AsyncTask<SOAPClient, Void, String>
    {
        SOAPClient mClient;
        protected String doInBackground(SOAPClient ... aClient)
        {
            mClient=aClient[0];
            mClient.sendRequest();
            return mClient.getResponse();
        }

        protected void onPostExecution(String jResponse)
        {

        }
    }

    final static String TESTKEY="e0VS5s2nxxyXxRy7y5wFfGEWwTTZYLPWig2Ul5DJc/lKFcKZDBPibA==";
    final static String USRID="C6153873-13CD-4E1D-8B9A-47374DC8393F";
    final static String TESTSN="B3A4D01A-4270-4D0F-A710-1311DECFFE4D";
    public final static int SPLIT_SIZE=200*1024;
    Uri fileUri=null;
    String mMethodName;
    int mOption;
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

        mOption=position;
        mMethodName=null;
        switch (position) {
            case 1:
                mMethodName="getDirectoryTree";
                startSoapClient();
                break;
            case 2:
                fileUri=null;
                mMethodName="beginUploadFile";
                pickFile();
                break;
            case 3:
                fileUri=null;
                mMethodName="beginDownloadFile";
                startSoapClient();
                break;
            default:
                break;

        }

        //aClient.sendRequest();
        PlaceholderFragment nFragment=PlaceholderFragment.newInstance(position + 1);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, nFragment)
                .commit();
    }

    static final int REQ_PICK_IMAGE=1;
    FileDescriptor mFd=null;
    long mFileSize;
    String mFileName=null;
    private void pickFile()
    {
        mFileSize=0;
        mFileName=null;
        Intent pickIntent = new Intent();
        pickIntent.setType("video/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra
                (
                        Intent.EXTRA_INITIAL_INTENTS,
                        new Intent[] { takePhotoIntent }
                );
        //updatingPhoto=true;
        startActivityForResult(chooserIntent, REQ_PICK_IMAGE);

    }

    private void startSoapClient()
    {
        SOAPClient aClient=null;
        aClient = new SOAPClient(mMethodName);
        String key=TESTKEY;
        String usrId=USRID;
        String phoneSN=TESTSN;
        String jsonDataString=null;
        switch (mOption) {
            case 1:
                jsonDataString=new BuildJasonFormatString().getDirectoryRequestJason(TESTKEY, USRID, TESTSN );
                aClient.setJsonDataString(jsonDataString);
                break;
            case 2:
                    String fileBase=mFileName;
                    String ext="";
                    int iDot=mFileName.indexOf('.');
                    if (iDot>0)
                    {
                        fileBase=mFileName.substring(0, iDot);
                        ext=mFileName.substring(iDot+1);
                    }
                jsonDataString=new BuildJasonFormatString().buildJsonToUpLoadRequest(key, usrId, phoneSN,
                        fileBase, mFileSize, ext, (int)(mFileSize/BuildJasonFormatString.SPLIT_SIZE)+1, "Video", "");
                    aClient.setJsonDataString(jsonDataString);

                break;
            case 3:
                //aClient.setJsonDataString(new BuildJasonFormatString().buildJsonToUpLoadRequest(key, usrId, phoneSN,
                       // fileBase, mFileSize,ext, BuildJasonFormatString.SPLIT_SIZE, "Video", ""));

                break;
            default:
                break;

        }
        String toastMsg="Sending Msg to Server :  "+jsonDataString;
        Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
        aClient.setActivity(this);
        new AsyncSoapConnect().execute(aClient);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //String photo_file_path;
        Uri photo_uri=null;
        if (data != null) photo_uri=data.getData();

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PICK_IMAGE && photo_uri != null)
        {
           // try {
                fileUri = photo_uri;
                Cursor returnCursor =
                        getContentResolver().query(fileUri, null, null, null, null);
    /*
     * Get the column indexes of the data in the Cursor,
     * move to the first row in the Cursor, get the data,
     * and display it.
     */
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                mFileName=returnCursor.getString(nameIndex);
                mFileSize=returnCursor.getLong(sizeIndex);
            String toastMsg="picked file "+mFileName+" has size : "+mFileSize;
            Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            startSoapClient();
/*
                InputStream mCurrentInput = getContentResolver().openInputStream(fileUri);
                if (mCurrentInput != null) {
                    mFileSize=mCurrentInput.available();
                    mFd = ((FileInputStream) mCurrentInput).getFD();
                    mCurrentInput.close();
                }
                */
           // }
            //catch (FileNotFoundException e){}
               // catch (IOException e){}
        }
    }

    public void onSectionAttached(int number) {
        switch (number+1) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
        }
    }

    public void restoreActionBar() {
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setTitle(mTitle);
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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
