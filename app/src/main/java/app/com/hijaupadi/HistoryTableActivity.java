package app.com.hijaupadi;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.com.hijaupadi.helper.Databasehelper;

/**
 * Created by CV. GLOBAL SOLUSINDO on 2/28/2018.
 */

public class HistoryTableActivity extends AppCompatActivity {
    Databasehelper dbcenter;
    Cursor cursor;
    String[][] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_2);
        dbcenter = new Databasehelper(this);
        new HistoryTableActivity.getdata().execute();
    }

    public void init() {
        TableLayout stk = (TableLayout) findViewById(R.id.table_history);
        TableRow tbrow0 = new TableRow(this);
        TextView tv0 = new TextView(this);
        tv0.setText(" No ");
        tv0.setBackgroundColor(0x5DFC0A);
        tv0.setTextColor(Color.BLACK);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText(" Lokasi ");
        tv1.setBackgroundColor(0x5DFC0A);
        tv1.setTextColor(Color.BLACK);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText(" Tanggal dan Waktu ");
        tv2.setBackgroundColor(0x5DFC0A);
        tv2.setTextColor(Color.BLACK);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText(" Hasil ");
        tv3.setBackgroundColor(0x5DFC0A);
        tv3.setTextColor(Color.BLACK);
        tbrow0.addView(tv3);
        stk.addView(tbrow0);
        for (int i = 0; i < data.length; i++) {
            TableRow tbrow = new TableRow(this);
            TextView t1v = new TextView(this);
            t1v.setText("" + (i+1));
            t1v.setBackgroundColor(Color.DKGRAY);
            t1v.setTextColor(Color.WHITE);
            t1v.setGravity(Gravity.CENTER);
            tbrow.addView(t1v);
            TextView t2v = new TextView(this);
            t2v.setText(" " +data[i][0] + " " + data[i][1]);
            t2v.setBackgroundColor(Color.DKGRAY);
            t2v.setTextColor(Color.WHITE);
            t2v.setGravity(Gravity.CENTER);
            tbrow.addView(t2v);
            TextView t3v = new TextView(this);
            t3v.setText(data[i][2]);
            t3v.setBackgroundColor(Color.DKGRAY);
            t3v.setTextColor(Color.WHITE);
            t3v.setGravity(Gravity.CENTER);
            tbrow.addView(t3v);
            TextView t4v = new TextView(this);
            t4v.setText(data[i][3]);
            t4v.setBackgroundColor(Color.DKGRAY);
            t4v.setTextColor(Color.WHITE);
            t4v.setGravity(Gravity.CENTER);
            tbrow.addView(t4v);
            stk.addView(tbrow);
        }
    }

    class getdata extends AsyncTask<Bitmap, Void, Void> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(HistoryTableActivity.this);
            dialog.setMessage("Mohon tunggu... ");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Bitmap... params) {
            SQLiteDatabase db = dbcenter.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM history order by created_at ASC", null);
            List<String[]> data_list = new ArrayList<String[]>();
            int i = 1;
            if (cursor.moveToFirst()) {
                do {
                    String [] data_array = {cursor.getString(2).toString(),cursor.getString(3).toString(),cursor.getString(1).toString(),cursor.getString(4).toString()};
                    data_list.add(data_array);
                } while (cursor.moveToNext());
            }
            data = new String[data_list.size()][4];

            data_list.toArray(data);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            init();
            dialog.dismiss();
        }
    }
}
