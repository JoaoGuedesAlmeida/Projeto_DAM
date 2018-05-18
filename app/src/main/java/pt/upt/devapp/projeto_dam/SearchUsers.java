package pt.upt.devapp.projeto_dam;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SearchUsers extends AppCompatActivity {

    private RecyclerView lista_utilizadores;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        db = FirebaseDatabase.getInstance().getReference().child("Utilizadores");

        lista_utilizadores = (RecyclerView) findViewById(R.id.lista_utilizadores);
        lista_utilizadores.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Utilizador, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Utilizador, UsersViewHolder>(Utilizador.class,R.layout.user_item, UsersViewHolder.class, db) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Utilizador model, int position) {
                //colocar o nome do utilizador igual ao da nossa classe Utilizador
                viewHolder.setNome(model.getNome());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setImg(model.getImgpeq());

                final String user_id = getRef(position).getKey();

                //quando clickamos num dos utilizadores, vai para o perfil dele
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //passamos a chave do utilizador para o intent do perfil para conseguirmos mostrar os dados do mesmo
                        Intent perfil = new Intent(SearchUsers.this, ProfileActivity.class);
                        perfil.putExtra("user_id", user_id);
                        startActivity(perfil);
                    }
                });
            }
        };

        lista_utilizadores.setAdapter(firebaseRecyclerAdapter);
    }

    //classe para tratar dos utilizadores que vão aparecer na Recycler View
    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        View view;

        public UsersViewHolder(View itemView) {
            super(itemView);
            view = itemView;
        }

        public void setNome(String nome){
            //ir buscar ao nosso layout do menu o item para colocar o nome
            TextView nomeText = view.findViewById(R.id.user_name);
            nomeText.setText(nome);
        }

        public void setStatus(String status){
            //ir buscar ao nosso layout do menu o item para colocar o status
            TextView statusText = view.findViewById(R.id.user_status);
            statusText.setText(status);
        }

        public void setImg(String imgpeq){
            //ir buscar ao nosso layout do menu o item para colocar a img
            ImageView imgView = view.findViewById(R.id.user_img);
            if(imgpeq.equals("default")){
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/projetodam-123d2.appspot.com/o/Imagens%2Fthumbs%2Fdefault.jpg?alt=media&token=4f5ffb2d-95d8-44bd-b6f6-bd4f19c471aa").into(imgView);
            } else {
                Picasso.get().load(imgpeq).into(imgView);
            }
        }
    }
}
