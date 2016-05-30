package com.itfocus.lanzhiming.flatwheeldatepicker;

/*
 *  Android Wheel Control.
 *  https://code.google.com/p/android-wheel/
 *
 *  Copyright 2010 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.itfocus.lanzhiming.flatwheeldatepicker.OnWheelChangedListener;
import com.itfocus.lanzhiming.flatwheeldatepicker.OnWheelScrollListener;
import com.itfocus.lanzhiming.flatwheeldatepicker.WheelAdapter;

import java.util.LinkedList;
import java.util.List;

/**
 * Numeric wheel view.
 *
 */
public class WheelView extends View {
	/**
	 * Scrolling duration
	 *
	 *  </br>
	 * 滚动持续时间(毫秒)
	 */
	private static final int SCROLLING_DURATION = 400;

	/**
	 * Minimum delta for scrolling
	 *
	 *  </br>
	 * 滚动的最小差值
	 */
	private static final int MIN_DELTA_FOR_SCROLLING = 1;

	/**
	 * Current value & label text color
	 *
	 *  </br>
	 * 当前选中项  的 文字 的 颜色
	 */
	private static final int VALUE_TEXT_COLOR = 0xff0000CD;

	/**
	 * Items text color
	 *
	 *  </br>
	 * 选项 的 文字 的 颜色
	 */
	private static final int ITEMS_TEXT_COLOR = 0xFF000000;

	/**
	 * Top and bottom shadows colors
	 *
	 *  </br>
	 * 顶部和底部阴影 的 颜色   </br>
	 * 选择器 顶部和底部颜色是渐变的，这里指定一个渐变色的数组
	 */
	private static final int[] SHADOWS_COLORS = new int[] { 0xFFFFFFFF,
//			0x00EEEED5, 0x00EEEED5 };
			0xFFFFFFFF, 0xFFFFFFFF };

	/**
	 * Additional items height (is added to standard text item height)
	 *
	 *  </br>
	 * 附加项的高度项的高度  (单位应该是dp) </br>
	 * 从后面getDesiredHeight() 函数看出，这个值应该是平分给每一个选项的。 </br>
	 * 类似于设置行距吧，不过这是一个总和，也就是有5个可见项，那么每个可见项的附加高就是 ADDITIONAL_ITEM_HEIGHT/5
	 */
	private static final int ADDITIONAL_ITEM_HEIGHT = 50;

	/**
	 * Text size
	 *
	 *  </br>
	 * 字号
	 */
	private static final int TEXT_SIZE = 30;

	/**
	 * Top and bottom items offset (to hide that)
	 *
	 *  </br>
	 * 这个是选项在顶部和底部被抵消的值。 </br>
	 * 怎么解释呢~ 其实试一下就知道了， </br>
	 * &nbsp; 比如说在picker中显示的五项(中间那个是选中的)，剩余4是没选中的。 </br>
	 * &nbsp; 在没选中的这4项中，位于顶部和底部的项，会用阴影遮挡(遮挡一部分，这样用户就明白是需要滑动操作了) </br>
	 * &nbsp; 这里设定的值，就是指定遮挡的size，这里的默认值 TEXT_SIZE / 5 是遮挡了1/5的字号 (那么单位也应该是sp吧)
	 */
	private static final int ITEM_OFFSET = TEXT_SIZE / 5;

	/**
	 * Additional width for items layout
	 *
	 *  </br>
	 * 附加项空间？ 不理解~~还是试试吧 </br>
	 * 应该是项的宽，这个属性应该是受制于外边区域的， 设置的太宽里面的文字显示会出问题 </br>
	 * 具体影响 有待进一步实验证明
	 */
	private static final int ADDITIONAL_ITEMS_SPACE = 10;

	/**
	 * Label offset
	 *
	 *  </br>
	 * 标签抵消 （作用未知） 用1,8，和800 三个值实验（8是默认值） 效果是一样的。
	 */
	private static final int LABEL_OFFSET = 8;

	/**
	 * Left and right padding value
	 *
	 *  </br>
	 * 填充  </br>
	 * 这个选项的内部填充，如果选项是个TextView的话，那这里设置的即是TextView的填充 </br>
	 * =。=！后面代码还米有看，item是啥我也不知道
	 */
	private static final int PADDING = 10;

	/**
	 * Default count of visible items
	 *
	 *  </br>
	 * 默认可见项的数目。就是picker中显示几项
	 */
	private static final int DEF_VISIBLE_ITEMS = 5;

	// Wheel Values
	/**
	 * Wheel Values
	 *
	 *  </br>
	 * 适配器，items就通过适配器来提供的吧
	 */
	private WheelAdapter adapter = null;
	/**
	 * Wheel Values
	 *
	 *  </br>
	 * 当前项
	 */
	private int currentItem = 0;

	// Widths
	/**
	 * Widths
	 *
	 *  </br>
	 * 做了实验 把值设为100 没有任何变化
	 */
	private int itemsWidth = 0;
	/**
	 * Widths
	 *
	 *  </br>
	 * 也做了实验 把值设为100 没有任何变化
	 */
	private int labelWidth = 0;

	// Count of visible items
	/**
	 * Count of visible items
	 *
	 *  </br>
	 * 可见项目的条数
	 */
	private int visibleItems = DEF_VISIBLE_ITEMS;

	// Item height
	/**
	 * Item height
	 *
	 *  </br>
	 * 是item的高
	 */
	private int itemHeight = 0;

	// Text paints
	/**
	 * Text paints
	 *
	 *  </br>
	 * 选中文本的颜色
	 */
	private TextPaint itemsPaint;
	/**
	 * Text paints
	 *
	 *  </br>
	 * 未选中文本的颜色
	 */
	private TextPaint valuePaint;

	// Layouts
	/**
	 * Layouts
	 *
	 *  </br>
	 * 选项  的 布局
	 */
	private StaticLayout itemsLayout;
	/**
	 * Layouts
	 *
	 *  </br>
	 * 标签 的 布局
	 */
	private StaticLayout labelLayout;
	/**
	 * Layouts
	 *
	 *  </br>
	 * 选中项 的 布局
	 */
	private StaticLayout valueLayout;

	// Label & background
	/**
	 * Label & background
	 *
	 *  </br>
	 * 标签
	 */
	private String label;
	/**
	 * Label & background
	 * 中间的几何体 </br>
	 * 选中区域的背景
	 */
	private Drawable centerDrawable;

	// Shadows drawables
	/**
	 * Shadows drawables
	 *
	 *  </br>
	 * 上边 和 底部 的阴影部分的背景资源
	 */
	private GradientDrawable topShadow;
	private GradientDrawable bottomShadow;

	// Scrolling
	/**
	 * Scrolling
	 *
	 *  </br>
	 * 执行滚动？
	 */
	private boolean isScrollingPerformed;
	/**
	 * Scrolling
	 *
	 *  </br>
	 * 滚动抵消？
	 */
	private int scrollingOffset;

	// Scrolling animation
	/**
	 * Scrolling animation
	 *
	 *  </br>
	 * 手势检测器
	 */
	private GestureDetector gestureDetector;
	/**
	 * Scrolling animation
	 *
	 *  </br>
	 * 卷轴
	 */
	private Scroller scroller;
	/**
	 * Scrolling animation
	 *
	 *  </br>
	 * 最后的 卷轴Y
	 */
	private int lastScrollY;

	// Cyclic
	/**
	 * Cyclic
	 *
	 *  </br>
	 * 是否循环
	 */
	boolean isCyclic = false;

	// Listeners
	/**
	 * Listeners
	 *
	 *  </br>
	 * 控件改变监听器的 集合
	 */
	private List<OnWheelChangedListener> changingListeners = new LinkedList<OnWheelChangedListener>();
	/**
	 * Listeners
	 *
	 *  </br>
	 * 控件滚动监听器的 集合
	 */
	private List<OnWheelScrollListener> scrollingListeners = new LinkedList<OnWheelScrollListener>();

	/**
	 * Constructor
	 *
	 * </br>
	 * 构造器 并实例了手势检测器 和 卷轴
	 */
	public WheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initData(context);
	}

	/**
	 * Constructor
	 *
	 * </br>
	 * 构造器 并实例了手势检测器 和 卷轴
	 */
	public WheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData(context);
	}

	/**
	 * Constructor
	 *
	 * </br>
	 * 构造器 并实例了手势检测器 和 卷轴
	 */
	public WheelView(Context context) {
		super(context);
		initData(context);
	}

	/**
	 * Initializes class data
	 * @param context the context
	 *
	 * </br>
	 * 数据初始化 </br>
	 * 就是把手势检测器 和 卷轴类 实例了 </br>
	 * 这个方法在所有的构造器中都被调用了
	 */
	private void initData(Context context) {
		gestureDetector = new GestureDetector(context, gestureListener);
		gestureDetector.setIsLongpressEnabled(false); //这个没看出来有什么用，不设置，或设置成true都不影响效果

		scroller = new Scroller(context);
	}

	/**
	 * Gets wheel adapter
	 * @return the adapter
	 *
	 * </br>
	 * 获得适配器
	 */
	public WheelAdapter getAdapter() {
		return adapter;
	}

	/**
	 * Sets wheel adapter
	 * @param adapter the new wheel adapter
	 *
	 * </br>
	 * 设置适配器 </br>
	 * 还会界面进行了重绘
	 */
	public void setAdapter(WheelAdapter adapter) {
		this.adapter = adapter;
		invalidateLayouts();
		invalidate();
	}

	/**
	 * Set the the specified scrolling interpolator
	 * @param interpolator the interpolator
	 *
	 * </br>
	 * 作用也是设置 卷轴的吧，是通过动画插补器来实例 卷轴对象
	 */
	public void setInterpolator(Interpolator interpolator) {
		scroller.forceFinished(true);
		scroller = new Scroller(getContext(), interpolator);
	}

	/**
	 * Gets count of visible items
	 *
	 * @return the count of visible items
	 *
	 * </br>
	 * 获得可见项的条数
	 */
	public int getVisibleItems() {
		return visibleItems;
	}

	/**
	 * Sets count of visible items
	 *
	 * @param count
	 *            the new count
	 *
	 * </br>
	 * 设置可见项的条数 并重绘view
	 */
	public void setVisibleItems(int count) {
		visibleItems = count;
		invalidate();
	}

	/**
	 * Gets label
	 *
	 * @return the label
	 *
	 * </br>
	 * 获得标签
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Sets label
	 *
	 * @param newLabel
	 *            the label to set
	 *
	 * </br>
	 * 设置标签
	 */
	public void setLabel(String newLabel) {
		if (label == null || !label.equals(newLabel)) {
			label = newLabel;
			labelLayout = null;
			invalidate();
		}
	}

	/**
	 * Adds wheel changing listener
	 * @param listener the listener
	 *
	 * </br>
	 * 添加控件改变监听器
	 */
	public void addChangingListener(OnWheelChangedListener listener) {
		changingListeners.add(listener);
	}

	/**
	 * Removes wheel changing listener
	 * @param listener the listener
	 *
	 * </br>
	 * 移除控件改变监听器
	 */
	public void removeChangingListener(OnWheelChangedListener listener) {
		changingListeners.remove(listener);
	}

	/**
	 * Notifies changing listeners
	 * @param oldValue the old wheel value
	 * @param newValue the new wheel value
	 *
	 * </br>
	 * 通知 改变监听器， </br>
	 * 循环  控件改变监听器集合， 并依次调用它们的onChenge方法
	 */
	protected void notifyChangingListeners(int oldValue, int newValue) {
		for (OnWheelChangedListener listener : changingListeners) {
			listener.onChanged(this, oldValue, newValue);
		}
	}

	/**
	 * Adds wheel scrolling listener
	 * @param listener the listener
	 *
	 * </br>
	 * 添加控件滚动监听器
	 */
	public void addScrollingListener(OnWheelScrollListener listener) {
		scrollingListeners.add(listener);
	}

	/**
	 * Removes wheel scrolling listener
	 * @param listener the listener
	 *
	 * </br>
	 * 移除控件滚动监听器
	 */
	public void removeScrollingListener(OnWheelScrollListener listener) {
		scrollingListeners.remove(listener);
	}

	/**
	 * Notifies listeners about starting scrolling
	 *
	 * </br>
	 * 通知控件滚动监听器调用开始滚动的方法
	 */
	protected void notifyScrollingListenersAboutStart() {
		for (OnWheelScrollListener listener : scrollingListeners) {
			listener.onScrollingStarted(this);
		}
	}

	/**
	 * Notifies listeners about ending scrolling
	 *
	 * </br>
	 * 通知控件滚动监听器调用结束滚动的方法
	 */
	protected void notifyScrollingListenersAboutEnd() {
		for (OnWheelScrollListener listener : scrollingListeners) {
			listener.onScrollingFinished(this);
		}
	}

	/**
	 * Gets current value
	 *
	 * @return the current value
	 *
	 * </br>
	 * 返回当前项的索引
	 */
	public int getCurrentItem() {
		return currentItem;
	}

	/**
	 * Sets the current item. Does nothing when index is wrong.
	 *
	 * @param index the item index
	 * @param animated the animation flag
	 *
	 * </br>
	 * 设置当前项 如果输入错误的索引，则控件什么都不会做哟 </br>
	 * 第二个参数是设置是否使用滚动动画的
	 */
	public void setCurrentItem(int index, boolean animated) {
		if (adapter == null || adapter.getItemsCount() == 0) {
			return; // throw?
		}
		if (index < 0 || index >= adapter.getItemsCount()) {
			if (isCyclic) {
				while (index < 0) {
					index += adapter.getItemsCount();
				}
				index %= adapter.getItemsCount();
			} else{
				return; // throw?
			}
		}
		if (index != currentItem) {
			if (animated) {
				scroll(index - currentItem, SCROLLING_DURATION);
			} else {
				invalidateLayouts();

				int old = currentItem;
				currentItem = index;

				notifyChangingListeners(old, currentItem);

				invalidate();
			}
		}
	}

	/**
	 * Sets the current item w/o animation. Does nothing when index is wrong.
	 *
	 * @param index the item index
	 *
	 * </br>
	 * 设置当前选中项，默认是不启动动画的
	 */
	public void setCurrentItem(int index) {
		setCurrentItem(index, false);
	}

	/**
	 * Tests if wheel is cyclic. That means before the 1st item there is shown the last one
	 * @return true if wheel is cyclic
	 *
	 * </br>
	 * 是否循环显示
	 */
	public boolean isCyclic() {
		return isCyclic;
	}

	/**
	 * Set wheel cyclic flag
	 * @param isCyclic the flag to set
	 *
	 * </br>
	 * 设置是否循环显示
	 */
	public void setCyclic(boolean isCyclic) {
		this.isCyclic = isCyclic;

		invalidate();
		invalidateLayouts();
	}

	/**
	 * Invalidates layouts
	 *
	 *  </br>
	 * 重绘布局 </br>
	 * 方法将 选项布局itemsLayout 和 选中项布局 valueLayout 赋值为null </br>
	 * 同事将 滚动抵消?scrollingOffset 设置为0 </br>
	 * （这个scrollingOffset 还没搞明白做什么用）
	 */
	private void invalidateLayouts() {
		itemsLayout = null;
		valueLayout = null;
		scrollingOffset = 0;
	}

	/**
	 * Initializes resources
	 *
	 * </br>
	 * 初始化源 </br>
	 * 这个方法是这样的， 判断前面定义的画笔、背景资源等私有属性的值，如果是null就重新从静态常量中取值，并付给响应的属性。 </br>
	 * 如果这些必要属性为空的话，这个函数应该起到了初始化的作用
	 */
	private void initResourcesIfNecessary() {
		if (itemsPaint == null) {
			itemsPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
					| Paint.FAKE_BOLD_TEXT_FLAG);
			//itemsPaint.density = getResources().getDisplayMetrics().density;
			itemsPaint.setTextSize(TEXT_SIZE);
		}

		if (valuePaint == null) {
			valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
					| Paint.FAKE_BOLD_TEXT_FLAG | Paint.DITHER_FLAG);
			//valuePaint.density = getResources().getDisplayMetrics().density;
			valuePaint.setTextSize(TEXT_SIZE);
			valuePaint.setShadowLayer(0.1f, 0, 0.1f, 0xFFC0C0C0);
		}

		if (centerDrawable == null) {
			centerDrawable = getContext().getResources().getDrawable(R.drawable.wheel_var);
			//TODO
		}

		if (topShadow == null) {
			topShadow = new GradientDrawable(Orientation.TOP_BOTTOM, SHADOWS_COLORS);
		}

		if (bottomShadow == null) {
			bottomShadow = new GradientDrawable(Orientation.BOTTOM_TOP, SHADOWS_COLORS);
		}

		setBackgroundResource(R.drawable.wheel_bg);
	}

	/**
	 * Calculates desired height for layout
	 *
	 * @param layout
	 *            the source layout
	 * @return the desired layout height
	 *
	 * </br>
	 * 获得理想的控件高度，并保证其不低于建议的最小高度
	 */
	private int getDesiredHeight(Layout layout) {
		if (layout == null) {
			return 0;
		}

		int desired = getItemHeight() * visibleItems - ITEM_OFFSET * 2
				- ADDITIONAL_ITEM_HEIGHT;

		// Check against our minimum height
		desired = Math.max(desired, getSuggestedMinimumHeight());

		return desired;
	}

	/**
	 * Returns text item by index
	 * @param index the item index
	 * @return the item or null
	 *
	 * </br>
	 * 指定索引，获得选项的文本值(Sring) </br>
	 * 如果索引超出范围，控件又不是循环的（isCyclic），则返回null </br>
	 * 如果是循环的情况，方法内部进行了处理， </br>
	 * 为负数则+count, 然会取余
	 */
	private String getTextItem(int index) {
		if (adapter == null || adapter.getItemsCount() == 0) {
			return null;
		}
		int count = adapter.getItemsCount();
		if ((index < 0 || index >= count) && !isCyclic) {
			return null;
		} else {
			while (index < 0) {
				index = count + index;
			}
		}

		index %= count;
		return adapter.getItem(index);
	}

	/**
	 * Builds text depending on current value
	 *
	 * @param useCurrentValue
	 * @return the text
	 *
	 *  </br>
	 * 依赖当前项 构建文本 </br>
	 * 获得一个字符串，如果当前项目的索引为5, 可见项目数为3  </br>
	 * 则字符串的值为 getTextItem(3).append("\n").getTextItem(4).append("\n").getTextItem(5).append("\n").getTextItem(6).append("\n").getTextItem(7) </br>
	 * 如果 参数useCurrentValue为false </br>
	 * 则返回的字符串为 getTextItem(3).append("\n").getTextItem(4).append("\n").getTextItem(6).append("\n").getTextItem(7) </br>
	 * 如果getTextItem(i)返回null, 则不会向返回值中追加
	 */
	private String buildText(boolean useCurrentValue) {
		StringBuilder itemsText = new StringBuilder();
		int addItems = visibleItems / 2 + 1;

		for (int i = currentItem - addItems; i <= currentItem + addItems; i++) {
			if (useCurrentValue || i != currentItem) {
				String text = getTextItem(i);
				if (text != null) {
					itemsText.append(text);
				}
			}
			if (i < currentItem + addItems) {
				itemsText.append("\n");
			}
		}

		return itemsText.toString();
	}

	/**
	 * Returns the max item length that can be present
	 * @return the max length
	 *
	 *  </br>
	 *  获得最大的文本长度</br>
	 *  后面计算控件宽度用的
	 */
	private int getMaxTextLength() {
		WheelAdapter adapter = getAdapter();
		if (adapter == null) {
			return 0;
		}

		int adapterLength = adapter.getMaximumLength();
		if (adapterLength > 0) {
			return adapterLength;
		}

		String maxText = null;
		int addItems = visibleItems / 2;
		for (int i = Math.max(currentItem - addItems, 0);
			 i < Math.min(currentItem + visibleItems, adapter.getItemsCount()); i++) {
			String text = adapter.getItem(i);
			if (text != null && (maxText == null || maxText.length() < text.length())) {
				maxText = text;
			}
		}//这个循环的范围没看明白呀，起始值不考虑循环吗？ 上限为什么是 当前项索引+可见项目数？

		return maxText != null ? maxText.length() : 0;
	}

	/**
	 * Returns height of wheel item
	 * @return the item height
	 *
	 * </br>
	 * 获得选项高
	 */
	private int getItemHeight() {
		if (itemHeight != 0) {
			return itemHeight;
		} else if (itemsLayout != null && itemsLayout.getLineCount() > 2) {
			itemHeight = itemsLayout.getLineTop(2) - itemsLayout.getLineTop(1);
			return itemHeight;
		}//如果是itemlayout 为什么要用 第三行的top减第一行的top呢，不是应该返回layout的高嘛？没看明白

		return getHeight() / visibleItems;
	}

	/**
	 * Calculates control width and creates text layouts
	 * @param widthSize the input layout width
	 * @param mode the layout mode
	 * @return the calculated control width
	 *
	 * </br>
	 * 计算布局宽
	 */
	private int calculateLayoutWidth(int widthSize, int mode) {
		initResourcesIfNecessary();

		int width = widthSize;

		int maxLength = getMaxTextLength();
		if (maxLength > 0) {
			float textWidth = (float)Math.ceil(Layout.getDesiredWidth("0", itemsPaint));
			itemsWidth = (int) (maxLength * textWidth);
		} else {
			itemsWidth = 0;
		}
		itemsWidth += ADDITIONAL_ITEMS_SPACE; // make it some more

		labelWidth = 0;
		if (label != null && label.length() > 0) {
			labelWidth = (int)Math.ceil(Layout.getDesiredWidth(label, valuePaint));
		}

		boolean recalculate = false;
		if (mode == MeasureSpec.EXACTLY) {
			width = widthSize;
			recalculate = true;
		} else {
			width = itemsWidth + labelWidth + 2 * PADDING;
			if (labelWidth > 0) {
				width += LABEL_OFFSET;
			}

			// Check against our minimum width
			width = Math.max(width, getSuggestedMinimumWidth());

			if (mode == MeasureSpec.AT_MOST && widthSize < width) {
				width = widthSize;
				recalculate = true;
			}
		}

		if (recalculate) {
			// recalculate width
			int pureWidth = width - LABEL_OFFSET - 2 * PADDING;
			if (pureWidth <= 0) {
				itemsWidth = labelWidth = 0;
			}
			if (labelWidth > 0) {
				double newWidthItems = (double) itemsWidth * pureWidth
						/ (itemsWidth + labelWidth);
				itemsWidth = (int) newWidthItems;
				labelWidth = pureWidth - itemsWidth;
			} else {
				itemsWidth = pureWidth + LABEL_OFFSET; // no label
			}
		}

		if (itemsWidth > 0) {
			createLayouts(itemsWidth, labelWidth);
		}

		return width;
	}

	/**
	 * Creates layouts
	 * @param widthItems width of items layout
	 * @param widthLabel width of label layout
	 *
	 * </br>
	 * 创建布局</br>
	 */
	private void createLayouts(int widthItems, int widthLabel) {
		if (itemsLayout == null || itemsLayout.getWidth() > widthItems) {
			itemsLayout = new StaticLayout(buildText(isScrollingPerformed), itemsPaint, widthItems,
					widthLabel > 0 ? Layout.Alignment.ALIGN_OPPOSITE : Layout.Alignment.ALIGN_CENTER,
					1, ADDITIONAL_ITEM_HEIGHT, false);
		} else {
			itemsLayout.increaseWidthTo(widthItems);
		}

		if (!isScrollingPerformed && (valueLayout == null || valueLayout.getWidth() > widthItems)) {
			String text = getAdapter() != null ? getAdapter().getItem(currentItem) : null;
			valueLayout = new StaticLayout(text != null ? text : "",
					valuePaint, widthItems, widthLabel > 0 ?
					Layout.Alignment.ALIGN_OPPOSITE : Layout.Alignment.ALIGN_CENTER,
					1, ADDITIONAL_ITEM_HEIGHT, false);
		} else if (isScrollingPerformed) {
			valueLayout = null;
		} else {
			valueLayout.increaseWidthTo(widthItems);
		}

		if (widthLabel > 0) {
			if (labelLayout == null || labelLayout.getWidth() > widthLabel) {
				labelLayout = new StaticLayout(label, valuePaint,
						widthLabel, Layout.Alignment.ALIGN_NORMAL, 1,
						ADDITIONAL_ITEM_HEIGHT, false);
			} else {
				labelLayout.increaseWidthTo(widthLabel);
			}
		}
	}

	/**
	 * 重写onMeasure 设置尺寸
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width = calculateLayoutWidth(widthSize, widthMode);

		int height;
		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			height = getDesiredHeight(itemsLayout);

			if (heightMode == MeasureSpec.AT_MOST) {
				height = Math.min(height, heightSize);
			}
		}

		setMeasuredDimension(width, height);
	}

	/**
	 * 绘制
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (itemsLayout == null) {
			if (itemsWidth == 0) {
				calculateLayoutWidth(getWidth(), MeasureSpec.EXACTLY);
			} else {
				createLayouts(itemsWidth, labelWidth);
			}
		}

		drawCenterRect(canvas); // 放在此处，字在背景色上
		if (itemsWidth > 0) {
			canvas.save();
			// Skip padding space and hide a part of top and bottom items
			canvas.translate(PADDING, -ITEM_OFFSET);
			drawItems(canvas);
			drawValue(canvas);
			canvas.restore();
		}

//		drawCenterRect(canvas);// 放在此处，字在背景色下
		drawShadows(canvas);
	}

	/**
	 * Draws shadows on top and bottom of control
	 * @param canvas the canvas for drawing
	 *
	 * </br>
	 * 绘制控件顶端 和底部的 阴影区域 </br>
	 * 需要传入画布对象
	 */
	private void drawShadows(Canvas canvas) {
		topShadow.setBounds(0, 0, getWidth(), getHeight() / visibleItems);
		topShadow.draw(canvas);

		bottomShadow.setBounds(0, getHeight() - getHeight() / visibleItems,
				getWidth(), getHeight());
		bottomShadow.draw(canvas);
	}

	/**
	 * Draws value and label layout
	 * @param canvas the canvas for drawing
	 *
	 * </br>
	 * 绘制选中项 和 标签
	 */
	private void drawValue(Canvas canvas) {
		valuePaint.setColor(Color.parseColor("#00C8A0"));
		valuePaint.drawableState = getDrawableState();

		Rect bounds = new Rect();
		itemsLayout.getLineBounds(visibleItems / 2, bounds);

		// draw label
		if (labelLayout != null) {
			canvas.save();
			canvas.translate(itemsLayout.getWidth() + LABEL_OFFSET, bounds.top);
			labelLayout.draw(canvas);
			canvas.restore();
		}

		// draw current value
		if (valueLayout != null) {
			canvas.save();
			canvas.translate(0, bounds.top + scrollingOffset);
			valueLayout.draw(canvas);
			canvas.restore();
		}
	}

	/**
	 * Draws items
	 * @param canvas the canvas for drawing
	 *
	 * </br>
	 * 绘制选项
	 */
	private void drawItems(Canvas canvas) {
		canvas.save();

		int top = itemsLayout.getLineTop(1);
		canvas.translate(0, - top + scrollingOffset);

		itemsPaint.setColor(ITEMS_TEXT_COLOR);
		itemsPaint.drawableState = getDrawableState();
		itemsLayout.draw(canvas);

		canvas.restore();
	}

	/**
	 * Draws rect for current value
	 * @param canvas the canvas for drawing
	 *
	 * </br>
	 * 绘制中间的矩形区域
	 */
	private void drawCenterRect(Canvas canvas) {
		int center = getHeight() / 2;
		int offset = getItemHeight() / 2;
		centerDrawable.setBounds(0, center - offset, getWidth(), center + offset);
		centerDrawable.draw(canvas);
	}

	/**
	 * 触摸事件的 回调方法</br>
	 * 看到了吧 如果适配器 adapter是null 的话，这里是什么都不会做的。</br>
	 * 如果适配器不为空，将MotionEvent传递给手势识别器。 并判断是否已ACTION_UP </br>
	 * 如果是说明操作已结束 调用justify()方法。</br>
	 * return true. 不会泄露
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		WheelAdapter adapter = getAdapter();
		if (adapter == null) {
			return true;
		}

		if (!gestureDetector.onTouchEvent(event) && event.getAction() == MotionEvent.ACTION_UP) {
			justify();
		}
		return true;
	}

	/**
	 * Scrolls the wheel
	 * @param delta the scrolling value
	 *
	 * </br>
	 * 滚动</br>
	 * 好像只是重新定义了 scrollingOffset的值，</br>
	 * 执行滚动的操作是这里吗？</br>
	 * 先往后看吧。
	 */
	private void doScroll(int delta) {
		scrollingOffset += delta;

		int count = scrollingOffset / getItemHeight();
		int pos = currentItem - count;
		if (isCyclic && adapter.getItemsCount() > 0) {
			// fix position by rotating
			while (pos < 0) {
				pos += adapter.getItemsCount();
			}
			pos %= adapter.getItemsCount();
		} else if (isScrollingPerformed) {
			//
			if (pos < 0) {
				count = currentItem;
				pos = 0;
			} else if (pos >= adapter.getItemsCount()) {
				count = currentItem - adapter.getItemsCount() + 1;
				pos = adapter.getItemsCount() - 1;
			}
		} else {
			// fix position
			pos = Math.max(pos, 0);
			pos = Math.min(pos, adapter.getItemsCount() - 1);
		}

		int offset = scrollingOffset;
		if (pos != currentItem) {
			setCurrentItem(pos, false);
		} else {
			invalidate();
		}

		// update offset
		scrollingOffset = offset - count * getItemHeight();
		if (scrollingOffset > getHeight()) {
			scrollingOffset = scrollingOffset % getHeight() + getHeight();
		}
	}

	// gesture listener
	/**
	 * gesture listener
	 *
	 * </br>
	 * 手势监听器</br>
	 * 这里是响应用户fling 以及 scroll操作的代码
	 */
	private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
		/**
		 * 这个是按下时停止滚动的操作
		 */
		public boolean onDown(MotionEvent e) {
			if (isScrollingPerformed) {
				scroller.forceFinished(true);
				clearMessages();
				return true;
			}
			return false;
		}

		/**
		 * scroll
		 */
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			startScrolling();
			doScroll((int)-distanceY);
			return true;
		}

		/**
		 * fling
		 */
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			lastScrollY = currentItem * getItemHeight() + scrollingOffset;
			int maxY = isCyclic ? 0x7FFFFFFF : adapter.getItemsCount() * getItemHeight();
			int minY = isCyclic ? -maxY : 0;
			scroller.fling(0, lastScrollY, 0, (int) -velocityY / 2, 0, 0, minY, maxY);
			setNextMessage(MESSAGE_SCROLL);
			return true;
		}
	};

	// Messages
	/**
	 * Messages
	 *
	 * </br>
	 * 向动画处理器发送的消息 -滚动
	 */
	private final int MESSAGE_SCROLL = 0;
	/**
	 * Messages
	 *
	 * </br>
	 * 向动画处理器发送的消息 -证明
	 */
	private final int MESSAGE_JUSTIFY = 1;

	/**
	 * Set next message to queue. Clears queue before.
	 *
	 * @param message the message to set
	 *
	 * </br>
	 * 清楚动画处理器animationHandler中的原有消息，并发送新消息
	 */
	private void setNextMessage(int message) {
		clearMessages();
		animationHandler.sendEmptyMessage(message);
	}

	/**
	 * Clears messages from queue
	 *
	 * </br>
	 * 清楚动画处理器中原有的消息
	 */
	private void clearMessages() {
		animationHandler.removeMessages(MESSAGE_SCROLL);
		animationHandler.removeMessages(MESSAGE_JUSTIFY);
	}

	// animation handler
	/**
	 * animation handler
	 *
	 * </br>
	 * 动画处理器
	 */
	private Handler animationHandler = new Handler() {
		public void handleMessage(Message msg) {
			scroller.computeScrollOffset();
			int currY = scroller.getCurrY();
			int delta = lastScrollY - currY;
			lastScrollY = currY;
			if (delta != 0) {
				doScroll(delta);
			}

			// scrolling is not finished when it comes to final Y
			// so, finish it manually
			if (Math.abs(currY - scroller.getFinalY()) < MIN_DELTA_FOR_SCROLLING) {
				currY = scroller.getFinalY();
				scroller.forceFinished(true);
			}
			if (!scroller.isFinished()) {
				animationHandler.sendEmptyMessage(msg.what);
			} else if (msg.what == MESSAGE_SCROLL) {
				justify();
			} else {
				finishScrolling();
			}
		}
	};

	/**
	 * Justifies wheel
	 *
	 * </br>
	 * 验证轮子
	 */
	private void justify() {
		if (adapter == null) {
			return;
		}

		lastScrollY = 0;
		int offset = scrollingOffset;
		int itemHeight = getItemHeight();
		boolean needToIncrease = offset > 0 ? currentItem < adapter.getItemsCount() : currentItem > 0;
		if ((isCyclic || needToIncrease) && Math.abs((float) offset) > (float) itemHeight / 2) {
			if (offset < 0)
				offset += itemHeight + MIN_DELTA_FOR_SCROLLING;
			else
				offset -= itemHeight + MIN_DELTA_FOR_SCROLLING;
		}
		if (Math.abs(offset) > MIN_DELTA_FOR_SCROLLING) {
			scroller.startScroll(0, 0, 0, offset, SCROLLING_DURATION);
			setNextMessage(MESSAGE_JUSTIFY);
		} else {
			finishScrolling();
		}
	}

	/**
	 * Starts scrolling
	 *
	 * </br>
	 * 开始滚动</br>
	 * 并通知开始滚动监听器
	 */
	private void startScrolling() {
		if (!isScrollingPerformed) {
			isScrollingPerformed = true;
			notifyScrollingListenersAboutStart();
		}
	}

	/**
	 * Finishes scrolling
	 *
	 * </br>
	 * 结束滚动</br>
	 * 并通知结束滚动监听器
	 */
	void finishScrolling() {
		if (isScrollingPerformed) {
			notifyScrollingListenersAboutEnd();
			isScrollingPerformed = false;
		}
		invalidateLayouts();
		invalidate();
	}


	/**
	 * Scroll the wheel
	 * @param itemsToSkip items to scroll
	 * @param time scrolling duration
	 *
	 * 滚动
	 */
	public void scroll(int itemsToScroll, int time) {
		scroller.forceFinished(true);

		lastScrollY = scrollingOffset;
		int offset = itemsToScroll * getItemHeight();

		scroller.startScroll(0, lastScrollY, 0, offset - lastScrollY, time);
		setNextMessage(MESSAGE_SCROLL);

		startScrolling();
	}

}

