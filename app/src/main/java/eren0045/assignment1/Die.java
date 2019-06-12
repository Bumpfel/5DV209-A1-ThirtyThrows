package eren0045.assignment1;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Random;

class Die implements Parcelable {

    private int value = 0;
    private Random random = new Random();
    private boolean disabled = false;

    public Die() {
        throwDie();
    }

    public void throwDie() {
        value = random.nextInt(6) + 1;
    }

    public void toggleDie() {
        disabled = !disabled;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public int getValue() {
        return value;
    }


    //Parcelable stuff
    private int mData;

    // used to write object to Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mData);
    }

    // constructor used by Creator
    private Die(Parcel in) {
        mData = in.readInt();
    }

    // used to restore object from parcel
    public static final Creator<Die> CREATOR = new Creator<Die>() {

        public Die createFromParcel(Parcel in) {
            return new Die(in);
        }

        public Die[] newArray(int size) {
            return new Die[size];
        }
    };

    // usage optional (?) must be implemented tho
    public int describeContents() {
        return 0;
    }

}
