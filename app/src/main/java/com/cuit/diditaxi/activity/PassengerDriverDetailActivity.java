package com.cuit.diditaxi.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cuit.diditaxi.R;
import com.cuit.diditaxi.model.User;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class PassengerDriverDetailActivity extends BaseActivity {

    private User mDriver;

    @Bind(R.id.tv_passenger_driver_detail_name)
    TextView mTvDriverName;

    @Bind(R.id.tv_passenger_driver_detail_car_number)
    TextView mTvCarNumber;

    @Bind(R.id.tv_passenger_driver_detail_tip)
    TextView mTvTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_driver_detail);

        ButterKnife.bind(this);

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
                        //显示接单司机信息
                        mTvDriverName.setText("司机：".concat(mDriver.getNickName()));
                        mTvCarNumber.setText("车牌号：".concat(mDriver.getCarNumber()));
                        mTvTip.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        }
    }
}
