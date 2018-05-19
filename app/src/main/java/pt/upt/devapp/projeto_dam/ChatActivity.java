package pt.upt.devapp.projeto_dam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {

    private String destinatario;
    private String username;
    private boolean online;
    private Toolbar toolbar;

    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        db = FirebaseDatabase.getInstance().getReference();

        //buscar o id de quem estamos a enviar mensagem e o nome
        destinatario = getIntent().getStringExtra("user_id");
        username = getIntent().getStringExtra("nome");
        online = getIntent().getBooleanExtra("online",online);

        //definir o titulo da activity com o nome do utilizador com o qual estamos a falar
        getSupportActionBar().setTitle(username);
        if(online == true){
            getSupportActionBar().setSubtitle("Online");
        } else {
            getSupportActionBar().setSubtitle("Offline");
        }



    }
}
