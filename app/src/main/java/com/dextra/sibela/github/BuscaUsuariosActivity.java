package com.dextra.sibela.github;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
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

public class BuscaUsuariosActivity extends ListActivity implements AbsListView.OnScrollListener {

    private Button btnPesquisar;
    private EditText txtUsername;
    private ListView lsvUsuarios;

    private GithubUser usuarioSelecionado;

    private String termoPesquisa = "";
    private final String TERMO_PESQUISA_KEY = "TERMO_PESQUISA";

    private UsuarioAdapter usuarioAdapter;
    ArrayList<GithubUser> githubUsers;
    private final String USERS_KEY = "GITHUB_USERS";

    private Boolean complementarLista;
    private Integer currentPage = 1;
    private final String CURRENT_PAGE_KEY = "CURRENT_PAGE";
    private Integer total_count;
    private final String TOTAL_COUNT_KEY = "TOTAL_COUNT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_usuarios);

        btnPesquisar = (Button) findViewById(R.id.btnPesquisar);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        lsvUsuarios = (ListView) findViewById(android.R.id.list);

        if(savedInstanceState != null) {
            restoreData(savedInstanceState);
        }

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

        lsvUsuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                usuarioSelecionado = (GithubUser) lsvUsuarios.getItemAtPosition(position);
                Intent dadosUsuarios = new Intent(getBaseContext(), DadosUsuarioActivity.class);
                dadosUsuarios.putExtra("usuario", usuarioSelecionado);
                startActivity(dadosUsuarios);
            }
        });
    }

    private void restoreData(Bundle savedInstanceState) {

        if(savedInstanceState.containsKey(USERS_KEY) &&
                savedInstanceState.containsKey(TERMO_PESQUISA_KEY) &&
                savedInstanceState.containsKey(CURRENT_PAGE_KEY) &&
                savedInstanceState.containsKey(TOTAL_COUNT_KEY)) {

            termoPesquisa = savedInstanceState.getString(TERMO_PESQUISA_KEY);
            currentPage = savedInstanceState.getInt(CURRENT_PAGE_KEY);
            total_count = savedInstanceState.getInt(TOTAL_COUNT_KEY);

            githubUsers = savedInstanceState.getParcelableArrayList(USERS_KEY);

            usuarioAdapter = new UsuarioAdapter(this, githubUsers);
            lsvUsuarios.setAdapter(usuarioAdapter);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putParcelableArrayList(USERS_KEY, githubUsers);
        outState.putString(TERMO_PESQUISA_KEY, termoPesquisa);
        outState.putInt(CURRENT_PAGE_KEY, currentPage);
        outState.putInt(TOTAL_COUNT_KEY, total_count);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) { }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if((firstVisibleItem + visibleItemCount) >= totalItemCount && !"".equals(termoPesquisa)) {

            if(total_count > currentPage * 30) {

                complementarLista = true;
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
            txtUsername.setText("");
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
        url.append("%20in:login");

        if(complementarLista) {
            url.append("&page=" + ++currentPage);
        }

        return url.toString();
    }

    private void carregarListView(String result) {

        githubUsers = strJsonToList(result);

        if(githubUsers.isEmpty()) {
            Toast.makeText(getBaseContext(), "Nenhum usuário encontrado", Toast.LENGTH_SHORT).show();
        } else {

            if(complementarLista) {

                usuarioAdapter.addData(githubUsers);

            } else {

                usuarioAdapter = new UsuarioAdapter(this, githubUsers);
                lsvUsuarios.setAdapter(usuarioAdapter);
            }
        }
    }

    private ArrayList<GithubUser> strJsonToList(String strJsonArray) {

        Gson gson = new Gson();

        ArrayList<GithubUser> githubUsers = new ArrayList<>();

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