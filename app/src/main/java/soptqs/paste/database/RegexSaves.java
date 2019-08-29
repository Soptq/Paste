package soptqs.paste.database;

import org.litepal.crud.DataSupport;

/**
 * Created by S0ptq on 2018/3/25.
 */

public class RegexSaves extends DataSupport {
    private int id;
    private String regex;
    private Boolean isEnable;

    public Boolean getEnable() {
        return isEnable;
    }

    public void setEnable(Boolean enable) {
        isEnable = enable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

}
