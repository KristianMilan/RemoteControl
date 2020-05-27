package org.vhdl.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Network;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.vhdl.controller.BT.BluetoothChatFragment;
import org.vhdl.controller.ui.main.MainFragment;
import org.vhdl.controller.ui.main.MainViewModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ImageView choose_wifi =(ImageView) findViewById(R.id.connect_wifi);
        ImageView choose_bt =(ImageView) findViewById(R.id.connect_bt);
        //final MainFragment mainFragment = new MainFragment();
        //final BluetoothChatFragment bluetoothChatFragment = new BluetoothChatFragment();
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.connect_wifi){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container,new MainFragment())
                            .commitNow();
                }else if(v.getId()==R.id.connect_bt){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container,new BluetoothChatFragment())
                            .commitNow();
                }
            }
        };
        choose_bt.setOnClickListener(listener);
        choose_wifi.setOnClickListener(listener);
    }
}
