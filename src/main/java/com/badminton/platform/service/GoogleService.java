package com.badminton.platform.service;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleService {

    private static final String CLIENT_ID = "248980513097-e1btvc6qb7dj7e9j0go4bn8eilr2rp5v.apps.googleusercontent.com";

public GoogleIdToken.Payload verify(String idTokenString) throws Exception {

    var transport = GoogleNetHttpTransport.newTrustedTransport(); 

    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
            transport,
            GsonFactory.getDefaultInstance()
    )
        .setAudience(Collections.singletonList(CLIENT_ID))
        .build();

    GoogleIdToken idToken = verifier.verify(idTokenString);

    if (idToken == null) {
        throw new RuntimeException("Invalid ID token");
    }

    return idToken.getPayload();
}

}