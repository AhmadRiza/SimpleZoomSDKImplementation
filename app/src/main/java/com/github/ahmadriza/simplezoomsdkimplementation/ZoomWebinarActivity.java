package com.github.ahmadriza.simplezoomsdkimplementation;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import us.zoom.sdk.FreeMeetingNeedUpgradeType;
import us.zoom.sdk.InMeetingAudioController;
import us.zoom.sdk.InMeetingChatMessage;
import us.zoom.sdk.InMeetingEventHandler;
import us.zoom.sdk.InMeetingService;
import us.zoom.sdk.InMeetingServiceListener;
import us.zoom.sdk.InMeetingVideoController;
import us.zoom.sdk.InviteOptions;
import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.MeetingServiceListener;
import us.zoom.sdk.MeetingStatus;
import us.zoom.sdk.MeetingViewsOptions;
import us.zoom.sdk.ZoomError;
import us.zoom.sdk.ZoomSDK;

public abstract class ZoomWebinarActivity extends AppCompatActivity implements InitAuthSDKCallback, MeetingServiceListener, InMeetingServiceListener {

    private ZoomSDK mZoomSDK;
    private InMeetingService inMeetingService;

    private String TAG = "ZoomSDKActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mZoomSDK = ZoomSDK.getInstance();
        
        InitAuthSDKHelper.getInstance().initSDK(this, this);

        if (mZoomSDK.isInitialized()) {
            showProgressPanel(true);
            mZoomSDK.getMeetingService().addListener(this);
            mZoomSDK.getMeetingSettingsHelper().enable720p(true);
        } else {
            showProgressPanel(false);
        }

    }

    abstract String provideUserName();
    abstract String provideUserID();
    abstract void onSDKReady();
    abstract void onSDKInit();

    protected int joinWebinar(String meetID, String meetPass) {
        int ret = -1;
        MeetingService meetingService = mZoomSDK.getMeetingService();
        if (meetingService.getMeetingStatus() == MeetingStatus.MEETING_STATUS_IDLE) {
            meetingService.addListener(this);

            JoinMeetingOptions opts = new JoinMeetingOptions();
            opts.no_driving_mode = true;
            opts.no_invite = true;
            opts.no_meeting_end_message = true;
            opts.no_titlebar = true;
            opts.no_bottom_toolbar = true;
            opts.no_dial_in_via_phone = true;
            opts.no_dial_out_to_phone = true;
            opts.no_disconnect_audio = true;
            opts.no_share = true;
            opts.no_audio = true;
            opts.no_video = true;
            opts.invite_options = InviteOptions.INVITE_DISABLE_ALL;
            opts.meeting_views_options = MeetingViewsOptions.NO_BUTTON_SHARE;
            opts.no_meeting_error_message = true;

            opts.no_webinar_register_dialog = true;

            JoinMeetingParams params = new JoinMeetingParams();
            params.displayName = provideUserName();
            params.meetingNo = meetID;
            params.password = meetPass;

            ret = meetingService.joinMeetingWithParams(this, params, opts);
        } else {
            meetingService.returnToMeeting(this);
        }

        return ret;
    }


    @Override
    public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
        Log.i(TAG, "onZoomSDKInitializeResult, errorCode=" + errorCode + ", internalErrorCode=" + internalErrorCode);

        if (errorCode != ZoomError.ZOOM_ERROR_SUCCESS) {
            Toast.makeText(this, "Failed to initialize Zoom SDK. Error: " + errorCode + ", internalErrorCode=" + internalErrorCode, Toast.LENGTH_LONG).show();
        } else {
            inMeetingService = mZoomSDK.getInMeetingService();
            inMeetingService.addListener(this);
            showProgressPanel(true);
        }
    }

    @Override
    public void onZoomAuthIdentityExpired() {
        Log.i(TAG, "AuthExpired");
    }

    @Override
    public void onMeetingStatusChanged(MeetingStatus meetingStatus, int i, int i1) {

        if (meetingStatus == MeetingStatus.MEETING_STATUS_INMEETING) {
            InMeetingAudioController inMeetingAudioController =
                    inMeetingService.getInMeetingAudioController();
            inMeetingAudioController.muteMyAudio(true);

            Log.i(TAG, "onCreate: is muted audio setting = "+
                    inMeetingAudioController.isMyAudioMuted());

            InMeetingVideoController inMeetingVideoController =
                    inMeetingService.getInMeetingVideoController();
            inMeetingVideoController.muteMyVideo(true);

            Log.i(TAG, "onCreate: is muted audio setting = "+
                    inMeetingVideoController.isMyVideoMuted());
        } else if (meetingStatus == MeetingStatus.MEETING_STATUS_IN_WAITING_ROOM ||
                meetingStatus == MeetingStatus.MEETING_STATUS_WAITINGFORHOST) {
            Toast.makeText(this, "Please Wait ...", Toast.LENGTH_LONG)
                    .show();
        }


    }
    

    private void showProgressPanel(boolean show) {
        if (show) {
            onSDKReady();
        } else {
            onSDKInit();
        }
    }

    @Override
    public void onBackPressed() {

        if(inMeetingService==null){
            super.onBackPressed();
            return;
        }

        if(inMeetingService.isMeetingConnected()){
            inMeetingService.leaveCurrentMeeting(true);
        }else{
            super.onBackPressed();
        }

    }

    @Override
    public void onDestroy() {

        if(null!= inMeetingService)
        {
            inMeetingService.removeListener(this);
            inMeetingService.leaveCurrentMeeting(true);
        }

        InitAuthSDKHelper.getInstance().reset();

        super.onDestroy();

    }


    @Override
    public void onMeetingNeedPasswordOrDisplayName(boolean b, boolean b1, InMeetingEventHandler inMeetingEventHandler) {
    }

    @Override
    public void onWebinarNeedRegister() {

    }

    @Override
    public void onJoinWebinarNeedUserNameAndEmail(InMeetingEventHandler inMeetingEventHandler) {
        inMeetingEventHandler.setRegisterWebinarInfo(provideUserName(), provideUserID(), false);
    }

    @Override
    public void onMeetingNeedColseOtherMeeting(InMeetingEventHandler inMeetingEventHandler) {

    }

    @Override
    public void onMeetingFail(int i, int i1) {

    }

    @Override
    public void onMeetingLeaveComplete(long l) {

    }

    @Override
    public void onMeetingUserJoin(List<Long> list) {

    }

    @Override
    public void onMeetingUserLeave(List<Long> list) {

    }

    @Override
    public void onMeetingUserUpdated(long l) {

    }

    @Override
    public void onMeetingHostChanged(long l) {

    }

    @Override
    public void onMeetingCoHostChanged(long l) {

    }

    @Override
    public void onActiveVideoUserChanged(long l) {

    }

    @Override
    public void onActiveSpeakerVideoUserChanged(long l) {

    }

    @Override
    public void onSpotlightVideoChanged(boolean b) {

    }

    @Override
    public void onUserVideoStatusChanged(long l) {

    }

    @Override
    public void onUserVideoStatusChanged(long l, VideoStatus videoStatus) {

    }

    @Override
    public void onUserNetworkQualityChanged(long l) {

    }

    @Override
    public void onMicrophoneStatusError(InMeetingAudioController.MobileRTCMicrophoneError mobileRTCMicrophoneError) {

    }

    @Override
    public void onUserAudioStatusChanged(long l) {

    }

    @Override
    public void onUserAudioStatusChanged(long l, AudioStatus audioStatus) {

    }

    @Override
    public void onHostAskUnMute(long l) {

    }

    @Override
    public void onHostAskStartVideo(long l) {

    }

    @Override
    public void onUserAudioTypeChanged(long l) {

    }

    @Override
    public void onMyAudioSourceTypeChanged(int i) {

    }

    @Override
    public void onLowOrRaiseHandStatusChanged(long l, boolean b) {

    }

    @Override
    public void onMeetingSecureKeyNotification(byte[] bytes) {

    }

    @Override
    public void onChatMessageReceived(InMeetingChatMessage inMeetingChatMessage) {

    }

    @Override
    public void onSilentModeChanged(boolean b) {

    }

    @Override
    public void onFreeMeetingReminder(boolean b, boolean b1, boolean b2) {

    }

    @Override
    public void onMeetingActiveVideo(long l) {

    }

    @Override
    public void onSinkAttendeeChatPriviledgeChanged(int i) {

    }

    @Override
    public void onSinkAllowAttendeeChatNotification(int i) {

    }

    @Override
    public void onUserNameChanged(long l, String s) {

    }

    @Override
    public void onFreeMeetingNeedToUpgrade(FreeMeetingNeedUpgradeType freeMeetingNeedUpgradeType, String s) {

    }

    @Override
    public void onFreeMeetingUpgradeToGiftFreeTrialStart() {

    }

    @Override
    public void onFreeMeetingUpgradeToGiftFreeTrialStop() {

    }

    @Override
    public void onFreeMeetingUpgradeToProMeeting() {

    }

    @Override
    public void onClosedCaptionReceived(String s) {

    }
}
