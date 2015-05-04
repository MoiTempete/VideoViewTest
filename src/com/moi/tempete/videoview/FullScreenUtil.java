package com.moi.tempete.videoview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.*;

/**
 * Created by MoiTempete.
 */
public class FullScreenUtil {

    private static final String TAG = FullScreenUtil.class.getSimpleName();

    private final static int KEY_VIEW_LAYOUT_PARAM = R.id.view_layout_params;
    private final static int KEY_VIEW_VISIBILITY = R.id.view_visibility;
    private final static int KEY_VIEW_FULLSCREEN = R.id.view_full_screen;
    private final static int KEY_PADDING_TOP = R.id.padding_top;
    private final static int KEY_PADDING_BOTTOM = R.id.padding_bottom;
    private final static int KEY_PADDING_LEFT = R.id.padding_left;
    private final static int KEY_PADDING_RIGHT = R.id.padding_right;
    private final static int DEFAULT_ANIMATION_DURATION = 2000;
    // private static ViewGroup sOriginalParent;
    // private static ViewGroup.LayoutParams sOriginalParam;

    private static int sDevicesScreenWidth;
    private static int sDevicesScreenHeight;

    private static int animationDuration;

    // // by remove and re-add
    // public static void toggleFullscreenX(View fullscreenView) {
    // boolean fullscreen = isViewFullscreen(fullscreenView);
    // fullscreenView.setTag(KEY_VIEW_FULLSCREEN, !fullscreen);
    //
    // ViewGroup contentViewGroup = (ViewGroup)
    // ((Activity)fullscreenView.getContext()).findViewById(android.R.id.content);
    // if (fullscreen) {
    // sOriginalParent = (ViewGroup) fullscreenView.getParent();
    // sOriginalParam = fullscreenView.getLayoutParams();
    // sOriginalParent.removeView(fullscreenView);
    //
    // FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
    // FrameLayout.LayoutParams.FILL_PARENT);
    // contentViewGroup.addView(fullscreenView, p);
    // processAllSiblingView(contentViewGroup, fullscreenView, new ViewOperation() {
    //
    // @Override
    // public void onOperation(View view) {
    // view.setTag(KEY_VIEW_VISIBILITY, view.getVisibility());
    // }
    // });
    // } else {
    // ((ViewGroup)fullscreenView.getParent()).removeView(fullscreenView);
    // sOriginalParent.addView(fullscreenView, 0, sOriginalParam);
    //
    // processAllSiblingView(contentViewGroup, fullscreenView, new ViewOperation() {
    //
    // @Override
    // public void onOperation(View view) {
    // view.setVisibility((Integer) view.getTag(KEY_VIEW_VISIBILITY));
    // }
    // });
    // }
    // }
    //
    // private static void processAllSiblingView(ViewGroup p, View view, ViewOperation operation){
    // final int count = p.getChildCount();
    // View child = null;
    // for (int i = 0 ; i < count ; i++) {
    // child = p.getChildAt(i);
    // if (child != view) {
    // operation.onOperation(child);
    // }
    // }
    // }

    private static boolean isViewFullscreen(View view) {
        Object tag = view.getTag(KEY_VIEW_FULLSCREEN);
        return tag == null ? true : (Boolean) tag;
    }

    // by change view visibility and layout params
    private static void toggleFullscreen(View fullscreenView) {
        boolean fullscreen = isViewFullscreen(fullscreenView);
        fullscreenView.setTag(KEY_VIEW_FULLSCREEN, !fullscreen);

        if (fullscreen) {
            startFullscreen(fullscreenView);
        } else {
            stopFullScreen(fullscreenView);
        }
    }

    private static void startFullscreen(View fullscreenView) {
        if (null == fullscreenView) {
            return;
        }

        if (shouldStop(fullscreenView))
            return;

        ViewGroup g = (ViewGroup) fullscreenView.getParent();
        getViewGroupPadding(g);
        setAllSiblingViewParam(g, fullscreenView, true);
        startFullscreen(g);
    }

    private static void stopFullScreen(View fullscreenView) {
        if (null == fullscreenView) {
            return;
        }

        if (shouldStop(fullscreenView))
            return;

        ViewGroup g = (ViewGroup) fullscreenView.getParent();
        setViewGroupPadding(g);
        setAllSiblingViewParam(g, fullscreenView, false);
        stopFullScreen(g);
    }

    private static void getViewGroupPadding(ViewGroup viewGroup) {
        viewGroup.setTag(KEY_PADDING_TOP, viewGroup.getPaddingTop());
        viewGroup.setTag(KEY_PADDING_BOTTOM, viewGroup.getPaddingBottom());
        viewGroup.setTag(KEY_PADDING_LEFT, viewGroup.getPaddingLeft());
        viewGroup.setTag(KEY_PADDING_RIGHT, viewGroup.getPaddingRight());
        viewGroup.setPadding(0, 0, 0, 0);
    }

    private static void setViewGroupPadding(ViewGroup viewGroup) {
        viewGroup.setPadding((Integer) viewGroup.getTag(KEY_PADDING_LEFT), (Integer) viewGroup.getTag(KEY_PADDING_TOP),
                (Integer) viewGroup.getTag(KEY_PADDING_RIGHT), (Integer) viewGroup.getTag(KEY_PADDING_BOTTOM));
    }

    private static void setAllSiblingViewParam(ViewGroup g, View fullscreenView, boolean fullscreen) {
        final int count = g.getChildCount();
        View child;
        for (int i = 0; i < count; i++) {
            child = g.getChildAt(i);
            if (fullscreen) {
                ViewGroup.LayoutParams originLp = child.getLayoutParams();
                int originalV = child.getVisibility();
                if (child != fullscreenView) {
                    // save all sibling view's param, and gone them.
                    // for every used layout, change new layoutParam
                    child.setTag(KEY_VIEW_VISIBILITY, originalV);
                    child.setTag(KEY_VIEW_LAYOUT_PARAM, originLp);
                    child.setVisibility(View.GONE);
                    Log.d(TAG, "save state.    " + originalV + " layoutP: " + originLp + " view: " + child);
                } else {
                    // make fullscreen view 'fullscreen'
                    child.setTag(KEY_VIEW_VISIBILITY, originalV);
                    child.setTag(KEY_VIEW_LAYOUT_PARAM, originLp);
                    Log.d(TAG, "save state.    " + originalV + " layoutP: " + originLp + " view: " + child);

                    if (g instanceof AbsoluteLayout) {
                        AbsoluteLayout.LayoutParams p = new AbsoluteLayout.LayoutParams(getFullscreenWidth(),
                                getFullscreenHeight(), 0, 0);
                        child.setLayoutParams(p);
                    } else if (g instanceof RelativeLayout) {
                        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(originLp);
                        p.height = getFullscreenHeight();
                        p.width = getFullscreenWidth();
                        p.leftMargin = 0;
                        p.rightMargin = 0;
                        p.topMargin = 0;
                        p.bottomMargin = 0;
                        child.setLayoutParams(p);
                    } else if (g instanceof LinearLayout) {
                        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(originLp);
                        p.height = getFullscreenHeight();
                        p.width = getFullscreenWidth();
                        p.leftMargin = 0;
                        p.rightMargin = 0;
                        p.topMargin = 0;
                        p.bottomMargin = 0;
                        child.setLayoutParams(p);
                    } else if (g instanceof FrameLayout) {
                        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(originLp);
                        p.height = getFullscreenHeight();
                        p.width = getFullscreenWidth();
                        p.leftMargin = 0;
                        p.rightMargin = 0;
                        p.topMargin = 0;
                        p.bottomMargin = 0;
                        child.setLayoutParams(p);
                    }
                    // else if (g instanceof ViewPager) {
                    // ViewPager.LayoutParams p = new ViewPager.LayoutParams((ViewPager.LayoutParams)originLp);
                    // p.height = getFullscreenHeight(g);
                    // p.width = getFullscreenWidth(g);
                    // // p.leftMargin = 0;
                    // // p.rightMargin = 0;
                    // // p.topMargin = 0;
                    // // p.bottomMargin = 0;
                    // child.setLayoutParams(p);
                    // }
                }
            } else if (child != null){
                // restore all sibling view's param.
                int originalV = (Integer) child.getTag(KEY_VIEW_VISIBILITY);
                ViewGroup.LayoutParams originalLp = (ViewGroup.LayoutParams) child.getTag(KEY_VIEW_LAYOUT_PARAM);
                child.setVisibility(originalV);
                child.setLayoutParams(originalLp);

                Log.d(TAG, "restore state. " + originalV + " layoutP: " + originalLp + " view: " + child);
            }

        }
    }

    private static int getFullscreenWidth() {
        assureScreenDimen();
        // return Math.max(sDevicesScreenWidth, parent.getWidth());
        return sDevicesScreenWidth;
    }

    private static int getFullscreenHeight() {
        assureScreenDimen();
        // return Math.max(sDevicesScreenWidth, parent.getHeight());
        return sDevicesScreenHeight;
    }

    private static void assureScreenDimen() {
        if (sDevicesScreenHeight == 0 || sDevicesScreenWidth == 0) {
            throw new IllegalStateException("you must init sDevicesScreenHeight and/or sDevicesScreenWidth.");
        }
    }

    private static boolean shouldStop(View fullscreenView) {
        return !(fullscreenView.getParent() instanceof View);
    }

//    interface ViewOperation {
//        public void onOperation(View view);
//    }

    public static void initParamsWithDisplay(Activity activity) {
        sDevicesScreenHeight = getDisplayHeight(activity);
        sDevicesScreenWidth = getDisplayWidth(activity);
    }

    private static int getDisplayHeight(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        return display.getHeight();
    }

    private static int getDisplayWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        return display.getWidth();
    }

    private static float zoomX, zoomY;
    private static float transX, transY;
    private static int[] location = new int[2];
    private static ImageView imageView;
    private static AbsoluteLayout absoluteLayout;
    private static ViewGroup contentViewGroup;

    /**
     * 放大/缩小目标View
     * @param activity view所在的Activity
     * @param view 需要进行缩放的View
     * @param listener 缩放动画监听
     * @param duration 缩放动画时长设置,单位ms,duration<=0时使用默认值,默认值为2000ms
     */
    public static void zoomView(Activity activity, View view, OnViewZoomListener listener, int duration) {
        boolean isFullScreen = FullScreenUtil.isViewFullscreen(view);
        if (duration >= 0) {
            animationDuration = duration;
        } else {
            animationDuration = DEFAULT_ANIMATION_DURATION;
        }

        Log.d(TAG, "toggleFullscreen without animation isFullScreen is " + isFullScreen);
        if (!isFullScreen) {
            listener.onViewZoomStart();
            toggleFullscreen(view);
            listener.onViewZoomEnd();
        } else {
            initAnimationData(activity, view);
            view.setVisibility(View.INVISIBLE);
            startZoomAnimation(view, listener);
        }
    }

    private static void initAnimationData(Activity activity, View view) {
        int displayWidth, displayHeight, viewWidth, viewHeight;
        displayHeight = FullScreenUtil.getDisplayHeight(activity);
        displayWidth = FullScreenUtil.getDisplayWidth(activity);
        viewWidth = view.getWidth();
        viewHeight = view.getHeight();
        zoomX = (float) displayHeight / (float) viewHeight;
        zoomY = (float) displayWidth / (float) viewWidth;
        view.getLocationOnScreen(location);
        transX = -location[0];
        transY = -location[1];
        Log.d(TAG, "\ndisplayHeight = " + displayHeight + "\n displayWidth = " + displayWidth);
        Log.d(TAG, "\nview.getHeight() = " + view.getHeight() + "\n view.getWidth() = " + view.getWidth());
        Log.d(TAG, "\nzoomX = " + zoomX + " \nzoomY = " + zoomY + "\nlocation is " + location[0] + " & " + location[1]
                + "\ntransX = " + transX + " & transY = " + transY);

        contentViewGroup = (ViewGroup) ((Activity) view.getContext()).findViewById(android.R.id.content);
        absoluteLayout = new AbsoluteLayout(activity);
        FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
        FrameLayout.LayoutParams.FILL_PARENT);
        imageView = new ImageView(activity);
        imageView.setBackgroundDrawable(new BitmapDrawable(convertViewToBitmap(view)));
        AbsoluteLayout.LayoutParams rp = new AbsoluteLayout.LayoutParams(view.getWidth(), view.getHeight(),
        location[0], location[1]);
        absoluteLayout.addView(imageView, rp);
        contentViewGroup.addView(absoluteLayout, fp);
    }

    private static void startZoomAnimation(final View view, final OnViewZoomListener listener) {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, zoomX, 1.0f, zoomY);

        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, 0f, Animation.ABSOLUTE,
                transX, Animation.ABSOLUTE, 0f, Animation.ABSOLUTE, transY);

        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(translateAnimation);
        animationSet.setDuration(animationDuration);
        animationSet.setFillAfter(true);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                listener.onViewZoomStart();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                absoluteLayout.removeAllViews();
                contentViewGroup.removeView(absoluteLayout);
                toggleFullscreen(view);
                listener.onViewZoomEnd();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                listener.onViewZoomRepeat();
            }
        });
        imageView.startAnimation(animationSet);
    }

    private static Bitmap convertViewToBitmap(View view) {
//        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_4444);
//        view.draw(new Canvas(bitmap));
        Bitmap bitmap = view.getDrawingCache();
        if (bitmap != null) {
            Log.d(TAG, "bitmap is not null \n width = " + bitmap.getWidth() + "\nheight = " + bitmap.getHeight());
        } else {
            Log.d(TAG, "bitmap is null");
        }

        return bitmap;
    }

    interface OnViewZoomListener {
        void onViewZoomStart();
        void onViewZoomEnd();
        void onViewZoomRepeat();
    }
}
