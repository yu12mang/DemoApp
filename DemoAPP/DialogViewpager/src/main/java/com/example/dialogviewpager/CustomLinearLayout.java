package com.example.dialogviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CustomLinearLayout extends LinearLayout {
    private Context context;
    private List<View> views;

    public CustomLinearLayout(final Context context) {
        super(context);
    }

    public CustomLinearLayout(final Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        LayoutInflater mInflater = LayoutInflater.from(context);
        View myView = mInflater.inflate(R.layout.layout_custom_linear, null);
        addView(myView);
        ViewPager viewPager = (ViewPager) this.findViewById(R.id.viewpager);

        views=new ArrayList<View>();
        View viewOne = mInflater.inflate(R.layout.viewpager_one, null);
        Button button = (Button) viewOne.findViewById(R.id.btn_click);

        views.add(viewOne);
        views.add(mInflater.inflate(R.layout.viewpager_two, null));
        views.add(mInflater.inflate(R.layout.viewpager_three, null));
        PagerAdapter adapter=new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0==arg1;
            }
            //有多少个切换页
            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return views.size();
            }

            //对超出范围的资源进行销毁
            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                // TODO Auto-generated method stub
                //super.destroyItem(container, position, object);
                container.removeView(views.get(position));
            }
            //对显示的资源进行初始化
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                //return super.instantiateItem(container, position);
                container.addView(views.get(position));
                return views.get(position);
            }

        };
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "您选择了："+arg0+"页面", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });

    }
}
