package com.itfocus.lanzhiming.flatwheeldatepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.Calendar;

/**
 * DatePicker
 */
public class MyDatePicker extends LinearLayout {

	private Calendar calendar = Calendar.getInstance(); //日历类
	private WheelView years, months, days; //Wheel picker
	private OnChangeListener onChangeListener; //onChangeListener

	//Constructors
	public MyDatePicker(Context context) {
		super(context);
		init(context);
	}

	public MyDatePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * 初始化
	 * @param context
	 */
	private void init(Context context){
		years = new WheelView(context);
		LinearLayout.LayoutParams yearParams = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		yearParams.weight = 1;
		years.setLayoutParams(yearParams);

		months = new WheelView(context);
		LinearLayout.LayoutParams monthParams = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		monthParams.weight = 1;
		months.setLayoutParams(monthParams);

		days = new WheelView(context);
		LinearLayout.LayoutParams dayParams = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		dayParams.weight = 1;
		days.setLayoutParams(dayParams);

		NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(1900, 9999);
		years.setAdapter(numericWheelAdapter);
		years.setLabel("年");
//		years.setCurrentItem(2005-1801);

		months.setAdapter(new NumericWheelAdapter(1, 12, "%02d"));
		months.setLabel("月");
		months.setCyclic(true);
		int maxday_of_month = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		days.setAdapter(new NumericWheelAdapter(1, maxday_of_month, "%02d"));
		days.setLabel("日");
		days.setCyclic(true);

		years.addChangingListener(onYearsChangedListener);
		months.addChangingListener(onMonthsChangedListener);
		days.addChangingListener(onDaysChangedListener);

		this.addView(years);
		addView(months);
		addView(days);
	}



	//listeners
	private OnWheelChangedListener onYearsChangedListener = new OnWheelChangedListener(){
		@Override
		public void onChanged(WheelView hours, int oldValue, int newValue) {
			calendar.set(Calendar.YEAR, 1900 + newValue);
			onChangeListener.onChange(getYear(), getMonth(), getDay(), getDayOfWeek());
			setMonth(getMonth());
			setDay(getDay());
			days.setAdapter(new NumericWheelAdapter(1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), "%02d"));
		}
	};
	private OnWheelChangedListener onMonthsChangedListener = new OnWheelChangedListener(){
		@Override
		public void onChanged(WheelView mins, int oldValue, int newValue) {
			calendar.set(Calendar.MONTH, 1 + newValue - 1);
			onChangeListener.onChange(getYear(), getMonth(), getDay(), getDayOfWeek());
			setMonth(getMonth());
			setDay(getDay());
			days.setAdapter(new NumericWheelAdapter(1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), "%02d"));
		}
	};
	private OnWheelChangedListener onDaysChangedListener = new OnWheelChangedListener(){
		@Override
		public void onChanged(WheelView mins, int oldValue, int newValue) {
			calendar.set(Calendar.DAY_OF_MONTH, newValue + 1);
			onChangeListener.onChange(getYear(), getMonth(), getDay(), getDayOfWeek());
			days.setAdapter(new NumericWheelAdapter(1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), "%02d"));
		}
	};


	/**
	 * 定义了监听时间改变的监听器借口
	 */
	public interface OnChangeListener {
		void onChange(int year, int month, int day, int day_of_week);
	}

	/**
	 * 设置监听器的方法
	 * @param onChangeListener
	 */
	public void setOnChangeListener(OnChangeListener onChangeListener){
		this.onChangeListener = onChangeListener;
	}

	/**
	 * 设置年
	 * @param year
	 */
	public void setYear(int year){
		years.setCurrentItem(year-1900);
	}

	/**
	 * 获得年
	 * @return
	 */
	public int getYear(){
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 设置月
	 */
	public void setMonth(int month){
		months.setCurrentItem(month - 1);
	}

	/**
	 * 获得月
	 * @return
	 */
	public int getMonth(){
		return calendar.get(Calendar.MONTH)+1;
	}

	/**
	 * 设置日
	 * @param day
	 */
	public void setDay(int day){
		days.setCurrentItem(day - 1);
	}

	/**
	 * 获得日
	 * @return
	 */
	public int getDay(){
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获得星期
	 * @return
	 */
	public int getDayOfWeek(){
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 获得星期
	 * @return
	 */
	public static String getDayOfWeekCN(int day_of_week){
		String result = null;
		switch(day_of_week){
			case 1:
				result = "日";
				break;
			case 2:
				result = "一";
				break;
			case 3:
				result = "二";
				break;
			case 4:
				result = "三";
				break;
			case 5:
				result = "四";
				break;
			case 6:
				result = "五";
				break;
			case 7:
				result = "六";
				break;
			default:
				break;
		}
		return result;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//默认设置为系统时间
		setYear(getYear());
		setMonth(getMonth());
		setDay(getDay());
	}
}


