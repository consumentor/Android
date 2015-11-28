package consumentor.shopgun.aidroid.customview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;


import consumentor.shopgun.aidroid.model.AdviceInfo;
import consumentor.shopgun.aidroid.view.R;

import java.util.List;

/**
 * Created by Björn on 2013-09-24.
 */
public class CircleDiagram extends View {

    private Context context;

    private int greens;
    private int yellows;
    private int reds;
    private float total;

    private int mCircleDiagramSize;
    private int mRadius = 105;
    private int mDrawPosX; // This is set in onMeasure()
    private int mDrawPosY = mRadius + 2; // +2 to not "cut" the shadow
    private boolean sectorTextEnabled = true;

    private Paint paint;
    private RectF oval;

    private int greenColor = getResources().getColor(R.color.shopgun_green);//Color.rgb(129, 186, 65);
    private int yellowColor = getResources().getColor(R.color.shopgun_yellow_darker);//Color.rgb(255, 204, 0);
    private int redColor = getResources().getColor(R.color.shopgun_red);//Color.rgb(180, 9, 56);
    private int transparentColor = getResources().getColor(R.color.shopgun_gray_lighter);

    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private int mTotalTransparent;
    private int mTotalGreens = 0;
    private int mTotalReds = 0;
    private int mTotalYellows = 0;
    private int mShadowPadding;

    /**
     * Creates the circle diagram with the specified numbers of colours.
     *
     * @param context      The context.
     * @param attributeSet Attribute set.
     */
    public CircleDiagram(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        // Without setting the layer type, the shadow is cut
        // http://stackoverflow.com/questions/17410195/setshadowlayer-android-api-differences
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // Get screen size independent size from dimens.xml
        this.setDiagramRadius(getResources().getDimensionPixelSize(R.dimen.circleDiagramSize));
        Resources r = getResources();
        mShadowPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());
    }

    /**
     * Initiates the circle diagram by setting the color proportions.
     *
     * @param greens  The number of greens.
     * @param yellows The number of yellows.
     * @param reds    The number of reds.
     */
    public void init(int greens, int yellows, int reds) {
        this.greens = greens;
        this.yellows = yellows;
        this.reds = reds;
        this.total = Math.abs(greens) + Math.abs(yellows) + Math.abs(reds);
        this.paint = new Paint();
        //this.setSectorTextEnabled(false);
        invalidate();
    }

    public void init(List<? extends AdviceInfo> advices) {
        mTotalGreens = 0;
        mTotalReds = 0;
        mTotalYellows = 0;
        int yellowScore = 0;
        int greenScore = 0;
        int redScore = 0;
        mTotalTransparent = 0;
        for (AdviceInfo info : advices) {
            switch (info.semaphoreValue) {
                case 1: // Green
                    mTotalGreens++;
                    break;
                case 0: // Yellow
                    mTotalYellows++;
                    break;
                case -1: // Red
                    mTotalReds++;
                    break;
                case -2: // Transparent
                    mTotalTransparent++;
                    break;
                default:
                    break;
            }
        }

        for (AdviceInfo info : advices) {
            switch (info.semaphoreValue) {
                case 1: // Green
                    if (info.adviceScore > 0){
                        greenScore += info.adviceScore;
                    }
                    else {
                        yellowScore += mTotalYellows * Math.abs(info.adviceScore);
                        redScore += mTotalReds * Math.abs(info.adviceScore);
                    }
                    break;
                case 0: // Yellow
                    if (info.adviceScore > 0){
                        yellowScore += info.adviceScore;
                    }
                    else {
                        greenScore += mTotalGreens * Math.abs(info.adviceScore);
                        redScore += mTotalReds * Math.abs(info.adviceScore);
                    }
                    break;
                case -1: // Red
                    if (info.adviceScore > 0){
                        redScore += info.adviceScore;
                    }
                    else {
                        greenScore += mTotalGreens * Math.abs(info.adviceScore);
                        yellowScore += mTotalYellows * Math.abs(info.adviceScore);
                    }
                    break;
                case -2: // Transparent
                    break;
                default:
                    break;
            }
        }
        //init(mTotalGreens, mTotalYellows, mTotalReds);
        init(greenScore + mTotalGreens, yellowScore + mTotalYellows, redScore + mTotalReds);
    }

    public boolean hasSemaphores() {
        return greens > 0 || yellows > 0 || reds > 0;
    }

    /**
     * Sets the mRadius of the diagram
     *
     * @param radius The mRadius
     */
    public void setDiagramRadius(int radius) {
        mRadius = radius - 2* mShadowPadding;
        mDrawPosX = mRadius + mShadowPadding +getPaddingLeft(); // middle of screen
        mDrawPosY = mRadius + mShadowPadding +getPaddingTop();
        mMeasuredWidth =(2 * (mRadius + mShadowPadding)) + getPaddingLeft() + getPaddingRight();
        mMeasuredHeight =(2 * (mRadius + mShadowPadding)) + getPaddingTop() + getPaddingBottom();
    }

    /**
     * Enables or disables the number shown in the colored sectors.
     *
     * @param b
     */
    public void setSectorTextEnabled(boolean b) {
        sectorTextEnabled = b;
    }

    protected void onDraw(Canvas canvas) {

        if (paint == null){
            this.paint = new Paint();
        }
        paint.setAntiAlias(true);

        // Draws the shadow around the diagram
        paint.setShadowLayer(4, 0, 1, Color.DKGRAY);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mDrawPosX, mDrawPosY, mRadius - 1, paint);

        paint.setShader(null);
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(255);
        paint.setShadowLayer(0, 1, 1, Color.DKGRAY);

        if (total < 1 ) {
            float start = 0 - 90; // start at the top
            float sweep = 360;
            boolean drawInCenter = true;
            if(mTotalTransparent == 0){
                paint.setColor(Color.WHITE);
                canvas.drawArc(oval, start, sweep, true, paint);
                if (sectorTextEnabled) {
                    drawSectorText(canvas, 0, start, start + sweep, drawInCenter);
                }
            }else{
                paint.setColor(transparentColor);
                canvas.drawArc(oval, start, sweep, true, paint);
                if (sectorTextEnabled) {
                    drawSectorText(canvas, mTotalTransparent, start, start + sweep, drawInCenter);
                }
            }
            return;
        }

        // Draws the colored sectors of the circle diagram
        paint.setColor(greenColor);
        float start = 0 - 90; // start at the top
        float sweep = greens / total * 360;
        canvas.drawArc(oval, start, sweep, true, paint);
        boolean drawInCenter = (mTotalGreens > 0 && (mTotalYellows + mTotalReds == 0)); // if green only color
        if (mTotalGreens > 0 && sectorTextEnabled) {
            drawSectorText(canvas, mTotalGreens, start, start + sweep, drawInCenter);
        }
        paint.setColor(yellowColor);
        start += sweep;
        sweep = yellows / total * 360;
        canvas.drawArc(oval, start, sweep, true, paint);
        drawInCenter = (mTotalYellows > 0 && (mTotalGreens + mTotalReds == 0)); // if yellow only color
        if (mTotalYellows > 0 && sectorTextEnabled) {
            drawSectorText(canvas, mTotalYellows, start, start + sweep, drawInCenter);
        }
        paint.setColor(redColor);
        start += sweep;
        sweep = reds / total * 360;
        canvas.drawArc(oval, start, sweep, true, paint);
        drawInCenter = (mTotalReds > 0 && (mTotalGreens + mTotalYellows == 0)); // if red only color
        if (mTotalReds > 0 && sectorTextEnabled) {
            drawSectorText(canvas, mTotalReds, start, start + sweep, drawInCenter);
        }

        /* Used for debugging. Draws the borders of the canvas
        paint.setColor(Color.BLACK);
        canvas.drawLine(0, 0, mMeasuredWidth, mMeasuredHeight, paint);
        canvas.drawLine(0, mMeasuredHeight, mMeasuredWidth, 0, paint);
        canvas.drawLine(0, 0, 0, mMeasuredHeight, paint);
        canvas.drawLine(0, mMeasuredHeight, mMeasuredWidth, mMeasuredHeight, paint);
        canvas.drawLine(mMeasuredWidth, mMeasuredHeight, mMeasuredWidth, 0, paint);
        canvas.drawLine(0, 0, mMeasuredWidth, 0, paint);
        canvas.drawLine(0, mMeasuredHeight -1, mMeasuredWidth, mMeasuredHeight -1, paint);
        */
    }

    private void drawSectorText(Canvas canvas, int numberToDraw, float startSweep, float endSweep, boolean drawInCenter) {
        String numberText = "" + numberToDraw;
        int textSize = getResources().getDimensionPixelSize(R.dimen.circleDiagramFontSize);
        double midDegrees = (startSweep + endSweep) / 2;
        double midRadians = midDegrees * Math.PI / 180;

        int textPosX = mDrawPosX;
        int textPosY = mDrawPosY;
        if (!drawInCenter) {
            // If not drawing in center, then find the correct position
            textPosX = mDrawPosX + (int) ((Math.cos(midRadians) * mRadius * 0.6));
            textPosY = mDrawPosY + (int) ((Math.sin(midRadians) * mRadius * 0.6));
        }
        paint.setColor(Color.BLACK);
        paint.setTextSize(textSize);
        Rect rect = new Rect();
        paint.getTextBounds(numberText, 0, numberText.length(), rect);
        canvas.drawText(numberText, textPosX - rect.width(), textPosY + rect.height() / 2, paint);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // This is needed since onDraw() won't be called if any measured
        // dimension is 0

        int requiredW = MeasureSpec.getSize(widthMeasureSpec);
        int requiredH = MeasureSpec.getSize(heightMeasureSpec);

        int w = requiredW - getPaddingLeft() - getPaddingRight();

        int h = requiredH - getPaddingBottom() - getPaddingTop();

        int radius = 0;
        if (w == 0 || h == 0){
            radius = Math.max(w, h)/2;
        }
        else{
            radius = Math.min(w, h)/2;
        }
        setDiagramRadius(radius);

        setMeasuredDimension(mMeasuredWidth, mMeasuredHeight);

        oval = new RectF(mDrawPosX - mRadius, mDrawPosY - mRadius, mDrawPosX
                + mRadius, mDrawPosY + mRadius);
    }
}