package soptqs.paste.database;

import org.litepal.crud.DataSupport;

/**
 * Created by S0ptq on 2018/2/6.
 */

public class ClipSaves extends DataSupport {

    private int id;

    private String content;

    private String time;

    private String pakageName;

    private boolean isCollection;

    private boolean isTranslated;

    private String translation;

    private boolean isShared;

    private boolean isImage;


    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getPakageName() {
        return pakageName;
    }

    public void setPakageName(String pakageName){
        this.pakageName = pakageName;
    }

    public Boolean isCollection() {
        return isCollection;
    }

    public Boolean isTranslated() {
        return isTranslated;
    }

    public Boolean isShared() {
        return isShared;
    }

    public Boolean isImage() {
        return isImage;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public void setCollection(Boolean isCollection){
        this.isCollection = isCollection;
    }

    public void setTranslated(Boolean isTranslated) {
        this.isTranslated = isTranslated;
    }

    public void setShared(Boolean isShared) {
        this.isShared = isShared;
    }

    public void setImage(Boolean isImage) {
        this.isImage = isImage;
    }
}
