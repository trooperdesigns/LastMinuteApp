package trooperdesigns.com.lastminute;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

// Contact class to hold contact information
public class Contact implements Parcelable {

    private boolean isChecked;
    private String name;
    private String phone;

    public Contact(String name, String phone, boolean isChecked) {
        this.name = name;
        this.phone = phone;
        this.isChecked = isChecked;
    }

    // constructor for reconstructing from parcel
    public Contact(Parcel parcel) {
        this.name = parcel.readString();
        this.phone = parcel.readString();

    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getPhone(){
        return phone;
    }

    public void setIsChecked(boolean isChecked){
        this.isChecked = isChecked;
    }

    public boolean getIsChecked(){
        return isChecked;
    }

    public void toggle(){
        this.isChecked = !isChecked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phone);
    }

    public void readFromParcel(Parcel in){
        name = in.readString();
        phone = in.readString();
    }

    // Method to recreate a Contact from a Parcel
    public static Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {

        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }

    };
}
