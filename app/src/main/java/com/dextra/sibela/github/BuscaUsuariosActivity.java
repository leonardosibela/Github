package com.dextra.sibela.github;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.dextra.sibela.github.adapter.UsuarioAdapter;
import com.dextra.sibela.github.bean.GithubUser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BuscaUsuariosActivity extends ListActivity implements AbsListView.OnScrollListener {

    private Button btnPesquisar;
    private EditText txtUsername;
    private ListView lsvUsuarios;

    private String termoPesquisa = "";

    private UsuarioAdapter usuarioAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_usuarios);

        btnPesquisar = (Button) findViewById(R.id.btnPesquisar);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        lsvUsuarios = (ListView) findViewById(android.R.id.list);

        getListView().setOnScrollListener(this);

        btnPesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                termoPesquisa = txtUsername.getText().toString();
                new GetUsersTask().execute(termoPesquisa);

            }
        });

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) { }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if((firstVisibleItem + visibleItemCount) >= totalItemCount && !"".equals(termoPesquisa)) {

            // TODO: Carregar mais elementos
        }
    }

    public class GetUsersTask extends AsyncTask<String, Integer, String> {

        ProgressDialog progLoadingUsers;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progLoadingUsers = new ProgressDialog(BuscaUsuariosActivity.this);
            progLoadingUsers.setMessage("Buscando usuários...");
            progLoadingUsers.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String response = "";
            String strUrl = creatUserSearchUrl(params);

            try {

                URL url = new URL(strUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                response = readStream(urlConnection.getInputStream());

            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            carregarListView(result);
            progLoadingUsers.dismiss();
        }

        private String creatUserSearchUrl(String... params) {

            StringBuffer url = new StringBuffer();

            url.append("https://api.github.com/search/users?q=");
            url.append((String) params[0].trim());
            url.append("%20type:users");

            return url.toString();
        }

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
    }

    private void carregarListView(String result) {

        List<GithubUser> githubUsers = strJsonToList(result);

        if(githubUsers.isEmpty()) {
            Toast.makeText(getBaseContext(), "Nenhum usuário encontrado", Toast.LENGTH_LONG).show();
        }

        this.usuarioAdapter = new UsuarioAdapter(this, githubUsers);
            lsvUsuarios.setAdapter(usuarioAdapter);
    }

    private List<GithubUser> strJsonToList(String strJsonArray) {

        Gson gson = new Gson();

        List<GithubUser> githubUsers = new ArrayList<>();

        try {

            JSONObject jsonGithubUsers = new JSONObject(strJsonArray);
            JSONArray jsonArrayGitUsers = new JSONArray(jsonGithubUsers.get("items").toString());

            for(int i = 0; i < jsonArrayGitUsers.length(); i++) {

                String strGithubUser = jsonArrayGitUsers.get(i).toString();
                JSONObject githubUser = new JSONObject(strGithubUser);

                githubUsers.add(gson.fromJson(githubUser.toString(), GithubUser.class));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return githubUsers;
    }
}