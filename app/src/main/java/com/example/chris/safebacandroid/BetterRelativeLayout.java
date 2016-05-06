package com.example.chris.safebacandroid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

/**
 * Created by Steel on 4/28/16.
 */
public class BetterRelativeLayout extends RelativeLayout {
    private float yPosition = 0;
    private float xPosition = 0;
    private ViewTreeObserver.OnPreDrawListener pdListen = null;

    public BetterRelativeLayout(Context context){
        super(context);
    }
    public BetterRelativeLayout(Context context, AttributeSet attributes){
        super(context, attributes);
    }
    public BetterRelativeLayout(Context context, AttributeSet attributes, int defStyleAttr){
        super(context, attributes, defStyleAttr);
    }

    public void setYPosition(float position){
        this.yPosition = position;

        if (getHeight() == 0) {
            if (pdListen == null){
                pdListen = new ViewTreeObserver.OnPreDrawListener(){
                    @Override
                    public boolean onPreDraw(){
                        getViewTreeObserver().removeOnPreDrawListener(pdListen);
                        setYPosition(yPosition);
                        return true;
                    }
                };
                getViewTreeObserver().addOnPreDrawListener(pdListen);
            }
            return;
        }
        float locationY = getHeight() * position;
        setTranslationY(locationY);
    }

    public float getYPosition(){
        return this.yPosition;
    }

    public void setXPosition(float location){
        this.xPosition = location;
        if (getWidth() == 0){
            if (pdListen == null){
                pdListen = new ViewTreeObserver.OnPreDrawListener(){
                    @Override
                    public boolean onPreDraw(){
                        getViewTreeObserver().removeOnPreDrawListener(pdListen);
                        setXPosition(xPosition);
                        return true;
                    }
                };
                getViewTreeObserver().addOnPreDrawListener(pdListen);
            }
            return;
        }
        float locationX = getHeight() * location;
    }

    public float getxPosition(){
        return this.xPosition;
    }
}
