package com.vipercn.viper4android_v2.misc;

import java.io.File;
import java.io.Serializable;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class ItemInfo implements Parcelable, Serializable {

    private String mUserLogo;
    private String mUser;
    private String mIntroduce;
    private String mPicture;
    private String mConfigure;

    private ItemInfo() {
        // Use the builder
    }

    private ItemInfo(Parcel in) {
        readFromParcel(in);
    }

    public String getUserLogo() {
        return mUserLogo;
    }

    public String getUser() {
        return mUser;
    }

    public String getIntroduce() {
        return mIntroduce;
    }

    public String getPicture() {
        return mPicture;
    }

    public String getConfigure() {
        return mConfigure;
    }

    public static final Parcelable.Creator<ItemInfo> CREATOR = new Parcelable.Creator<ItemInfo>() {
        public ItemInfo createFromParcel(Parcel in) {
            return new ItemInfo(in);
        }

        public ItemInfo[] newArray(int size) {
            return new ItemInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUserLogo);
        dest.writeString(mUser);
        dest.writeString(mIntroduce);
        dest.writeString(mPicture);
        dest.writeString(mConfigure);
    }

    private void readFromParcel(Parcel in) {
        mUserLogo = in.readString();
        mUser = in.readString();
        mIntroduce = in.readString();
        mPicture = in.readString();
        mConfigure = in.readString();
    }

    public static class Builder {
        private String mUserLogo;
        private String mUser;
        private String mIntroduce;
        private String mPicture;
        private String mConfigure;

        public Builder setUserLogo(String logo) {
            mUserLogo = logo;
            return this;
        }

        public Builder setUser(String user) {
            mUser = user;
            return this;
        }

        public Builder setIntroduce(String introduce) {
            mIntroduce = introduce;
            return this;
        }

        public Builder setPicture(String picture) {
            mPicture = picture;
            return this;
        }

        public Builder setConfigure(String configure) {
            mConfigure = configure;
            return this;
        }

        public ItemInfo build() {
            ItemInfo info = new ItemInfo();
            info.mUserLogo = mUserLogo;
            info.mUser = mUser;
            info.mIntroduce = mIntroduce;
            info.mPicture = mPicture;
            info.mConfigure = mConfigure;
            return info;
        }
    }
}
