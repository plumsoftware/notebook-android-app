package ru.plumsoftware.notebook.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.common.InitializationListener;
import com.yandex.mobile.ads.common.MobileAds;
import com.yandex.mobile.ads.nativeads.MediaView;
import com.yandex.mobile.ads.nativeads.NativeAd;
import com.yandex.mobile.ads.nativeads.NativeAdEventListener;
import com.yandex.mobile.ads.nativeads.NativeAdException;
import com.yandex.mobile.ads.nativeads.NativeAdRequestConfiguration;
import com.yandex.mobile.ads.nativeads.NativeAdView;
import com.yandex.mobile.ads.nativeads.NativeAdViewBinder;
import com.yandex.mobile.ads.nativeads.NativeBulkAdLoadListener;
import com.yandex.mobile.ads.nativeads.NativeBulkAdLoader;

import java.util.List;

import ru.plumsoftware.notebook.fragments.NotepadFragment;
import ru.plumsoftware.notebook.dialogs.ProgressDialog;
import ru.plumsoftware.notebook.R;

public class MainMenuActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private Context context;
    private ProgressDialog progressDialog;
    private LinearLayout l1, l2;

    private CardView adsCard;
    private NativeAdView mNativeAdView;
    private MediaView mediaView;
    private TextView age;
    private TextView bodyView;
    private TextView call_to_action;
    private TextView priceView;
    private TextView storeView;
    private TextView tvHeadline;
    private TextView warning;
    private TextView domain;
    private ImageView favicon;
    private ImageView imageViewFeedback;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);

        //Native ads
        mNativeAdView = (NativeAdView) findViewById(R.id.nativeAdView);
        mediaView = (MediaView) findViewById(R.id.media);
        age = (TextView) findViewById(R.id.age);
        bodyView = (TextView) findViewById(R.id.tvAdvertiser);
        call_to_action = (TextView) findViewById(R.id.btnVisitSite);
        domain = (TextView) findViewById(R.id.textViewDomain);
        favicon = (ImageView) findViewById(R.id.adsPromo);
        imageViewFeedback = (ImageView) findViewById(R.id.imageViewFeedback);
        priceView = (TextView) findViewById(R.id.priceView);
        storeView = (TextView) findViewById(R.id.storeView);
        tvHeadline = (TextView) findViewById(R.id.tvHeadline);
        warning = (TextView) findViewById(R.id.textViewWarning);
        adsCard = (CardView) findViewById(R.id.cardView2);

//        Variables
        context = MainMenuActivity.this;
        progressDialog = new ProgressDialog(context);

        l1 = (LinearLayout) findViewById(R.id.l1);
        l2 = (LinearLayout) findViewById(R.id.l2);

//        Get instance
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

//        ADS
        MobileAds.initialize(context, new InitializationListener() {
            @Override
            public void onInitializationCompleted() {

            }
        });
        progressDialog.showDialog();
        adsCard.setVisibility(View.GONE);
        final NativeBulkAdLoader nativeBulkAdLoader = new NativeBulkAdLoader(context);
        nativeBulkAdLoader.setNativeBulkAdLoadListener(new NativeBulkAdLoadListener() {
            @Override
            public void onAdsLoaded(@NonNull final List<NativeAd> nativeAds) {
                for (final NativeAd nativeAd : nativeAds) {
                    final NativeAdViewBinder nativeAdViewBinder = new NativeAdViewBinder.Builder(mNativeAdView)
                            .setAgeView(age)
                            .setBodyView(bodyView)
                            .setCallToActionView(call_to_action)
                            .setDomainView(domain)
                            //.setFaviconView(notesViewHolder.favicon)
                            .setFeedbackView(imageViewFeedback)
                            .setIconView(favicon)
                            .setMediaView(mediaView)
                            .setPriceView(priceView)
                            //.setRatingView((MyRatingView) findViewById(R.id.rating))
                            //.setReviewCountView((TextView) findViewById(R.id.review_count))
                            .setSponsoredView(storeView)
                            .setTitleView(tvHeadline)
                            .setWarningView(warning)
                            .build();

                    try {
                        nativeAd.bindNativeAd(nativeAdViewBinder);
                        nativeAd.setNativeAdEventListener(new NativeAdEventListener() {
                            @Override
                            public void onAdClicked() {

                            }

                            @Override
                            public void onLeftApplication() {

                            }

                            @Override
                            public void onReturnedToApplication() {

                            }

                            @Override
                            public void onImpression(@Nullable ImpressionData impressionData) {

                            }
                        });
                        mNativeAdView.setVisibility(View.VISIBLE);
                    } catch (final NativeAdException exception) {
                        Toast.makeText(context, exception.toString(), Toast.LENGTH_LONG).show();
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onAdsFailedToLoad(@NonNull final AdRequestError error) {
                adsCard.setVisibility(View.GONE);
            }
        });

        final NativeAdRequestConfiguration nativeAdRequestConfiguration = new NativeAdRequestConfiguration.Builder("R-M-1957919-1").build();
        //final NativeAdRequestConfiguration nativeAdRequestConfiguration = new NativeAdRequestConfiguration.Builder("R-M-1769412-1").build();
        //final NativeAdRequestConfiguration nativeAdRequestConfiguration = new NativeAdRequestConfiguration.Builder("R-M-1742395-1").build();
        nativeBulkAdLoader.loadAds(nativeAdRequestConfiguration, 1);

//        Clickers
        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new NotepadFragment()).commit();
            }
        });
    }
}