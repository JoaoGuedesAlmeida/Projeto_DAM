package pt.upt.devapp.projeto_dam;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ViewPager vp;
    private PageAdapter pageAdapter;
    private TabLayout tabs;

    private DatabaseReference db_users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            //buscar o utilizador logged
            db_users = FirebaseDatabase.getInstance().getReference().child("Utilizadores").child(mAuth.getCurrentUser().getUid());
        }
        db_users.child("online").setValue(true);

        //buscar as nossas Pages e preenche-las com os 3 fragmentos (Chat, Requests e Friends)
        vp = (ViewPager) findViewById(R.id.pages);
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        vp.setAdapter(pageAdapter);

        //pegar nas tabs e colocar as pages correspondentes
        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(vp);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Verificar se o utilizador está logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // se não está logged in, troca para a activity de log in/registo
        if (currentUser == null){
            sair();
        } else{ //está online
            db_users.child("online").setValue(true);
        }
    }

    @Override
    protected void onStop() { //quando sai da app tira de online
        super.onStop();
        FirebaseUser current = mAuth.getCurrentUser();
        if(current != null){
            db_users.child("online").setValue(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser current = mAuth.getCurrentUser();
        if(current != null){
            db_users.child("online").setValue(true);
        };
    }

    private void sair() {
        db_users.child("online").setValue(false);
        Intent startIntent = new Intent(MainActivity.this, WelcomeActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() ==  R.id.logout_btn){
            mAuth.signOut();
            sair();
        }

        if(item.getItemId() == R.id.accset_btn){
            Intent intent = new Intent(MainActivity.this, AccountSettings.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.users_btn){
            Intent intent = new Intent(MainActivity.this, SearchUsers.class);
            startActivity(intent);
        }
        return true;
    }


}
