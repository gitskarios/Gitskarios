package com.alorma.github.sdk.bean.dto.response.events.payload;

import android.os.Parcel;
import android.os.Parcelable;
import com.alorma.github.sdk.bean.dto.response.Commit;
import java.util.List;

/**
 * Created by Bernat on 03/10/2014.
 */
public class PushEventPayload extends GithubEventPayload implements Parcelable {
  public static final Creator<PushEventPayload> CREATOR = new Creator<PushEventPayload>() {
    public PushEventPayload createFromParcel(Parcel source) {
      return new PushEventPayload(source);
    }

    public PushEventPayload[] newArray(int size) {
      return new PushEventPayload[size];
    }
  };
  public long push_id;
  public int size;
  public int distinct_size;
  public String ref;
  public String head;
  public String before;
  public List<Commit> commits;

  public PushEventPayload() {
  }

  protected PushEventPayload(Parcel in) {
    super(in);
    this.push_id = in.readLong();
    this.size = in.readInt();
    this.distinct_size = in.readInt();
    this.ref = in.readString();
    this.head = in.readString();
    this.before = in.readString();
    this.commits = in.createTypedArrayList(Commit.CREATOR);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    super.writeToParcel(dest, flags);
    dest.writeLong(this.push_id);
    dest.writeInt(this.size);
    dest.writeInt(this.distinct_size);
    dest.writeString(this.ref);
    dest.writeString(this.head);
    dest.writeString(this.before);
    dest.writeTypedList(commits);
  }
}
