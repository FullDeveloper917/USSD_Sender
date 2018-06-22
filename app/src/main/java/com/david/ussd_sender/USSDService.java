package com.david.ussd_sender;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Collections;
import java.util.List;

public class USSDService extends AccessibilityService {

    public static final String TAG = USSDService.class.getSimpleName();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent");

//        AccessibilityNodeInfo source = event.getSource();
//        /* if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !event.getClassName().equals("android.app.AlertDialog")) { // android.app.AlertDialog is the standard but not for all phones  */
//        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !String.valueOf(event.getClassName()).contains("AlertDialog")) {
//            return;
//        }
//        if(event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && (source == null || !source.getClassName().equals("android.widget.TextView"))) {
//            return;
//        }
//        if(event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && TextUtils.isEmpty(source.getText())) {
//            return;
//        }
//
//        List<CharSequence> eventText;
//
//        if(event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//            eventText = event.getText();
//        } else {
//            eventText = Collections.singletonList(source.getText());
//        }

//        String text = processUSSDText(eventText);
//
//        if( TextUtils.isEmpty(text) ) return;
//
//        // Close dialog
//        performGlobalAction(GLOBAL_ACTION_BACK); // This works on 4.1+ only
//
//        Log.d(TAG, text);
//        // Handle USSD response here

        String text = event.getText().toString();

        if (event.getClassName().equals("android.app.AlertDialog")) {
            performGlobalAction(GLOBAL_ACTION_BACK);
            Log.d(TAG, text);
            Intent intent = new Intent(Consts.USSD_CONTENT);
            intent.putExtra(Consts.USSD_MESSAGE, text);
            sendBroadcast(intent);
            // write a broad cast receiver and call sendbroadcast() from here, if you want to parse the message for balance, date
        }

    }

//    private String processUSSDText(List<CharSequence> eventText) {
//        for (CharSequence s : eventText) {
//            String text = String.valueOf(s);
//            // Return text if text is the expected ussd response
//            if( true ) {
//                return text;
//            }
//        }
//        return null;
//    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.packageNames = new String[]{"com.android.phone"};
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }
}
