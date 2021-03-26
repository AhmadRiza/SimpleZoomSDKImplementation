package com.github.ahmadriza.simplezoomsdkimplementation;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import us.zoom.sdk.MeetingActivity;

public class InMeetingActivity extends MeetingActivity {

    private static final String TAG = InMeetingActivity.class.getSimpleName();

    @Override
    protected boolean isSensorOrientationEnabled() {
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        muteAudio(true);
        muteVideo(true);

        Log.i(TAG, "onCreate: activity zoom meeting created");
    }

    @Override
    public void onBackPressed() {
        onClickLeave();
    }

    @Override
    protected void onMyAudioTypeChanged() {
        super.onMyAudioTypeChanged();
    }
}
