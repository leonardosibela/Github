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

    private Boolean complementarLista;
    private Integer currentPage = 1;
    private Integer total_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_repositorios);

        btnPesquisar = (Button) findViewById(R.id.btnPesquisar);
        txtRepoName = (EditText) findViewById(R.id.txtRepoName);
        lsvRepositorios = (ListView) findViewById(android.R.id.list);

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

        List<String> nomesRepositorios = strJsonToList(result);

        if(nomesRepositorios.isEmpty()) {
            Toast.makeText(getBaseContext(), "Nenhum repositório encontrado", Toast.LENGTH_LONG).show();
        }

        lsvRepositorios.setAdapter(new ArrayAdapter(
                this, android.R.layout.simple_list_item_1, nomesRepositorios));
    }

    private List<String> strJsonToList(String strJsonArray) {

        List<String> nomesRepositorios = new ArrayList<>();

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
