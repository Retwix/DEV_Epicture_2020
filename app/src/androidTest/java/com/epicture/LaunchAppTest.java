package com.epicture;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)

public class LaunchAppTest {

    private String clientId = "0be29f86c5c606f";
    private String responseType = "token";
    private String baseUrl = "https://api.imgur.com/oauth2/authorize";

    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.epicture", appContext.getPackageName());
    }

    @Test
    public void onCreate() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl + "?client_id=" + clientId + "&response_type=" + responseType));
        assertNotNull(intent);
    }

}

