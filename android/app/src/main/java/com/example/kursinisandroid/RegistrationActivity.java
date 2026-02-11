package com.example.kursinisandroid;

import static com.example.kursinisandroid.Utils.Constants.CREATE_BASIC_USER_URL;
import static com.example.kursinisandroid.Utils.Constants.CREATE_DRIVER_URL;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kursinisandroid.Utils.RestOperations;
import com.example.kursinisandroid.models.Customer;
import com.example.kursinisandroid.models.Driver;
import com.example.kursinisandroid.models.VehicleType;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegistrationActivity extends AppCompatActivity {

    private CheckBox isDriverCheckbox;
    private TextView driverFieldsLabel;
    private EditText licenceField;
    private EditText birthDateField;
    private TextView vehicleTypeLabel;
    private Spinner vehicleTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        isDriverCheckbox = findViewById(R.id.regIsDriverCheckbox);
        driverFieldsLabel = findViewById(R.id.driverFieldsLabel);
        licenceField = findViewById(R.id.regLicenceField);
        birthDateField = findViewById(R.id.regBirthDateField);
        vehicleTypeLabel = findViewById(R.id.vehicleTypeLabel);
        vehicleTypeSpinner = findViewById(R.id.regVehicleTypeSpinner);

        ArrayAdapter<VehicleType> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                VehicleType.values()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleTypeSpinner.setAdapter(adapter);


        isDriverCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int visibility = isChecked ? View.VISIBLE : View.GONE;
            driverFieldsLabel.setVisibility(visibility);
            licenceField.setVisibility(visibility);
            birthDateField.setVisibility(visibility);
            vehicleTypeLabel.setVisibility(visibility);
            vehicleTypeSpinner.setVisibility(visibility);
        });
    }

    public void createAccount(View view) {
        EditText loginField = findViewById(R.id.regLoginField);
        EditText passwordField = findViewById(R.id.regPasswordField);
        EditText nameField = findViewById(R.id.regNameField);
        EditText surnameField = findViewById(R.id.regSurnameField);
        EditText phoneField = findViewById(R.id.regPhoneField);

        String login = loginField.getText().toString();
        String password = passwordField.getText().toString();
        String name = nameField.getText().toString();
        String surname = surnameField.getText().toString();
        String phone = phoneField.getText().toString();


        if (login.isEmpty() || password.isEmpty() || name.isEmpty() ||
            surname.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();
        String userInfo;
        String endpoint;


        if (isDriverCheckbox.isChecked()) {
            String licence = licenceField.getText().toString();
            String birthDate = birthDateField.getText().toString();
            VehicleType vehicleType = (VehicleType) vehicleTypeSpinner.getSelectedItem();

            if (licence.isEmpty() || birthDate.isEmpty()) {
                Toast.makeText(this, "Please fill in all driver fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Driver driver = new Driver(login, password, name, surname, phone,
                    licence, birthDate, vehicleType);
            userInfo = gson.toJson(driver, Driver.class);
            endpoint = CREATE_DRIVER_URL;
        } else {
            Customer customer = new Customer(login, password, name, surname, phone, "");
            userInfo = gson.toJson(customer, Customer.class);
            endpoint = CREATE_BASIC_USER_URL;
        }

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String finalUserInfo = userInfo;
        String finalEndpoint = endpoint;

        executor.execute(() -> {
            try {
                String response = RestOperations.sendPost(finalEndpoint, finalUserInfo);
                handler.post(() -> {
                    if (!response.equals("Error") && !response.isEmpty() && !response.equals("null")) {
                        Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Registration failed. Username might already exist.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                handler.post(() -> {
                    Toast.makeText(this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void goToLogin(View view) {
        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
