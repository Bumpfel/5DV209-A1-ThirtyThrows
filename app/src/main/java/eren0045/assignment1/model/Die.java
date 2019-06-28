package eren0045.assignment1.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Random;

public class Die implements Parcelable {

    private int mValue;
    private Random mRandom = new Random();
    private boolean mEnabled = true;

    Die() {
        roll();
    }

    void setDie(int n) { // TODO for debugging
        mValue = n;
    } //TODO temp

    void roll() {
        if(mEnabled)
            mValue = mRandom.nextInt(6) + 1;
    }

    void reset() {
        mEnabled = true;
        roll();
    }

    void toggleDie() {
        mEnabled = !mEnabled;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public int getValue() {
        return mValue;
    }

    public String toString() {
        return "" + getValue();
    }


    /* **************** */
    /* Parcelable stuff */
    /* **************** */

    // used to write object to Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mValue);
    }

    // constructor used by Creator
    private Die(Parcel in) {
        mValue = in.readInt();
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

    public int describeContents() {
        return 0;
    }

}
