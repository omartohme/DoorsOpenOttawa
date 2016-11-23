package com.algonquinlive.tohm0011.omar.doorsopenottawa;

/**
 *  Purpose/Description of class
 *  @tohm0011 Omar Tohme (tohm0011@algonquinlive.com)
 */

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algonquinlive.tohm0011.omar.doorsopenottawa.Parsers.BuildingJSONParser;
import com.algonquinlive.tohm0011.omar.doorsopenottawa.model.Building;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ListActivity {

    // URL to my RESTful API Service hosted on my Bluemix account.
    public static final String IMAGES_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";

    private ProgressBar pb;
    private List<MyTask> tasks;

    private List<Building> buildingList;

    private AboutDialogFragment mAboutDialog;

    private static final String ABOUT_DIALOG_TAG;
    private static final String LOG_TAG;

    //TODO pro-tip: class vars (i.e. static vars) can be initialized within a static block initializer.
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

        //single selection && register this ListActivity as the event handler
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
                startActivity( intent );
            }
        });
        }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_about) {
            DialogFragment newFragment = new AboutDialogFragment();
            newFragment.show( getFragmentManager(), ABOUT_DIALOG_TAG );
            return true;
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

            String content = HttpManager.getData(params[0]);
            //return content;
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
}