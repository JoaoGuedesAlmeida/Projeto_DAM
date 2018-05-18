package pt.upt.devapp.projeto_dam;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeStatus extends AppCompatActivity {

    private TextInputLayout status;

    private DatabaseReference db;
    private FirebaseUser utilizador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_status);

        utilizador = FirebaseAuth.getInstance().getCurrentUser();
        String user_id = utilizador.getUid();

        db = FirebaseDatabase.getInstance().getReference().child("Utilizadores").child(user_id);
        //buscar a caixa de texto e pôr o status atual na mesma
        status = (TextInputLayout) findViewById(R.id.cs_input);
        String statusAnterior = getIntent().getStringExtra("status");
        status.getEditText().setText(statusAnterior);


    }

    public void saveStatus(View view){

        String statusT = status.getEditText().getText().toString();
        //trocar o status do utilizador na DB
        db.child("status").setValue(statusT).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(ChangeStatus.this, AccountSettings.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(ChangeStatus.this, "Error. Please try again.", Toast.LENGTH_LONG).show();

                }
            }
        });
    }
}
