package com.wll.main.model;

/**
 * Created by wll on 2015/11/9.
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import java.lang.reflect.Constructor;

/**
 * 单个选项卡类，每个选项卡包含名字，图标以及提示（可选，默认不显示）
 */
public class TabInfo implements Parcelable {

    private int id;
    private int normalIcon; // 没有选中时的图标
    private int selectedIcon; // 选中时的图标
    private String name = null; // tab的文本
    private Fragment fragment = null; // 关联的Fragment
    public boolean notifyChange = false;
    public Class<? extends Fragment> fragmentClass = null;// 关联的Fragment类

    public TabInfo(int id, String name, Class<? extends Fragment> clazz) {
        this(id, name, 0, clazz);
    }

    public TabInfo(int id, String name, int iconid,
                   Class<? extends Fragment> clazz) {
        this(id, name, iconid, iconid, clazz);
    }

    public TabInfo(int id, String name, int normalIcon, int selectedIcon,
                   Class<? extends Fragment> clazz) {
        this.id = id;
        this.normalIcon = normalIcon;
        this.selectedIcon = selectedIcon;
        if (selectedIcon == 0) {
            this.selectedIcon = normalIcon;
        }
        this.name = name;
        this.fragmentClass = clazz;
    }

    public TabInfo(Parcel p) {
        this.id = p.readInt();
        this.name = p.readString();
        this.normalIcon = p.readInt();
        this.selectedIcon = p.readInt();
        this.notifyChange = p.readInt() == 1;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public int getNormalIcon() {
        return normalIcon;
    }


    public int getSelectedIcon() {
        return selectedIcon;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public Fragment createFragment(int pos) {
        if (fragment == null) {
            Constructor<? extends Fragment> constructor;
            try {
                constructor = fragmentClass.getConstructor(new Class[0]);
                fragment = (Fragment) constructor
                        .newInstance(new Object[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fragment;
    }

    public final Creator<TabInfo> CREATOR = new Creator<TabInfo>() {
        public TabInfo createFromParcel(Parcel p) {
            return new TabInfo(p);
        }

        public TabInfo[] newArray(int size) {
            return new TabInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeInt(id);
        p.writeString(name);
        p.writeInt(normalIcon);
        p.writeInt(selectedIcon);
        p.writeInt(notifyChange ? 1 : 0);
    }

}
