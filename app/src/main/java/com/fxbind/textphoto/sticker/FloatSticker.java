package com.fxbind.textphoto.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fxbind.textphoto.R;
import com.fxbind.textphoto.interfaces.OnFloatViewTouchListener;
import com.fxbind.textphoto.main.MainActivity;

/**
 * Created by TienDam on 11/14/2016.
 */

public class FloatSticker extends ImageView {
    public Bitmap mainBitmap, rotateBitmap, scaleBitmap;
    public Paint paint;
    public RelativeLayout.LayoutParams params;
    public Rect rectBorder;
    public MainActivity mActivity;
    public Point initScalePoint, initCenterPoint, initRotatePoint,
            initTopRightPoint, initBottomLeftPoint, initTopLeftPoint, initBottomRightPoint;

    public int width, height;
    public float x, y, xExport, yExport,
                    xMin, yMin, xMax, yMax;
    public float rotation = 0;
    public float[] scalePoint, centerPoint, rotatePoint,
            topRightPoint, bottomLeftPoint, topLeftPoint, bottomRightPoint;
    public float scaleValue = 1f;
    public float widthScale, heightScale;
    public boolean isCompact;
    public boolean drawBorder;
    private OnFloatViewTouchListener mCallback;

    public static final int MAX_DIMENSION = 300;
    public static final int ROTATE_CONSTANT = 30;
    public static final int INIT_X = 300, INIT_Y = 300;

    public FloatSticker(Context context, Bitmap bitmap) {
        super(context);
        mActivity = (MainActivity) context;
        mCallback = mActivity.mTextFragment;
        boolean maxWidth = bitmap.getWidth()>bitmap.getHeight();
        width = maxWidth? MAX_DIMENSION: MAX_DIMENSION*bitmap.getWidth()/bitmap.getHeight();
        height = maxWidth? MAX_DIMENSION*bitmap.getHeight()/bitmap.getWidth():MAX_DIMENSION;
        widthScale = width;
        heightScale = height;
        x = INIT_X;
        y = INIT_Y;

        initRotatePoint = new Point(0, 0);
        initCenterPoint = new Point(width/2, height/2);
        initScalePoint = new Point(width, height);
        initBottomLeftPoint = new Point(0, height);
        initTopRightPoint = new Point(width, 0);
        initTopLeftPoint = new Point(0, 0);
        initBottomRightPoint = new Point(width, height);

        rotatePoint = new float[2];
        scalePoint = new float[2];
        centerPoint = new float[2];
        topRightPoint = new float[2];
        bottomLeftPoint = new float[2];
        topLeftPoint = new float[2];
        bottomRightPoint = new float[2];

        rotateBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_rotate);
        scaleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_scale);
        mainBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        paint = new Paint();
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setFullLayout();
        setOnTouchListener(onTouchListener);
        setOnClickListener(onClickListener);
        rectBorder = new Rect(0, 0, (int)widthScale, (int)heightScale);
        drawBorder = true;
    }

    private void initBorderPoints(){
        rotatePoint[0] = initRotatePoint.x;
        rotatePoint[1] = initRotatePoint.y;
        scalePoint[0] = initScalePoint.x;
        scalePoint[1] = initScalePoint.y;

        centerPoint[0] = initCenterPoint.x;
        centerPoint[1] = initCenterPoint.y;

        topRightPoint[0] = initTopRightPoint.x;
        topRightPoint[1] = initTopRightPoint.y;
        bottomLeftPoint[0] = initBottomLeftPoint.x;
        bottomLeftPoint[1] = initBottomLeftPoint.y;
        topLeftPoint[0] = initTopLeftPoint.x;
        topLeftPoint[1] = initTopLeftPoint.y;
        bottomRightPoint[0] = initBottomRightPoint.x;
        bottomRightPoint[1] = initBottomRightPoint.y;
    }

    public void drawBorder(boolean draw){
        drawBorder = draw;
        if (draw) {
            bringToFront();
        }
        invalidate();
    }

    private void setFullLayout(){
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.topMargin = 0;
        params.leftMargin = 0;
        setLayoutParams(params);
        invalidate();
        isCompact = false;
    }

    private double getAngle(double xTouch, double yTouch) {
        double x = xTouch - centerPoint[0];
        double y = centerPoint[1] - yTouch;

        switch (getQuadrant(x, y)) {

            case 1:
                return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 2:
                return 180 - Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 3:
                return 180 + (-1 * Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
            case 4:
                return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            default:
                return 0;
        }
    }

    private static int getQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }
    public void scaleImage(float moveX, float moveY){
        if (Math.abs(scalePoint[0]-centerPoint[0])>100){
            if (scalePoint[0] >= centerPoint[0]) {
                widthScale += moveX;
            } else {
                widthScale -= moveX;
            }
            scaleValue = widthScale/width;
            heightScale = scaleValue*height;
        } else {
            if (scalePoint[1] >= centerPoint[1]){
                heightScale += moveY;
            } else {
                heightScale -= moveY;
            }
            scaleValue = heightScale/height;
            widthScale = scaleValue*width;
        }
        invalidate();
    }

    public void moveImage(float moveX, float moveY) {
        x += moveX;
        y += moveY;
        invalidate();
    }

    private void getBorderPointsCoord(Matrix matrix){
        matrix.mapPoints(scalePoint);
        matrix.mapPoints(rotatePoint);
        matrix.mapPoints(bottomLeftPoint);
        matrix.mapPoints(topRightPoint);
        matrix.mapPoints(topLeftPoint);
        matrix.mapPoints(bottomRightPoint);
    }

    private void getLayoutLimit(){
        xExport = Math.min(Math.min(Math.min(topLeftPoint[0], bottomRightPoint[0]), bottomLeftPoint[0]), topRightPoint[0]);
        yExport = Math.min(Math.min(Math.min(topLeftPoint[1], bottomRightPoint[1]), bottomLeftPoint[1]), topRightPoint[1]);
        xMin = xExport;
        yMin = yExport;
        xMax = Math.max(Math.max(Math.max(topLeftPoint[0], bottomRightPoint[0]), bottomLeftPoint[0]), topRightPoint[0]);
        yMax = Math.max(Math.max(Math.max(topLeftPoint[1], bottomRightPoint[1]), bottomLeftPoint[1]), topRightPoint[1]);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Matrix matrix = new Matrix();

        matrix.postTranslate(x, y);

        initBorderPoints();

        matrix.mapPoints(centerPoint);

        matrix.postScale(scaleValue, scaleValue, centerPoint[0], centerPoint[1]);
        matrix.postRotate(-rotation, centerPoint[0], centerPoint[1]);

        getBorderPointsCoord(matrix);
        getLayoutLimit();

        canvas.drawBitmap(mainBitmap, matrix, paint);

        if(!drawBorder) {
            return;
        }
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.CYAN);
        paint.setPathEffect(new DashPathEffect(new float[] {8,6}, 0));

        canvas.save();
        // befor N canvas apply matrix from leftside of screen
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
//            matrix.postTranslate(mActivity.mVideoViewLeft, 0);
//            Rect rectBound = canvas.getClipBounds();
//            canvas.clipRect(rectBound.left, rectBound.top,
//                    rectBound.right + mActivity.mVideoViewLeft, rectBound.bottom, Region.Op.REPLACE);
//        }

        canvas.setMatrix(matrix);
        canvas.drawRect(rectBorder, paint);
        canvas.restore();

        canvas.drawBitmap(rotateBitmap, (int) rotatePoint[0]-ROTATE_CONSTANT, (int) rotatePoint[1]-ROTATE_CONSTANT, paint);
        canvas.drawBitmap(scaleBitmap, (int) scalePoint[0] - ROTATE_CONSTANT, (int) scalePoint[1]-ROTATE_CONSTANT, paint);
    }

    OnTouchListener onTouchListener = new OnTouchListener() {
        float oldX, oldY, moveX, moveY;
        double startAngle, currentAngle;
        int touch = 0;
        float delta = 10;
        boolean isTouch;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    bringToFront();
                    oldX = motionEvent.getX();
                    oldY = motionEvent.getY();
                    startAngle = getAngle(oldX, oldY);
                    int eps = 75;
                    if (oldX < scalePoint[0]+eps && oldX > scalePoint[0]-eps && oldY < scalePoint[1]+eps && oldY > scalePoint[1]-eps){
                        touch = 1;
                    } else if (oldX < centerPoint[0]+eps && oldX > centerPoint[0]-eps && oldY < centerPoint[1]+eps && oldY > centerPoint[1]-eps) {
                        touch = 2;
                    } else if (oldX < rotatePoint[0]+eps && oldX > rotatePoint[0]-eps && oldY < rotatePoint[1]+eps && oldY > rotatePoint[1]-eps){
                        touch = 3;
                    } else {
                        touch = 0;
                    }

                    isTouch = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    moveX = motionEvent.getX() - oldX;
                    moveY = motionEvent.getY() - oldY;
                    if (Math.abs(moveX) >= delta && Math.abs(moveY)>= delta) {
                        isTouch = true;
                    }

                    if (!isTouch || !drawBorder) {
                        return false;
                    }

                    if (touch == 1) {
                        scaleImage(moveX, moveY);
                    }
                    if (touch == 2) {
                        moveImage(moveX, moveY);
                    }
                    if (touch == 3) {
                        currentAngle = getAngle(motionEvent.getX(), motionEvent.getY());
                        rotation += (currentAngle-startAngle);
                        invalidate();
                        startAngle = currentAngle;
                    }
                    oldX = motionEvent.getX();
                    oldY = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    if (!isTouch){
                        if (touch != 0) {
                            performClick();
                        } else {
                            mCallback.onTouch(oldX, oldY);
                        }
                    }
                    break;
            }
            return true;
        }
    };


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (drawBorder) {
                drawBorder(false);
                mActivity.setBtnDeleteTextVisible(false);
            } else {
                drawBorder(true);
                mActivity.setBtnDeleteTextVisible(true);
                mCallback.onSelected(FloatSticker.this);
            }
            invalidate();
        }
    };
    private void log(String msg){
        Log.e("Log for FloatImage",msg);
    }
}
