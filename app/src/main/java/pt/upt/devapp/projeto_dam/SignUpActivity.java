package pt.upt.devapp.projeto_dam;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout name;
    private TextInputLayout email;
    private TextInputLayout password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
    }

    public void guardarUser(View view) {
        // vai buscar os inputs do utilizador
        name = (TextInputLayout) findViewById(R.id.su_name);
        email = (TextInputLayout) findViewById(R.id.su_email);
        password = (TextInputLayout) findViewById(R.id.su_password);

        String nameT = name.getEditText().getText().toString();
        String emailT = email.getEditText().getText().toString();
        String passwordT = password.getEditText().getText().toString();

        // se nenhum campo estiver vazio, chama função para registar
        if(!TextUtils.isEmpty(nameT) || !TextUtils.isEmpty(emailT) || !TextUtils.isEmpty(passwordT)){
            registarUser(nameT, emailT, passwordT);
        }

    }

    // método para registar o utilizador (firebase)
    private void registarUser(String nameT, String emailT, String passwordT) {
        mAuth.createUserWithEmailAndPassword(emailT, passwordT)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent mainIntent = new Intent(SignUpActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Cannot Sign in. Please check the form and try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
