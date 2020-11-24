package com.example.animlinearlayout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

    private AnimLinearLayout animLinearLayoutVertical;
    private AnimLinearLayout animLinearLayoutHorizontal;
    private RadioGroup radioGroup, orientationRadioGroup;

    private boolean isLeft = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animLinearLayoutVertical = findViewById(R.id.anim_linear_layout_vertical);
        animLinearLayoutHorizontal = findViewById(R.id.anim_linear_layout_horizontal);
        setClickListener(animLinearLayoutVertical);
        setClickListener(animLinearLayoutHorizontal);

        radioGroup = findViewById(R.id.left_right_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                isLeft = true;
                if(i == R.id.radio_right){
                    isLeft = false;
                }
            }
        });

        orientationRadioGroup = findViewById(R.id.orientation_radio_group);
        orientationRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.radio_vertical){
                    animLinearLayoutVertical.setVisibility(View.VISIBLE);
                    animLinearLayoutHorizontal.setVisibility(View.GONE);
                }else if(i == R.id.radio_horizontal){
                    animLinearLayoutVertical.setVisibility(View.GONE);
                    animLinearLayoutHorizontal.setVisibility(View.VISIBLE);
                }
            }
        });

        findViewById(R.id.open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orientationRadioGroup.getCheckedRadioButtonId() == R.id.radio_vertical){
                    animLinearLayoutVertical.showAnim(null, isLeft);
                }else{
                    animLinearLayoutHorizontal.showAnim(null, isLeft);
                }
            }
        });
    }

    private void setClickListener(final LinearLayout linearLayout){
        int childCount = linearLayout.getChildCount();
        for(int i = 0 ; i < childCount ; i ++){
            View child = linearLayout.getChildAt(i);
            final int finalI = i;
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(linearLayout.getOrientation() == LinearLayout.VERTICAL){
                        animLinearLayoutVertical.hideAnim(animLinearLayoutVertical.getChildAt(finalI), null, isLeft);
                    }else{
                        animLinearLayoutHorizontal.hideAnim(animLinearLayoutHorizontal.getChildAt(finalI), null, isLeft);
                    }
                }
            });
        }
    }
}