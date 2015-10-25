package com.alorma.github.ui.fragment.donate;

import java.text.DecimalFormat;

/**
 * Created by a557114 on 25/07/2015.
 */
public class DonateItem {

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
        return euro + " " + df.format(quantity);
    }
}
