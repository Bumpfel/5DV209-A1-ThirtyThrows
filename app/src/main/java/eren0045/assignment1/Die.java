package eren0045.assignment1;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Random;

class Die implements Parcelable {

    private int mValue = 0;
    private Random mRandom = new Random();
    private boolean mEnabled = true;

    Die() {
        roll();
    }

    public String toString() {
        return "" + getValue();
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

    boolean isEnabled() {
        return mEnabled;
    }

    int getValue() {
        return mValue;
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

    // usage optional (?) must be implemented tho
    public int describeContents() {
        return 0;
    }

}
