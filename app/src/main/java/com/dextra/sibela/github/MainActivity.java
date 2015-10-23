package com.dextra.sibela.github;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnFindUsers;
    Button btnFindRepos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFindUsers = (Button) findViewById(R.id.btnFindUsers);
        btnFindRepos = (Button) findViewById(R.id.btnFindRepos);

        btnFindUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent userSearch = new Intent(getBaseContext(), BuscaUsuariosActivity.class);
                startActivity(userSearch);

            }
        });

        btnFindRepos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent repoSearch = new Intent(getBaseContext(), BuscaRepositoriosActivity.class);
                startActivity(repoSearch);

            }
        });

    }
}
