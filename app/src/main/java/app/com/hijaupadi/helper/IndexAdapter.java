package app.com.hijaupadi.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import app.com.hijaupadi.R;

/**
 * Created by CV. GLOBAL SOLUSINDO on 3/5/2018.
 */

public class IndexAdapter extends BaseAdapter {
    Context context;
    Bitmap[] imageId;

    private static LayoutInflater inflater = null;

    public IndexAdapter(Context mainActivity,  Bitmap[] prgmImages) {
        // TODO Auto-generated constructor stub

        context = mainActivity;
        imageId = prgmImages;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imageId.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView imgid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        IndexAdapter.Holder holder = new IndexAdapter.Holder();
        //final View rowView;
        convertView = inflater.inflate(R.layout.activity_subgridview, null);
        holder.imgid = (TextView) convertView.findViewById(R.id.idCapture);
        holder.imgid.setText("" +(position+1));
        return convertView;
    }
}
