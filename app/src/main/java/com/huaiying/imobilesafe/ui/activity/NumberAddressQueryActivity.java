package com.huaiying.imobilesafe.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huaiying.imobilesafe.R;

public class NumberAddressQueryActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtNumber;
    private Button mBtnQuery;
    private TextView mTvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_address_query);

        mEtNumber = (EditText) findViewById(R.id.naq_et_number);
        mBtnQuery = (Button) findViewById(R.id.naq_btn_query);
        mTvAddress = (TextView) findViewById(R.id.naq_tv_address);

        mBtnQuery.setOnClickListener(this);

        mEtNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                String address = AddressDao.findAddress(getApplicationContext(), s.toString()); TODO AddressDao
//                mTvAddress.setText("归属地: " + address);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnQuery) {
            doQuery();
        }
    }

    private void doQuery() {
        //no empty check
        String number = mEtNumber.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "号码不能为空", Toast.LENGTH_SHORT).show();

            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            mEtNumber.startAnimation(shake);
            return;
        }

//        String address = AddressDao.findAddress(this, number);
//        mTvAddress.setText("归属地：" + address);
    }
}
