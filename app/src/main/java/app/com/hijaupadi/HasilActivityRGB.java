package app.com.hijaupadi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
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
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import app.com.hijaupadi.helper.Databasehelper;

public class HasilActivityRGB extends AppCompatActivity {
    ImageView imageView;
    Bitmap bitmap;
    TextView tv_bwd, tv_1, tv_2;
    ValueLineChart mCubicValueLineChart, mCubicValueLineChart2;
    ValueLineSeries series, series2;
    private static final int MAX_INTENSITY = (int) Math.pow(2, 8) - 1;
    BarChart mBarChart;
    Databasehelper dbcenter;
    int jml_bin[];
    String image_loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil);
        Intent intent = getIntent();
        imageView = (ImageView) findViewById(R.id.img);
        bitmap = (Bitmap) intent.getParcelableExtra("BitmapImage");
        image_loc = intent.getStringExtra("LocImage");
        imageView.setImageBitmap(bitmap);
        tv_bwd = (TextView) findViewById(R.id.tv_bwd);
        tv_1 = (TextView) findViewById(R.id.tv_1);
        //tv_2 = (TextView) findViewById(R.id.tv_2);
        mBarChart = (BarChart) findViewById(R.id.cubiclinechart);
        dbcenter = new Databasehelper(this);
        //mCubicValueLineChart2 = (ValueLineChart) findViewById(R.id.cubiclinechart2);
        new scan().execute(bitmap);
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
            dialog = new ProgressDialog(HasilActivityRGB.this);
            dialog.setMessage("Mohon tunggu... ");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected int[] doInBackground(Bitmap... bitmaps) {
            series2 = new ValueLineSeries();
            series2.setColor(0xFFFF0000);
            int width, height;
            Bitmap bmpOriginal = resizeBitmap(bitmaps[0]);
            height = bmpOriginal.getHeight();
            width = bmpOriginal.getWidth();
            int[] red = new int[width * height];
            int[] green = new int[width * height];
            int[] blue = new int[width * height];
            int[] srgb = new int[width * height];
            int[][][] bin = new int[16][3][width * height];
            jml_bin = new int[16];
            int[] hasil = new int[10];
            List<Integer> srgbl = new ArrayList<Integer>();
            List<Integer> redl = new ArrayList<Integer>();
            List<Integer> bluel = new ArrayList<Integer>();
            List<Integer> greenl = new ArrayList<Integer>();
            double r1 = 0, r2 = 0, g1 = 0, g2 = 0, b1 = 0, b2 = 0;
            int statr1 = 0, statr2 = 0, statg1 = 0, statg2 = 0, statb1 = 0, statb2 = 0, z1 = 0, z2 = 0;
            int j = 0;
            int g = 0;
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    int pixel = bmpOriginal.getPixel(x, y);
                    red[j] = (pixel >> 16) & 0xff;
                    green[j] = (pixel >> 8) & 0xff;
                    blue[j] = (pixel)& 0xff;
                    srgb[j] = red[j] + green[j] + blue[j];
                    if (!srgbl.contains(srgb[j])) {
                        srgbl.add(srgb[j]);
                    }
                    if (!redl.contains(red[j])) {
                        redl.add(red[j]);
                    }
                    if (!bluel.contains(green[j])) {
                        bluel.add(blue[j]);
                    }
                    if (!greenl.contains(green[j])) {
                        greenl.add(green[j]);
                    }

                    double d = srgb[j] / 47.812;
                    int k = (int) d;
                    Log.wtf("" + j, d + " : " + jml_bin[k]);
                    jml_bin[k] += 1;
                    bin[k][0][g] = (pixel >> 16) & 0xff;
                    bin[k][1][g] = (pixel >> 8) & 0xff;
                    bin[k][2][g] = (pixel) & 0xff;
                    j++;
                    g++;
                }
            }
            int[] srgbunique = convertIntegers(srgbl);
            for (int s : srgbunique) {
                series2.addPoint(new ValueLinePoint("" + s, (float) getMaxValue(s, srgb)));
            }
            Log.wtf("arraylength", bin[0][0].length + "");
            hasil[0] = mode(srgb);
            hasil[1] = mode(red);
            hasil[2] = mode(green);
            hasil[3] = mode(blue);
            hasil[4] = 1;
            if (hasil[1] >= 0 && hasil[1] <= 57.7) {
                r1 = 1;
                r2 = 1;
                statr1 = 1;
                statr2 = 1;
            } else if (hasil[1] > 57.7 && hasil[1] <= 115.3) {
                r1 = (127.5 - hasil[1]) / (127.5 - 42.5);
                r2 = (hasil[1] - 42.5) / (127.5 - 42.5);
                statr1 = 1;
                statr2 = 2;
            } else if (hasil[1] > 127.5 && hasil[1] <= 212.5) {
                r1 = (212.5 - hasil[1]) / (212.5 - 127.5);
                r2 = (hasil[1] - 127.5) / (212.5 - 127.5);
                statr1 = 2;
                statr2 = 3;
            } else if (hasil[1] > 212.5) {
                r1 = 1;
                r2 = 1;
                statr1 = 3;
                statr2 = 3;
            }

            if (hasil[2] > 0 && hasil[2] <= 42.5) {
                g1 = 1;
                g2 = 1;
                statg1 = 1;
                statg2 = 1;
            } else if (hasil[2] > 42.5 && hasil[2] <= 127.5) {
                g1 = (127.5 - hasil[2]) / (127.5 - 42.5);
                g2 = (hasil[2] - 42.5) / (127.5 - 42.5);
                statg1 = 1;
                statg2 = 2;
            } else if (hasil[2] > 127.5 && hasil[2] <= 212.5) {
                g1 = (212.5 - hasil[2]) / (212.5 - 127.5);
                g2 = (hasil[2] - 127.5) / (212.5 - 127.5);
                statg1 = 2;
                statg2 = 3;
            } else if (hasil[2] > 212.5) {
                g1 = 1;
                g2 = 1;
                statg1 = 3;
                statg2 = 3;
            }


            if (hasil[3] > 0 && hasil[3] <= 42.5) {
                b1 = 1;
                b2 = 1;
                statb1 = 1;
                statb2 = 1;
            } else if (hasil[3] > 42.5 && hasil[3] <= 127.5) {
                b1 = (127.5 - hasil[3]) / (127.5 - 42.5);
                b2 = (hasil[3] - 42.5) / (127.5 - 42.5);
                statb1 = 1;
                statb2 = 2;
            } else if (hasil[3] > 127.5 && hasil[3] <= 212.5) {
                b1 = (212.5 - hasil[3]) / (212.5 - 127.5);
                b2 = (hasil[3] - 127.5) / (212.5 - 127.5);
                statb1 = 2;
                statb2 = 3;
            } else if (hasil[3] > 212.5) {
                b1 = 1;
                b2 = 1;
                statb1 = 3;
                statb2 = 3;
            }
            //r1
            if (statr1 == 1) {
                if (statg1 == 1) {
                    if (statb1 == 1) {
                        z1 = 20;
                    } else if (statb1 == 2) {
                        z1 = 20;
                    } else if (statb1 == 3) {
                        z1 = 20;
                    }
                } else if (statg1 == 2) {
                    if (statb1 == 1) {
                        z1 = 20;
                    } else if (statb1 == 2) {
                        z1 = 20;
                    } else if (statb1 == 3) {
                        z1 = 20;
                    }
                } else if (statg1 == 3) {
                    if (statb1 == 1) {
                        z1 = 20;
                    } else if (statb1 == 2) {
                        z1 = 20;
                    } else if (statb1 == 3) {
                        z1 = 20;
                    }
                }
            } else if (statr1 == 2) {
                if (statg1 == 1) {
                    if (statb1 == 1) {
                        z1 = 60;
                    } else if (statb1 == 2) {
                        z1 = 60;
                    } else if (statb1 == 3) {
                        z1 = 60;
                    }
                } else if (statg1 == 2) {
                    if (statb1 == 1) {
                        z1 = 60;
                    } else if (statb1 == 2) {
                        z1 = 60;
                    } else if (statb1 == 3) {
                        z1 = 60;
                    }
                } else if (statg1 == 3) {
                    if (statb1 == 1) {
                        z1 = 80;
                    } else if (statb1 == 2) {
                        z1 = 80;
                    } else if (statb1 == 3) {
                        z1 = 80;
                    }
                }
            } else if (statr1 == 3) {
                if (statg1 == 1) {
                    if (statb1 == 1) {
                        z1 = 100;
                    } else if (statb1 == 2) {
                        z1 = 100;
                    } else if (statb1 == 3) {
                        z1 = 100;
                    }
                } else if (statg1 == 2) {
                    if (statb1 == 1) {
                        z1 = 100;
                    } else if (statb1 == 2) {
                        z1 = 100;
                    } else if (statb1 == 3) {
                        z1 = 100;
                    }
                } else if (statg1 == 3) {
                    if (statb1 == 1) {
                        z1 = 100;
                    } else if (statb1 == 2) {
                        z1 = 100;
                    } else if (statb1 == 3) {
                        z1 = 100;
                    }
                }
            }
            //r2
            if (statr2 == 1) {
                if (statg2 == 1) {
                    if (statb2 == 1) {
                        z2 = 20;
                    } else if (statb2 == 2) {
                        z2 = 20;
                    } else if (statb2 == 3) {
                        z2 = 20;
                    }
                } else if (statg2 == 2) {
                    if (statb2 == 1) {
                        z2 = 20;
                    } else if (statb2 == 2) {
                        z2 = 20;
                    } else if (statb2 == 3) {
                        z2 = 20;
                    }
                } else if (statg2 == 3) {
                    if (statb2 == 1) {
                        z2 = 20;
                    } else if (statb2 == 2) {
                        z2 = 20;
                    } else if (statb2 == 3) {
                        z2 = 20;
                    }
                }
            } else if (statr2 == 2) {
                if (statg2 == 1) {
                    if (statb2 == 1) {
                        z2 = 60;
                    } else if (statb2 == 2) {
                        z2 = 60;
                    } else if (statb2 == 3) {
                        z2 = 60;
                    }
                } else if (statg2 == 2) {
                    if (statb2 == 1) {
                        z2 = 60;
                    } else if (statb2 == 2) {
                        z2 = 60;
                    } else if (statb2 == 3) {
                        z2 = 60;
                    }
                } else if (statg2 == 3) {
                    if (statb2 == 1) {
                        z2 = 80;
                    } else if (statb2 == 2) {
                        z2 = 80;
                    } else if (statb2 == 3) {
                        z2 = 80;
                    }
                }
            } else if (statr2 == 3) {
                if (statg2 == 1) {
                    if (statb2 == 1) {
                        z2 = 100;
                    } else if (statb2 == 2) {
                        z2 = 100;
                    } else if (statb2 == 3) {
                        z2 = 100;
                    }
                } else if (statg2 == 2) {
                    if (statb2 == 1) {
                        z2 = 100;
                    } else if (statb2 == 2) {
                        z2 = 100;
                    } else if (statb2 == 3) {
                        z2 = 100;
                    }
                } else if (statg2 == 3) {
                    if (statb2 == 1) {
                        z2 = 100;
                    } else if (statb2 == 2) {
                        z2 = 100;
                    } else if (statb2 == 3) {
                        z2 = 100;
                    }
                }
            }
            double[] apr1 = {r1, b1, g1};
            double[] apr2 = {r2, b2, g2};
            double w = (getMinValue(apr1) * z1) + (getMinValue(apr2) * z2) / (getMinValue(apr1) + getMinValue(apr2));
            Log.wtf("h", "" + w);
            Log.wtf("apr1", "" + getMinValue(apr1));
            Log.wtf("apr2", "" + getMinValue(apr2));
            if (w > 0 && w <= 20) {
                hasil[5] = 2;
            } else if (w > 20 && w <= 60) {
                hasil[5] = 3;
            } else if (w > 60 && w <= 80) {
                hasil[5] = 4;
            } else {
                hasil[5] = 5;
            }
            hasil[6] = getMinValueint(srgb);

            return hasil;
        }
        // 2^5 for RGB 565

        public int intensityLevel(final int color) {
            // also see Color#getRed(), #getBlue() and #getGreen()
            final int red = (color >> 16) & 0xFF;
            final int blue = (color >> 0) & 0xFF;
            final int green = (color >> 8) & 0xFF;
            assert red == blue && green == blue; // doesn't hold for green in RGB 565
            return MAX_INTENSITY - blue;
        }

        @Override
        protected void onPostExecute(int[] result) {
            if (result[5] == 2) {
                tv_bwd.setText("BWD : 4");
                tv_1.setText("5 t/ha : 0 kg/ha , 6 t/ha : 0 atau 50 kg/ha , 7 t/ha : 50 kg/ha , 8 t/ha : 50 kg/ha. " +
                        "Untuk hasil harapan hanya 5-6 t/ha tidak perlu memberikan pupuk. Tambahkan 50 kg Urea/ha bila hasil harapan lebih dari 6 t/ha. ");
            }
            if (result[5] == 3) {
                tv_bwd.setText("BWD : 3");
                tv_1.setText("5 t/ha : 50 kg/ha , 6 t/ha : 75 kg/ha , 7 t/ha : 100 kg/ha , 8 t/ha : 125 kg/ha." +
                        "Berikan 50 kg Urea/ha pada hasil harapan sebesar 5 t/ha. Tambahkan lagi 25 kg Urea/ha untuk setiap ton/ha lebih tingginya hasil harapan. ");
            }
            if (result[5] == 4) {
                tv_bwd.setText("BWD : 2");
                tv_1.setText("5 t/ha : 50 kg/ha , 6 t/ha : 75 kg/ha , 7 t/ha : 100 kg/ha , 8 t/ha : 125 kg/ha."+
                        "Berikan 50 kg Urea/ha pada hasil harapan sebesar 5 t/ha. Tambahkan lagi 25 kg Urea/ha untuk setiap ton/ha lebih tingginya hasil harapan. ");
            }
            if (result[5] == 5) {
                tv_bwd.setText("BWD : 1");
                tv_1.setText("5 t/ha : 75 kg/ha , 6 t/ha : 100 kg/ha , 7 t/ha : 125 kg/ha , 8 t/ha : 150 kg/ha. " +
                        "Berikan 75 kg Urea/ha pada hasil harapan sebesar 5 t/ha. Tambahkan lagi 25 kg Urea/ha untuk setiap satu t/ha lebih tingginya hasil harapan.");
            }
            tv_bwd.setVisibility(View.VISIBLE);
            Log.wtf("h", "" + result[5]);
            //tv_2.setText("Hasil modus S-RGB : " + result[0] + " ,modus R : " + result[1] + " modus G : " + result[2] + " modus B : " + result[3] + ", Nilai terkecil : " + result[6]);
            for (int s : jml_bin) {
                mBarChart.addBar(new BarModel((float) s, 0xFF123456));
            }
            mBarChart.startAnimation();
            mCubicValueLineChart2.addSeries(series2);
            mCubicValueLineChart2.startAnimation();

            SQLiteDatabase db = dbcenter.getWritableDatabase();
            db.execSQL("UPDATE history SET hasil ='" + tv_1.getText().toString().substring(0,80) + "' WHERE gambar = '" +image_loc +"'");
            dialog.dismiss();
            if (result[4] == 0)
                TastyToast.makeText(getApplicationContext(), "Terjadi kesalahan", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
            else
                TastyToast.makeText(getApplicationContext(), "Scaning berhasil", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
        }
    }

    public static int[] convertIntegers(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }

    // getting the maximum value
    public static int getMaxValue(int j, int[] array) {
        int maxValue = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == j) {
                maxValue++;
            }
        }
        return maxValue;
    }

    // getting the miniumum value
    public static double getMinValue(double[] array) {
        double minValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < minValue) {
                minValue = array[i];
            }
        }
        return minValue;
    }

    public static int getMinValueint(int[] array) {
        int minValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < minValue) {
                minValue = array[i];
            }
        }
        return minValue;
    }
}
