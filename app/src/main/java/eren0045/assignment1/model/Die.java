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

    /**
     * Rolls the die if die is enabled
     */
    void roll() {
        if(mEnabled)
            mValue = mRandom.nextInt(6) + 1;
    }

    /**
     * enables the die and rolls it
     */
    void reset() {
        mEnabled = true;
        roll();
    }

    /**
     * toggles whether the die can be rolled or not
     */
    void toggleDie() {
        mEnabled = !mEnabled;
    }

    /**
     * @return whether die is enabled or not
     */
    public boolean isEnabled() {
        return mEnabled;
    }

    /**
     * @return the die value
     */
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
        out.writeInt(mEnabled ? 1 : 0);
    }

    // constructor used by Creator
    private Die(Parcel in) {
        mValue = in.readInt();
        mEnabled = in.readInt() == 1;
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
