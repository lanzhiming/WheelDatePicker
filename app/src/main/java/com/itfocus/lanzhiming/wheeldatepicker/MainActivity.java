package com.itfocus.lanzhiming.wheeldatepicker;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.itfocus.lanzhiming.flatwheeldatepicker.MyDatePicker;

public class MainActivity extends AppCompatActivity {

    private int dateYear;
    private int dateMonth;
    private int dateDay;
    private TextView myTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myTextView=(TextView)findViewById(R.id.myTextView);
        myTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                datePickerShow(myTextView);
            }
        });
    }

    /***
     * 时间控件
     * @paramtextview
     */
    protected void datePickerShow(final TextView textView) {
//		DatePickerDialog picker = new DatePickerDialog(this,
//			new OnDateSetListener() {
//				@Override
//				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//					if (monthOfYear < 9 && dayOfMonth < 10) {
//						textView.setText(year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth);
//					} else if (monthOfYear >= 9 && dayOfMonth < 10) {
//						textView.setText(year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth);
//					} else if (monthOfYear < 9 && dayOfMonth >= 10) {
//						textView.setText(year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth);
//					} else {
//						textView.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
//					}
//				}
//			}, cd.get(Calendar.YEAR), cd.get(Calendar.MONTH), cd.get(Calendar.DAY_OF_MONTH));
//		picker.show();

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.datepickerdialog);

        final MyDatePicker dpicker = (MyDatePicker) dialog.findViewById(R.id.datepicker_layout);
//        final TextView txDateAndWeekDay = (TextView) dialog.findViewById(R.id.datepicker_date_and_weekday);
        TextView btBeDown = (TextView) dialog.findViewById(R.id.datepicker_btsure);
        TextView btCancel = (TextView) dialog.findViewById(R.id.datepicker_btcancel);
        TextView btAlldata = (TextView) dialog.findViewById(R.id.datepicker_btalldata);
//        CheckBox allDate=(CheckBox)dialog.findViewById(R.id.allDate);
//        Button btCancel = (Button) dialog.findViewById(R.id.datepicker_btcancel);
//        allDate.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton paramCompoundButton,
//					boolean isChecked) {
//				if(isChecked){
//					dpicker.setVisibility(View.GONE);
//				}else{
//					dpicker.setVisibility(View.VISIBLE);
//				}
//			}
//		});
        dpicker.setOnChangeListener(new MyDatePicker.OnChangeListener() {
            @Override
            public void onChange(int year, int month, int day, int day_of_week) {
//				txDateAndWeekDay.setText(year + "年" + month + "月" + day + "日  星期" + MyDatePicker.getDayOfWeekCN(day_of_week));
                dateYear = year;
                dateMonth = month;
                dateDay = day;

//				if (dateMonth < 10 && dateDay < 10) {
//					textView.setText(dateYear + "-0" + dateMonth + "-0" + dateDay+ " 星期" + MyDatePicker.getDayOfWeekCN(day_of_week));
//				} else if (dateMonth >= 10 && dateDay < 10) {
//					textView.setText(dateYear + "-" + dateMonth + "-0" + dateDay+ " 星期" + MyDatePicker.getDayOfWeekCN(day_of_week));
//				} else if (dateMonth < 10 && dateDay >= 10) {
//					textView.setText(dateYear + "-0" + dateMonth + "-" + dateDay+ " 星期" + MyDatePicker.getDayOfWeekCN(day_of_week));
//				} else {
//					textView.setText(dateYear + "-" + dateMonth + "-" + dateDay+ " 星期" + MyDatePicker.getDayOfWeekCN(day_of_week));
//				}
            }
        });


        btBeDown.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (dateMonth < 10 && dateDay < 10) {
                    textView.setText(dateYear + "-0" + dateMonth  + "-0" + dateDay);
                } else if (dateMonth >= 10 && dateDay < 10) {
                    textView.setText(dateYear + "-" + dateMonth  + "-0" + dateDay);
                } else if (dateMonth < 10 && dateDay >= 10) {
                    textView.setText(dateYear + "-0" + dateMonth + "-" + dateDay);
                } else {
                    textView.setText(dateYear + "-" + dateMonth  + "-" + dateDay);
                }
//				textView.setText(dateYear + "-" + dateMonth + "-" + dateDay);
                dialog.dismiss();
            }
        });

        btCancel.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btAlldata.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View view) {
                textView.setText("全部日期");
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
//        dialog.setOnKeyListener(new android.content.DialogInterface.OnKeyListener() {
//			@Override
//			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//				switch (keyCode) {
//					case KeyEvent.KEYCODE_BACK:
//						return true;
//				}
//				return false;
//			}
//		});
        dialog.show();

    }
}
