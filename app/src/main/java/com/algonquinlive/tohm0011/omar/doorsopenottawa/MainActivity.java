package com.algonquinlive.tohm0011.omar.doorsopenottawa;

/**
 *  Purpose/Description of class
 *  @tohm0011 Omar Tohme (tohm0011@algonquinlive.com)
 */

import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.algonquinlive.tohm0011.omar.doorsopenottawa.Parsers.BuildingJSONParser;
import com.algonquinlive.tohm0011.omar.doorsopenottawa.model.Building;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends ListActivity {

    public static final String IMAGES_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";
    public static final String LOGOUT = "https://doors-open-ottawa-hurdleg.mybluemix.net/users/logout";


    private ProgressBar pb;
    private List<MyTask> tasks;

    private List<Building> buildingList;

    private AboutDialogFragment mAboutDialog;

    private static final String ABOUT_DIALOG_TAG;
    private static final String LOG_TAG;

    private SwipeRefreshLayout mySwipeRefreshLayout;

    static {
        ABOUT_DIALOG_TAG = "About Dialog";
        LOG_TAG = "Doors Open Ottawa";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();

        mAboutDialog = new AboutDialogFragment();

        requestData(REST_URI);

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Building selectedBuilding = buildingList.get(position);

                Intent intent = new Intent( getApplicationContext(), DetailsActivity.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                intent.putExtra( "name",  selectedBuilding.getName());
                intent.putExtra( "description", selectedBuilding.getDescription() );
                intent.putExtra( "address", selectedBuilding.getAddress() );
                intent.putExtra( "buildingID", selectedBuilding.getBuildingId());
                startActivity( intent );
            }
        });

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        updateDisplay();
                    }
                }
        );


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                ((BuildingAdapter) getListAdapter()).getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ((BuildingAdapter) getListAdapter()).getFilter().filter(s);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if ( item.isCheckable() ) {
            // leave if the list is null
            if ( buildingList == null ) {
                return true;
            }

            // which sort menu item did the user pick?
            switch( item.getItemId() ) {
                case R.id.action_sort_name_asc:
                    Collections.sort( buildingList, new Comparator<Building>() {
                        @Override
                        public int compare( Building lhs, Building rhs ) {
                            Log.i( "BUILDINGS", "Sorting buildings by name (a-z)" );
                            return lhs.getName().compareTo( rhs.getName() );
                        }
                    });
                    break;

                case R.id.action_sort_name_dsc:
                    Collections.sort( buildingList, Collections.reverseOrder(new Comparator<Building>() {
                        @Override
                        public int compare( Building lhs, Building rhs ) {
                            Log.i( "BUILDINGS", "Sorting buildings by name (z-a)" );
                            return lhs.getName().compareTo( rhs.getName() );
                        }
                    }));
                    break;
            }
            item.setChecked( true );
            ((ArrayAdapter)getListAdapter()).notifyDataSetChanged();
        }

        int id = item.getItemId();

        if (id == R.id.action_about) {
            DialogFragment newFragment = new AboutDialogFragment();
            newFragment.show( getFragmentManager(), ABOUT_DIALOG_TAG );
            return true;
        }

        if (id == R.id.adding) {
            Intent intent = new Intent( getApplicationContext( ), AddingActivity.class );


            startActivity( intent );
        }

        return super.onOptionsItemSelected(item);
    }


    private void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
    }

    protected void updateDisplay() {
        //Use PlanetAdapter to display data
        BuildingAdapter adapter = new BuildingAdapter(this, R.layout.item_building, buildingList);
        setListAdapter(adapter);

        mySwipeRefreshLayout.setRefreshing(false);
    }


    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Building theSelectedPlanet = buildingList.get(position);
//        Toast.makeText(this, theSelectedPlanet.getName(), Toast.LENGTH_SHORT).show();
//    }

    private class MyTask extends AsyncTask<String, String, List<Building>> {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<Building> doInBackground(String... params) {

            String content = HttpManager.getData( params[0], "tohm0011", "password" );
            buildingList = BuildingJSONParser.parseFeed(content);


            return buildingList;
        }

        @Override
        protected void onPostExecute(List<Building> result) {

            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }

            buildingList = result;
            updateDisplay();
        }
    }

    private class MyTerminate extends AsyncTask<String, String, String> {



        @Override
        protected String doInBackground(String... params) {

            String content = HttpManager.getData( params[0], "tohm0011", "password" );


            return content;
        }

        @Override
        protected void onPostExecute(String result) {

           Log.d("xD", result);
        }
    }

    public void onDestroy() {
        new MyTerminate().execute(LOGOUT);
        super.onDestroy();
    }
}