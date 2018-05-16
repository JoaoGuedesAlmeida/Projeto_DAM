package pt.upt.devapp.projeto_dam;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void signup(View view){
        Intent startIntent = new Intent(WelcomeActivity.this, SignUpActivity.class);
        startActivity(startIntent);
    }

    public void login(View view){
        Intent startIntent = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(startIntent);
    }
}
