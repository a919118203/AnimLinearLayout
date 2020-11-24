package com.example.animlinearlayout;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AnimLinearLayout extends LinearLayout {
    private static final String TAG = "AnimLinearLayout";
    private AnimatorSet animatorSet;

    private int itemAnimDuration = 200;

    public AnimLinearLayout(Context context) {
        super(context);
    }

    public AnimLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 每个动画的时长
     * @param itemAnimDuration
     */
    public void setItemAnimDuration(int itemAnimDuration) {
        this.itemAnimDuration = itemAnimDuration;
    }

    /**
     * 展开
     * @param animatorListener
     * @param isLeftOpen       展开的方向
     */
    public void showAnim(final Animator.AnimatorListener animatorListener, final boolean isLeftOpen){
        if(animatorSet != null){
            animatorSet.cancel();
        }

        post(new Runnable() {
            @Override
            public void run() {
                int count = getChildCount();
                animatorSet = new AnimatorSet();
                List<Animator> animators = new ArrayList<>();

                for(int i = 0 ; i < count ; i ++){
                    View child = getChildAt(i);
                    animators.add(createShowAnim(child, isLeftOpen, i));
                }

                animatorSet.playSequentially(animators);
                animatorSet.setStartDelay(250);
                animatorSet.start();
                if(animatorListener != null){
                    animatorSet.addListener(animatorListener);
                }
            }
        });
    }

    /**
     * 收起
     * @param clickView                 往哪个子View收起
     * @param animatorListener
     * @param isLeftOpen           收起的方向
     */
    public void hideAnim(final View clickView, final Animator.AnimatorListener animatorListener, final boolean isLeftOpen){
        if(animatorSet != null){
            animatorSet.cancel();
        }

        post(new Runnable() {
            @Override
            public void run() {
                int count = getChildCount();
                animatorSet = new AnimatorSet();

                List<Animator> topAnim = new ArrayList<>();
                List<Animator> bottomAnim = new ArrayList<>();
                boolean isTop = true;
                Animator targetAnim = null;

                for(int i = 0 ; i < count ; i ++){
                    View child = getChildAt(i);
                    if(clickView == child){
                        isTop = false;
                        targetAnim = createHideAnim(child, 0, isLeftOpen);
                    }else if(isTop){
                        topAnim.add(createHideAnim(child, -1, isLeftOpen));
                    }else{
                        bottomAnim.add(0, createHideAnim(child, 1, isLeftOpen));
                    }
                }

                if(targetAnim == null){
                    return ;
                }

                List<Animator> heti = new ArrayList<>();

                int index = 0;
                for(; index < Math.min(topAnim.size(),  bottomAnim.size()); index ++){
                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(topAnim.get(index), bottomAnim.get(index));

                    heti.add(set);
                }

                for(; index < topAnim.size() ; index ++){
                    heti.add(topAnim.get(index));
                }

                for(; index < bottomAnim.size() ; index ++){
                    heti.add(bottomAnim.get(index));
                }

                heti.add(targetAnim);

                animatorSet.playSequentially(heti);
                animatorSet.setStartDelay(250);
                animatorSet.start();

                if(animatorListener != null){
                    animatorSet.addListener(animatorListener);
                }
            }
        });
    }

    /**
     * 判断线性布局的方向，建造动画
     * @param child
     * @param isLeftOpen
     * @param childIndex
     * @return
     */
    private Animator createShowAnim(View child, boolean isLeftOpen, int childIndex){
        Animator animator;

        if(getOrientation() == LinearLayout.VERTICAL){
            if(childIndex == 0){
                if(isLeftOpen){
                    animator = createLeftAnim(true, child);
                }else{
                    animator = createRightAnim(true, child);
                }
            }else{
                animator = createTopAnim(true, child);
            }
        }else{
            if(childIndex == 0){
                if(isLeftOpen){
                    animator = createBottomAnim(true, child);
                }else{
                    animator = createTopAnim(true, child);
                }
            }else{
                animator = createLeftAnim(true, child);
            }
        }
        return animator;
    }

    /**
     *判断线性布局的方向，建造动画
     * @param childView
     * @param inClickViewPosition  -1： 在被点击的View的上面 0： 这个childView为被点击的View 1 在被点击的View的下面
     * @param isLeftOpen
     * @return
     */
    private Animator createHideAnim(View childView, int inClickViewPosition, boolean isLeftOpen){
        Animator animator;

        if(getOrientation() == VERTICAL){
            if(inClickViewPosition == 0){
                if(isLeftOpen){
                    animator = createLeftAnim(false, childView);
                }else{
                    animator = createRightAnim(false, childView);
                }
            }else if(inClickViewPosition == -1){
                animator = createBottomAnim(false, childView);
            }else{
                animator = createTopAnim(false, childView);
            }
        }else{
            if(inClickViewPosition == 0){
                if(isLeftOpen){
                    animator = createBottomAnim(false, childView);
                }else{
                    animator = createTopAnim(false, childView);
                }
            }else if(inClickViewPosition == -1){
                animator = createRightAnim(false, childView);
            }else{
                animator = createLeftAnim(false, childView);
            }
        }
        return animator;
    }

    private Animator createTopAnim(boolean isShow, View child){
        ObjectAnimator oa;

        if(isShow){
            child.setPivotY(0);
            child.setPivotX(child.getWidth() / 2f);
            child.setRotationX(-90);
            child.setRotationY(0);
            oa = ObjectAnimator.ofFloat(child, "rotationX", -90, 0);
            oa.setDuration(itemAnimDuration);
        }else{
            child.setPivotY(0);
            child.setPivotX(getWidth() / 2f);
            child.setRotationX(0);
            child.setRotationY(0);
            oa = ObjectAnimator.ofFloat(child, "rotationX", 0, -90);
            oa.setDuration(itemAnimDuration);
        }

        return oa;
    }

    private Animator createLeftAnim(boolean isShow, View child){
        ObjectAnimator oa;

        if(isShow){
            child.setPivotX(0);
            child.setPivotY(child.getHeight() / 2f);
            child.setRotationY(90);
            child.setRotationX(0);
            oa = ObjectAnimator.ofFloat(child, "rotationY", 90, 0);
            oa.setDuration(itemAnimDuration);
        }else{
            child.setPivotX(0);
            child.setPivotY(child.getHeight() / 2f);
            child.setRotationY(0);
            child.setRotationX(0);
            oa = ObjectAnimator.ofFloat(child, "rotationY", 0 , 90);
            oa.setDuration(itemAnimDuration);
        }

        return oa;
    }

    private Animator createRightAnim(boolean isShow, View child){
        ObjectAnimator oa;

        if(isShow){
            child.setPivotX(child.getWidth());
            child.setPivotY(child.getHeight() / 2f);
            child.setRotationY(-90);
            child.setRotationX(0);
            oa = ObjectAnimator.ofFloat(child, "rotationY", -90, 0);
            oa.setDuration(itemAnimDuration);
        }else{
            child.setPivotX(child.getWidth());
            child.setPivotY(child.getHeight() / 2f);
            child.setRotationY(0);
            child.setRotationX(0);
            oa = ObjectAnimator.ofFloat(child, "rotationY", 0 , -90);
            oa.setDuration(itemAnimDuration);
        }

        return oa;
    }

    private Animator createBottomAnim(boolean isShow, View child){
        ObjectAnimator oa;

        if(isShow){
            child.setPivotY(child.getHeight());
            child.setPivotX(child.getWidth() / 2f);
            child.setRotationX(90);
            child.setRotationY(0);
            oa = ObjectAnimator.ofFloat(child, "rotationX", 90, 0);
            oa.setDuration(itemAnimDuration);
        }else{
            child.setPivotY(child.getHeight());
            child.setPivotX(child.getWidth() / 2f);
            child.setRotationX(0);
            child.setRotationY(0);
            oa = ObjectAnimator.ofFloat(child, "rotationX", 0, 90);
            oa.setDuration(itemAnimDuration);
        }

        return oa;
    }
}
