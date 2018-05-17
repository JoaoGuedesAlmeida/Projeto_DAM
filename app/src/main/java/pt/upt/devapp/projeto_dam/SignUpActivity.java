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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout name;
    private TextInputLayout email;
    private TextInputLayout password;
    private FirebaseAuth mAuth;
    private DatabaseReference db;

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
    private void registarUser(final String nameT, String emailT, String passwordT) {
        mAuth.createUserWithEmailAndPassword(emailT, passwordT)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //temos que ir buscar o id do utilizador para guardar na nossa DB os dados do mesmo
                            FirebaseUser utilizador = FirebaseAuth.getInstance().getCurrentUser();
                            String user_id = utilizador.getUid();

                            //vamos buscar à nossa db dentro dos utilizadores o id do nosso utilizador que fomos buscar em cima
                            db = FirebaseDatabase.getInstance().getReference().child("Utilizadores").child(user_id);

                            //gravar a informação sobre o utilizador num hashmap
                            HashMap<String,String> infoUtilizador = new HashMap<>();
                            infoUtilizador.put("nome", nameT);
                            infoUtilizador.put("status", "Change your status!");
                            infoUtilizador.put("img", "default");
                            infoUtilizador.put("imgpeq", "default");

                            //por fim colocamos o nosso HashMap na DB e se for bem sucedido, passamos para o intent do utilizador
                            db.setValue(infoUtilizador).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent mainIntent = new Intent(SignUpActivity.this, MainActivity.class);
                                    //linha para garantir que o utilizador depois do registo não volte atrás para uma activity
                                    //fora da zona do utilizador (welcome page ou login/registo)
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();
                                }
                            });


                        } else {
                            Toast.makeText(SignUpActivity.this, "Cannot Sign in. Please check the form and try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
