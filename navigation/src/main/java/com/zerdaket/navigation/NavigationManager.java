package com.zerdaket.navigation;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.collection.SparseArrayCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

/**
 * Created by zerdaket on 2019-11-14.
 */
public class NavigationManager {

    private NavigationBar mNavigationBar;
    private FragmentManager mFragmentManager;
    private int mContainerViewId;
    private int mCurrentSelectedId = View.NO_ID;

    private SparseArrayCompat<FragmentInfo> mInfoSparseArray;

    /**
     * @param bar             底部导航栏
     * @param manager         FragmentManager
     * @param containerViewId 展示 Fragment 的 View Id
     */
    public NavigationManager(NavigationBar bar, FragmentManager manager, @IdRes int containerViewId) {
        mNavigationBar = bar;
        mFragmentManager = manager;
        mContainerViewId = containerViewId;
        mInfoSparseArray = new SparseArrayCompat<>();
        mNavigationBar.setOnSelectedChangeManagerListener(new SelectedIdChangeListener());
    }

    private void showFragment(FragmentInfo info) {
        if (mContainerViewId == View.NO_ID || mFragmentManager == null || info == null) {
            return;
        }
        if (info.fragment == null) {
            info.fragment = mFragmentManager.findFragmentByTag(info.tag);
        }
        if (info.fragment == null) {
            try {
                info.fragment = (Fragment) info.clazz.newInstance();
                if (info.args != null) {
                    info.fragment.setArguments(info.args);
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (info.fragment == null) {
            return;
        }
        if (info.fragment.isAdded()) {
            mFragmentManager.beginTransaction().show(info.fragment).commit();
        } else {
            mFragmentManager.beginTransaction().add(mContainerViewId, info.fragment, info.tag).commit();
        }
    }

    private void hideFragment(FragmentInfo info) {
        if (mFragmentManager == null || info == null) {
            return;
        }
        if (info.fragment == null) {
            return;
        }
        if (!info.fragment.isHidden()) {
            mFragmentManager.beginTransaction().hide(info.fragment).commit();
        }
    }

    private void switchToFragment(int selectedId) {
        hideFragment(mInfoSparseArray.get(mCurrentSelectedId));
        showFragment(mInfoSparseArray.get(selectedId));
        mCurrentSelectedId = selectedId;
    }

    /**
     * @param navigationItemId Item 的 Id
     * @param fragmentClass    Fragment 的类
     * @param tag              Fragment 的 Tag
     */
    public void bindFragment(@IdRes int navigationItemId, Class fragmentClass, String tag) {
        bindFragment(navigationItemId, fragmentClass, tag, null);
    }

    /**
     * @param navigationItemId Item 的 Id
     * @param fragmentClass    Fragment 的类
     * @param tag              Fragment 的 Tag
     * @param args             Fragment 的参数
     */
    public void bindFragment(@IdRes int navigationItemId, Class fragmentClass, String tag, Bundle args) {
        FragmentInfo info = new FragmentInfo(fragmentClass, tag, args);
        info.fragment = mFragmentManager.findFragmentByTag(info.tag);
        if (info.fragment == null) {
            try {
                info.fragment = (Fragment) info.clazz.newInstance();
                if (info.args != null) {
                    info.fragment.setArguments(info.args);
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        mInfoSparseArray.put(navigationItemId, info);
        if (mNavigationBar.getSelectedNavigationItemId() == navigationItemId) {
            switchToFragment(navigationItemId);
        }
    }

    /**
     * @param navigationItemId Item 的 Id
     * @return 关联的 Fragment
     */
    public <T extends Fragment> T getFragment(@IdRes int navigationItemId) {
        return (T) mInfoSparseArray.get(navigationItemId).fragment;
    }

    private class SelectedIdChangeListener implements NavigationBar.OnSelectedChangeListener {
        @Override
        public void onSelectedChanged(NavigationBar bar, int selectedId) {
            switchToFragment(selectedId);
        }
    }

    private class FragmentInfo {

        private Class clazz;
        private String tag;
        private Bundle args;
        private Fragment fragment;

        FragmentInfo(Class clazz, String tag, Bundle args) {
            this.clazz = clazz;
            this.tag = tag;
            this.args = args;
        }
    }
}
