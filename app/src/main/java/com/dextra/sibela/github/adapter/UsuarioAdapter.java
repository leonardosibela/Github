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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class UsuarioAdapter extends BaseAdapter {

    private JSONArray jsonUsuarios;
    private Activity activity;

    public UsuarioAdapter(Activity activity, JSONArray jsonUsuarios) {

        this.jsonUsuarios = jsonUsuarios;
        this.activity = activity;

    }

    @Override
    public int getCount() {
        return jsonUsuarios.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return jsonUsuarios.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public long getItemId(int position) {

        try {
            JSONObject usuario = (JSONObject) jsonUsuarios.get(position);
            return usuario.getLong("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = activity.getLayoutInflater().inflate(R.layout.adapter_usuario, null);

        try {

            JSONObject usuario = (JSONObject) jsonUsuarios.get(position);

            TextView txtNomeUsuario = (TextView) view.findViewById(R.id.txtNomeUsuario);
            ImageView imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);

            txtNomeUsuario.setText(usuario.getString("login"));

            URL avatar_url = new URL(usuario.getString("avatar_url"));
            Bitmap avatar = BitmapFactory.decodeStream(avatar_url.openConnection().getInputStream());
            imgAvatar.setImageBitmap(avatar);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }
}
