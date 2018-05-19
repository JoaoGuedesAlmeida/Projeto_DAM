package pt.upt.devapp.projeto_dam;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout loginEmail;
    private TextInputLayout loginPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference utilizadores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        utilizadores = FirebaseDatabase.getInstance().getReference().child("Utilizadores");
    }

    // classe para verificar se os dados do login estão corretos
    public void login(View view){
        loginEmail = (TextInputLayout) findViewById(R.id.log_email);
        loginPassword = (TextInputLayout) findViewById(R.id.log_password);

        String email = loginEmail.getEditText().getText().toString();
        String password = loginPassword.getEditText().getText().toString();

        // se os campos não estiverem vazios, chama a função para fazer o login
        if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){
            loginUser(email, password);
            System.out.println("TESTE");
        }
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    String user_id = mAuth.getCurrentUser().getUid();

                    String token = FirebaseInstanceId.getInstance().getToken();

                    utilizadores.child(user_id).child("token").setValue(token).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            //linha para garantir que o utilizador depois do login não volte atrás para uma activity
                            //fora da zona do utilizador (welcome page ou login/registo)
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                        }
                    });


                } else {
                    Toast.makeText(LoginActivity.this, "Email or Password is incorrect", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
