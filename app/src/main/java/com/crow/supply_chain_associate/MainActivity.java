package com.crow.supply_chain_associate;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.crow.supply_chain_associate.dto.User;
import com.crow.supply_chain_associate.service.APIClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private TextView userNameField,passwordField;
    private Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userNameField=(TextView) findViewById(R.id.userNameId);
        passwordField=(TextView) findViewById(R.id.passwordId);
        loginButton=(Button) findViewById(R.id.signinBtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });
    }

    public void login(View view){
        String userName=userNameField.getText().toString();
        String password=passwordField.getText().toString();
        if(userName.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
        }else {
            login(userName,password);
        }
    }

    public void login(String userName,String password){
        User user=new User();
        user.setUserName(userName);
        user.setPassword(password);
        Call<Object> call= APIClient.getInstance().getApi().loginUser(user);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Intent intent=new Intent(MainActivity.this,RegisterProductActivity.class);
//                intent.putExtra("userName",user.getUserName());
//                intent.putExtra("password",user.getPassword());
                startActivity(intent);
            }
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("Failure",t.getMessage());
            }
        });

    }
}