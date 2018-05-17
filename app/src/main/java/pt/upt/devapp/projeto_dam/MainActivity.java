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

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ViewPager vp;
    private PageAdapter pageAdapter;
    private TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

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
        }
    }

    private void sair() {
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
        return true;
    }


}
