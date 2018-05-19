package pt.upt.devapp.projeto_dam;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Geral extends Application{

    private DatabaseReference db_users;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();

        mAuth = FirebaseAuth.getInstance();
        db_users = FirebaseDatabase.getInstance().getReference().child("Utilizadores").child(mAuth.getCurrentUser().getUid());
        //definir o estado offline caso o utilizador se desconecte
        db_users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null){
                    db_users.child("online").onDisconnect().setValue(false);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
