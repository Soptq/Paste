package soptqs.paste.nodes;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by S0ptq on 2018/3/26.
 */

public class CopyNode implements Parcelable {

    public static final Parcelable.Creator<CopyNode> CREATOR = new Parcelable.Creator<CopyNode>() {
        @Override
        public CopyNode createFromParcel(Parcel source) {
            return new CopyNode(source);
        }

        @Override
        public CopyNode[] newArray(int size) {
            return new CopyNode[size];
        }
    };

    private Rect rect;
    private String string;

    public CopyNode(Rect rect, String string) {
        this.rect = rect;
        this.string = string;
    }

    protected CopyNode(Parcel in) {
        this.rect = in.readParcelable(Rect.class.getClassLoader());
        this.string = in.readString();
    }

    public long caculateSize() {
        return (long) (this.rect.width() * this.rect.height());
    }

    public Rect getRect() {
        return rect;

    }

    public String getString() {
        return string;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.rect, flags);
        dest.writeString(this.string);
    }
}
