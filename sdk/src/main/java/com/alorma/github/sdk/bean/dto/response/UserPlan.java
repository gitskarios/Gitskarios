package com.alorma.github.sdk.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

public class UserPlan implements Parcelable {

    /** serialVersionUID */
    private static final long serialVersionUID = 4759542049129654659L;

    private long collaborators;

    private long privateRepos;

    private long space;

    private String name;

    /**
     * @return contributors
     */
    public long getCollaborators() {
        return collaborators;
    }

    /**
     * @param collaborators
     * @return this user plan
     */
    public UserPlan setCollaborators(long collaborators) {
        this.collaborators = collaborators;
        return this;
    }

    /**
     * @return privateRepos
     */
    public long getPrivateRepos() {
        return privateRepos;
    }

    /**
     * @param privateRepos
     * @return this user plan
     */
    public UserPlan setPrivateRepos(long privateRepos) {
        this.privateRepos = privateRepos;
        return this;
    }

    /**
     * @return space
     */
    public long getSpace() {
        return space;
    }

    /**
     * @param space
     * @return this user plan
     */
    public UserPlan setSpace(long space) {
        this.space = space;
        return this;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     * @return this user plan
     */
    public UserPlan setName(String name) {
        this.name = name;
        return this;
    }

    protected UserPlan(Parcel in) {
        collaborators = in.readLong();
        privateRepos = in.readLong();
        space = in.readLong();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(collaborators);
        dest.writeLong(privateRepos);
        dest.writeLong(space);
        dest.writeString(name);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserPlan> CREATOR = new Parcelable.Creator<UserPlan>() {
        @Override
        public UserPlan createFromParcel(Parcel in) {
            return new UserPlan(in);
        }

        @Override
        public UserPlan[] newArray(int size) {
            return new UserPlan[size];
        }
    };
}