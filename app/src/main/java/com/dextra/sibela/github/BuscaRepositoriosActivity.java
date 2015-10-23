package com.dextra.sibela.github;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dextra.sibela.github.bean.GithubUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class BuscaRepositoriosActivity extends AppCompatActivity {

    Button btnPesquisar;
    EditText txtRepoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_repositorios);

        btnPesquisar = (Button) findViewById(R.id.btnPesquisar);
        txtRepoName = (EditText) findViewById(R.id.txtRepoName);

        btnPesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String repoName = txtRepoName.getText().toString();

                //TODO: Buscar repositorios

            }
        });

    }
}
