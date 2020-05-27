package org.vhdl.controller.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


public class MainViewModel extends AndroidViewModel {
    enum Connect_mode{
        Network,
        BT;
    }
    private Application application;
    private static MutableLiveData<Connect_mode> mConnectMode = null;
    private MutableLiveData<String> LocalID = new MutableLiveData<>();
    private MutableLiveData<String> RemoteID = new MutableLiveData<>();
    // TODO: 需要兩個列表


    public MainViewModel(@NonNull Application application) {
        super(application);
        this.application = application;

        if (mConnectMode == null) {
            mConnectMode = new MutableLiveData<>();
            mConnectMode.setValue(Connect_mode.Network);
        }
    }

    // 連線類型相關
    public static void ConnectUseBT(){mConnectMode.setValue(Connect_mode.BT);}

    public static void ConnectUseNetwork(){mConnectMode.setValue(Connect_mode.Network);}

    public MutableLiveData<Connect_mode> getConnectMode(){return mConnectMode;}

    // ID 相關
    public void setLocalID(String id){this.LocalID.setValue(id);}

    public void setRemoteID(String id){this.RemoteID.setValue(id);}

    public MutableLiveData<String> getLocalID(){return this.LocalID;}

    public MutableLiveData<String> getRemoteID(){return this.RemoteID;}

}
