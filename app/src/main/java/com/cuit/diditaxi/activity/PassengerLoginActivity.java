package com.cuit.diditaxi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.cuit.diditaxi.R;
import com.cuit.diditaxi.model.User;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.listener.SaveListener;

public class PassengerLoginActivity extends BaseActivity {

    @Bind(R.id.et_passenger_login_username)
    EditText mEtUsername;

    @Bind(R.id.et_passenger_login_password)
    EditText mEtPassword;

    @Bind(R.id.btn_passenger_login)
    Button mBtnLogin;

    @OnClick(R.id.btn_passenger_login)
    void login() {
        mBtnLogin.setEnabled(false);

        final User user = new User();
        user.setUsername(mEtUsername.getText().toString());
        user.setPassword(mEtPassword.getText().toString());
        user.login(PassengerLoginActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {
                mBtnLogin.setEnabled(true);

                Intent intent = new Intent();
                intent.setClass(PassengerLoginActivity.this, PassengerMainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(int i, String s) {
                mBtnLogin.setEnabled(true);
                showToastLong(s);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_login);

        ButterKnife.bind(this);
    }
}