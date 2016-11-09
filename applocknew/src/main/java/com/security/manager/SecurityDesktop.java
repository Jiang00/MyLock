package com.security.manager;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.privacy.lock.R;
import com.security.lib.customview.SecurityWidget;
import com.security.manager.meta.SecurityMyPref;
import com.security.manager.page.WidgetSwitch;
import com.security.manager.lib.io.SafeDB;
import com.privacy.lock.aidl.IWorker;
import com.security.manager.meta.SecuritProfiles;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SongHualin on 4/21/2015.
 */
public class SecurityDesktop {
    static class WidgetAdapter extends BaseAdapter {
        public List<WidgetSwitch.Data> data;
        public Context context;
        SecurityWidget parent;
        IWorker worker;

        public void notifyDataSetChanged(List<WidgetSwitch.Data> data){
            if (this.data != null && this.data.size() > 0){
                this.data.clear();
            }
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            WidgetSwitch sw;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.security_mywidget_switch, parent, false);
                sw = new WidgetSwitch(convertView);
                convertView.setTag(sw);
            } else {
                sw = (WidgetSwitch) convertView.getTag();
            }

            sw.idx = position;

            WidgetSwitch.Data d = data.get(position);
            sw.title.setText(d.title);
            sw.icon.setImageResource(d.icon);
            if (d.active) {
                sw.selected(d.filter);
            } else {
                sw.unselected(d.filter);
            }

            return convertView;
        }

        public AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p, View view, int position, long id) {
                final WidgetSwitch.Data d = data.get(position);
                switch (position) {
                    case 0:
                        try {
                            if (d.active) {
                                context.startActivity(new Intent(context,
                                        SecurityTogglePatternActivity.class).putExtra("action", SecurityTogglePatternActivity.ACTION_TOGGLE_PROTECT).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                parent.listener.onBackPressed();
                            } else {
                                d.active = true;
                                SecurityMyPref.stopProtect(false);
                                worker.updateProtectStatus();
                                if (App.getSharedPreferences().getBoolean("sn", false)) {
                                    worker.showNotification(true);
                                }
                                notifyDataSetChanged();
                                d.title = context.getString(R.string.security_protecting);
                                Toast.makeText(context, R.string.security_protecting, Toast.LENGTH_SHORT).show();
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;

                    default:
                        if (!d.active) {
                            App.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    context.startActivity(new Intent(context,
                                            SecurityTogglePatternActivity.class).putExtra("action", SecurityTogglePatternActivity.ACTION_SWITCH_PROFILE).putExtra("profileName", d.title).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    parent.listener.onBackPressed();
                                }
                            });
                        }
                        break;
                }
            }
        };
    }

    public static void updateData(SecurityWidget container){
        try{
            WidgetAdapter adapter = (WidgetAdapter) ((GridView)container.findViewById(R.id.switches)).getAdapter();
            adapter.notifyDataSetChanged(updateData(adapter.context));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static View getView(final Context context, final SecurityWidget parent, final IWorker worker) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View v = inflater.inflate(R.layout.security_mywidget_container, parent, false);

        v.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.listener.onBackPressed();
            }
        });

        final GridView gv = (GridView) v.findViewById(R.id.switches);

        List<WidgetSwitch.Data> data = updateData(context);
        WidgetAdapter adapter = new WidgetAdapter();
        adapter.context = context;
        adapter.data = data;
        adapter.parent = parent;
        adapter.worker = worker;

        gv.setAdapter(adapter);
        gv.setOnItemClickListener(adapter.listener);

        return v;
    }

    public static List<WidgetSwitch.Data> updateData(Context context) {
        List<WidgetSwitch.Data> data = new ArrayList<>();

        WidgetSwitch.Data toggleProtect = new WidgetSwitch.Data(
                context.getResources().getString(!SecurityMyPref.isProtectStopped() ? R.string.security_protecting : R.string.security_pause_protect),
                R.drawable.security_protect_switch, !SecurityMyPref.isProtectStopped(), true);

        data.add(toggleProtect);

        String activeProfile = SafeDB.defaultDB().getString(SecurityMyPref.PREF_ACTIVE_PROFILE, SecurityMyPref.PREF_DEFAULT_LOCK);

        for (String profile : SecuritProfiles.getProfiles()) {
            WidgetSwitch.Data d = new WidgetSwitch.Data(profile, R.drawable.ic_launcher, profile.equals(activeProfile), true);
            data.add(d);
        }

        return data;
    }
}
