package com.nadajp.littletalkers.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Kid implements Parcelable {
    public static final Parcelable.Creator<Kid> CREATOR = new Parcelable.Creator<Kid>() {
        public Kid createFromParcel(Parcel source) {
            return new Kid(source);
        }

        public Kid[] newArray(int size) {
            return new Kid[size];
        }
    };

    private int mId;
    private String mName;
    private String mLocation;
    private String mLanguage;
    private String mPictureUri;
    private long mBirthdate;
    private int mNumOfWords;
    private int mNumOfQAs;

    public Kid() {
    }

    public Kid(int id, String name, String location, String language) {
        mId = id;
        mName = name;
        mLocation = location;
        mLanguage = language;
        mNumOfWords = 0;
        mNumOfQAs = 0;
    }

    public Kid(int id, String name, String location, String language, String pictureUri, long birthdate){
        mId = id;
        mName = name;
        mLocation = location;
        mLanguage = language;
        mPictureUri = pictureUri;
        mBirthdate = birthdate;
        mNumOfWords = 0;
        mNumOfQAs = 0;
    }

    protected Kid(Parcel in) {
        this.mId = in.readInt();
        this.mName = in.readString();
        this.mLocation = in.readString();
        this.mLanguage = in.readString();
        this.mPictureUri = in.readString();
        this.mBirthdate = in.readLong();
        this.mNumOfWords = in.readInt();
        this.mNumOfQAs = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mName);
        dest.writeString(this.mLocation);
        dest.writeString(this.mLanguage);
        dest.writeString(this.mPictureUri);
        dest.writeLong(this.mBirthdate);
        dest.writeInt(this.mNumOfWords);
        dest.writeInt(this.mNumOfQAs);
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setLanguage(String language) {
        this.mLanguage = language;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        this.mLocation = location;
    }

    public String getPictureUri() { return mPictureUri; }

    public void setPictureUri(String pictureUri){ this.mPictureUri = pictureUri; }

    public long getBirthdate() { return mBirthdate; }

    public void setBirthdate(long birthdate) { this.mBirthdate = birthdate; }

    public int getNumOfWords() { return mNumOfWords; }

    public void setNumOfWords(int n) { this.mNumOfWords = n; }

    public int getNumOfQAs() { return mNumOfQAs; }

    public void setNumOfQAs(int n) { this.mNumOfQAs = n; }

}
