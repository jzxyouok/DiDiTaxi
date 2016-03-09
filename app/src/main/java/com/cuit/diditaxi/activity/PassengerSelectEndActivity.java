package com.cuit.diditaxi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.cuit.diditaxi.R;
import com.cuit.diditaxi.adapter.EndRecommendAdapter;
import com.cuit.diditaxi.view.ListRecyclerViewDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PassengerSelectEndActivity extends BaseActivity {

    //城市
    private String mCity;

    @Bind(R.id.btn_select_end_back)
    ImageButton mBtnBack;

    @OnClick(R.id.btn_select_end_back)
    void back() {
        finish();
    }

    @Bind(R.id.et_select_end_keyword)
    EditText mEtKeyword;

    @Bind(R.id.btn_select_end_search)
    Button mBtnSearch;

    @OnClick(R.id.btn_select_end_search)
    void search() {

        if (TextUtils.isEmpty(mEtKeyword.getText())) {
            showToastShort("请输入目的地");
        } else {

            if (mCity != null) {
                String searchKeyword = mEtKeyword.getText().toString().trim();
                //输入提示
                InputtipsQuery inputTipsQuery = new InputtipsQuery(searchKeyword, mCity);
                //严格城市限制,默认false
                inputTipsQuery.setCityLimit(true);
                Inputtips inputTips = new Inputtips(this, inputTipsQuery);
                inputTips.requestInputtipsAsyn();
                inputTips.setInputtipsListener(new Inputtips.InputtipsListener() {
                    @Override
                    public void onGetInputtips(final List<Tip> list, int i) {

                        if (i == 0) {
                            if (list.size() > 0) {

                                List<String> tipNameList = new ArrayList<>();

                                for (Tip tip : list) {
                                    tipNameList.add(tip.getName());
                                }

                                mAdapter = new EndRecommendAdapter(PassengerSelectEndActivity.this, tipNameList);
                                mRvRecommend.setAdapter(mAdapter);
                                mAdapter.setOnItemClickListener(new EndRecommendAdapter.OnItemClickListener() {
                                    @Override
                                    public void itemLongClick(View view, int position) {

                                        //点击推荐位置，回到地图界面
                                        Intent intent = getIntent();
                                        intent.putExtra("tip",list.get(position));
                                        setResult(0, intent);
                                        finish();
                                    }
                                });

                            }
                        }
                    }
                });
            }
        }//search end
    }

    @Bind(R.id.rv_select_end_recommend_position)
    RecyclerView mRvRecommend;
    private EndRecommendAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_select_end);

        ButterKnife.bind(this);

        mCity = getIntent().getStringExtra("city");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PassengerSelectEndActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvRecommend.setLayoutManager(linearLayoutManager);
        ListRecyclerViewDivider itemDivider = new ListRecyclerViewDivider(PassengerSelectEndActivity.this, LinearLayoutManager.VERTICAL);
        mRvRecommend.addItemDecoration(itemDivider);
    }
}
