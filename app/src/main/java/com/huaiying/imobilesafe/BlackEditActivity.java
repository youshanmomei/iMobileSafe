package com.huaiying.imobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.huaiying.imobilesafe.db.BlackDao;
import com.huaiying.imobilesafe.db.dao.BlackInfo;

public class BlackEditActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    public final static String ACTION_ADD = "add";
    public final static String ACTION_UPDATE = "update";
    public static final String EXTRA_NUMBEER = "number";
    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_POSITION = "position";
    private BlackDao mDao;
    private TextView mTvTitle;
    private Button mBtnOk;
    private Button mBtnCancel;
    private EditText mEtNumber;
    private RadioGroup mRgType;
    private boolean isUpdate;
    private int mPosition = -1;
    private int mCheckedId = -1; //selected id


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_edit);

        mDao = new BlackDao(this);

        //init view
        mTvTitle = (TextView) findViewById(R.id.be_tv_title);
        mBtnOk = (Button) findViewById(R.id.be_btn_ok);
        mBtnCancel = (Button) findViewById(R.id.be_btn_cancel);
        mEtNumber = (EditText) findViewById(R.id.be_et_number);
        mRgType = (RadioGroup) findViewById(R.id.be_rg_type);

        //set event
        mBtnOk.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        mRgType.setOnCheckedChangeListener(this);

        //judge whether to update or add
        Intent intent = getIntent();
        String action = intent.getAction();

        if (ACTION_UPDATE.equals(action)) {
            isUpdate = true;
            mPosition = intent.getIntExtra(EXTRA_POSITION, -1);

            //update ui&&title
            mTvTitle.setText("更新黑名单");
            //btn
            mBtnOk.setText("更新");

            //et
            mEtNumber.setEnabled(false);

            //data
            //input box - "the number need to update"
            String number = intent.getStringExtra(EXTRA_NUMBEER);
            mEtNumber.setText(number);

            //radio button --- current mode
            int type = intent.getIntExtra(EXTRA_TYPE, -1);

            switch (type) {
                case BlackInfo.TYPE_CALL:
                    mCheckedId = R.id.be_rb_call;
                    break;
                case BlackInfo.TYPE_SMS:
                    mCheckedId = R.id.be_rb_sms;
                    break;
                case BlackInfo.TYPE_ALL:
                    mCheckedId = R.id.be_rb_all;
                    break;
                default:
                    break;
            }
            mRgType.check(mCheckedId); //radioButton id
        } else {
            // add
        }

    }

    @Override
    public void onClick(View v) {
        if (v == mBtnOk) {
            performOk();
        } else if (v == mBtnCancel){
            performCancel();
        }

    }

    private void performOk() {
        //check out
        //input box
        String number = mEtNumber.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "号码不能为空", Toast.LENGTH_SHORT).show();
            mEtNumber.requestFocus();
            return;
        }

        //radio
        if (mCheckedId == -1) {
            Toast.makeText(this, "类型不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        int type = -1;
        switch (mCheckedId) {
            case R.id.be_rb_call: //0
                type = BlackInfo.TYPE_CALL;
                break;
            case R.id.be_rb_sms:
                type = BlackInfo.TYPE_SMS;
                break;
            case R.id.be_rb_all:
                type = BlackInfo.TYPE_ALL;
                break;
        }

        if (isUpdate) {
            //更新
            boolean update = mDao.update(number, type);
            if (update) {
                Intent data = new Intent();
                data.putExtra(EXTRA_POSITION, mPosition);
                data.putExtra(EXTRA_TYPE, type);
                setResult(Activity.RESULT_OK, data);

                Toast.makeText(this, "更行成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            //database operation
            boolean add = mDao.add(number, type);
            if (add) {
                // return data
                Intent data = new Intent();
                data.putExtra(EXTRA_NUMBEER, number);
                data.putExtra(EXTRA_TYPE, type);
                setResult(Activity.RESULT_OK, data);

                Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
            }
        }

        //finish itself
        finish();

    }

    private void performCancel() {
        finish();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        this.mCheckedId = checkedId;
    }
}
