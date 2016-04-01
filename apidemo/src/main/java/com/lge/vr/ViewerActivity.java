package com.lge.vr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.lge.friendsCamera.R;

public class ViewerActivity extends CardboardActivity {
    private final String TAG = "ViewerActivity";

    private CardboardView.StereoRenderer mRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_layout);

        if (getIntent() != null && getIntent().getData() != null) {
            Uri uri = getIntent().getData();
            String type = getIntent().getType();
            if (type.contains("image")) {
                type = "Image";
                mRenderer = new ImageRenderer(this, uri);
            } else if (type.contains("video")) {
                type = "Video";
                mRenderer = new VideoRenderer(this, uri);
            } else {
                finish();
            }
            CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
            cardboardView.setSettingsButtonEnabled(false);
            cardboardView.setRestoreGLStateEnabled(false);
            cardboardView.setRenderer(mRenderer);
            setCardboardView(cardboardView);
            getCardboardView().setVRModeEnabled(false);
            openDialog(type);
        } else {
            finish();
        }
    }

    public void openDialog(String type) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Do you want to display the " + type +" in Cardboard mode ? ");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                getCardboardView().setVRModeEnabled(true);
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getCardboardView().setVRModeEnabled(false);
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRenderer instanceof VideoRenderer)
            ((VideoRenderer) mRenderer).pauseMediaPlayer();
    }
}
