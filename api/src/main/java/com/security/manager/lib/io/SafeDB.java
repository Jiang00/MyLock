package com.security.manager.lib.io;

import android.content.Context;

import com.security.manager.lib.BaseApp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by song on 15/7/14.
 */
public class SafeDB {
    static final SafeDB ins = new SafeDB();

    public static SafeDB defaultDB() {
        return ins;
    }

    enum DataType {
        INT,
        LONG,
        STRING,
        ARRAY
    }

    public static final int KEY = 1;
    public static final int VALUE = 2;
    Map<String, Integer> integerMap = new HashMap<>();
    Map<String, Long> longMap = new HashMap<>();
    Map<String, String> stringMap = new HashMap<>();
    Map<String, List<?>> arrayMap = new HashMap<>();

    public SafeDB putInt(String key, int value) {
        integerMap.put(key, value);
        return this;
    }

    public int getInt(String key, int def) {
        return integerMap.containsKey(key) ? integerMap.get(key) : def;
    }

    public SafeDB putLong(String key, long value) {
        longMap.put(key, value);
        return this;
    }

    public SafeDB putBool(String key, boolean value) {
        integerMap.put(key, value ? 1 : 0);
        return this;
    }

    public boolean getBool(String key, boolean def) {
        return integerMap.containsKey(key) ? (integerMap.get(key) != 0) : def;
    }

    public long getLong(String key, long def) {
        return longMap.containsKey(key) ? longMap.get(key) : def;
    }

    public SafeDB putString(String key, String value) {
        stringMap.put(key, value);
        return this;
    }

    public String getString(String key, String def) {
        return stringMap.containsKey(key) ? stringMap.get(key) : def;
    }

    public SafeDB putArray(String key, List<?> value) {
        arrayMap.put(key, value);
        return this;
    }

    public List<?> getArray(String key) {
        return arrayMap.get(key);
    }

    private SafeDB() {
        try {
            FileInputStream fis = BaseApp.getContext().openFileInput("_safe_");
            DataInputStream dis = new DataInputStream(fis);
            while (dis.available() > 0) {
                int ordinal = dis.readInt();
                DataType type = DataType.values()[ordinal];
                int count = dis.readInt();
                switch (type) {
                    case INT:
                        for (int i = 0; i < count; ++i) {
                            dis.readInt();//KEY
                            String key = dis.readUTF();
                            dis.readInt();//VALUE
                            int value = dis.readInt();
                            integerMap.put(key, value);
                        }
                        break;

                    case LONG:
                        for (int i = 0; i < count; ++i) {
                            dis.readInt();//KEY
                            String key = dis.readUTF();
                            dis.readInt();//VALUE
                            long value = dis.readLong();
                            longMap.put(key, value);
                        }
                        break;

                    case STRING:
                        for (int i = 0; i < count; ++i) {
                            dis.readInt();//KEY
                            String key = dis.readUTF();
                            dis.readInt();//VALUE
                            String value = dis.readUTF();
                            stringMap.put(key, value);
                        }
                        break;

                    case ARRAY:
                        for (int i = 0; i < count; ++i) {
                            dis.readInt();//KEY
                            String key = dis.readUTF();
                            dis.readInt();//VALUE
                            int valueSize = dis.readInt();
                            if (valueSize != 0) {
                                int valueOrdinal = dis.readInt();
                                DataType valueType = DataType.values()[valueOrdinal];
                                readArray(dis, key, arrayMap, valueType, valueSize);
                            }
                        }
                        break;
                }
            }
            dis.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readArray(DataInputStream dis, String key, Map<String, List<?>> map, DataType type, int count) throws IOException {
        switch (type) {
            case INT: {
                List<Integer> list = new ArrayList<>();
                for (int i = 0; i < count; ++i) {
                    list.add(dis.readInt());
                }
                map.put(key, list);
            }
            break;

            case STRING: {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < count; ++i) {
                    list.add(dis.readUTF());
                }
                map.put(key, list);
            }
            break;

            case LONG: {
                List<Long> list = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    list.add(dis.readLong());
                }
                map.put(key, list);
            }
            break;
        }
    }

    public void commit() {
        try {
            FileOutputStream fos = BaseApp.getContext().openFileOutput("_safe_", Context.MODE_PRIVATE);
            DataOutputStream dos = new DataOutputStream(fos);
            saveMap(dos, integerMap, DataType.INT);
            saveMap(dos, longMap, DataType.LONG);
            saveMap(dos, arrayMap, DataType.ARRAY);
            saveMap(dos, stringMap, DataType.STRING);
            dos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveMap(DataOutputStream dos, Map<String, ?> map, DataType type) throws IOException {
        if (map.size() == 0) return;
        dos.writeInt(type.ordinal());
        dos.writeInt(map.size());
        Set<String> keys = map.keySet();
        for (String key : keys) {
            try {
                Object value = map.get(key);
                dos.writeInt(KEY);
                dos.writeUTF(key);
                dos.writeInt(VALUE);
                switch (type) {
                    case INT:
                        dos.writeInt((Integer) value);
                        break;

                    case STRING:
                        dos.writeUTF((String) value);
                        break;

                    case ARRAY:
                        if (value == null) {
                            dos.writeInt(0);
                        } else {
                            writeArray(dos, (List<?>) value);
                        }
                        break;

                    case LONG:
                        dos.writeLong((Long) value);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void writeArray(DataOutputStream dos, List<?> values) throws IOException {
        int count = values.size();
        dos.writeInt(count);
        if (count == 0) return;

        Object v = values.get(0);
        if (v instanceof Integer) {
            dos.writeInt(DataType.INT.ordinal());
            for (Object i : values) {
                dos.writeInt((Integer) i);
            }
        } else if (v instanceof Long) {
            dos.writeInt(DataType.LONG.ordinal());
            for (Object l : values) {
                dos.writeLong((Long) l);
            }
        } else if (v instanceof String) {
            dos.writeInt(DataType.STRING.ordinal());
            for (Object str : values) {
                dos.writeUTF((String) str);
            }
        } else {
            throw new RuntimeException("Unknown data type " + values);
        }
    }
}
