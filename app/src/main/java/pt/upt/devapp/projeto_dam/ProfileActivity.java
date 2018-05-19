package pt.upt.devapp.projeto_dam;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profile_imgView;
    private TextView profile_nameView, profile_statusView, profile_friendsView;

    private DatabaseReference db;
    private DatabaseReference db_requests;
    private DatabaseReference db_friends;
    private DatabaseReference notificacoes;
    private FirebaseAuth mAuth;

    private FirebaseUser utilizador_logged;
    private String user_id; //id do utilizador a qual o perfil está a ser visitado

    private int estado; //se já é amigo ou não
    private Button pedidoBtn;
    private Button declineBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        pedidoBtn = (Button) findViewById(R.id.profile_btn);
        declineBtn = (Button) findViewById(R.id.profile_btn2);

        mAuth = FirebaseAuth.getInstance();

        user_id= getIntent().getStringExtra("user_id"); //id do perfil visitado

        //buscar o utilizador do perfil à bd
        db = FirebaseDatabase.getInstance().getReference().child("Utilizadores").child(user_id);
        db.child("online").setValue(true);

        db_friends = FirebaseDatabase.getInstance().getReference().child("Amigos");

        db_requests = FirebaseDatabase.getInstance().getReference().child("Pedidos");
        notificacoes = FirebaseDatabase.getInstance().getReference().child("Notificacoes");

        utilizador_logged = FirebaseAuth.getInstance().getCurrentUser();

        profile_imgView = (ImageView) findViewById(R.id.profile_img);
        profile_nameView = (TextView) findViewById(R.id.profile_name);
        profile_statusView = (TextView) findViewById(R.id.profile_status);
        profile_friendsView = (TextView) findViewById(R.id.profile_friends);

        declineBtn.setVisibility(View.INVISIBLE);
        declineBtn.setEnabled(false);

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

                // vamos verificar o estado entre os dois utilizadores(visitante e visitado)
                db_requests.child(utilizador_logged.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id)){
                            String tipo = dataSnapshot.child(user_id).child("tipo").getValue().toString();

                            //se o tipo for recebido então damos a opção de aceitar o pedido de amizade
                            if(tipo.equals("recebido")){
                                estado = 2; //com pedido recebido
                                pedidoBtn.setText("Accept Friend Request");
                                declineBtn.setVisibility(View.VISIBLE);
                                declineBtn.setEnabled(true);
                            }

                            //se o tipo for recebido então damos a opção de aceitar o pedido de amizade
                            if(tipo.equals("enviado")){
                                estado = 1; //com pedido enviado
                                pedidoBtn.setText("Cancel Friend Request");
                                declineBtn.setVisibility(View.INVISIBLE);
                                declineBtn.setEnabled(false);
                            }
                        } else { //se nao houver nenhum pedido entre os 2 verificamos se ja são amigos
                            db_friends.child(utilizador_logged.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //se ja são amigos define o estado como 4 (amigos)
                                    if (dataSnapshot.hasChild(user_id)){
                                        estado = 4; //amigos
                                        pedidoBtn.setText("UnFriend");
                                        declineBtn.setVisibility(View.INVISIBLE);
                                        declineBtn.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void pedidoFriend(View view){
        pedidoBtn.setEnabled(false);

        // NÃO SÃO AMIGOS; estado = 0

        if(estado == 0){ //se não são amigos quer dizer que está a enviar pedido de amizade
            //grava que o utilizador logged enviou pedido ao utilizador visitado
            db_requests.child(utilizador_logged.getUid()).child(user_id).child("tipo").setValue("enviado").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        //grava que o utilizador visitado recebeu o pedido do utilizador logged in
                        db_requests.child(user_id).child(utilizador_logged.getUid()).child("tipo").setValue("recebido").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //por fim envia uma Notificação (Firebase) ao utilizador que recebe o pedido
                                HashMap<String,String> dadosNotifica = new HashMap<>();
                                dadosNotifica.put("from", utilizador_logged.getUid());
                                dadosNotifica.put("type", "pedido");

                                notificacoes.child(user_id).push().setValue(dadosNotifica).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        pedidoBtn.setEnabled(true);
                                        estado = 1; //pedido enviado
                                        pedidoBtn.setText("Cancel Friend Request");

                                        declineBtn.setVisibility(View.INVISIBLE);
                                        declineBtn.setEnabled(false);
                                    }
                                });


                            }
                        });
                    }
                }
            });
        }

        // PEDIDO ENVIADO MAS PENDENTE; estado = 1

        if(estado == 1){
            //apaga o pedido enviado do utilizador logged in
            db_requests.child(utilizador_logged.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    db_requests.child(user_id).child(utilizador_logged.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pedidoBtn.setEnabled(true);
                            estado = 0; //sem pedido e não são amigos
                            pedidoBtn.setText("Send Friend Request");

                            declineBtn.setVisibility(View.INVISIBLE);
                            declineBtn.setEnabled(false);
                        }
                    });
                }
            });
        }

        // PEDIDO RECEBIDO ; estado = 2

        if(estado == 2){
            final String data = DateFormat.getDateTimeInstance().format(new Date());
            db_friends.child(utilizador_logged.getUid()).child(user_id).child("data").setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    db_friends.child(user_id).child(utilizador_logged.getUid()).child("data").setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //apaga o pedido enviado do utilizador logged in
                            db_requests.child(utilizador_logged.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    db_requests.child(user_id).child(utilizador_logged.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pedidoBtn.setEnabled(true);
                                            estado = 4; //amigos
                                            pedidoBtn.setText("UnFriend");

                                            declineBtn.setVisibility(View.INVISIBLE);
                                            declineBtn.setEnabled(false);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }

        // se são amigos, faz unfriend
        if(estado == 4){
            db_friends.child(utilizador_logged.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    db_friends.child(user_id).child(utilizador_logged.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pedidoBtn.setEnabled(true);
                            estado = 0; //tira de amigos
                            pedidoBtn.setText("Send Friend Request");

                            declineBtn.setVisibility(View.INVISIBLE);
                            declineBtn.setEnabled(false);
                        }
                    });
                }
            });
        }
    }

    public void decline(View view){
        db_requests.child(utilizador_logged.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                db_requests.child(user_id).child(utilizador_logged.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pedidoBtn.setEnabled(true);
                        estado = 0; //sem pedido e não são amigos
                        pedidoBtn.setText("Send Friend Request");

                        declineBtn.setVisibility(View.INVISIBLE);
                        declineBtn.setEnabled(false);
                    }
                });
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        /*FirebaseUser current = mAuth.getCurrentUser();
        if(current != null){
            db.child("online").setValue(false);
        }
        */
    }
}
