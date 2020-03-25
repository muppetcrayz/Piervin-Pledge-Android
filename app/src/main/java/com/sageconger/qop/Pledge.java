package com.sageconger.qop;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Pledge implements Parcelable {

    String id;
    String name;
    String description;
    String amount;
    String frequency;
    Campaign campaign;
    Date date;
    String creator;

    Pledge() {

    }

    Pledge(String id, String name, String description, String amount, String frequency, Date date, String creator, Campaign campaign) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.frequency = frequency;
        this.date = date;
        this.creator = creator;
        this.campaign = campaign;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public String getCreator() {
        return creator;
    }

    public String getDescription() {
        return description;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    protected Pledge(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        amount = in.readString();
        frequency = in.readString();
        long tmpDate = in.readLong();
        date = tmpDate != -1 ? new Date(tmpDate) : null;
        creator = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(amount);
        dest.writeString(frequency);
        dest.writeLong(date != null ? date.getTime() : -1L);
        dest.writeString(creator);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Pledge> CREATOR = new Parcelable.Creator<Pledge>() {
        @Override
        public Pledge createFromParcel(Parcel in) {
            return new Pledge(in);
        }

        @Override
        public Pledge[] newArray(int size) {
            return new Pledge[size];
        }
    };
}