package pt.upt.devapp.projeto_dam;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsF extends Fragment {

    private RecyclerView lista_utilizadores;
    private DatabaseReference db_friends;
    private DatabaseReference db_users;
    private FirebaseAuth mAuth;

    private String user_id;



    public FriendsF() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View main = inflater.inflate(R.layout.fragment_friends, container, false);

        //buscar a nossa recycler view
        lista_utilizadores = (RecyclerView) main.findViewById(R.id.lista_amigos);
        mAuth = FirebaseAuth.getInstance();

        //buscar o id do utilizador logged
        user_id = mAuth.getCurrentUser().getUid();

        //ir a db de amigos buscar todos os amigos que o utilizador tem
        db_friends = FirebaseDatabase.getInstance().getReference().child("Amigos").child(user_id);
        db_friends.keepSynced(true);
        db_users = FirebaseDatabase.getInstance().getReference().child("Utilizadores");
        db_users.keepSynced(true);

        lista_utilizadores.setHasFixedSize(true);
        lista_utilizadores.setLayoutManager(new LinearLayoutManager(getContext()));

        return main;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friend, FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friend, FriendsViewHolder>(Friend.class, R.layout.user_item, FriendsViewHolder.class, db_friends) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friend model, int position) {
                //popular a RecyclerView com os Amigos e definir ir buscar a data de quando passaram a ser amigos
                viewHolder.setData(model.getData());

                final String list_user_id = getRef(position).getKey();
                //buscar o resto das infos do utilizador
                db_users.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("nome").getValue().toString();
                        String thumb = dataSnapshot.child("imgpeq").getValue().toString();
                        final Boolean online = (boolean) dataSnapshot.child("online").getValue();
                        viewHolder.setImg(thumb);
                        viewHolder.setNome(userName);
                        viewHolder.setOnline(online);

                        //definir o que acontece quando o utilizador carrega num dos amigos
                        viewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //pode ver o ser perfil ou enviar uma mensagem
                                CharSequence options[] = new CharSequence[]{"Open Profile", "Send Message"};
                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Select option");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        //dependo da opçao que o utilizador escolhe
                                        if(i == 0){ //se quiser ver o perfil
                                            //passamos a chave do utilizador para o intent do perfil para conseguirmos mostrar os dados do mesmo
                                            Intent perfil = new Intent(getContext(), ProfileActivity.class);
                                            perfil.putExtra("user_id", list_user_id);
                                            startActivity(perfil);
                                        }

                                        if(i == 1){
                                            Intent perfil = new Intent(getContext(), ChatActivity.class);
                                            perfil.putExtra("user_id", list_user_id);
                                            perfil.putExtra("nome", userName);
                                            perfil.putExtra("online",online);
                                            startActivity(perfil);
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        lista_utilizadores.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
        View view;
        public FriendsViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }
        //colocar na text view desde quando são amigos
        public void setData(String data){
            TextView since = (TextView) view.findViewById(R.id.user_status);
            since.setText(data);
        }

        public void setNome(String nome){
            TextView nomeT = (TextView) view.findViewById(R.id.user_name);
            nomeT.setText(nome);
        }

        public void setImg(String img){
            ImageView image = (ImageView) view.findViewById(R.id.user_img);
            if(img.equals("default")){
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/projetodam-123d2.appspot.com/o/Imagens%2Fthumbs%2Fdefault.jpg?alt=media&token=4f5ffb2d-95d8-44bd-b6f6-bd4f19c471aa").into(image);
            } else {
                Picasso.get().load(img).into(image);
            }

        }

        //mostrar o icon de online consoante o estado
        public void setOnline(boolean online){
            ImageView img = (ImageView) view.findViewById(R.id.user_online);
            if(online == true){
                img.setVisibility(View.VISIBLE);
            } else {
                img.setVisibility(View.INVISIBLE);
            }
        }
    }
}
