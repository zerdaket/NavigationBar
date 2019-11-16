

#NavigationBar

底部导航栏：方便实现点击 Tab 时切换 Fragment ，自定义 Tab 图标、标题，支持设置角标等功能。

## 主要的类

1. NavigationBar: 父布局，用于放置 Tab ，用法类似 RadioGroup ；
2. NormalItem: 普通的 Tab 样式，支持设置图标、标题和角标，用法类似 RadioButton ；
3. NavigationManager: 将 Tab 和 Fragment 绑定，选中 Tab 时自动切换至对应的 Fragment 。

## 具体用法

页面布局示例如下：

```xml
<com.zerdaket.navigation.NavigationBar
        android:id="@+id/nb_main"
        android:layout_width="match_parent"
        android:layout_height="56dp"
        android:layout_alignParentBottom="true"
        android:background="@android:color/white"
        app:nb_selectedItem="@+id/ni_home">

        <!-- 注： ni_icon 和 ni_colorTint 可接受 selector 资源-->  
        <com.zerdaket.navigation.NormalItem
            android:id="@+id/ni_home"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:background="?attr/selectableItemBackground"
            app:ni_showBadge="true"
            app:ni_isIconTint="true"
            app:ni_colorTint="@color/ui_selector_navi_item"
            app:ni_icon="@drawable/ic_home"
            app:ni_title="首页"/>

        <com.zerdaket.navigation.NormalItem
            android:id="@+id/ni_album"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:background="?attr/selectableItemBackground"
            app:ni_showBadge="true"
            app:ni_badgeContent="99+"
            app:ni_isIconTint="true"
            app:ni_colorTint="@color/ui_selector_navi_item"
            app:ni_icon="@drawable/ic_album"
            app:ni_title="音乐"/>

        <com.zerdaket.navigation.NormalItem
            android:id="@+id/ni_email"
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_weight="1"
            android:background="?attr/selectableItemBackground"
            app:ni_isIconTint="true"
            app:ni_colorTint="@color/ui_selector_navi_item"
            app:ni_icon="@drawable/ic_email"
            app:ni_title="邮件"/>

    </com.zerdaket.navigation.NavigationBar>
```

### NavigationBar

##### attrs 属性

```xml
<declare-styleable name="NavigationBar">
    <!-- 选中item的Id -->    
    <attr name="nb_selectedItem" format="reference"/>
</declare-styleable>
```

`nb_selectedItem` 参数设置默认选中的 Tab。

##### public 方法

```java
/** 
 * 设置 item 为选中状态 
 * @param id 选中 Item 的 Id 
 */
public void select(@IdRes int id);

/**
 * @return 选中 Item 的Id
 */
@IdRes
public int getSelectedNavigationItemId();

/**
 * 重置选中 Item 的状态
 */
public void clearSelect();

/**
 * @param listener 选中 Item 改变的监听
 */
public void setOnSelectedChangeListener(OnSelectedChangeListener listener);

```

### NormalItem

##### attrs 属性

```xml
<declare-styleable name="NormalItem">    
    <!-- 图标，设置selector资源时不需要着色 -->    
    <attr name="ni_icon" format="reference"/>
    <!-- 标题 -->    
    <attr name="ni_title" format="string|reference"/>
    <!-- 着色，定义不同状态时的颜色 -->    
    <attr name="ni_colorTint" format="reference"/>
    <!-- 是否显示角标 -->    
    <attr name="ni_showBadge" format="boolean"/>
    <!-- 角标内容，不填默认展示红点 -->
    <attr name="ni_badgeContent" format="string|reference"/>
    <!-- 角标字体颜色 -->
    <attr name="ni_badgeTextColor" format="color|reference" />
    <!-- 角标背景颜色 -->
    <attr name="ni_badgeColor" format="color|reference" />
    <!-- 图标是否需要着色 -->    
    <attr name="ni_isIconTint" format="boolean"/>
</declare-styleable>
```

##### public 方法

```java
/**
 * 设置是否选中
 * @param selected 是否为选中状态
 */
public void setSelected(boolean selected);

/**
 * 设置监听
 * @param listener 选中状态改变的监听
 */
public void setOnSelectedChangeListener(@Nullable onSelectedChangeListener listener);

/** 
 * 设置图标
 * @param drawable Item 的图标，传 null 时隐藏 
 */
public void setIcon(@Nullable Drawable drawable);

/**
 * 设置图标，若 isIconTint 为 true ，选中状态下 Drawable 会填充为对应颜色
 * @param drawable Item 的图标，传 null 时隐藏
 * @param isIconTint 图标是否着色
 */
public void setIcon(@Nullable Drawable drawable, boolean isIconTint);

/**
 * 设置标题
 * @param title Item 的标题，传 null 时隐藏
 */
public void setTitle(@Nullable String title);

/**
 * 设置各种状态下 Title 的颜色
 * @param colorTint Item 着色
 */
public void setColorTint(ColorStateList colorTint);

/**
 * 设置各种状态下 Title 的颜色，若 isIconTint 为 true ，选中状态下 Drawable 会填充为对应颜色
 * @param colorTint Item 着色
 * @param isIconTint 图标是否着色
 */
public void setColorTint(ColorStateList colorTint, boolean isIconTint);

/**
 * 展示小圆点
 */
public void showBadge();

/**
 * 展示角标
 * @param content 展示角标的内容
 */
public void showBadge(String content);

/**
 * @param color 角标字体颜色
 */
public void setBadgeTextColor(@ColorInt int color);

/**
 * @param color 角标背景颜色
 */
public void setBadgeColor(@ColorInt int color);

/**
 * 清除角标
 */
public void clearBadge();

```

### NavigationManager

##### 具体用法

```java
// 传入 NavigationBar 、FragmentManager 和 用于承载 Fragment 的 View Id
NavigationManager navigationManager = new NavigationManager(navigationBar, getSupportFragmentManager(), R.id.fl_container);

// 将对应的 Item 和 Fragment 绑定，并传入 Fragment 的 Tag 和 参数（参数可省略）
navigationManager.bindFragment(R.id.ni_home, HomeFragment.class, TAG_HOME, args);
// 根据绑定的 Item Id 获取 Fragment 对象
HomeFragment homeFragment = navigationManager.getFragment(R.id.ni_home);
```

### 小技巧

有时候我们会需要在切换 Tab 的时候判断是否有权限进入，此时我们可以设置对应 Tab 的 `OnTouchListener` ，拦截按下的点击事件：

```java
mNormalItem.setOnTouchListener(new View.OnTouchListener() {
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN
                && 不允许切换的条件) {
            // TODO
            return true;
        }
        return false;
    }
});
```

如果需要自定义底部的 Tab 导航，继承 NavigationItem 即可，具体实现可参考 NormalItem。

## License

```
MIT License

Copyright (c) 2019 zerdaket

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

