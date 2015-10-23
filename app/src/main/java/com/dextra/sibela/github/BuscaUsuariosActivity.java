package com.dextra.sibela.github;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class BuscaUsuariosActivity extends AppCompatActivity {

    Button btnPesquisar;
    EditText txtUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_usuarios);

        btnPesquisar = (Button) findViewById(R.id.btnPesquisar);
        txtUsername = (EditText) findViewById(R.id.txtUsername);

        btnPesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nomeUsuario = txtUsername.getText().toString();

                // TODO: Pesquisar usuarios

            }
        });

    }
}
