package org.vhdl.controller.ui.main;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;
import org.vhdl.controller.R;
import org.vhdl.controller.utils.JsonDecoder;
import org.vhdl.controller.utils.JsonMaker;
import org.vhdl.controller.utils.PostUtil;
import org.vhdl.controller.utils.ShowDialog;
import org.vhdl.controller.utils.SupportedAction;

import java.util.List;

public class MainFragment extends Fragment {

    private static MainViewModel mViewModel;
    private Button ShowHideButton, applyServerAddr;
    private ImageButton doAction;
    private CardView card1;
    private TextView localID, connectMode, remoteStatus;
    private EditText additionalAttribute, serverUrl;
    private Switch setConnectMode;
    private Spinner DeviceSelect, OperationSelect;
    private static boolean ifWifi = true;
    private String action = "command";
    private static Thread Polling;


    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.main_fragment, container, false);

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

        // 伺服器連線地址
        applyServerAddr = root.findViewById(R.id.apply_server_url);
        serverUrl = root.findViewById(R.id.input_server_addr);
        applyServerAddr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("button", "apply");
                String url = serverUrl.getText().toString();
                MainViewModel.setServerURL(url);
            }
        });

        // 選擇設備下拉
        DeviceSelect = root.findViewById(R.id.device_selector);
        DeviceSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MainViewModel.setRemoteID(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // 點擊的時候刷新一次裝置列表
        root.findViewById(R.id.refresh_device_list_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("button", "refresh");

                if(ifWifi)new GetDevicesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });


        // 選擇操作下拉
        OperationSelect = root.findViewById(R.id.operation_selector);
        OperationSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                action = SupportedAction.getAllowed()[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ArrayAdapter<String> OpAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, SupportedAction.getAllowed());
        OperationSelect.setAdapter(OpAdapter);

        // 設定執行按鍵
        doAction = root.findViewById(R.id.doAction);
        doAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("button", "send");

                if(ifWifi)sendOperationViaWifi();
            }
        });

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
                    ifWifi = true;

                }
                if(connect_mode == MainViewModel.Connect_mode.BT){
                    connectMode.setText("Connect via BT");
                    ifWifi = false;
                }

            }
        });

        // 當本機 ID 改變時，改變 textView
        mViewModel.getLocalID().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s != null && s.length() != 0)localID.setText(s);

            }
        });

        // 伺服器地址輸入框
        mViewModel.getServerURL().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                serverUrl.setText(s);
                if(ifWifi)reconnectViaWifi();
            }
        });
        // 下拉列表
        mViewModel.getRemoteList().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_dropdown_item_1line, strings);
                DeviceSelect.setAdapter(adapter);
            }
        });
        // 選定了遠端裝置
        mViewModel.getRemoteID().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                remoteStatus.setText(s);
            }
        });
        // 發送操作後顯示對話框
        mViewModel.getShouldDialog().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(!aBoolean) return;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(MainViewModel.getDialogTitle().getValue());
                builder.setMessage(MainViewModel.getDialogmsg().getValue());
                builder.setPositiveButton("ao", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        Polling = new LongPolling();
    }

    private void reconnectViaWifi(){
        new RegisterTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
    private void sendOperationViaWifi(){
        String action = this.action;
        String additional = additionalAttribute.getText().toString();
        // String[] param = {action, additional};
        new SendOpTask().execute(action, additional);
    }
    private static class RegisterTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            Log.d("async", "register start");
            JsonMaker maker = new JsonMaker();
            maker.setAction("register");
            maker.setType("controller");
            String Jsontosend;
            try {
                Jsontosend = maker.getJsonStr();
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            String serverurl = mViewModel.getServerURL().getValue();
            String resultjson =  PostUtil.sendPost(serverurl, Jsontosend);
            JsonDecoder decoder;
            try {
                decoder = new JsonDecoder(resultjson);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return decoder.querys[0];
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            MainViewModel.setLocalID(s);
            new GetDevicesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            if(Polling.isAlive())Polling.interrupt();
            Polling = new LongPolling();
            Polling.start();
        }
    }
    private static class GetDevicesTask extends AsyncTask<Void, Void, String[]>{

        @Override
        protected String[] doInBackground(Void... voids) {
            Log.d("async", "get devices start");

            JsonMaker maker = new JsonMaker();
            maker.setAction("request");
            maker.setID(mViewModel.getLocalID().getValue(), null);
            maker.setType("peer_finding");
            String Jsontosend;
            try {
                Jsontosend = maker.getJsonStr();
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            String serverurl = mViewModel.getServerURL().getValue();
            String resultjson =  PostUtil.sendPost(serverurl, Jsontosend);
            JsonDecoder decoder;
            try {
                decoder = new JsonDecoder(resultjson);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return decoder.querys;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            MainViewModel.setRemoteList(strings);
        }
    }
    private static class SendOpTask extends AsyncTask<String, Void, String[]>{
        @Override
        protected String[] doInBackground(String... strings) {
            Log.d("async", "send operation start");

            JsonMaker maker = new JsonMaker();
            maker.setAction(strings[0]);
            maker.setID(mViewModel.getLocalID().getValue(), mViewModel.getRemoteID().getValue());
            maker.setType(strings[1]);
            String Jsontosend;
            try {
                Jsontosend = maker.getJsonStr();
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            String serverurl = mViewModel.getServerURL().getValue();
            String resultjson =  PostUtil.sendPost(serverurl, Jsontosend);
            //Log.d("Json", resultjson);
            JsonDecoder decoder;
            try {
                decoder = new JsonDecoder(resultjson);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            String[] result = {decoder.fromID, decoder.action, decoder.type};
            return result;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            if(result == null){
                Log.d("error", "responsed json cannot read");
                MainViewModel.setDialog("最後警告，，，", "Server Error");
                MainViewModel.ShowDialog();
            }
            else {
                if(!result[1].equals("error"))return;
                MainViewModel.setDialog("最後警告，，，", "From: " + result[0]
                + "\nAction: " + result[1] + "\nType: " + result[2]);
                MainViewModel.ShowDialog();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private static Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.d("thread", "thread Message");

            String[] result = msg.getData().getStringArray("POLL_RESULT");
            if(result == null){
                Log.d("request", "no response");
            }
            else {
                MainViewModel.setDialog("高雅電文", "From: " + result[0]
                        + "\nAction: " + result[1] + "\nType: " + result[2]);
                MainViewModel.ShowDialog();
            }
            try {
                if (result == null && result[1].equals("error"))
                    if (Polling.isAlive()) Polling.interrupt();
            }catch (Exception e){
                e.printStackTrace();
                if (Polling.isAlive()) Polling.interrupt();

            }
            //if(Polling.isAlive())Polling.stop();
            //if(ifWifi)Polling.run();
        }
    };
    static class LongPolling extends Thread {

        @Override
        public void run() {
            super.run();
            while (true){
                try{
                    String[] result = worker();

                    Bundle mbundle = new Bundle();
                    mbundle.putCharSequenceArray("POLL_RESULT", result);
                    Message msg = new Message();
                    msg.setData(mbundle);
                    mHandler.sendMessage(msg);
                    if(result[1].equals("error"))throw new InterruptedException();
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d("thread", "worker exception");

                    break;
                }
            }
        }
        private String[] worker(){
            JsonMaker maker = new JsonMaker();
            Log.d("thread", "worker start");
            maker.setAction("request");
            maker.setID(mViewModel.getLocalID().getValue(), null);
            String Jsontosend;
            try {
                Jsontosend = maker.getJsonStr();
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            String serverurl = mViewModel.getServerURL().getValue();
            String resultjson;
            try{
                resultjson =  PostUtil.sendPost(serverurl, Jsontosend);
            }catch (Exception e){
                return null;
            }
            Log.d("request", resultjson);
            JsonDecoder decoder;
            try {
                decoder = new JsonDecoder(resultjson);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            String[] result = {decoder.fromID, decoder.action, decoder.type};
            return result;
        }
    }
}
