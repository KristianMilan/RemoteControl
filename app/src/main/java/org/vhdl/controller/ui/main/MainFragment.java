package org.vhdl.controller.ui.main;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.vhdl.controller.R;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;
    private Button ShowHideButton;
    private CardView card1;
    private TextView localID, connectMode, remoteStatus;
    private EditText additionalAttribute;
    private Switch setConnectMode;
    private Spinner DeviceSelect, OperationSelect;


    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_bluetooth_chat, container, false);

        // 隱藏/顯示第一張卡片的按鍵
        ShowHideButton = root.findViewById(R.id.show_hide_card1);
        card1 = root.findViewById(R.id.card1);
        ShowHideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status = card1.getVisibility();
                if(status == View.VISIBLE) card1.setVisibility(View.GONE);
                else card1.setVisibility(View.VISIBLE);
            }
        });

        setConnectMode = root.findViewById(R.id.switch_control_mode);
        connectMode = root.findViewById(R.id.var_control_mode);

        // 開關改變連線方式
        setConnectMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) MainViewModel.ConnectUseBT();
                else MainViewModel.ConnectUseNetwork();
            }
        });

        localID = root.findViewById(R.id.var_local_id);
        remoteStatus = root.findViewById(R.id.var_device_status);
        additionalAttribute = root.findViewById(R.id.attribute);
        DeviceSelect = root.findViewById(R.id.device_selector);
        OperationSelect = root.findViewById(R.id.operation_selector);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);


        // 當連線方式改變時，改變 textview
        mViewModel.getConnectMode().observe(getViewLifecycleOwner(), new Observer<MainViewModel.Connect_mode>() {
            @Override
            public void onChanged(MainViewModel.Connect_mode connect_mode) {
                if(connect_mode == MainViewModel.Connect_mode.Network){
                    connectMode.setText("Connect via WiFi");
                }
                if(connect_mode == MainViewModel.Connect_mode.BT){
                    connectMode.setText("Connect via BT");
                }
                // TODO: 可能需要一個成員變數指示連線方式
            }
        });

        // 當本機 ID 改變時，改變 textView
        mViewModel.getLocalID().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                localID.setText(s);
            }
        });

        //TODO: 整合那兩個下拉列表
    }

}
