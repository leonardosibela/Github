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

    private Boolean complementarLista;
    private Integer currentPage = 1;
    private Integer total_count;

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

                complementarLista = false;
                currentPage = 1;

                termoPesquisa = txtUsername.getText().toString();
                String urlPesquisa = creatUserSearchUrl(termoPesquisa, complementarLista);
                new GetUsersTask().execute(urlPesquisa);
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) { }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if((firstVisibleItem + visibleItemCount) >= totalItemCount && !"".equals(termoPesquisa)) {

            if(total_count > currentPage * 30) {

                complementarLista = true;
                termoPesquisa = txtUsername.getText().toString();
                String urlPesquisa = creatUserSearchUrl(termoPesquisa, complementarLista);
                new GetUsersTask().execute(urlPesquisa);
            }
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
            String strUrl = params[0];

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

    private String creatUserSearchUrl(String termoPesquisa, Boolean complementarLista) {

        StringBuffer url = new StringBuffer();

        url.append("https://api.github.com/search/users?q=");
        url.append(termoPesquisa.trim());
        url.append("%20type:users");

        if(complementarLista) {
            url.append("&page=" + ++currentPage);
        }

        return url.toString();
    }



    private void carregarListView(String result) {

        List<GithubUser> githubUsers = strJsonToList(result);

        if(githubUsers.isEmpty()) {
            Toast.makeText(getBaseContext(), "Nenhum usuário encontrado", Toast.LENGTH_SHORT).show();
        }

        if(complementarLista) {

            usuarioAdapter.addData(githubUsers);

        } else {

            usuarioAdapter = new UsuarioAdapter(this, githubUsers);
            lsvUsuarios.setAdapter(usuarioAdapter);
        }
    }

    private List<GithubUser> strJsonToList(String strJsonArray) {

        Gson gson = new Gson();

        List<GithubUser> githubUsers = new ArrayList<>();

        try {

            JSONObject jsonGithubUsers = new JSONObject(strJsonArray);
            total_count = jsonGithubUsers.getInt("total_count");
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