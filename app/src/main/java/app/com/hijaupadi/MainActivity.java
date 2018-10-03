package app.com.hijaupadi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import app.com.hijaupadi.helper.AndroidVersion;
import app.com.hijaupadi.helper.DataAdapter;

public class MainActivity extends AppCompatActivity {
    private final String android_version_names[] = {
            "Scan Image",
            //"About Us",
            "Data Hasil"
    };

    private final int android_image_urls[] = {
            R.drawable.scan,
            //R.drawable.about,
            R.drawable.test
    };
    private final Class in[] = {
            ScanImageActivity.class,
            //MainActivity.class,
            HistoryActivity.class,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }
    private void initViews() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<AndroidVersion> androidVersions = prepareData();
        DataAdapter adapter = new DataAdapter(getApplicationContext(), androidVersions);
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<AndroidVersion> prepareData() {
        ArrayList<AndroidVersion> android_version = new ArrayList<>();
        for (int i = 0; i < android_version_names.length; i++) {
            AndroidVersion androidVersion = new AndroidVersion();
            androidVersion.setAndroid_version_name(android_version_names[i]);
            androidVersion.setAndroid_image_url(android_image_urls[i]);
            androidVersion.setAndroid_intent(in[i]);
            android_version.add(androidVersion);
        }
        return android_version;
    }
}
