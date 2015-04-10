package com.dev.callofbeer.authentication.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.dev.callofbeer.authentication.utils.CobAuthenticator;

public class CobAuthenticatorService extends Service {
    public CobAuthenticatorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        CobAuthenticator authenticator = new CobAuthenticator(this);
        return authenticator.getIBinder();
    }
}
