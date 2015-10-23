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

                new GetUsersTask().execute(nomeUsuario);

            }
        });

    }

    public class GetUsersTask extends AsyncTask<String, Integer, String> {

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return response.toString();
        }

        @Override
        protected String doInBackground(String... params) {

            String response = "";

            String urlPrefix = "https://api.github.com/search/users?q=";
            String login = (String) params[0];
            String urlSulfix = "%20type:users";

            String strUrl = urlPrefix + login + urlSulfix;

            try {
                URL url = new URL(strUrl);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                response = readStream(urlConnection.getInputStream());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        private List<GithubUser> jsonToList(String strJsonArray) {

            try {

                JSONArray jsonArray = new JSONArray(strJsonArray);
                List<GithubUser> githubUsers = new ArrayList<>();

                for(int i = 0; i < jsonArray.length(); i++) {

                    String strGithubUser = jsonArray.get(i).toString();
                    JSONObject githubUser = new JSONObject(strGithubUser);

                    // githubUsers.add(githubUser);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

    }
}
