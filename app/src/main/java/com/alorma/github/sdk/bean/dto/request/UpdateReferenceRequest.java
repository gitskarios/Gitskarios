package com.alorma.github.sdk.bean.dto.request;

import android.os.Parcel;
import android.os.Parcelable;

public class UpdateReferenceRequest implements Parcelable{
    public static final Parcelable.Creator<UpdateReferenceRequest> CREATOR =
            new Parcelable.Creator<UpdateReferenceRequest>() {
                public UpdateReferenceRequest createFromParcel(Parcel source) {
                    return new UpdateReferenceRequest(source);
                }

                public UpdateReferenceRequest[] newArray(int size) {
                    return new UpdateReferenceRequest[size];
                }
            };
    public String sha;
    public boolean force;

    public UpdateReferenceRequest() {
    }

    protected UpdateReferenceRequest(Parcel in) {
        this.sha = in.readString();
        this.force = in.readByte() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sha);
        dest.writeByte(this.force ? (byte) 1 : (byte) 0);
    }
}
