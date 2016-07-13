package com.xcccf.client.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.xcccf.client.android.model.RequestData;
import com.xcccf.client.android.model.ResponseData;
import com.xcccf.client.android.util.Config;
import com.xcccf.client.android.util.DataUtil;
import com.xcccf.client.android.util.MD5;
import com.xcccf.client.android.util.MessageSender;
import com.xcccf.client.android.util.MoneyFilter;
import com.google.gson.Gson;

/**
 * Created by Administrator on 2016/7/10.
 */
public class Recharge extends Activity implements TradePwdPopUtils.CallBackTradePwd {

    private EditText rechargeSum;
    private TradePwdPopUtils pop;
    private LinearLayout lin;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recharge);
        initView();
    }

    public void initView(){
        rechargeSum = (EditText) findViewById(R.id.recharge_sum);
        pop = new TradePwdPopUtils();
        pop.setCallBackTradePwd(this);
        lin = (LinearLayout) findViewById(R.id.recharge_lin);
        InputFilter[] filters = {new MoneyFilter()};
        rechargeSum.setFilters(filters);
    }

    public void rechargeCommit(View view){
        String sum = rechargeSum.getText().toString();
        if(!sum.trim().equals(""))
            pop.showPopWindow(this, this, lin);
        else{
            Toast.makeText(this, "Please input valid number", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void callBaceTradePwd(String pwd) {
        String sum = rechargeSum.getText().toString();
        double money = Double.parseDouble(sum);
        String paywordMD5 = MD5.EncodeByMd5(pwd);
        System.out.println(pwd);
        RequestData requestData = new RequestData();
        requestData.setToken(DataUtil.payToken).setAmount(money).setPayword(paywordMD5);
        Gson gson = new Gson();
        String param = gson.toJson(requestData);
        System.out.println(param);
        String response = MessageSender.sendMessage(Config.getRechargeUrl(),param);
        ResponseData responseData = gson.fromJson(response, ResponseData.class);

        System.out.println(response);
        System.out.println(responseData);

        if(responseData.getError() == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Recharge successfully！");
            builder.setTitle("Tip");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Recharge.this.finish();
                }
            });
            builder.show();
        }else{
            Toast.makeText(this, responseData.getError().getMessage(), Toast.LENGTH_LONG).show();
            rechargeSum.setText("");
        }

    }
}
