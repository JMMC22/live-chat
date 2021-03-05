package com.example.chatfb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText  password;
    private TextInputEditText email;
    private Button register;

    private FirebaseAuth mAuth;

    private TextInputLayout _email, _password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        email = (TextInputEditText) findViewById(R.id.campo_email);
        password = (EditText) findViewById(R.id.password);

        register = (Button) findViewById(R.id.button);

        _email = (TextInputLayout) findViewById(R.id.til_correo);
        _password = (TextInputLayout) findViewById(R.id.til_password);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarDatos()) {
                    register(email.getText().toString(), password.getText().toString());
                }
            }
        });
    }

    public void register(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(RegisterActivity.this, "Bienvenido " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                            Intent chat = new Intent(getApplicationContext(), ChatActivity.class);
                            startActivity(chat);
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private boolean esCorreoValido(String correo) {
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            _email.setError("Correo electrónico inválido");
            return false;
        } else {
            _email.setError(null);
        }

        return true;
    }

    private boolean esContraseñaValida(String pass) {
        if (pass.length() < 6) {
            _password.setError("Contraseña debe tener mínimo 6 dígitos.");
            return false;
        } else {
            _password.setError(null);
        }

        return true;
    }

    //Validación datos formulario.
    private boolean validarDatos() {

        String correo = _email.getEditText().getText().toString();
        String pass = _password.getEditText().getText().toString();

        boolean a = esCorreoValido(correo);
        boolean b = esContraseñaValida(pass);

        if (a && b) {
            // OK, se pasa a la siguiente acción
            //Toast.makeText(this, "Se guarda el registro", Toast.LENGTH_LONG).show();
            return true;
        } else {
            return false;
        }

    }
}
