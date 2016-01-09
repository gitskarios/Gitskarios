package com.alorma.github.ui.activity;

import android.app.Fragment;
import android.content.Intent;

/**
 * Created by bernat.borras on 24/10/15.
 */
public class PurchasesFragment extends Fragment {

    public void checkSku(PurchasesCallback callback) {
        if (callback != null) {
            callback.onMultiAccountPurchaseResult(true);
        }
    }

    public void showDialogBuyMultiAccount() {

    }

    public void finishPurchase(int requestCode, int resultCode, Intent data, PurchasesCallback callback) {
        if (callback != null) {
            callback.onMultiAccountPurchaseResult(true);
        }
    }

    public interface PurchasesCallback {
        void onMultiAccountPurchaseResult(boolean bool);
    }
}
