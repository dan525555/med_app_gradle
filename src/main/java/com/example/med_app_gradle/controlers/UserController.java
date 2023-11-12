package com.example.med_app_gradle.controlers;

 import com.example.med_app_gradle.models.UserDataDto;
 import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @PostMapping("/check")
    public ResponseEntity<String> checkRegistrationConditions(
            @RequestParam String email,
            @RequestParam String pesel,
            @RequestParam String phoneNumber) {
        try {
            // Sprawdź warunki rejestracji
            Firestore firestore = FirestoreClient.getFirestore();

            // Sprawdź, czy istnieje użytkownik o tym samym numerze telefonu, adresie email, peselu i roli pacjenta
            boolean emailExists = checkIfExists(firestore, "Users", "email", email);
            boolean phoneNumberExists = checkIfExists(firestore, "Users", "phoneNumber", phoneNumber);
            boolean peselExists = checkIfExists(firestore, "Users", "pesel", pesel, "role", "pacjent");

            if (emailExists) {
                return ResponseEntity.badRequest().body("Podany adres email jest już zarejestrowany.");
            }

            if (phoneNumberExists) {
                return ResponseEntity.badRequest().body("Podany numer telefonu jest już zarejestrowany.");
            }

            if (peselExists) {
                return ResponseEntity.badRequest().body("Podany pesel jest już zarejestrowany.");
            }

            return ResponseEntity.ok("Warunki rejestracji są spełnione. Możesz przejść do rejestracji.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Wystąpił błąd podczas sprawdzania warunków rejestracji.");
        }
    }


    @PostMapping("/addPatient")
    public ResponseEntity<String> addUserData(@RequestBody UserDataDto userDataDto) {
        try {
            // Zapisz dane użytkownika w Firestore
            Firestore firestore = FirestoreClient.getFirestore();
            Map<String, Object> userData = new HashMap<>();
            userData.put("firstName", userDataDto.getFirstName());
            userData.put("lastName", userDataDto.getLastName());
            userData.put("pesel", userDataDto.getPesel());
            userData.put("phoneNumber", userDataDto.getPhoneNumber());
            userData.put("role", "pacjent");
            firestore.collection("Users").document(userDataDto.getUserId()).set(userData);

            return ResponseEntity.ok("Dane użytkownika zostały dodane do Firestore.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Wystąpił błąd podczas dodawania danych do Firestore.");
        }
    }

    private boolean checkIfExists(Firestore firestore, String collection, String field, String value) throws Exception {
        CollectionReference users = firestore.collection(collection);
        Query query = users.whereEqualTo(field, value);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot queryResult = querySnapshot.get();
        return !queryResult.isEmpty();
    }

    private boolean checkIfExists(Firestore firestore, String collection, String field1, String value1, String field2, String value2) throws Exception {
        CollectionReference users = firestore.collection(collection);
        Query query = users.whereEqualTo(field1, value1).whereEqualTo(field2, value2);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot queryResult = querySnapshot.get();
        return !queryResult.isEmpty();
    }
}
