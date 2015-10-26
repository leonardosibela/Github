package com.dextra.sibela.github.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dextra.sibela.github.R;
import com.dextra.sibela.github.bean.GithubUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class UsuarioAdapter extends BaseAdapter {

    private List<GithubUser> githubUsers;
    private Activity activity;

    public UsuarioAdapter(Activity activity, List<GithubUser> githubUsers) {

        this.githubUsers = githubUsers;
        this.activity = activity;

    }

    @Override
    public int getCount() {
        return githubUsers.size();
    }

    @Override
    public Object getItem(int position) {

        return githubUsers.get(position);

    }

    @Override
    public long getItemId(int position) {

        return Long.parseLong(githubUsers.get(position).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = activity.getLayoutInflater().inflate(R.layout.adapter_usuario, null);

        GithubUser usuario = githubUsers.get(position);

        TextView txtNomeUsuario = (TextView) view.findViewById(R.id.txtNomeUsuario);

        txtNomeUsuario.setText(usuario.getLogin());

        return view;
    }

    public void addData(List<GithubUser> githubUsers) {

        this.githubUsers.addAll(githubUsers);
        notifyDataSetChanged();

    }
}
