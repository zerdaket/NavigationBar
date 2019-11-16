package com.zerdaket.navigation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by zerdaket on 2019-11-14.
 */
public class NavigationItem extends FrameLayout {

    private boolean mBroadcasting;

    private OnSelectedChangeListener mOnSelectedChangeListener;
    private OnSelectedChangeListener mOnSelectedChangeWidgetListener;

    public NavigationItem(@NonNull Context context) {
        this(context, null);
    }

    public NavigationItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
    }

    /**
     * @param selected 是否为选中状态
     */
    @Override
    public void setSelected(boolean selected) {
        if (isSelected() != selected) {
            super.setSelected(selected);

            if (mBroadcasting) {
                return;
            }

            mBroadcasting = true;
            onSelectedChange(selected);
            if (mOnSelectedChangeListener != null) {
                mOnSelectedChangeListener.onSelectedChanged(this, selected);
            }
            if (mOnSelectedChangeWidgetListener != null) {
                mOnSelectedChangeWidgetListener.onSelectedChanged(this, selected);
            }
            mBroadcasting = false;
        }
    }

    private void toggle() {
        if (!isSelected()) {
            setSelected(!isSelected());
        }
    }

    @Override
    public boolean performClick() {
        toggle();

        final boolean handled = super.performClick();
        if (!handled) {
            playSoundEffect(SoundEffectConstants.CLICK);
        }

        return handled;
    }

    protected int dp2px(float dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * @param listener 选中状态改变的监听
     */
    public void setOnSelectedChangeListener(@Nullable OnSelectedChangeListener listener) {
        mOnSelectedChangeListener = listener;
    }

    void setOnSelectedChangeWidgetListener(OnSelectedChangeListener listener) {
        mOnSelectedChangeWidgetListener = listener;
    }

    public void onSelectedChange(boolean selected) {

    }
}
