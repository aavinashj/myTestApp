package com.avinash.testapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class User implements Parcelable {

    public User(){}

    String documentId;
    String name;
    String email;
    String password;
    Date createdOn;
    Date modifiedOn;


    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }


    protected User(android.os.Parcel in) {
        documentId = in.readString();
        name = in.readString();
        email = in.readString();
        password = in.readString();

        createdOn = new Date(in.readLong());
        modifiedOn = new Date(in.readLong());
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(android.os.Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(documentId);
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(password);
        if (createdOn != null)
            parcel.writeLong(createdOn.getTime());
        else
            parcel.writeLong(System.currentTimeMillis());

        if (modifiedOn != null)
            parcel.writeLong(createdOn.getTime());
        else
            parcel.writeLong(System.currentTimeMillis());
    }
}
