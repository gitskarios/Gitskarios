package com.alorma.github.sdk.bean.dto.response.events.payload;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.dto.response.Team;

public class TeamAddEventPayload extends GithubEventPayload implements Parcelable {
  public static final Creator<TeamAddEventPayload> CREATOR = new Creator<TeamAddEventPayload>() {
    public TeamAddEventPayload createFromParcel(Parcel source) {
      return new TeamAddEventPayload(source);
    }

    public TeamAddEventPayload[] newArray(int size) {
      return new TeamAddEventPayload[size];
    }
  };
  public Team team;
  public Repo repository;

  public TeamAddEventPayload() {
  }

  protected TeamAddEventPayload(Parcel in) {
    super(in);
    this.team = in.readParcelable(Team.class.getClassLoader());
    this.repository = in.readParcelable(Repo.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeParcelable(this.team, 0);
    dest.writeParcelable(this.repository, 0);
  }
}
