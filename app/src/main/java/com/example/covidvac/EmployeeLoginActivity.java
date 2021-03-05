package com.example.covidvac;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covidvac.models.Employee;

public class EmployeeLoginActivity extends AppCompatActivity {

    boolean isLoginIn = false;
    Button btnLogin;
    SharedPreferences sharedPref;
    final String USERNAME_KEY = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_login);

        sharedPref = getApplicationContext().getSharedPreferences("LOGIN_PREFS", Context.MODE_PRIVATE);
        EditText etUsername = findViewById(R.id.etUsername);
        EditText etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLoginEmp);

        //gets last given username for easier use
        etUsername.setText(sharedPref.getString(USERNAME_KEY, ""));

        btnLogin.setEnabled(false);
        btnLogin.setOnClickListener(v -> login(etUsername.getText().toString().trim(),
                                            etPassword.getText().toString().trim()));

        //TextWatcher implementation. checks if credential fields are not empty in order to avoid
        //pointless login attempts
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

    //login method
    private void login(String username, String password){

        isLoginIn = true;
        btnLogin.setEnabled(false);
        Employee employee = new Employee();
        employee.login(username, password, success -> {

            if (success){

                Intent intent = new Intent(getApplicationContext(), EmployeeMainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("employee", employee);
                intent.putExtras(bundle);
                startActivity(intent);
            }
            else {

                Toast.makeText(getApplicationContext(),
                        getString(R.string.citizen_login_wrong_credentials), Toast.LENGTH_SHORT)
                        .show();
            }
            isLoginIn = false;
            btnLogin.setEnabled(true);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sharedPref != null) {
            SharedPreferences.Editor editor = sharedPref.edit();

            //saves last given username for easier use
            EditText etUsername = findViewById(R.id.etUsername);
            editor.putString(USERNAME_KEY, etUsername.getText().toString());
            editor.apply();
        }
    }
}
