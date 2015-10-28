package com.dextra.sibela.github;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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

public class BuscaRepositoriosActivity extends ListActivity implements AbsListView.OnScrollListener {

    private Button btnPesquisar;
    private EditText txtRepoName;
    private ListView lsvRepositorios;

    private String termoPesquisa = "";
    private final String TERMO_PESQUISA_KEY = "TERMO_PESQUISA";

    ArrayList<String> nomesRepositorios;
    private final String REPOS_KEY = "GITHUB_REPOS";

    private Boolean complementarLista = false;
    private Integer currentPage = 1;
    private final String CURRENT_PAGE_KEY = "CURRENT_PAGE";
    private Integer total_count;
    private final String TOTAL_COUNT_KEY = "TOTAL_COUNT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_repositorios);

        btnPesquisar = (Button) findViewById(R.id.btnPesquisar);
        txtRepoName = (EditText) findViewById(R.id.txtRepoName);
        lsvRepositorios = (ListView) findViewById(android.R.id.list);

        if(savedInstanceState != null) {
            restoreData(savedInstanceState);
        }

        getListView().setOnScrollListener(this);

        btnPesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                complementarLista = false;
                currentPage = 1;

                termoPesquisa = txtRepoName.getText().toString();
                String urlPesquisa = creatRepoSearchUrl(termoPesquisa, complementarLista);
                new GetReposTask().execute(urlPesquisa);
            }
        });
    }

    private void restoreData(Bundle savedInstanceState) {

        if(savedInstanceState.containsKey(REPOS_KEY) &&
                savedInstanceState.containsKey(TERMO_PESQUISA_KEY) &&
                savedInstanceState.containsKey(CURRENT_PAGE_KEY) &&
                savedInstanceState.containsKey(TOTAL_COUNT_KEY)) {

            termoPesquisa = savedInstanceState.getString(TERMO_PESQUISA_KEY);
            currentPage = savedInstanceState.getInt(CURRENT_PAGE_KEY);
            total_count = savedInstanceState.getInt(TOTAL_COUNT_KEY);
            nomesRepositorios = savedInstanceState.getStringArrayList(REPOS_KEY);

            lsvRepositorios.setAdapter(new ArrayAdapter(
                    this, android.R.layout.simple_list_item_1, nomesRepositorios));
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putStringArrayList(REPOS_KEY, nomesRepositorios);
        outState.putString(TERMO_PESQUISA_KEY, termoPesquisa);
        outState.putInt(CURRENT_PAGE_KEY, currentPage);
        outState.putInt(TOTAL_COUNT_KEY, total_count);

        super.onSaveInstanceState(outState);
    }

    private String creatRepoSearchUrl(String termoPesquisa, Boolean complementarLista) {

        StringBuffer url = new StringBuffer();

        url.append("https://api.github.com/search/repositories?q=");
        url.append(termoPesquisa.trim());
        url.append("%20in:name");

        if(complementarLista) {
            url.append("&page=" + ++currentPage);
        }

        return url.toString();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) { }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        if((firstVisibleItem + visibleItemCount) >= totalItemCount && !"".equals(termoPesquisa)) {

            if(total_count > currentPage * 30) {

                complementarLista = true;

                String urlPesquisa = creatRepoSearchUrl(termoPesquisa, complementarLista);
                new GetReposTask().execute(urlPesquisa);

            }


        }
    }

    public class GetReposTask extends AsyncTask<String, Integer, String> {

        ProgressDialog progLoadingRepos;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progLoadingRepos = new ProgressDialog(BuscaRepositoriosActivity.this);
            progLoadingRepos.setMessage("Buscando repositórios...");
            progLoadingRepos.show();
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
            progLoadingRepos.dismiss();
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

        nomesRepositorios = strJsonToList(result);

        if(nomesRepositorios.isEmpty()) {
            Toast.makeText(getBaseContext(), "Nenhum repositório encontrado", Toast.LENGTH_LONG).show();
        }

        lsvRepositorios.setAdapter(new ArrayAdapter(
                this, android.R.layout.simple_list_item_1, nomesRepositorios));
    }

    private ArrayList<String> strJsonToList(String strJsonArray) {

        ArrayList<String> nomesRepositorios = new ArrayList<>();

        try {

            JSONObject jsonResponse = new JSONObject(strJsonArray);
            total_count = jsonResponse.getInt("total_count");
            JSONArray jsonRepositories = new JSONArray(jsonResponse.get("items").toString());

            for(int i = 0; i < jsonRepositories.length(); i++) {

                String strRepository = jsonRepositories.get(i).toString();
                JSONObject jsonRepository = new JSONObject(strRepository);

                nomesRepositorios.add(jsonRepository.get("name").toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return nomesRepositorios;
    }
}
