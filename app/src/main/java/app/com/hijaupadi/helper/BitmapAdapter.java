package app.com.hijaupadi.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import app.com.hijaupadi.R;

/**
 * Created by CV. GLOBAL SOLUSINDO on 2/20/2018.
 */

public class BitmapAdapter extends BaseAdapter {
    Context context;
    Bitmap[] imageId;

    private static LayoutInflater inflater = null;

    public BitmapAdapter(Context mainActivity,  Bitmap[] prgmImages) {
        // TODO Auto-generated constructor stub

        context = mainActivity;
        imageId = prgmImages;
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
        BitmapAdapter.Holder holder = new BitmapAdapter.Holder();
        //final View rowView;
        convertView = inflater.inflate(R.layout.activity_gridview, null);
        holder.img = (ImageView) convertView.findViewById(R.id.capture);
        //Log.wtf("image",imageId[position]);
        //Bitmap bmp = BitmapFactory.decodeFile(imageId[position]);
        holder.img.setImageBitmap(imageId[position]);
        return convertView;
    }
}
