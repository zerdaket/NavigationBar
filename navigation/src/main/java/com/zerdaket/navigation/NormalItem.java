package com.zerdaket.navigation;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

/**
 * Created by zerdaket on 2019-11-14.
 */
public class NormalItem extends NavigationItem {

    private ItemInnerLayout mInnerLayout;
    private ColorStateList mTint;
    private BadgeTextView mBadgeText;

    public NormalItem(@NonNull Context context) {
        this(context, null);
    }

    public NormalItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NormalItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mInnerLayout = new ItemInnerLayout(context);
        addView(mInnerLayout);

        mBadgeText = new BadgeTextView(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(dp2px(16), 0, 0, dp2px(12));
        layoutParams.gravity = Gravity.CENTER;
        mBadgeText.setLayoutParams(layoutParams);
        mBadgeText.setTextSize(10);
        addView(mBadgeText);

        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.NormalItem);

        Drawable drawable = array.getDrawable(R.styleable.NormalItem_ni_icon);

        String title = array.getString(R.styleable.NormalItem_ni_title);

        int colorTint = array.getResourceId(R.styleable.NormalItem_ni_colorTint, R.color.ui_selector_navi_item);

        boolean showBadge = array.getBoolean(R.styleable.NormalItem_ni_showBadge, false);

        String badgeContent = array.getString(R.styleable.NormalItem_ni_badgeContent);

        int badgeTextColor = array.getColor(R.styleable.NormalItem_ni_badgeTextColor, Color.WHITE);

        int badgeColor = array.getColor(R.styleable.NormalItem_ni_badgeColor, Color.RED);

        boolean isIconTint = array.getBoolean(R.styleable.NormalItem_ni_isIconTint, false);

        array.recycle();

        mTint = ResourcesCompat.getColorStateList(getResources(), colorTint, context.getTheme());

        setIcon(drawable, isIconTint);
        setTitle(title);
        setColorTint(mTint);
        if (showBadge) {
            showBadge(badgeContent);
        } else {
            clearBadge();
        }
        setBadgeColor(badgeColor);
        setBadgeTextColor(badgeTextColor);
    }

    private Drawable wrap(Drawable drawable) {
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable.mutate());
            DrawableCompat.setTintList(drawable, mTint);
        }
        return drawable;
    }

    /**
     * @param drawable Item 的图标
     */
    public void setIcon(@Nullable Drawable drawable) {
        setIcon(drawable, false);
    }

    /**
     * @param drawable   Item 的图标
     * @param isIconTint 图标是否着色
     */
    public void setIcon(@Nullable Drawable drawable, boolean isIconTint) {
        if (isIconTint) {
            drawable = wrap(drawable);
        }
        if (drawable == null) {
            mInnerLayout.setIconVisible(false);
        } else {
            mInnerLayout.mIconView.setImageDrawable(drawable);
            mInnerLayout.setIconVisible(true);
        }
        mInnerLayout.resetTitleSize();
    }

    /**
     * @param title Item 的标题
     */
    public void setTitle(@Nullable String title) {
        if (TextUtils.isEmpty(title)) {
            mInnerLayout.setTitleVisible(false);
        } else {
            mInnerLayout.mTitleText.setText(title);
            mInnerLayout.setTitleVisible(true);
        }
    }

    /**
     * @param colorTint Item 着色
     */
    public void setColorTint(ColorStateList colorTint) {
        setColorTint(colorTint, false);
    }

    /**
     * @param colorTint  Item 着色
     * @param isIconTint 图标是否着色
     */
    public void setColorTint(ColorStateList colorTint, boolean isIconTint) {
        mTint = colorTint;
        setIcon(mInnerLayout.mIconView.getDrawable(), isIconTint);
        mInnerLayout.mTitleText.setTextColor(colorTint);
    }

    /**
     * 展示小圆点
     */
    public void showBadge() {
        mBadgeText.setVisibility(VISIBLE);
        mBadgeText.setHighLightMode();
    }

    /**
     * @param content 展示角标的内容
     */
    public void showBadge(String content) {
        mBadgeText.setVisibility(VISIBLE);
        if (TextUtils.isEmpty(content)) {
            mBadgeText.setHighLightMode();
        } else {
            mBadgeText.setText(content);
        }
    }

    /**
     * @param color 角标字体颜色
     */
    public void setBadgeTextColor(@ColorInt int color) {
        mBadgeText.setTextColor(color);
    }

    /**
     * @param color 角标背景颜色
     */
    public void setBadgeColor(@ColorInt int color) {
        mBadgeText.setBackgroundColor(color);
    }

    /**
     * 清除角标
     */
    public void clearBadge() {
        mBadgeText.setVisibility(GONE);
        mBadgeText.clearHighLightMode();
    }

    private final class ItemInnerLayout extends LinearLayout {

        private ImageView mIconView;
        private TextView mTitleText;

        private ItemInnerLayout(Context context) {
            this(context, null);
        }

        private ItemInnerLayout(Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        private ItemInnerLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            setWillNotDraw(false);
            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER);

            mIconView = new ImageView(context);
            mIconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams layoutParams = new LayoutParams(dp2px(24), dp2px(24));
            mIconView.setLayoutParams(layoutParams);
            mIconView.setVisibility(GONE);
            addView(mIconView);

            mTitleText = new TextView(context);
            mTitleText.setTextSize(12);
            mTitleText.setGravity(Gravity.CENTER);
            mTitleText.setVisibility(GONE);
            addView(mTitleText);
        }

        private void setIconVisible(boolean visible) {
            mIconView.setVisibility(visible ? VISIBLE : GONE);
            invalidate();
        }

        private boolean isIconVisible() {
            return mIconView.getVisibility() == VISIBLE;
        }

        private void resetTitleSize() {
            mTitleText.setTextSize(isIconVisible() ? 12 : 14);
        }

        private void setTitleVisible(boolean visible) {
            mTitleText.setVisibility(visible ? VISIBLE : GONE);
            invalidate();
        }

    }

    private class BadgeTextView extends AppCompatTextView {

        private int backgroundColor = Color.RED;

        private static final float RADIUS = 3.5f;
        private int radius;
        private int diffWH;

        private boolean isHighLightMode;

        public BadgeTextView(Context context) {
            this(context, null);
        }

        public BadgeTextView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public BadgeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            setGravity(Gravity.CENTER);
            float density = getContext().getResources().getDisplayMetrics().density;
            radius = (int) (density * RADIUS);
            int basePadding = (int) (radius * 1.5f);
            float textHeight = getTextSize();
            float textWidth = textHeight / 4;
            diffWH = (int) (Math.abs(textHeight - textWidth) / 2);
            int horizontalPadding = basePadding + diffWH;
            setPadding(horizontalPadding, basePadding, horizontalPadding, basePadding);
        }

        @Override
        protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
            super.onTextChanged(text, start, lengthBefore, lengthAfter);
            /** 纯色小红点模式下若有文本需要从无变为有, 要归位view的大小.*/
            String strText = text == null ? "" : text.toString().trim();
            if (isHighLightMode && !strText.isEmpty()) {
                ViewGroup.LayoutParams lp = getLayoutParams();
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                setLayoutParams(lp);
                isHighLightMode = false;
            }
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            refreshBackgroundDrawable(w, h);
        }

        private void refreshBackgroundDrawable(int targetWidth, int targetHeight) {
            if (targetWidth <= 0 || targetHeight <= 0) {
                return;
            }
            CharSequence text = getText();
            if (text == null) {
                return;
            }
            if (text.length() == 1) {/**第一种背景是一个正圆形, 当文本为个位数字时 */
                int max = Math.max(targetWidth, targetHeight);
                final int diameter = max - (2 * radius);
                OvalShape oval = new CircleShape(diameter);
                ShapeDrawable circle = new ShapeDrawable(oval);
                setLayerType(LAYER_TYPE_SOFTWARE, circle.getPaint());
                circle.getPaint().setColor(backgroundColor);
                setBackground(circle);
            } else if (text.length() > 1) {/**第二种背景是上下两边为直线的椭圆, 当文本长度大于1时 */
                SemiCircleRectDrawable sr = new SemiCircleRectDrawable();
                setLayerType(LAYER_TYPE_SOFTWARE, sr.getPaint());
                sr.getPaint().setColor(backgroundColor);
                setBackground(sr);
            } else {
                /** 第三种情况就是text="", 即文本长度为0, 因为无任何文本, 则对当前的TextView背景不做任何更新,
                 * 但是有时候我们需要一个无字的纯色小圆形,用来表达强调.这种情况因为要重新设置View的大小, 所以不在这里表现, 请使用另外一个方法setHighLightMode()来完成.
                 */
            }

        }

        /**
         * 明确的展现一个无任何文本的红色圆点,
         * 主要是通过设置文本setText("")触发onTextChanged(), 再连锁触发onSizeChanged()最后更新了背景.
         */
        public void setHighLightMode() {
            isHighLightMode = true;
            ViewGroup.LayoutParams params = getLayoutParams();
            params.width = dp2px(8);
            params.height = params.width;
            setLayoutParams(params);
            ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
            setLayerType(LAYER_TYPE_SOFTWARE, drawable.getPaint());
            drawable.getPaint().setColor(backgroundColor);
            drawable.getPaint().setAntiAlias(true);
            setBackground(drawable);
            setText("");
        }

        public void clearHighLightMode() {
            isHighLightMode = false;
        }

        public void setBackgroundColor(int color) {
            backgroundColor = color;
            refreshBackgroundDrawable(getWidth(), getHeight());
        }

        private class CircleShape extends OvalShape {
            private int mCircleDiameter;

            CircleShape(int circleDiameter) {
                super();
                mCircleDiameter = circleDiameter;
            }

            @Override
            public void draw(Canvas canvas, Paint paint) {
                final int viewWidth = BadgeTextView.this.getWidth();
                final int viewHeight = BadgeTextView.this.getHeight();
                canvas.drawCircle(viewWidth >> 1, viewHeight >> 1, (float) (mCircleDiameter * 0.4), paint);
            }
        }

        private class SemiCircleRectDrawable extends Drawable {
            private final Paint mPaint;
            private RectF rectF;

            Paint getPaint() {
                return mPaint;
            }

            SemiCircleRectDrawable() {
                mPaint = new Paint();
                mPaint.setAntiAlias(true);
            }

            @Override
            public void setBounds(int left, int top, int right, int bottom) {
                super.setBounds(left, top, right, bottom);
                if (rectF == null) {
                    rectF = new RectF(left + diffWH, top + radius + 4, right - diffWH, bottom - radius - 4);
                } else {
                    rectF.set(left + diffWH, top + radius + 4, right - diffWH, bottom - radius - 4);
                }
            }

            @Override
            public void draw(Canvas canvas) {
                float R = (float) (rectF.bottom * 0.4);
                if (rectF.right < rectF.bottom) {
                    R = (float) (rectF.right * 0.4);
                }
                canvas.drawRoundRect(rectF, R, R, mPaint);
            }

            @Override
            public void setAlpha(int alpha) {
                mPaint.setAlpha(alpha);
            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {
                mPaint.setColorFilter(colorFilter);
            }

            @Override
            public int getOpacity() {
                return PixelFormat.TRANSPARENT;
            }
        }

    }
}
