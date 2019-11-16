package com.zerdaket.navigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

/**
 * Created by zerdaket on 2019-11-14.
 */
public class NavigationBar extends LinearLayout {

    private static final String LOG_TAG = NavigationBar.class.getSimpleName();

    private int mSelectedId = View.NO_ID;
    private com.zerdaket.navigation.OnSelectedChangeListener mChildOnSelectedChangeListener;
    private boolean mProtectFromSelectedChange = false;
    private OnSelectedChangeListener mOnSelectedChangeListener;
    private OnSelectedChangeListener mOnSelectedChangeManagerListener;
    private PassThroughHierarchyChangeListener mPassThroughListener;

    public NavigationBar(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        init();
    }

    public NavigationBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.NavigationBar);

        int value = attributes.getResourceId(R.styleable.NavigationBar_nb_selectedItem, View.NO_ID);
        if (value != View.NO_ID) {
            mSelectedId = value;
        }

        attributes.recycle();

        init();
    }

    private void init() {
        mChildOnSelectedChangeListener = new SelectedStateTracker();
        mPassThroughListener = new PassThroughHierarchyChangeListener();
        super.setOnHierarchyChangeListener(mPassThroughListener);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
        // the user listener is delegated to our pass-through listener
        mPassThroughListener.mOnHierarchyChangeListener = listener;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // checks the appropriate navigation item as requested in the XML file
        if (mSelectedId != View.NO_ID) {
            mProtectFromSelectedChange = true;
            setSelectedStateForView(mSelectedId, true);
            mProtectFromSelectedChange = false;
            setSelectedId(mSelectedId);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof NavigationItem) {
            final NavigationItem item = (NavigationItem) child;
            if (item.isSelected()) {
                mProtectFromSelectedChange = true;
                if (mSelectedId != View.NO_ID) {
                    setSelectedStateForView(mSelectedId, false);
                }
                mProtectFromSelectedChange = false;
                setSelectedId(item.getId());
            }
        }

        super.addView(child, index, params);
    }

    /**
     * 设置 item 为选中状态
     *
     * @param id 选中 Item 的 Id
     */
    public void select(@IdRes int id) {
        if (id != View.NO_ID && (id == mSelectedId)) {
            return;
        }

        if (mSelectedId != View.NO_ID) {
            setSelectedStateForView(mSelectedId, false);
        }

        if (id != View.NO_ID) {
            setSelectedStateForView(id, true);
        }

        setSelectedId(id);
    }

    private void setSelectedId(@IdRes int id) {
        mSelectedId = id;

        if (mOnSelectedChangeListener != null) {
            mOnSelectedChangeListener.onSelectedChanged(this, mSelectedId);
        }
        if (mOnSelectedChangeManagerListener != null) {
            mOnSelectedChangeManagerListener.onSelectedChanged(this, mSelectedId);
        }
    }

    private void setSelectedStateForView(int viewId, boolean selected) {
        View selectedView = findViewById(viewId);
        if (selectedView instanceof NavigationItem) {
            selectedView.setSelected(selected);
        }
    }

    /**
     * @return 选中 Item 的Id
     */
    @IdRes
    public int getSelectedNavigationItemId() {
        return mSelectedId;
    }

    /**
     * 重置选中 Item 的状态
     */
    public void clearSelect() {
        select(View.NO_ID);
    }


    public interface OnSelectedChangeListener {

        void onSelectedChanged(NavigationBar bar, @IdRes int selectedId);

    }

    /**
     * @param listener 选中 Item 改变的监听
     */
    public void setOnSelectedChangeListener(OnSelectedChangeListener listener) {
        mOnSelectedChangeListener = listener;
    }

    void setOnSelectedChangeManagerListener(OnSelectedChangeListener listener) {
        mOnSelectedChangeManagerListener = listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NavigationBar.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new NavigationBar.LayoutParams(getContext(), attrs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof NavigationBar.LayoutParams;
    }

    @Override
    protected LinearLayout.LayoutParams generateDefaultLayoutParams() {
        return new NavigationBar.LayoutParams(NavigationBar.LayoutParams.WRAP_CONTENT, NavigationBar.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return NavigationBar.class.getName();
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.selectedId = mSelectedId;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        select(ss.selectedId);
    }

    static class SavedState extends View.BaseSavedState {

        int selectedId;

        private SavedState(Parcel source) {
            super(source);
            selectedId = (Integer) source.readValue(getClass().getClassLoader());
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeValue(selectedId);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {

                    @Override
                    public SavedState createFromParcel(Parcel parcel) {
                        return new SavedState(parcel);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {
        /**
         * {@inheritDoc}
         */
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(int w, int h) {
            super(w, h);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(int w, int h, float initWeight) {
            super(w, h, initWeight);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        /**
         * <p>Fixes the child's width to
         * {@link ViewGroup.LayoutParams#WRAP_CONTENT} and the child's
         * height to  {@link ViewGroup.LayoutParams#WRAP_CONTENT}
         * when not specified in the XML file.</p>
         *
         * @param a          the styled attributes set
         * @param widthAttr  the width attribute to fetch
         * @param heightAttr the height attribute to fetch
         */
        @Override
        protected void setBaseAttributes(TypedArray a,
                                         int widthAttr, int heightAttr) {

            if (a.hasValue(widthAttr)) {
                width = a.getLayoutDimension(widthAttr, "layout_width");
            } else {
                width = WRAP_CONTENT;
            }

            if (a.hasValue(heightAttr)) {
                height = a.getLayoutDimension(heightAttr, "layout_height");
            } else {
                height = WRAP_CONTENT;
            }
        }
    }

    private class SelectedStateTracker implements com.zerdaket.navigation.OnSelectedChangeListener {
        @Override
        public void onSelectedChanged(NavigationItem item, boolean isSelected) {
            // prevents from infinite recursion
            if (mProtectFromSelectedChange) {
                return;
            }

            mProtectFromSelectedChange = true;
            if (mSelectedId != View.NO_ID) {
                setSelectedStateForView(mSelectedId, false);
            }
            mProtectFromSelectedChange = false;

            int id = item.getId();
            setSelectedId(id);
        }
    }

    private class PassThroughHierarchyChangeListener implements
            OnHierarchyChangeListener {
        private OnHierarchyChangeListener mOnHierarchyChangeListener;

        /**
         * {@inheritDoc}
         */
        @Override
        public void onChildViewAdded(View parent, View child) {
            if (parent == NavigationBar.this && child instanceof NavigationItem) {
                int id = child.getId();
                // generates an id if it's missing
                if (id == View.NO_ID) {
                    id = View.generateViewId();
                    child.setId(id);
                }
                ((NavigationItem) child).setOnSelectedChangeWidgetListener(
                        mChildOnSelectedChangeListener);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewAdded(parent, child);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onChildViewRemoved(View parent, View child) {
            if (parent == NavigationBar.this && child instanceof NavigationItem) {
                ((NavigationItem) child).setOnSelectedChangeWidgetListener(null);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }
    }

}
