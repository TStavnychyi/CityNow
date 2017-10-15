package com.tstv.infofrom.common.behaviors;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tstv on 13.10.2017.
 */

public class CircleImageBehavior extends CoordinatorLayout.Behavior<CircleImageView> {

    public CircleImageBehavior() {

    }

    public CircleImageBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, CircleImageView child, View dependency) {
        return dependency instanceof ImageView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, CircleImageView child, View dependency) {

        int offset = dependency.getTop();
        child.setTranslationY(offset);

        return true;
    }
}
