package app.com.hijaupadi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdsmdg.tastytoast.TastyToast;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.ValueLineSeries;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Random;

import app.com.hijaupadi.helper.Databasehelper;

public class HasilActivity extends AppCompatActivity {
    ImageView imageView;
    Bitmap bitmap;
    TextView tv_bwd, tv_1, tv_2, tv_long, tv_lat;
    ValueLineChart mCubicValueLineChart, mCubicValueLineChart2;
    ValueLineSeries series, series2;
    private static final int MAX_INTENSITY = (int) Math.pow(2, 8) - 1;
    BarChart mBarChart;
    Databasehelper dbcenter;
    double jml_bin[];
    String image_loc, image_long, image_lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil);
        Intent intent = getIntent();
        imageView = (ImageView) findViewById(R.id.img);
        bitmap = (Bitmap) intent.getParcelableExtra("BitmapImage");
        image_loc = intent.getStringExtra("LocImage");
        image_lat = intent.getStringExtra("Latitude");
        image_long = intent.getStringExtra("Longitude");
        imageView.setImageBitmap(bitmap);
        tv_bwd = (TextView) findViewById(R.id.tv_bwd);
        tv_1 = (TextView) findViewById(R.id.tv_1);
        //tv_2 = (TextView) findViewById(R.id.tv_2);
        tv_long = (TextView) findViewById(R.id.tv_long);
        tv_lat = (TextView) findViewById(R.id.tv_lat);
        mBarChart = (BarChart) findViewById(R.id.cubiclinechart);
        dbcenter = new Databasehelper(this);
        //mCubicValueLineChart2 = (ValueLineChart) findViewById(R.id.cubiclinechart2);
        if(getIntent().hasExtra("BitmapImage")) {
            bitmap = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("BitmapImage"),0,getIntent().getByteArrayExtra("BitmapImage").length);
            imageView.setImageBitmap(bitmap);

        }
        new scan().execute(bitmap);
        tv_long.setText("Longitude : "+image_long +"\nLatitude : "+image_lat);
        tv_lat.setText("Lihat lokasi pada maps");
        tv_lat.setLinksClickable(true);

        tv_lat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:" + image_lat + "," + image_long);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
    }

    public static int mode(int[] array) {
        HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();
        int max = 1, temp = 0;
        for (int i = 0; i < array.length; i++) {
            if (hm.get(array[i]) != null) {
                int count = hm.get(array[i]);
                count = count + 1;
                hm.put(array[i], count);
                if (count > max) {
                    max = count;
                    temp = array[i];
                }
            } else {
                hm.put(array[i], 1);
            }
        }
        return temp;
    }

    public Bitmap resizeBitmap(Bitmap image) {
        image = Bitmap.createScaledBitmap(image, 20, 20, true);
        return image;
    }

    private void SaveImage(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        Log.wtf("FILE : ", "" + file.getAbsolutePath());
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class scan extends AsyncTask<Bitmap, Void, int[]> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(HasilActivity.this);
            dialog.setMessage("Mohon tunggu... ");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected int[] doInBackground(Bitmap... bitmaps) {
            int width, height;
            int [] hasil = new int [6];
            Bitmap bmpOriginal = resizeBitmap(bitmaps[0]);
            height = bmpOriginal.getHeight();
            width = bmpOriginal.getWidth();

            int [][] matriksOri = new int [height][width];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    matriksOri[i][j] = bmpOriginal.getPixel(i, j);
                }
            }

            double [][] matriksY = new double [height][width];
            double [][] matriksU = new double [height][width];
            double [][] matriksV = new double [height][width];
            double [][] matriksG = new double [height][width];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int r = (matriksOri[i][j] & 0xff0000) >> 16;
                    int g = (matriksOri[i][j] & 0xff00) >> 8;
                    int b = (matriksOri[i][j] & 0xff) ;

                    matriksY[i][j] = 0.299 * r + 0.587 * g + 0.114 * b;
                    matriksU[i][j] = 0.492 * (b - matriksY[i][j]);
                    matriksV[i][j] = 0.877 * (r - matriksY[i][j]);
                    matriksG[i][j] = matriksY[i][j]-0.395 * matriksU[i][j]-0.581* matriksV[i][j];
                }
            }

            double [][][] matriksFLBP = new double[height-2][width-2][4];
            for (int k = 0; k < height-2; k++) {
                for (int l = 0; l <width-2; l++) {
                    matriksFLBP [k][l] = slidingWindowStep(matriksG, k, l);
                }
            }

            //int bin = 256;
            double bin = 8;
            double sBin = 256.0/ bin;
            jml_bin = new double [(int)bin];
            double count = 0;
            for (int i = 0; i < bin; i++) {
                for (int x = 0; x < matriksFLBP.length; x++) {
                    for (int y = 0; y < matriksFLBP[0].length; y++) {
                        for (int z = 0; z < 2; z++) {
                            int b = (int)((matriksFLBP[x][y][z] + sBin - 1) / sBin);
                            //int b = (int)matriksFLBP[x][y][z] ;
                            if (b == i) {
                                count += matriksFLBP[x][y][z+2];
                            }
                        }
                    }
                }
                jml_bin[i] = count;
                count = 0;
                System.out.println( "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA ");
                System.out.print(jml_bin[i]+" ");
            }

            /*double max = jml_bin[0];
            int maxIndex = 0;
            for (int i = 1; i < bin; i++) {
                if (jml_bin[i] > max) {
                    max = jml_bin[i];
                    maxIndex = i;
                }
            }
            */

            double [][] bwdtools = {{98.90010361505001  ,45.7500677633 ,13.9499910499,6.79992174255,11.10000676545, 14.900003888850003 , 5.549954025749999, 8.299997975499998},
                    {88.1001414913 , 65.5498475341,18.899885038700003, 10.5499259517 , 22.999998135649996, 25.950002876599996 , 11.899886903050007, 19.45004885085},
                    {94.80026673524996 ,56.94987209094999 , 17.649972778050003, 6.8499697413000025 ,17.24995589009999 ,18.249914445299996 , 11.799887755149998 , 3.9000380363999976},
                    {97.35008076239998, 49.699923606900015 , 7.299955890099994, 5.149949124649999,5.199996111150007, 27.000033295450006 , 6.549958074749992, 9.249977518999996}};

            String [] kelas = {"4", "3", "2", "1"};

            double[] kecil = minimum(bwdtools);
            double[] besar = maksimum(bwdtools);
            double[] range = new double [8];

            for (int i = 0; i < 8; i++){
                range [i] = besar[i] - kecil[i];
            }

            System.out.println("RANGE " + range[0] + " " + range[1] + " " + range[2] + " " + range[3] + " " + range[4] + " " + range[5] + " " + range[6] + " " + range[7] + " ");

            double [][] bwdtoolsNorm = normalisasi(bwdtools, kecil, range);
            double [] jml_binNorm = normalisasi(jml_bin, kecil, range);

            double [] distance = hitung_jarak(bwdtoolsNorm, jml_binNorm);
            String [] k = sort_data(distance, kelas);

            //int count1 = 0;
            //int count2 = 0;
            //int count3 = 0;
            //int count4 = 0;
            //for (int i = 0; i < 3; i++) {
                if (k[0].equalsIgnoreCase("1")) {
                    hasil[5] = 1;
                //    count1++;
                } else if (k[0].equalsIgnoreCase("2")) {
                    hasil[5] = 2;
                //    count2++;
                } else if (k[0].equalsIgnoreCase("3")) {
                    hasil[5] = 3;
                //    count3++;
                } else {
                    hasil[5] = 4;
                //    count4++;
                }
            //}

            /*if (count1 > count2 && count1 > count3 && count1 > count4) {
                hasil[5] = 1;
            } else if (count2 < count1 && count2 > count3 && count2 > count4){
                hasil[5] = 2;
            } else if (count3 < count1 && count3 > count2 && count3 > count4){
                hasil[5] = 3;
            }else{
                hasil[5] = 4;
            }
            */

            /*if (maxIndex >= 0 && maxIndex < 2) {
                hasil[5] = 1;
            }else if (maxIndex >= 2 && maxIndex < 4) {
                hasil[5] = 2;
            } else if (maxIndex >= 4 && maxIndex < 6) {
                hasil[5] = 3;
            } else if (maxIndex >= 6 && maxIndex < 8) {
                hasil[5] = 4;
            } else {
                hasil[5] = 0;
            }
            */
            hasil[4] = 1;
            return hasil;
        }
        // 2^5 for RGB 565

        public double[] slidingWindowStep(double [][] matriks, int k, int l){
            double [] content = {0.0 , 0.0, 0.0, 0.0};
            double middle = matriks[k+1][l+1];
            double [][][] mBwd20 = new double [3][3][2];
            double [][][] mBwd21 = new double [3][3][2];
            double fBwd2 = 10;
            double [][] dec2 = new double [4][8];

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (matriks[i+k][j+l] - middle > -fBwd2 && fBwd2 > matriks[i+k][j+l] - middle) {
                        mBwd20[i][j][0] = 0;
                        mBwd20[i][j][1] = ((fBwd2 - (matriks[i+k][j+l] - middle)) / (2*fBwd2));
                        mBwd21[i][j][0] = 1;
                        mBwd21[i][j][1] = ((fBwd2 + (matriks[i+k][j+l] - middle)) / (2*fBwd2));
                    }else{
                        if (matriks[i+k][j+l] > middle) {
                            mBwd20[i][j][0] = 1;
                            mBwd20[i][j][1] = 1;
                            mBwd21[i][j][0] = 1;
                            mBwd21[i][j][1] = 1;
                        }else{
                            mBwd20[i][j][0] = 0;
                            mBwd20[i][j][1] = 1;
                            mBwd21[i][j][0] = 0;
                            mBwd21[i][j][1] = 1;
                        }
                    }
                }
            }

            for (int i = 0; i < 4; i++) {
                if (i < 2) {
                    dec2 [i][0] = mBwd20 [0][0][i];
                    dec2 [i][1] = mBwd20 [0][1][i];
                    dec2 [i][2] = mBwd20 [0][2][i];
                    dec2 [i][3] = mBwd20 [1][2][i];
                    dec2 [i][4] = mBwd20 [2][2][i];
                    dec2 [i][5] = mBwd20 [2][1][i];
                    dec2 [i][6] = mBwd20 [2][0][i];
                    dec2 [i][7] = mBwd20 [1][0][i];
                }else{
                    dec2 [i][0] = mBwd21 [0][0][i-2];
                    dec2 [i][1] = mBwd21 [0][1][i-2];
                    dec2 [i][2] = mBwd21 [0][2][i-2];
                    dec2 [i][3] = mBwd21 [1][2][i-2];
                    dec2 [i][4] = mBwd21 [2][2][i-2];
                    dec2 [i][5] = mBwd21 [2][1][i-2];
                    dec2 [i][6] = mBwd21 [2][0][i-2];
                    dec2 [i][7] = mBwd21 [1][0][i-2];
                }
            }
            for (int i = 0; i < 8; i++) {
                content[0] += (dec2[0][i] * Math.pow(2, i));
                content[1] += (dec2[2][i] * Math.pow(2, i)) ;
                content[2] = dec2[1][i];
                content[3] = dec2[3][i];
            }
            return content;
        }

        public double[] maksimum(double data[][]) {
            int N = data.length;
            double[] maks = {0, 0, 0, 0, 0, 0, 0, 0};
            for (int i = 0; i < N; i++) {
                if (data[i][0] > maks[0]) {
                    maks[0] = data[i][0];
                }
                if (data[i][1] > maks[1]) {
                    maks[1] = data[i][1];
                }
                if (data[i][2] > maks[2]) {
                    maks[2] = data[i][2];
                }
                if (data[i][3] > maks[3]) {
                    maks[3] = data[i][3];
                }
                if (data[i][4] > maks[4]) {
                    maks[4] = data[i][4];
                }
                if (data[i][5] > maks[5]) {
                    maks[5] = data[i][5];
                }
                if (data[i][6] > maks[6]) {
                    maks[6] = data[i][6];
                }
                if (data[i][7] > maks[7]) {
                    maks[7] = data[i][7];
                }
            }
            return (maks);
        }

        public double[] minimum(double data[][]) {
            int N = data.length;
            double[] min = {10000, 10000, 10000,10000, 10000, 10000, 10000, 10000};
            for (int i = 0; i < N; i++) {
                if (data[i][0] < min[0]) {
                    min[0] = data[i][0];
                }
                if (data[i][1] < min[1]) {
                    min[1] = data[i][1];
                }
                if (data[i][2] < min[2]) {
                    min[2] = data[i][2];
                }
                if (data[i][3] < min[3]) {
                    min[3] = data[i][3];
                }
                if (data[i][4] < min[4]) {
                    min[4] = data[i][4];
                }
                if (data[i][5] < min[5]) {
                    min[5] = data[i][5];
                }
                if (data[i][6] < min[6]) {
                    min[6] = data[i][6];
                }
                if (data[i][7] < min[7]) {
                    min[7] = data[i][7];
                }
            }
            return (min);
        }

        public double[][] normalisasi(double training [][], double kecil [], double range []) {
            int x = training.length;
            double[][] normal = new double[x][8];
            System.out.println("OPO IKI MAENG");
            for (int a = 0; a < x; a++) {
                for (int b = 0; b < 8; b++){
                    normal[a][b] = (training[a][b] - kecil[b]) / range[b];
                    System.out.print(normal[a][b] + " ");
                }
                System.out.println();
            }
            return normal;
        }

        public double[] normalisasi(double testing [], double kecil [], double range[]) {
            double[] normal = new double[8];
            System.out.println("UJI");
            for (int a = 0; a < 8; a++){
                normal[a] = (testing[a] - kecil[a]) / range[a];
                System.out.print(normal[a] + " ");
            }
            return normal;
        }

        public double[] hitung_jarak(double[][] data, double[] uji) {
            int x = data.length;
            double distance[][] = new double[x][8];
            double finalDistance[] = new double[x];
            for (int i = 0; i < x; i++) {
                for (int j = 0; j < 8; j++){
                    distance[i][j] = Math.abs(uji[j] - data[i][j]);
                }
                finalDistance[i] = (distance[i][0] + distance[i][1] + distance[i][2] +
                        distance[i][3] + distance[i][4] + distance[i][5] + distance[i][6] + distance[i][7]) / 8;
                System.out.println("FINAL DISTANCE " + finalDistance[i]);
            }
            return finalDistance;
        }

        public String[] sort_data(double data[], String [] kelas) {
            int n = data.length;
            double[] dataBaru = new double[n];
            String[] kBaru = new String[n];
            for (int i = 0; i < n; i++) {
                kBaru[i] = kelas[i];
                dataBaru[i] = data[i];
            }

            boolean swapped = true;
            int j = 0;
            String tmps;
            double tmp;

            System.out.println("SORTING");
            while (swapped) {
                swapped = false;
                j++;
                for (int i = 0; i < n - j; i++) {
                    if (dataBaru[i] > dataBaru[i + 1]) {
                        tmp = dataBaru[i];
                        dataBaru[i] = dataBaru[i + 1];
                        dataBaru[i + 1] = tmp;

                        tmps = kBaru[i];
                        kBaru[i] = kBaru[i + 1];
                        kBaru[i + 1] = tmps;
                        swapped = true;
                    }
                }
            }
            System.out.println(kBaru[0] + " " + kBaru[1] + " " + kBaru[2] + " " + kBaru[3] + " ");
            return kBaru;
        }

        @Override
        protected void onPostExecute(int[] result) {
            if (result[5] == 4) {
                tv_bwd.setText("BWD : 4");
                tv_1.setText("5 t/ha : 0 kg/ha , 6 t/ha : 0 atau 50 kg/ha , 7 t/ha : 50 kg/ha , 8 t/ha : 50 kg/ha. " +
                        "Untuk hasil harapan hanya 5-6 t/ha tidak perlu memberikan pupuk. Tambahkan 50 kg Urea/ha bila hasil harapan lebih dari 6 t/ha. ");
            }
            if (result[5] == 3) {
                tv_bwd.setText("BWD : 3");
                tv_1.setText("5 t/ha : 50 kg/ha , 6 t/ha : 75 kg/ha , 7 t/ha : 100 kg/ha , 8 t/ha : 125 kg/ha." +
                        "Berikan 50 kg Urea/ha pada hasil harapan sebesar 5 t/ha. Tambahkan lagi 25 kg Urea/ha untuk setiap ton/ha lebih tingginya hasil harapan. ");
            }
            if (result[5] ==2) {
                tv_bwd.setText("BWD : 2");
                tv_1.setText("5 t/ha : 50 kg/ha , 6 t/ha : 75 kg/ha , 7 t/ha : 100 kg/ha , 8 t/ha : 125 kg/ha."+
                        "Berikan 50 kg Urea/ha pada hasil harapan sebesar 5 t/ha. Tambahkan lagi 25 kg Urea/ha untuk setiap ton/ha lebih tingginya hasil harapan. ");
            }
            if (result[5] == 1) {
                tv_bwd.setText("BWD : 1");
                tv_1.setText("5 t/ha : 75 kg/ha , 6 t/ha : 100 kg/ha , 7 t/ha : 125 kg/ha , 8 t/ha : 150 kg/ha. " +
                        "Berikan 75 kg Urea/ha pada hasil harapan sebesar 5 t/ha. Tambahkan lagi 25 kg Urea/ha untuk setiap satu t/ha lebih tingginya hasil harapan.");
            }
            tv_bwd.setVisibility(View.VISIBLE);
            Log.wtf("h", "" + result[5]);
            //tv_2.setText("Hasil modus S-RGB : " + result[0] + " ,modus R : " + result[1] + " modus G : " + result[2] + " modus B : " + result[3] + ", Nilai terkecil : " + result[6]);
            for (double s : jml_bin) {
                mBarChart.addBar(new BarModel((float) s, 0xFF123456));
            }
            mBarChart.startAnimation();

            SQLiteDatabase db = dbcenter.getWritableDatabase();
            db.execSQL("UPDATE history SET hasil ='" + tv_1.getText().toString().substring(0,80) + "' WHERE gambar = '" +image_loc +"'");
            dialog.dismiss();
            if (result[4] == 0)
                TastyToast.makeText(getApplicationContext(), "Terjadi kesalahan", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            else
                TastyToast.makeText(getApplicationContext(), "Scaning berhasil", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
        }
    }
}
