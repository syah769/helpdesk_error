package com.example.syahril.yourtaskapp.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/*
 * Created by Mahmoud on 3/13/2017.
 */

public class SubFirebaseInstanceIdService extends FirebaseInstanceIdService {

    String TAG = "FCM";

    @Override
    public void onTokenRefresh() {

        // Get updated InstanceID oAuthToken.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed oAuthToken: " + refreshedToken);


    }

}




