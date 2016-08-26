package com.alorma.github.ui.fragment.donate;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.R;
import com.alorma.github.ui.fragment.base.BaseFragment;
import com.android.vending.billing.IInAppBillingService;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

public class SkuCardFragment extends BaseFragment {

  private static final String ARG_DONATE = "ARG_DONATE";
  public static final String SKU_BASE_DONATE = "com.alorma.github.donate";

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

  @BindView(R.id.cardView) CardView mCardView;
  @BindView(R.id.skuPrice) TextView skuPrice;
  @BindView(R.id.button) View button;

  private String purchaseId;

  public static SkuCardFragment newInstance(DonateItem donateItem) {
    SkuCardFragment fragment = new SkuCardFragment();
    Bundle args = new Bundle();
    args.putParcelable(ARG_DONATE, donateItem);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    createBillingService();
  }

  private void createBillingService() {
    Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
    serviceIntent.setPackage("com.android.vending");
    getActivity().bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.sku_card_item, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    ButterKnife.bind(this, view);

    if (getArguments() != null) {
      DonateItem item = getArguments().getParcelable(ARG_DONATE);

      if (item != null) {
        mCardView.setMaxCardElevation(mCardView.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);
        skuPrice.setText(item.toString());

        button.setOnClickListener(view1 -> {
          if (tracker != null) {
            tracker.trackEvent("Donate", "selected", item.toString());
          }
          buy(item.getSku());
        });
      }
    }
  }

  public CardView getCardView() {
    return mCardView;
  }

  private void buy(String sku) {
    try {
      if (mService != null) {
        purchaseId = UUID.randomUUID().toString();
        Bundle buyIntentBundle = mService.getBuyIntent(3, getActivity().getPackageName(), sku, "inapp", purchaseId);
        if (buyIntentBundle != null) {
          PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
          if (pendingIntent != null) {
            getActivity().startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0, 0, 0);
          }
        }
      }
    } catch (RemoteException | IntentSender.SendIntentException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 1001) {
      String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");

      if (resultCode == Activity.RESULT_OK) {
        try {
          JSONObject jo = new JSONObject(purchaseData);
          String sku = jo.getString("productId");
          String developerPayload = jo.getString("developerPayload");
          if (developerPayload.equals(purchaseId) && SKU_BASE_DONATE.equals(sku)) {
            giveThanksForBuyDonate();
          }
        } catch (JSONException e) {

          e.printStackTrace();
        }
      }
    }
  }

  private void giveThanksForBuyDonate() {
    Toast.makeText(getActivity(), getString(R.string.thanks_for_donate), Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onDestroy() {
    if (mService != null) {
      getActivity().unbindService(mServiceConn);
    }
    super.onDestroy();
  }
}