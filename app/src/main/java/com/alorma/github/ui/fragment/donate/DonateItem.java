package com.alorma.github.ui.fragment.donate;

import android.os.Parcel;
import android.os.Parcelable;
import java.text.DecimalFormat;

public class DonateItem implements Parcelable {

  private String sku;
  private double quantity;

  public DonateItem(String sku, double quantity) {
    this.sku = sku;
    this.quantity = quantity;
  }

  public String getSku() {
    return sku;
  }

  public double getQuantity() {
    return quantity;
  }

  @Override
  public String toString() {
    String euro = "\u20ac";
    DecimalFormat df = new DecimalFormat("#.00");
    return df.format(quantity) + " " + euro;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.sku);
    dest.writeDouble(this.quantity);
  }

  protected DonateItem(Parcel in) {
    this.sku = in.readString();
    this.quantity = in.readDouble();
  }

  public static final Parcelable.Creator<DonateItem> CREATOR = new Parcelable.Creator<DonateItem>() {
    @Override
    public DonateItem createFromParcel(Parcel source) {
      return new DonateItem(source);
    }

    @Override
    public DonateItem[] newArray(int size) {
      return new DonateItem[size];
    }
  };
}
