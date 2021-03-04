package com.example.covidvac;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covidvac.models.Employee;

public class EmployeeLoginActivity extends AppCompatActivity {

    boolean isLoginIn = false;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_login);

        EditText etUsername = findViewById(R.id.etUsername);
        EditText etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLoginEmp);

        btnLogin.setEnabled(false);
        btnLogin.setOnClickListener(v -> login(etUsername.getText().toString().trim(),
                                            etPassword.getText().toString().trim()));

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!isLoginIn){
                    if (etUsername.getText().toString().trim().equals("") ||
                            etPassword.getText().toString().trim().equals("")){

                        btnLogin.setEnabled(false);
                    }
                    else{

                        btnLogin.setEnabled(true);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        etUsername.addTextChangedListener(watcher);
        etPassword.addTextChangedListener(watcher);
    }

    private void login(String username, String password){

        isLoginIn = true;
        btnLogin.setEnabled(false);
        Employee employee = new Employee();
        employee.login(username, password, success -> {

            if (success){
                //TODO next intent
            }
            else {
                //TODO display wrong password prompt
            }
            isLoginIn = false;
            btnLogin.setEnabled(true);
        });
    }
}
