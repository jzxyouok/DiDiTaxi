package com.cuit.diditaxi.activity;

import android.os.Bundle;

import com.cuit.diditaxi.R;
import com.cuit.diditaxi.model.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class PassengerDriverDetailActivity extends BaseActivity {

    private User mDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_driver_detail);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null&&bundle.get("username")!=null){
            String driverUsername = (String) bundle.get("username");

            BmobQuery<User> query = new BmobQuery<>();
            query.addWhereEqualTo("username",driverUsername);
            query.findObjects(getApplicationContext(), new FindListener<User>() {
                @Override
                public void onSuccess(List<User> list) {
                    if (list.size()>0){
                        mDriver = list.get(0);
                        showToastLong(mDriver.toString());
                    }
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        }
    }
}
