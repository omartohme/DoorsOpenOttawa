package com.algonquinlive.tohm0011.omar.doorsopenottawa;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.algonquinlive.tohm0011.omar.doorsopenottawa.model.Building;
import com.algonquinlive.tohm0011.omar.doorsopenottawa.utils.HttpMethod;
import com.algonquinlive.tohm0011.omar.doorsopenottawa.utils.RequestPackage;
import com.algonquinlive.tohm0011.omar.doorsopenottawa.HttpManager;




/**
 * Created by omar on 2016-12-06.
 */

public class AddingActivity  extends Activity {

    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";



    private EditText addingName;
    private EditText addingAddress;
    private EditText addingOpen;
    private EditText addingDesc;
    private Button addingButton;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adding_activity);
        addingName = (EditText) findViewById(R.id.editText);
        addingAddress = (EditText) findViewById(R.id.editText2);
        addingOpen = (EditText) findViewById(R.id.editText3);
        addingDesc = (EditText) findViewById(R.id.editText4);
        addingButton = (Button) findViewById(R.id.addButton);
        addingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                createBuilding( REST_URI);
            }
        });

        }

    private void createBuilding(String uri) {
        Building building = new Building();
        building.setBuildingId( 0 );
        building.setName( addingName.getText().toString() );
        building.setImage( "abc123.jpg");
        building.setDescription( addingDesc.getText().toString());
        building.setAddress( addingAddress.getText().toString());


        RequestPackage pkg = new RequestPackage();
        pkg.setMethod( HttpMethod.POST );
        pkg.setUri( uri );
        pkg.setParam("name", building.getName() );
        pkg.setParam("image", building.getImage() );
        pkg.setParam("description", building.getDescription() );

        DoTask postTask = new DoTask();
        postTask.execute( pkg );
    }

    private class DoTask extends AsyncTask<RequestPackage, String, String> {


        @Override
        protected String doInBackground(RequestPackage ... params) {

            String content = HttpManager.getData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String result) {


            if (result == null) {
                Toast.makeText(AddingActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

}

