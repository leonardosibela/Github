package com.dextra.sibela.github;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dextra.sibela.github.bean.GithubUser;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

public class DadosUsuarioActivity extends AppCompatActivity {

    private ImageView imgFullAvatar;
    private TextView login;
    private TextView tipo;
    private SeekBar skbScore;
    private TextView edtScore;

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

        skbScore = (SeekBar) findViewById(R.id.skbScore);
        skbScore.setProgress(usuario.getScore().intValue());

        edtScore = (TextView) findViewById(R.id.edtScore);
        edtScore.setText(usuario.getScore().toString());

        skbScore.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }
}
