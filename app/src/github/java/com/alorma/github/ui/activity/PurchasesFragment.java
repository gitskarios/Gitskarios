package com.alorma.github.ui.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

/**
 * Created by bernat.borras on 24/10/15.
 */
public class PurchasesFragment extends Fragment {

    private static final String SKU_MULTI_ACCOUNT = "com.alorma.github.multiaccount";

    private IInAppBillingService mService;
    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    private String purchaseId;

    private PurchasesCallback purchasesCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createBillingService();
    }

    private void createBillingService() {
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        if (mService == null) {
            getActivity().bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
        }
    }

    public void checkSku(PurchasesCallback purchasesCallback) {
        this.purchasesCallback = purchasesCallback;
        SKUTask task = new SKUTask();
        task.execute(SKU_MULTI_ACCOUNT);
    }

    public void finishPurchase(int requestCode, int resultCode, Intent data, PurchasesCallback callback) {
        if (requestCode == 1001) {
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            if (resultCode == Activity.RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    String developerPayload = jo.getString("developerPayload");
                    if (callback != null) {
                        boolean purchased = developerPayload.equals(purchaseId) && SKU_MULTI_ACCOUNT.equals(sku);
                        callback.onMultiAccountPurchaseResult(purchased);
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            } else {
                int response_code = data.getIntExtra("RESPONSE_CODE", 0);
                if (callback != null) {
                    callback.onMultiAccountPurchaseResult(response_code == 6);
                }
            }
        }
    }

    public void showDialogBuyMultiAccount() {
        try {
            purchaseId = UUID.randomUUID().toString();
            Bundle buyIntentBundle = mService.getBuyIntent(3, getActivity().getPackageName(), SKU_MULTI_ACCOUNT, "inapp", purchaseId);
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            getActivity().startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0, 0, 0);
        } catch (RemoteException | IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    public interface PurchasesCallback {
        void onMultiAccountPurchaseResult(boolean multiAccountPurchased);
    }

    private class SKUTask extends AsyncTask<String, Void, Bundle> {

        @Override
        protected Bundle doInBackground(String... strings) {
            if (strings != null) {
                ArrayList<String> skuList = new ArrayList<>();
                Collections.addAll(skuList, strings);
                Bundle querySkus = new Bundle();
                querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

                try {
                    return mService.getPurchases(3, getActivity().getPackageName(), "inapp", null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bundle ownedItems) {
            super.onPostExecute(ownedItems);
            if (ownedItems != null) {
                int response = ownedItems.getInt("RESPONSE_CODE");
                if (response == 0) {
                    ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");

                    if (ownedSkus != null && purchasesCallback != null) {
                        purchasesCallback.onMultiAccountPurchaseResult(!ownedSkus.isEmpty());
                    }
                }
            }
        }
    }
}
