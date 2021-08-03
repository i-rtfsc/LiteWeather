/*
 * Copyright (c) 2018 anqi.huang@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.journeyOS.widget.sky;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.AnimationUtils;

public class SkyView extends SurfaceView implements SurfaceHolder.Callback {

    static final String TAG = SkyView.class.getSimpleName();
    private DrawThread mDrawThread;

    public SkyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private BaseSky preSky, curSky;
    private float curDrawerAlpha = 0f;
    private SkyType curType = SkyType.DEFAULT;
    private int mWidth, mHeight;

    private void init(Context context) {
        curDrawerAlpha = 0f;
        mDrawThread = new DrawThread();
        final SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
        mDrawThread.start();
    }

    private void setDrawer(BaseSky baseSky) {
        if (baseSky == null) {
            return;
        }
        curDrawerAlpha = 0f;
        if (this.curSky != null) {
            this.preSky = curSky;
        }
        this.curSky = baseSky;
        // updateDrawerSize(getWidth(), getHeight());
        // invalidate();
    }

    public void setDrawerType(SkyType type) {
        if (type == null) {
            return;
        }

        if (type != curType) {
            curType = type;
            setDrawer(ResourceSky.getSky(getContext(), curType));
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        updateDrawerSize(w, h);
        mWidth = w;
        mHeight = h;
    }

    private void updateDrawerSize(int w, int h) {
        if (w == 0 || h == 0) {
            return;
        }// 这里必须加锁，因为在DrawThread.drawSurface的时候调用的是各种Drawer的绘制方法
        // 绘制的时候会遍历内部的各种holder
        // 然而那些个雨滴/星星的holder是在setSize的时候生成的
        if (this.curSky != null) {
            synchronized (curSky) {
                if (this.curSky != null) {
                    curSky.setSize(w, h);
                }
            }
        }
        if (this.preSky != null) {
            synchronized (preSky) {
                if (this.preSky != null) {//简直我就震惊了synchronized之前不是null，synchronized之后就有可能是null!
                    preSky.setSize(w, h);
                }
            }
        }

    }

    private boolean drawSurface(Canvas canvas) {
        final int w = mWidth;
        final int h = mHeight;
        if (w == 0 || h == 0) {
            return true;
        }
        boolean needDrawNextFrame = false;
        if (curSky != null) {
            curSky.setSize(w, h);
            needDrawNextFrame = curSky.draw(canvas, curDrawerAlpha);
        }
        if (preSky != null && curDrawerAlpha < 1f) {
            needDrawNextFrame = true;
            preSky.setSize(w, h);
            preSky.draw(canvas, 1f - curDrawerAlpha);
        }
        if (curDrawerAlpha < 1f) {
            curDrawerAlpha += 0.04f;
            if (curDrawerAlpha > 1) {
                curDrawerAlpha = 1f;
                preSky = null;
            }
        }
        // if (needDrawNextFrame) {
        // ViewCompat.postInvalidateOnAnimation(this);
        // }
        return needDrawNextFrame;
    }

    public void onResume() {
        // Let the drawing thread resume running.
        synchronized (mDrawThread) {
            mDrawThread.mRunning = true;
            mDrawThread.notify();
        }
        Log.i(TAG, "onResume");
    }

    public void onPause() {
        // Make sure the drawing thread is not running while we are paused.
        synchronized (mDrawThread) {
            mDrawThread.mRunning = false;
            mDrawThread.notify();
        }
    }

    public void onDestroy() {
        // Make sure the drawing thread goes away.
        synchronized (mDrawThread) {
            mDrawThread.mQuit = true;
            mDrawThread.notify();
        }
        Log.i(TAG, "onDestroy");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Tell the drawing thread that a surface is available.
        synchronized (mDrawThread) {
            mDrawThread.mSurface = holder;
            mDrawThread.notify();
        }
        Log.i(TAG, "surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // We need to tell the drawing thread to stop, and block until
        // it has done so.
        synchronized (mDrawThread) {
            mDrawThread.mSurface = holder;
            mDrawThread.notify();
            while (mDrawThread.mActive) {
                try {
                    mDrawThread.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        holder.removeCallback(this);
        Log.i(TAG, "surfaceDestroyed");
    }

    private class DrawThread extends Thread {
        // These are protected by the Thread's lock.
        SurfaceHolder mSurface;
        boolean mRunning;
        boolean mActive;
        boolean mQuit;

        @Override
        public void run() {
            while (true) {
                // Log.i(TAG, "DrawThread run..");
                // Synchronize with activity: block until the activity is ready
                // and we have a surface; report whether we are active or
                // inactive
                // at this point; exit thread when asked to quit.
                synchronized (this) {
                    while (mSurface == null || !mRunning) {
                        if (mActive) {
                            mActive = false;
                            notify();
                        }
                        if (mQuit) {
                            return;
                        }
                        try {
                            wait();
                        } catch (InterruptedException e) {
                        }
                    }

                    if (!mActive) {
                        mActive = true;
                        notify();
                    }
                    final long startTime = AnimationUtils.currentAnimationTimeMillis();
                    // Lock the canvas for drawing.
                    Canvas canvas = mSurface.lockCanvas();

                    if (canvas != null) {
//                        canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
                        // Update graphics.

                        drawSurface(canvas);
                        // All done!
                        mSurface.unlockCanvasAndPost(canvas);
                    } else {
                        Log.i(TAG, "Failure locking canvas");
                    }
                    final long drawTime = AnimationUtils.currentAnimationTimeMillis() - startTime;
                    final long needSleepTime = 16 - drawTime;
                    //Log.i(TAG, "drawSurface drawTime->" + drawTime + " needSleepTime->" + Math.max(0, needSleepTime));// needSleepTime);
                    if (needSleepTime > 0) {
                        try {
                            Thread.sleep(needSleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
    }

}