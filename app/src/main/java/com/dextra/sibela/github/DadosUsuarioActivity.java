package com.dextra.sibela.github;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dextra.sibela.github.bean.GithubUser;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

public class DadosUsuarioActivity extends AppCompatActivity {

    private ImageView imgFullAvatar;
    private TextView login;
    private TextView tipo;
    private SeekBar score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_usuario);

        Bundle data = getIntent().getExtras();
        GithubUser usuario = data.getParcelable("usuario");

        imgFullAvatar = (ImageView) findViewById(R.id.imgFullAvatar);
        UrlImageViewHelper.setUrlDrawable(imgFullAvatar, usuario.getAvatar_url());

        login = (TextView) findViewById(R.id.login);
        login.setText(usuario.getLogin());

        tipo = (TextView) findViewById(R.id.tipo);
        tipo.setText(usuario.getType());

        score = (SeekBar) findViewById(R.id.skbScore);
        score.setProgress(usuario.getScore().intValue());
    }
}
