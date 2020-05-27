package org.vhdl.controller.ui.main;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;


public class MainViewModel extends AndroidViewModel {
    public enum Connect_mode{
        Network,
        BT;
    }
    private static Application application;
    private static MutableLiveData<Connect_mode> mConnectMode = null;
    private static MutableLiveData<String> LocalID = new MutableLiveData<>();
    private static MutableLiveData<String> RemoteID = new MutableLiveData<>();
    private static MutableLiveData<String> ServerURL = new MutableLiveData<>();
    private static SharedPreferences sharedpre;
    private static SharedPreferences.Editor editor;
    // 列表
    private static MutableLiveData<List<String>> RemoteList = new MutableLiveData<>();
    // 對話框
    private static MutableLiveData<String> mDialogTitle = new MutableLiveData<>();
    private static MutableLiveData<String> mDialogmsg = new MutableLiveData<>();
    private static MutableLiveData<Boolean> shouldDialog = new MutableLiveData<>(false);


    public MainViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        sharedpre = application.getSharedPreferences("DEFAULT", Context.MODE_PRIVATE);

        if (mConnectMode == null) {
            mConnectMode = new MutableLiveData<>();
            mConnectMode.setValue(Connect_mode.Network);
        }
        String serverurl = sharedpre.getString("SERVER_URL", "http://home.slawn64.cf:8086/json");
        ServerURL.setValue(serverurl);
    }

    // 伺服器地址
    public static void setServerURL(String url){
        ServerURL.setValue(url);
        editor = sharedpre.edit();
        editor.putString("SERVER_URL", url);
        editor.apply();
    }

    public MutableLiveData<String> getServerURL(){return ServerURL;}

    // 連線類型相關
    public static void ConnectUseBT(){mConnectMode.setValue(Connect_mode.BT);}

    public static void ConnectUseNetwork(){mConnectMode.setValue(Connect_mode.Network);}

    public MutableLiveData<Connect_mode> getConnectMode(){return mConnectMode;}

    // ID 相關
    public static void setLocalID(String id){LocalID.setValue(id);}

    public static void setRemoteID(String id){RemoteID.setValue(id);}

    public static void setRemoteID(int position){
        String needCutControl = RemoteList.getValue().get(position);
        needCutControl =  needCutControl.replaceAll("controlled|controller", "");
        RemoteID.setValue(needCutControl);
    }

    public MutableLiveData<String> getLocalID(){return LocalID;}

    public MutableLiveData<String> getRemoteID(){return RemoteID;}

    // 列表相關
    public static void setRemoteList(@Nullable String[] querysFromQueryObj){
        ArrayList<String> mList = new ArrayList<String>();
        if(querysFromQueryObj != null && querysFromQueryObj.length !=0){
            for(int i = 0; i < querysFromQueryObj.length; i++){
                if(querysFromQueryObj[i].equals("controlled")||
                        querysFromQueryObj[i].equals("controller")){

                    mList.add(querysFromQueryObj[i] + querysFromQueryObj[++i]);

                    continue;
                }
                i++;
            }
        }
        RemoteList.setValue(mList);
    }

    public MutableLiveData<List<String>> getRemoteList(){return RemoteList;}

    // 對話框
    public static void setDialog(String title, String msg){
        mDialogTitle.setValue(title);
        mDialogmsg.setValue(msg);
    }

    public static MutableLiveData<String> getDialogTitle(){return mDialogTitle;}

    public static MutableLiveData<String> getDialogmsg(){return mDialogmsg;}

    public static void ShowDialog(){shouldDialog.setValue(true);shouldDialog.setValue(false);}

    public MutableLiveData<Boolean> getShouldDialog(){return shouldDialog;}
}
