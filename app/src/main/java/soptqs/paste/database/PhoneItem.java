package soptqs.paste.database;

/**
 * Created by S0ptq on 2018/2/6.
 */

public class PhoneItem {

    private String errorCode;
    private String province;
    private String city;
    private String sp;

    public PhoneItem(String errorCode, String province, String city, String sp){
        this.errorCode = errorCode;
        this.province = province;
        this.city = city;
        this.sp = sp;
    }

    public String getErrorCode(){
        return errorCode;
    }

    public  String getProvince(){
        return province;
    }

    public String getCity(){
        return city;
    }

    public String getSp(){
        return sp;
    }
}
