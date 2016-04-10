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
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

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
        closeKeyboard();

        final User user = new User();
        user.setUsername(mEtUsername.getText().toString());
        user.setPassword(mEtPassword.getText().toString());
        user.login(PassengerLoginActivity.this, new SaveListener() {
            @Override
            public void onSuccess() {

                //登录JMessage
                JMessageClient.login(mEtUsername.getText().toString(), "111111", new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        mBtnLogin.setEnabled(true);

                        if (i == 0) {
                            Intent intent = new Intent();
                            intent.setClass(PassengerLoginActivity.this, PassengerMainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            showToastLong(s.concat("------").concat(String.valueOf(i)));
                        }
                    }
                });
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

        //判断是否已登录
        if (BmobUser.getCurrentUser(PassengerLoginActivity.this, User.class) != null
                &&JMessageClient.getMyInfo()!=null) {

            Intent intent = new Intent(PassengerLoginActivity.this, PassengerMainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
