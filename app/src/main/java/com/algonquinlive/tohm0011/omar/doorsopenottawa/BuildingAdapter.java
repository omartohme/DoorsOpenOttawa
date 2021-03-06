package com.algonquinlive.tohm0011.omar.doorsopenottawa;
/**
 *  Purpose/Description of clas
 *  @tohm0011 Omar Tohme (tohm0011@algonquinlive.com)
 */
import java.util.ArrayList;
import java.util.List;

import java.io.InputStream;
import java.net.URL;

import android.util.LruCache;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.algonquinlive.tohm0011.omar.doorsopenottawa.model.Building;


public class BuildingAdapter extends ArrayAdapter<Building> implements Filterable {

    private Context context;
    private List<Building> buildingList;


    private LruCache<Integer, Bitmap> imageCache;

    public BuildingAdapter(Context context, int resource, List<Building> objects) {
        super(context, resource, objects);
        this.context = context;
        this.buildingList = objects;

        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() /1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_building, parent, false);


        Building building = buildingList.get(position);
        TextView tv = (TextView) view.findViewById(R.id.textView1);
        tv.setText(building.getName());

        TextView addressShow = (TextView) view.findViewById(R.id.textView2);
        addressShow.setText(building.getAddress());


        Bitmap bitmap = imageCache.get(building.getBuildingId());
        if (bitmap != null) {
            Log.i( "BUILDINGS", building.getName() + "\tbitmap in cache");
            ImageView image = (ImageView) view.findViewById(R.id.imageView1);
            image.setImageBitmap(building.getBitmap());
        }
        else {
            Log.i( "BUILDINGS", building.getName() + "\tfetching bitmap using AsyncTask");
            BuildingAndView container = new BuildingAndView();
            container.building = building;
            container.view = view;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }


        return view;
    }


private class BuildingAndView {
    public Building building;
    public View view;
    public Bitmap bitmap;
}

private class ImageLoader extends AsyncTask<BuildingAndView, Void, BuildingAndView> {

    @Override
    protected BuildingAndView doInBackground(BuildingAndView... params) {

        BuildingAndView container = params[0];
        Building building = container.building;

        try {
            String imageUrl = MainActivity.IMAGES_BASE_URL + building.getImage();
            InputStream in = (InputStream) new URL(imageUrl).getContent();
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            building.setBitmap(bitmap);
            in.close();
            container.bitmap = bitmap;
            return container;
        } catch (Exception e) {
            System.err.println("IMAGE: " + building.getName() );
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(BuildingAndView result) {
        ImageView image = (ImageView) result.view.findViewById(R.id.imageView1);
        image.setImageBitmap(result.bitmap);
//            result.building.setBitmap(result.bitmap);
        imageCache.put(result.building.getBuildingId(), result.bitmap);
    }
}
    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                buildingList = (List<Building>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<Building> FilteredArrayNames = new ArrayList<>();

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < buildingList.size(); i++) {
                    String dataNames = buildingList.get(i).getName();
                    if (dataNames.toLowerCase().startsWith(constraint.toString()))  {
                        FilteredArrayNames.add(buildingList.get(i));
                    }
                }

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;
                Log.e("VALUES", results.values.toString());

                return results;
            }
        };

        return filter;
    }
}