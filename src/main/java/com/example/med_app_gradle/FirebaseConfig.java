package com.example.med_app_gradle;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() {
        try {
            InputStream serviceAccount = getClass().getResourceAsStream("/serviceAccountKey.json");

            if (serviceAccount == null) {
                throw new IllegalStateException("Brak pliku serviceAccountKey.json");
            }

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            return FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            throw new RuntimeException("Błąd inicjalizacji Firebase", e);
        }
    }
}
