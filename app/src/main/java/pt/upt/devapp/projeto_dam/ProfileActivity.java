package pt.upt.devapp.projeto_dam;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profile_imgView;
    private TextView profile_nameView, profile_statusView, profile_friendsView;

    private DatabaseReference db;
    private DatabaseReference db_requests;

    private FirebaseUser utilizador_logged;
    private String user_id; //id do utilizador a qual o perfil está a ser visitado

    private int estado; //se já é amigo ou não

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user_id= getIntent().getStringExtra("user_id"); //id do perfil visitado

        //buscar o utilizador do perfil à bd
        db = FirebaseDatabase.getInstance().getReference().child("Utilizadores").child(user_id);

        db_requests = FirebaseDatabase.getInstance().getReference().child("Pedidos");
        utilizador_logged = FirebaseAuth.getInstance().getCurrentUser();

        profile_imgView = (ImageView) findViewById(R.id.profile_img);
        profile_nameView = (TextView) findViewById(R.id.profile_name);
        profile_statusView = (TextView) findViewById(R.id.profile_status);
        profile_friendsView = (TextView) findViewById(R.id.profile_friends);

        estado = 0; // não são amigos

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //buscar a info à db
                String nome = dataSnapshot.child("nome").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String img = dataSnapshot.child("img").getValue().toString();

                //preencher os vários itens com a info da db sobre o user em que estamos
                profile_nameView.setText(nome);
                profile_statusView.setText(status);
                Picasso.get().load(img).into(profile_imgView);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void pedidoFriend(View view){
        if(estado == 0){ //se não são amigos
            //grava que o utilizador logged enviou pedido ao utilizador visitado
            db_requests.child(utilizador_logged.getUid()).child(user_id).child("tipo").setValue("enviado").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        //grava que o utilizador visitado recebeu o pedido do utilizador logged in
                        db_requests.child(user_id).child(utilizador_logged.getUid()).child("tipo").setValue("recebido").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                System.out.println("Sucesso!");
                            }
                        });
                    }
                }
            });
        }
    }
}
