package pt.upt.devapp.projeto_dam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class AccountSettings extends AppCompatActivity {

    private DatabaseReference db;
    private FirebaseUser utilizador;

    private ImageView as_image;
    private TextView as_nome;
    private TextView as_status;

    private FirebaseAuth mAuth;

    private StorageReference storage;

    private static final int escolha=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        as_image = (ImageView) findViewById(R.id.as_img);
        as_nome = (TextView) findViewById(R.id.as_name);
        as_status = (TextView) findViewById(R.id.as_status);

        mAuth = FirebaseAuth.getInstance();

        utilizador = FirebaseAuth.getInstance().getCurrentUser();

        //pasta onde são guardadas as imagens
        storage = FirebaseStorage.getInstance().getReference();

        //id do utilizador logged in
        String user_id = utilizador.getUid();

        //buscar o nosso utilizador logged à DB
        db = FirebaseDatabase.getInstance().getReference().child("Utilizadores").child(user_id);
        db.child("online").setValue(true);
        //buscar as informações sobre o utilizador à db e colocar no perfil do mesmo
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nome = dataSnapshot.child("nome").getValue().toString();
                String img = dataSnapshot.child("img").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String imgpeq = dataSnapshot.child("imgpeq").getValue().toString();
                // usamos a lib Picasso para colocar a imagem do perfil através do URL em que guardamos a imagem na DB do Firebase
                // Lib: http://square.github.io/picasso/
                if(img.equals("default")){
                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/projetodam-123d2.appspot.com/o/Imagens%2Fdefault.jpg?alt=media&token=36fe0f48-81c7-4f04-b174-2621c6622c34").into(as_image);
                } else {
                    Picasso.get().load(img).into(as_image);
                }

                as_nome.setText(nome);
                as_status.setText(status);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void changeStatus(View view){
        String status = as_status.getText().toString();
        Intent intent = new Intent(AccountSettings.this, ChangeStatus.class);
        intent.putExtra("status", status);
        startActivity(intent);
    }

    //quando clickamos no botao para trocar a imagem
    public void changeImg(View view){

        // através de uma library, podemos escolher a imagem de perfil tirando uma foto ou escolhendo uma da galeria
        // a library encontra-se no seguinte link: https://github.com/ArthurHub/Android-Image-Cropper
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);
    }


    // método que vai buscar a imagem escolhida
    // a library encontra-se no seguinte link: https://github.com/ArthurHub/Android-Image-Cropper
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                //se a escolha da imagem for bem sucedida, vamos guardar a imagem na nossa storage da Firebase
                String user_id = utilizador.getUid(); //para gravar a imagem com o id do utilizador
                Uri resultUri = result.getUri();

                //comprimir a imagem num Bitmap para obter um loading mais rapido quando carregamos as imagens
                File thumb = new File(resultUri.getPath());
                Bitmap thumb_bit = null;
                try {
                    //lib usada para Comprimir as imagens: https://github.com/zetbaitsu/Compressor
                    thumb_bit = new Compressor(this).compressToBitmap(thumb);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //linhas retiradas da documentação do Firebase para armazenar o nosso bitmap comprimido na DB
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bit.compress(Bitmap.CompressFormat.JPEG,100,baos);
                final byte[] thumb_byte = baos.toByteArray();

                //buscar a pasta onde vão ser guardada a imagem original e a imagem bitmap comprimida
                StorageReference nomePasta = storage.child("Imagens").child(user_id +".jpg");
                final StorageReference thumbPasta = storage.child("Imagens").child("thumbs").child(user_id+".jpg");

                // colocar a imagem na nossa storage
                nomePasta.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){ //se a imagem original foi guardada com sucesso na Firebase
                            //vai buscar o link de download da imagem para mais tarde colocar na DB
                            final String url_imagem = task.getResult().getDownloadUrl().toString();

                            //upload do bitmap
                            UploadTask uploadTask = thumbPasta.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    //vai buscar o link de download da imagem comprimida bitmap
                                    String url_thumb = thumb_task.getResult().getDownloadUrl().toString();

                                    if(thumb_task.isSuccessful()){ //se o bitmap for colocado com sucesso
                                        //vamos ao nosso utilizador na DB e atualizamos os atributos img e imgpeq para os links que guardamos em cima
                                        Map update_hash = new HashMap();
                                        update_hash.put("img",url_imagem);
                                        update_hash.put("imgpeq", url_thumb);

                                        //finalmente vamos à base de dados atualizar os campos
                                        db.updateChildren(update_hash).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    System.out.println("Entrei");
                                                } else {

                                                }
                                            }
                                        });
                                    }
                                }
                            });


                        } else {
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*FirebaseUser current = mAuth.getCurrentUser();
        if(current != null){
            db.child("online").setValue(false);
        }*/

    }
}
