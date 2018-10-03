package app.com.hijaupadi;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import app.com.hijaupadi.helper.BitmapAdapter;
import app.com.hijaupadi.helper.CropingOption;
import app.com.hijaupadi.helper.CropingOptionAdapter;
import app.com.hijaupadi.helper.Databasehelper;
import app.com.hijaupadi.helper.IndexAdapter;

import static android.content.ContentValues.TAG;

public class ScanImageActivity extends AppCompatActivity {
    Button ambildata, scanImage;
    private Uri mCropImageUri;
    private final static int REQUEST_PERMISSION_REQ_CODE = 34;
    private static final int CAMERA_CODE = 101, GALLERY_CODE = 201, CROPING_CODE = 301;

    private Button btn_select_image;
    private ImageView imageView;
    private Uri mImageCaptureUri;
    private File outPutFile = null;
    protected Cursor cursor;
    Databasehelper dbcenter;
    TextView tv_long, tv_lat, tv_date;
    String strAdd = "";
    LocationManager locationManager;
    GridView imgGridView, idGridView;
    List<Bitmap> capture;
    Bitmap [] hasilCapture;
    boolean exit;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_image);
        outPutFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");

        imgGridView = (GridView) findViewById(R.id.imgGridView);
        idGridView = (GridView) findViewById(R.id.idGridView);
        ambildata = (Button) findViewById(R.id.take);
        //scanImage = (Button) findViewById(R.id.scan);
        imageView = (ImageView) findViewById(R.id.capture);
        tv_long = (TextView) findViewById(R.id.tv_long);
        tv_lat = (TextView) findViewById(R.id.tv_lat);
        tv_date = (TextView) findViewById(R.id.tv_date);
        locationManager = (LocationManager) getSystemService(getBaseContext().LOCATION_SERVICE);
        dbcenter = new Databasehelper(this);
        capture = new ArrayList<>();
        exit = false;


        idGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //imageView.buildDrawingCache();
                //Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());

                Bitmap b; // your bitmap
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                hasilCapture[position].compress(Bitmap.CompressFormat.PNG, 50, bs);
                //               i.putExtra("byteArray", bs.toByteArray());
                //              startActivity(i);

                Bundle extras = new Bundle();
                intent = new Intent(ScanImageActivity.this, HasilActivity.class);
                //       intent.putExtra("BitmapImage", hasilCapture[position]);
//                extras.putParcelable("imagebitmap", hasilCapture[position]);
                SaveImage(hasilCapture[position]);
                System.out.println("Iki gawe clik gambar "+ position);
                intent.putExtra("BitmapImage", bs.toByteArray());
                startActivity(intent);
            }
        });

        ambildata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    selectImageOption();
            }
        });
        /*scanImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mImageCaptureUri != null) {
                    imageView.buildDrawingCache();
                    Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());
                    Intent intent = new Intent(getApplicationContext(), HasilActivityRGB.class);
                    intent.putExtra("BitmapImage", bitmap);
                    SaveImage(bitmap);
                    startActivity(intent);
                } else {
                    TastyToast.makeText(getApplicationContext(), "Ambil Gambar terlebih dahulu", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                }
            }
        });*/
    }

    private void selectImageOption() {
        final CharSequence[] items = {"Capture Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ScanImageActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Capture Photo")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp1.jpg");
                    mImageCaptureUri = Uri.fromFile(f);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                    startActivityForResult(intent, CAMERA_CODE);

                } else if (items[item].equals("Choose from Gallery")) {
//                    Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                    getIntent.setType("image/*");
//
//                    Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    pickIntent.setType("image/*");
//                    Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
//                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
//
//                    startActivityForResult(chooserIntent, PICK_IMAGE);
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, GALLERY_CODE);

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                    exit = true;
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(ScanImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_REQ_CODE);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, final @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_REQ_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {

            mImageCaptureUri = data.getData();
            Log.wtf("Gallery Image URI : ", "" + mImageCaptureUri);
            CropingIMG();

        } else if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {

            Log.wtf("Gallery Image URI : ", "" + mImageCaptureUri);
            CropingIMG();
        } else if (requestCode == CROPING_CODE) {

            try {
                if (outPutFile.exists()) {
                    Bitmap photo = decodeFile(outPutFile);
                    //imageView.setImageBitmap(photo);

                    capture.add(photo);

                    hasilCapture = new Bitmap[capture.size()];
                    capture.toArray(hasilCapture);

                    BitmapAdapter bitmapAdapter = new BitmapAdapter(ScanImageActivity.this, hasilCapture);
                    imgGridView.setAdapter(bitmapAdapter);
                    IndexAdapter indexAdapter = new IndexAdapter(ScanImageActivity.this, hasilCapture);
                    idGridView.setAdapter(indexAdapter);

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String formattedDate = df.format(c.getTime());

                    tv_date.setText(formattedDate);
                    tv_date.setVisibility(View.VISIBLE);

                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    getLocation(location);
                    tv_long.setVisibility(View.VISIBLE);
                    tv_lat.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "Error while save image", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            SQLiteDatabase db = dbcenter.getWritableDatabase();
            intent.putExtra("LocImage", file.getAbsolutePath());
            intent.putExtra("Longitude", tv_long.getText().toString().substring(11));
            intent.putExtra("Latitude", tv_lat.getText().toString().substring(10));
            java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
            db.execSQL("INSERT INTO history (gambar, created_at, longitude, latitude, hasil) VALUES('" + file.getAbsolutePath() + "','" + date + "','" +
                    tv_long.getText() + "','" + tv_lat.getText() + "','Data tidak terproses')");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CropingIMG() {

        final ArrayList<CropingOption> cropOptions = new ArrayList<CropingOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "Can't find image croping app", Toast.LENGTH_SHORT).show();
            return;
        } else {
            intent.setData(mImageCaptureUri);
            intent.putExtra("outputX", 512);
            intent.putExtra("outputY", 512);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);

            //TODO: don't use return-data tag because it's not return large image data and crash not given any message
            //intent.putExtra("return-data", true);

            //Create output file here
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = (ResolveInfo) list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROPING_CODE);
            } else {
                for (ResolveInfo res : list) {
                    final CropingOption co = new CropingOption();

                    co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

                CropingOptionAdapter adapter = new CropingOptionAdapter(getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Croping App");
                builder.setCancelable(false);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResult(cropOptions.get(item).appIntent, CROPING_CODE);
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mImageCaptureUri != null) {
                            getContentResolver().delete(mImageCaptureUri, null, null);
                            mImageCaptureUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 512;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
}


    public void getLocation (Location loc) {
        String longitude = "Longitude: " +loc.getLongitude();
        Log.v(TAG, longitude);
        String latitude = "Latitude: " +loc.getLatitude();
        Log.v(TAG, latitude);

        tv_long.setText(longitude);
        tv_lat.setText(latitude);

    /*----------to get City-Name from coordinates ------------- */
        String cityName=null;
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        try {

            addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);

            if (!addresses.isEmpty()) {
                System.out.println("ADDRESSES NOT EMPTY");
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String locality = addresses.get(0).getLocality();
                String subLocality = addresses.get(0).getSubLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                  strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(" ");
                }
                strAdd = strReturnedAddress.toString();
                //textView.setText(address + locality + state + country);

                Log.e("MyCurrentLoctionAddress", "" + strReturnedAddress.toString());

                cityName=addresses.get(0).getLocality();

            } else {

                     Log.e("MyCurrentLoctionAddress", "No Address returned!");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        String s = longitude+"\n"+latitude +
                "\n\nMy Currrent City is: "+cityName;
        //editLocation.setText(s);
    }

}
