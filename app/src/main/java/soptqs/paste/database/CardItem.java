package soptqs.paste.database;

import android.graphics.drawable.Drawable;

/**
 * Created by S0ptq on 2018/2/6.
 */

public class CardItem {

    private String name;
    private String time;
    private Drawable imageid;
    private String content;
    private String translation;
    private String pakageName;
    private int topbg;
    private int words;
    private boolean isTranslated;
    private boolean isImage;

    public CardItem(String name,
                    String time,
                    Drawable imageid,
                    String content,
                    int topbg,
                    int words,
                    boolean isTranslated,
                    String translation,
                    String pakageName,
                    Boolean isImage) {
        this.name = name;
        this.time = time;
        this.imageid = imageid;
        this.content = content;
        this.topbg = topbg;
        this.words = words;
        this.isTranslated = isTranslated;
        this.translation = translation;
        this.pakageName = pakageName;
        this.isImage = isImage;
    }

    public String getName(){
        return name;
    }

    public String getTime(){
        return time;
    }

    public Drawable getImageid(){
        return imageid;
    }

    public String getContent(){
        return content;
    }

    public String getPakageName() {
        return pakageName;
    }

    public int getTopbg(){
        return topbg;
    }

    public int getWords(){
        return words;
    }

    public String getTranslation() {
        return translation;
    }

    public boolean isTranslated() {
        return isTranslated;
    }

    public boolean isImage() {
        return isImage;
    }

}
