package com.security.manager;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import com.privacy.lock.aidl.IWorker;

/**
 * Created by SongHualin on 6/26/2015.
 */
public abstract class ClientActivitySecurity extends SecurityAbsActivity implements ServiceConnection {
    public IWorker server;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        server = IWorker.Stub.asInterface(service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        server = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (!bindService(new Intent(this, SecurityService.class), this, 0)){
                Toast.makeText(this, "服务没有启动", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        try {
            unbindService(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }
}
