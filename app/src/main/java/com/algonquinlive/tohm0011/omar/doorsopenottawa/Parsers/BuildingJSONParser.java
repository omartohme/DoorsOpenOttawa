package com.algonquinlive.tohm0011.omar.doorsopenottawa.Parsers;

/**
 *  Purpose/Description of clas
 *  @tohm0011 Omar Tohme (tohm0011@algonquinlive.com)
 */

import com.algonquinlive.tohm0011.omar.doorsopenottawa.model.Building;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BuildingJSONParser {

    public static List<Building> parseFeed(String content) {

        try {
            JSONObject jsonResponse = new JSONObject(content);
            JSONArray buildingArray = jsonResponse.getJSONArray("buildings");
            List<Building> buildingList = new ArrayList<>();

            for (int i = 0; i < buildingArray.length(); i++) {

                JSONObject obj = buildingArray.getJSONObject(i);
                Building building = new Building();

                building.setBuildingId(obj.getInt("buildingId"));
                building.setName(obj.getString("name"));

                building.setImage(obj.getString("image"));
                building.setAddress(obj.getString("address"));
                building.setDescription(obj.getString("description"));


                JSONArray open_hours = obj.getJSONArray("open_hours");
                for(int j = 0; j < open_hours.length();j++){
//                    building.getDate(open_hours.getJSONObject(j).getString("date"));
                }

                buildingList.add(building);
            }

            return buildingList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}