package com.fxbind.textphoto.text;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fxbind.textphoto.R;
import com.fxbind.textphoto.export.TextHolder;
import com.fxbind.textphoto.fragment.TextFragment;
import com.fxbind.textphoto.helper.Utils;
import com.fxbind.textphoto.main.MainActivity;

import java.io.File;


/**
 * Created by TienDam on 11/14/2016.
 */

public class FloatText extends ImageView {
    public MainActivity mActivity;
    public TextFragment mTextFragment;

    public Bitmap rotateBitmap, scaleBitmap;
    public Paint paint;
    public RelativeLayout.LayoutParams params;
    public Rect rectBorder, rectBackground;
    public Point initBorderBottomRight, initCenterPoint, initBorderTopLeft, initBorderTopRight, initBorderBottomLeft,
            initTopRightPoint, initBottomLeftPoint, initTopLeftPoint, initBottomRightPoint;
    public String text;
    public TextPaint textPaint;
    public Rect bound;
    public Typeface mTypeface;
    public String fontPath;

    public int width, height;
    public float x, y, xExport, yExport, xMax, yMax, xMin, yMin;
    public float rotation = 0;
    public float[] borderBottomRight, centerPoint, borderTopLeft, borderBottomLeft, borderTopRight,
            topRightPoint, bottomLeftPoint, topLeftPoint, bottomRightPoint;
    public float scaleValue = 1f;
    public float widthScale, heightScale;
    public boolean isCompact;
    public boolean drawBorder;
    public int widthMax, heightMax;
    public int mStyle;
    public int mColor;
    public int mBackgroundColor;
    public float size, sizeScale;
    public int fontId;
    public int[] anchorPoint;

    public TextHolder textHolder;
    public OnFloatTextTouchListener mCallback;

    public static final int ROTATE_CONSTANT = 30;
    public static final int INIT_X = 300, INIT_Y = 300, INIT_SIZE = 60;
    public static final int PADDING = 30;

    public FloatText(MainActivity activity, String text) {
        super(activity);
        x = INIT_X;
        y = INIT_Y;
        size = INIT_SIZE;
        sizeScale = size;
        this.text = text;
        mActivity = activity;
        mTextFragment = mActivity.mTextFragment;
        mCallback = mTextFragment;
        int fontPosition = Utils.getSharedPref(mActivity).getInt(mActivity.getString(R.string.selected_font), 0);
        fontPath = mTextFragment.mListFont.get(fontPosition);
        mTypeface = Typeface.createFromFile(fontPath);
        paint = new Paint();
        textPaint = new TextPaint();
        textPaint.setTextSize(size);
        setText(text);
        textHolder = new TextHolder();
        anchorPoint = mTextFragment.getLayoutImagePosition();

        borderTopLeft = new float[2];
        borderBottomRight = new float[2];
        borderBottomLeft = new float[2];
        borderTopRight = new float[2];
        centerPoint = new float[2];
        topRightPoint = new float[2];
        bottomLeftPoint = new float[2];
        topLeftPoint = new float[2];
        bottomRightPoint = new float[2];

        rotateBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_rotate);
        scaleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_scale);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setFullLayout();
        setOnTouchListener(onTouchListener);
        setOnClickListener(onClickListener);
        drawBorder(true);
        mColor = Color.RED;
        mBackgroundColor = Color.TRANSPARENT;
    }

    public void updateTextHolder(float layoutScale){
        float textCorrection = 20;
        String textFile = Utils.getTempFolder()+"/"+System.currentTimeMillis()+".txt";
        Utils.writeToFile(new File(textFile), text);
        textHolder.textPath = textFile;
        textHolder.fontPath = this.fontPath;
        textHolder.size = this.sizeScale*layoutScale;
        textHolder.fontColor = convertToHexColor(this.mColor);
        textHolder.boxColor = convertToHexColor(this.mBackgroundColor);
        textHolder.x = this.xExport*layoutScale - textCorrection;
        textHolder.y = this.yExport*layoutScale - textCorrection;
        textHolder.rotate = (float) (-this.rotation* Math.PI/180);
        textHolder.width = (int) ((this.widthScale+FloatText.PADDING*2)*layoutScale);
        textHolder.height = (int) ((this.heightScale+FloatText.PADDING*2)*layoutScale);
        textHolder.padding = FloatText.PADDING * layoutScale;
    }

    public String convertToHexColor(int color) {
        String resultColor = "";
        String s = String.format("%08X", (0xFFFFFFFF & color));
        resultColor += s.substring(2) + "@0x" + s.substring(0, 2);
        return resultColor;
    }

    public void setTextBgrColor(int color){
        mBackgroundColor = color;
        invalidate();
    }

    public void setTextColor(int color){
        mColor = color;
        invalidate();
    }

    public void setText(String text){
        this.text = text;
        resetLayout();
    }

    public void setStyle(int style){
        mStyle = style;
        resetLayout();
    }

    public void setFont(String fontPath, int fontId){
        this.fontPath = fontPath;
        this.fontId = fontId;
        mTypeface = Typeface.createFromFile(fontPath);
        resetLayout();
    }

    private void updateTextDimesions(){
        String[] lines = text.split("\n");
        int lineCount = lines.length;

        float maxWidth = 0;
        for (String line: lines) {
            float lineWidth = textPaint.measureText(line);
            if (maxWidth < lineWidth) {
                maxWidth = lineWidth;
            }
        }
        width = (int) maxWidth;

        int lineSpace = 15;
        bound = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bound);
        height = bound.height()*lineCount + lineSpace*(lineCount-1);
    }

    public void resetLayout(){
        textPaint.setTypeface(Typeface.create(mTypeface, mStyle));
        updateTextDimesions();

        rectBorder = new Rect(-PADDING, -PADDING, width+PADDING, height+PADDING);

        rectBackground = new Rect(-PADDING, -PADDING, width+PADDING, height+PADDING);
        widthScale = width*scaleValue;
        heightScale = height*scaleValue;

        initBorderTopLeft = new Point(-PADDING, -PADDING);
        initCenterPoint = new Point(width/2, height/2);
        initBorderBottomRight = new Point(width+PADDING, height+PADDING);
        initBorderBottomLeft = new Point(-PADDING, height+PADDING);
        initBorderTopRight = new Point(width+PADDING, -PADDING);
        initBottomLeftPoint = new Point(0, height);
        initTopRightPoint = new Point(width, 0);
        initTopLeftPoint = new Point(0, 0);
        initBottomRightPoint = new Point(width, height);
        invalidate();
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

    public interface OnFloatTextTouchListener {
        void onTouch(float x, float y);
        void onSelected(FloatText floatText);
    }

    private static int getQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }
    public void scaleText(float moveX, float moveY){
        if (Math.abs(borderBottomRight[0]-centerPoint[0])>100){
            if (borderBottomRight[0] >= centerPoint[0]) {
                widthScale += moveX;
            } else {
                widthScale -= moveX;
            }
            scaleValue = widthScale/width;
            heightScale = scaleValue*height;
        } else {
            if (borderBottomRight[1] >= centerPoint[1]){
                heightScale += moveY;
            } else {
                heightScale -= moveY;
            }
            scaleValue = heightScale/height;
            widthScale = scaleValue*width;
        }
        sizeScale = size*scaleValue;
        invalidate();
    }

    public void moveText(float moveX, float moveY) {
        x += moveX;
        y += moveY;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        initBorderPoints();

        Matrix matrix = new Matrix();
        matrix.postTranslate(x, y);

        matrix.mapPoints(centerPoint);

        matrix.postScale(scaleValue, scaleValue, centerPoint[0], centerPoint[1]);
        matrix.postRotate(-rotation, centerPoint[0], centerPoint[1]);

        getBorderPointsCoord(matrix);
        getLayoutLimit();

        // befor N canvas apply matrix from leftside of screen
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            matrix.postTranslate(anchorPoint[0], anchorPoint[1]);
            Rect rectBound = canvas.getClipBounds();
            canvas.clipRect(rectBound.left, rectBound.top,
                    rectBound.right + anchorPoint[0], rectBound.bottom + anchorPoint[1], Region.Op.REPLACE);
        }

        canvas.setMatrix(matrix);

        textPaint.setColor(mColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mBackgroundColor);
        canvas.drawRect(rectBackground, paint);
        StaticLayout textLayout = new StaticLayout(text, textPaint,canvas.getWidth(),
                Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false);
        textLayout.draw(canvas);

        if (!drawBorder) {
            return;
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.MAGENTA);
        paint.setStrokeWidth(3);
        paint.setPathEffect(new DashPathEffect(new float[] {8,6}, 0));
        canvas.drawRect(rectBorder, paint);
        canvas.restore();

        canvas.drawBitmap(rotateBitmap, borderTopLeft[0]-ROTATE_CONSTANT, borderTopLeft[1]-ROTATE_CONSTANT, paint);
        canvas.drawBitmap(scaleBitmap, borderBottomRight[0]-ROTATE_CONSTANT, borderBottomRight[1]-ROTATE_CONSTANT, paint);
    }

    private void getLayoutLimit(){
        // text x, y
        xExport = Math.min(Math.min(Math.min(topLeftPoint[0], bottomRightPoint[0]), bottomLeftPoint[0]), topRightPoint[0]);
        yExport = Math.min(Math.min(Math.min(topLeftPoint[1], bottomRightPoint[1]), bottomLeftPoint[1]), topRightPoint[1]);
        // border x, y
        xMax = Math.max(Math.max(Math.max(borderTopRight[0], borderTopLeft[0]), borderBottomLeft[0]), borderBottomRight[0]);
        yMax = Math.max(Math.max(Math.max(borderTopRight[1], borderTopLeft[1]), borderBottomLeft[1]), borderBottomRight[1]);
        xMin = Math.min(Math.min(Math.min(borderTopRight[0], borderTopLeft[0]), borderBottomLeft[0]), borderBottomRight[0]);
        yMin = Math.min(Math.min(Math.min(borderTopRight[1], borderTopLeft[1]), borderBottomLeft[1]), borderBottomRight[1]);
        widthMax = (int)(xMax - xMin);
        heightMax = (int) (yMax - yMin);
    }

    private void getBorderPointsCoord(Matrix matrix){
        matrix.mapPoints(borderBottomRight);
        matrix.mapPoints(borderTopLeft);
        matrix.mapPoints(borderBottomLeft);
        matrix.mapPoints(borderTopRight);

        matrix.mapPoints(bottomLeftPoint);
        matrix.mapPoints(topRightPoint);
        matrix.mapPoints(topLeftPoint);
        matrix.mapPoints(bottomRightPoint);
    }

    private void initBorderPoints(){
        borderTopLeft[0] = initBorderTopLeft.x;
        borderTopLeft[1] = initBorderTopLeft.y;
        borderBottomRight[0] = initBorderBottomRight.x;
        borderBottomRight[1] = initBorderBottomRight.y;
        borderTopRight[0] = initBorderTopRight.x;
        borderTopRight[1] = initBorderTopRight.y;
        borderBottomLeft[0] = initBorderBottomLeft.x;
        borderBottomLeft[1] = initBorderBottomLeft.y;

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
                    float epsMove = Math.max(widthScale/2, heightScale/2);
                    if (oldX < borderBottomRight[0]+eps && oldX > borderBottomRight[0]-eps && oldY < borderBottomRight[1]+eps && oldY > borderBottomRight[1]-eps){
                        touch = 1;
                    } else if (oldX < borderTopLeft[0]+eps && oldX > borderTopLeft[0]-eps && oldY < borderTopLeft[1]+eps && oldY > borderTopLeft[1]-eps){
                        touch = 3;
                    } else if (oldX < centerPoint[0]+epsMove && oldX > centerPoint[0]-epsMove && oldY < centerPoint[1]+epsMove && oldY > centerPoint[1]-epsMove) {
                        touch = 2;
                    }  else {
                        touch = 0;
                    }
                    log("touch = " + touch);
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
                        scaleText(moveX, moveY);
                    }
                    if (touch == 2) {
                        moveText(moveX, moveY);
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
                            drawBorder(false);
                            mActivity.setBtnDeleteTextVisible(false);
                            mCallback.onTouch(oldX, oldY);
                        }
                    }
                    break;
            }
            return true;
        }
    };


    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
        if (drawBorder) {
            drawBorder(false);
            mActivity.setBtnDeleteTextVisible(false);
            log("onClickListener false");
        } else {
            drawBorder(true);
            log("onClickListener true");
            mCallback.onSelected(FloatText.this);
            mActivity.setBtnDeleteTextVisible(true);
        }
        invalidate();
        }
    };
    private void log(String msg){
        Log.e("Log for FloatImage",msg);
    }
}
