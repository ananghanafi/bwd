package app.com.hijaupadi.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import app.com.hijaupadi.R;

public class CustomAdapter extends BaseAdapter {

    Context context;
    String[] imageId;
    Databasehelper dbcenter;

    private static LayoutInflater inflater = null;

    public CustomAdapter(Context mainActivity,  String[] prgmImages) {
        // TODO Auto-generated constructor stub

        context = mainActivity;
        imageId = prgmImages;

        dbcenter = new Databasehelper(mainActivity);
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return imageId.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {

        ImageView img;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        final View rowView;
        rowView = inflater.inflate(R.layout.item, null);
        holder.img = (ImageView) rowView.findViewById(R.id.image_wisata);
        Log.wtf("image",imageId[position]);
        Bitmap bmp = BitmapFactory.decodeFile(imageId[position]);
        holder.img.setImageBitmap(bmp);

        return rowView;
    }

}