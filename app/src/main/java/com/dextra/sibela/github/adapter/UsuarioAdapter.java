package com.dextra.sibela.github.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dextra.sibela.github.R;
import com.dextra.sibela.github.bean.GithubUser;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

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

        ImageView imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);
        UrlImageViewHelper.setUrlDrawable(imgAvatar, usuario.getAvatar_url(), R.drawable.icon_user_default);

        return view;
    }

    public void addData(List<GithubUser> githubUsers) {

        this.githubUsers.addAll(githubUsers);
        notifyDataSetChanged();
    }
}
