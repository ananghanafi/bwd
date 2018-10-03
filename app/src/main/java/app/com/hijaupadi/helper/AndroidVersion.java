package app.com.hijaupadi.helper;

/**
 * Created by Imam on 10/21/2016.
 */

public class AndroidVersion {

    private String android_version_name;
    private int android_image_url;
    private Class in;

    public String getAndroid_version_name() {
        return android_version_name;
    }

    public void setAndroid_version_name(String android_version_name) {
        this.android_version_name = android_version_name;
    }

    public int getAndroid_image_url() {
        return android_image_url;
    }

    public void setAndroid_image_url(int android_image_url) {
        this.android_image_url = android_image_url;
    }

    public Class getAndroid_intent() {
        return in;
    }

    public void setAndroid_intent(Class in) {
        this.in = in;
    }
}