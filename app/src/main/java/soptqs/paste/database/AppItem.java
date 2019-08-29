package soptqs.paste.database;

import android.graphics.drawable.Drawable;

/**
 * Created by S0ptq on 2018/2/26.
 */

public class AppItem {
    private String name;
    private Drawable image;
    private String pakageName;

    public AppItem(String name, Drawable image, String pakageName) {
        this.name = name;
        this.image = image;
        this.pakageName = pakageName;
    }

    public AppItem() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }


    public String getPakageName() {
        return pakageName;
    }

    public void setPakageName(String appName) {
        this.pakageName = appName;
    }
}
