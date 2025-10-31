package aydin.firebasedemo;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.cloud.firestore.DocumentReference;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WelcomeController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    void handleRegister() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        if(email.isEmpty() || password.isEmpty()) {
            showAlert("Fields required", "Please enter both email and password.");
            return;
        }
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setEmailVerified(false)
                .setPassword(password)
                .setDisplayName(email)
                .setDisabled(false);

        try {
            UserRecord user = DemoApp.fauth.createUser(request);
            DocumentReference ref = DemoApp.fstore.collection("userpasswords").document(user.getUid());
            Map<String, Object> data = new HashMap<>();
            data.put("email", email);
            data.put("password", password);
            ref.set(data);

            showAlert("Registration Successful", "User registered: " + email);
            DemoApp.setRoot("primary");
        } catch (FirebaseAuthException | IOException ex) {
            showAlert("Registration Error", ex.getMessage());
        }
    }

    @FXML
    void handleSignIn() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        if(email.isEmpty() || password.isEmpty()) {
            showAlert("Fields required", "Please enter both email and password.");
            return;
        }
        try {
            var docs = DemoApp.fstore.collection("userpasswords")
                    .whereEqualTo("email", email).get().get().getDocuments();
            if(docs.isEmpty() || !docs.get(0).getString("password").equals(password)) {
                showAlert("Sign In Failed", "Invalid credentials.");
                return;
            }
            showAlert("Sign In Successful", "Welcome " + email);
            DemoApp.setRoot("primary");
        } catch (Exception ex) {
            showAlert("Sign In Error", ex.getMessage());
        }
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}
