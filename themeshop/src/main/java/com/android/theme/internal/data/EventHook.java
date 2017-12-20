package com.android.theme.internal.data;

import android.util.SparseArray;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by song on 2017/3/14.
 */

public class EventHook {
    public interface Hooker {
        boolean onHook(int event, Object... params);
    }

    public static final int EVENT_BACK_PRESSED = 1;
    public static final int EVENT_PAGE_SELECTED = 2;
    public static final int EVENT_DATA_SET_CHANGED = 3;

    private SparseArray<ArrayList<WeakReference<Hooker>>> eventHookers = new SparseArray<>();

    private static final EventHook HOOK = new EventHook();

    public static void register(Hooker hooker, int... events) {
        HOOK.register_(hooker, events);
    }

    private void register_(Hooker hooker, int... events) {
        for (int event : events) {
            ArrayList<WeakReference<Hooker>> hookers = eventHookers.get(event, null);
            if (hookers == null) {
                hookers = new ArrayList<>();
                eventHookers.put(event, hookers);
            }
            hookers.add(new WeakReference<>(hooker));
        }
    }

    public static void unregister(Hooker hooker, int... events) {
        HOOK.unregister_(hooker, events);
    }

    private void unregister_(Hooker hooker, int... events) {
        for (int event : events) {
            ArrayList<WeakReference<Hooker>> hookers = eventHookers.get(event, null);
            if (hookers != null) {
                for (int i = hookers.size() - 1; i >= 0; --i) {
                    final WeakReference<Hooker> r = hookers.get(i);
                    if (hooker == r.get()) {
                        hookers.remove(i);
                    }
                }
            }
        }
    }

    public static boolean hooked(int event, Object... params) {
        return HOOK.hooked_(event, params);
    }

    private boolean hooked_(int event, Object... params) {
        final ArrayList<WeakReference<Hooker>> hookers = eventHookers.get(event, null);
        if (hookers != null) {
            for (WeakReference<Hooker> hooker : hookers) {
                final Hooker h = hooker.get();
                if (h != null && h.onHook(event, params)) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }
}
