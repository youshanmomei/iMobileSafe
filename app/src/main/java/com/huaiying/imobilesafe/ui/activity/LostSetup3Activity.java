package com.huaiying.imobilesafe.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.ui.base.BaseSetupActivity;
import com.huaiying.imobilesafe.util.Constants;
import com.huaiying.imobilesafe.util.SharePreferenceUtils;

public class LostSetup3Activity extends BaseSetupActivity {

    private static final int REQUESR_CODE_CONTACT = 100;
    private EditText mEtNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_setup3);

        mEtNumber = (EditText) findViewById(R.id.setup3_et_number);

        //set security number
        String number = SharePreferenceUtils.getString(this, Constants.SJFD_NUMBER);
        mEtNumber.setText(number);
        if (!TextUtils.isEmpty(number)) {
            mEtNumber.setSelection(number.length());
        }

    }

    public void clickContact(View view){
        startActivityForResult(new Intent(this, ContactSelectedActivity2.class), REQUESR_CODE_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESR_CODE_CONTACT) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    String number = data.getStringExtra(ContactSelectedActivity2.KEY_NUMBER);
                    mEtNumber.setText(number);
                    if (!TextUtils.isEmpty(number)) {
                        mEtNumber.setSelection(number.length());
                    }
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    protected boolean performPre() {
        startActivity(new Intent(this, LostSetup2Activity.class));
        return false;
    }

    @Override
    protected boolean performNext() {
        // verity that the security number is empty
        String number = mEtNumber.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "如果要开启手机防盗，必须设置安全号码", Toast.LENGTH_SHORT).show();
            return true;
        }

        //record security number
        SharePreferenceUtils.putString(this, Constants.SJFD_NUMBER, number);

        startActivity(new Intent(this, LostSetup4Activity.class));

        return false;
    }
}
