package soptqs.paste.database;

import org.litepal.crud.DataSupport;

/**
 * Created by S0ptq on 2018/2/26.
 */

public class BlackListSaves extends DataSupport {
    private int id;
    private String pakageName;

    public int getId() {
        return id;
    }

    public void setId(int mid) {
        this.id = mid;
    }

    public String getPakageName() {
        return pakageName;
    }

    public void setPakageName(String mpakageName) {
        this.pakageName = mpakageName;
    }
}
