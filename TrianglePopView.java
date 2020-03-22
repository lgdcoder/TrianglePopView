package com.example.demo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;

/**
 * @Author: lgdcoder
 * @Date: 2020/3/4
 * @Description: 带三角形的泡泡窗口
 */
public class TrianglePopView extends FrameLayout {

    private Paint paint;
    private Path path;

    private RectF rect, connerRect;

    private float triangleWidth;
    private int corner;

    private Rect sourcePaddingRect; // 记录原始的边距

    private Direction direction = Direction.TOP;

    private PointF point1, point2, point3; // 三角形的三个点

    private int color = Color.parseColor("#2683F5");

    private float strokeWidth = 0;
    private int strokeColor = Color.GRAY;

    private float offset; // 三角形的偏移量，正数为坐标轴的正方向偏移，负数为坐标轴的负方向偏移

    public TrianglePopView(Context context) {
        this(context, null);
    }

    public TrianglePopView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrianglePopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        loadDefaultSetting();
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.TrianglePop);
            this.direction = Direction.getDirection(array.getInteger(R.styleable.TrianglePop_direction, Direction.TOP.value));
            float popCorner = array.getDimension(R.styleable.TrianglePop_pop_corner, 0);
            if (popCorner > 0)
                this.corner = (int) popCorner;
            float triangleWidth = array.getDimension(R.styleable.TrianglePop_triangle_width, 0);
            if (triangleWidth > 0)
                this.triangleWidth = triangleWidth;
            int background = array.getColor(R.styleable.TrianglePop_solid_color, 0);
            if (background != 0)
                this.color = background;
            float strokeWidth = array.getDimension(R.styleable.TrianglePop_stroke_width, 0);
            if (strokeWidth > 0)
                this.strokeWidth = strokeWidth;
            float offset = array.getDimension(R.styleable.TrianglePop_triangle_offset, 0);
            if (offset != 0)
                this.offset = offset;
            int strokeColor = array.getColor(R.styleable.TrianglePop_stroke_color, 0);
            if (strokeColor != 0)
                this.strokeColor = strokeColor;
            array.recycle();
        }
        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        path = new Path();

        rect = new RectF();
        connerRect = new RectF();

        point1 = new PointF();
        point2 = new PointF();
        point3 = new PointF();

        // 默认不执行onDraw，需要取消flag，也可以通过设置背景执行onDraw
        setWillNotDraw(false);
        //关闭硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        sourcePaddingRect = new Rect(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom());

        rebuild();
    }

    /**
     * 加载初始化配置
     */
    private void loadDefaultSetting() {
        triangleWidth = dp2px(12);
        corner = (int) dp2px(4);
    }

    private float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rebuild();
    }

    private void rebuild() {
        buildRectPosition();
        setCusPadding();
        invalidate();
    }


    /**
     * 根据原始Padding和边框宽度计算实际Padding
     */
    private void setCusPadding() {
        int extraSpace = (int) strokeWidth;
        Rect t = new Rect(sourcePaddingRect);
        if (extraSpace > 0)
            t.set(t.left + extraSpace, t.top + extraSpace, t.right + extraSpace, t.bottom + extraSpace);
        switch (direction) {
            case LEFT:
                setPadding((int) (t.left + triangleWidth / 2), t.top, t.right, t.bottom);
                break;
            case RIGHT:
                setPadding(t.left, t.top, (int) (t.right + triangleWidth / 2), t.bottom);
                break;
            case TOP:
                setPadding(t.left, (int) (triangleWidth / 2 + t.top), t.right, t.bottom);
                break;
            case BOTTOM:
                setPadding(t.left, t.top, t.right, (int) (t.bottom + triangleWidth / 2));
                break;
        }
    }

    /**
     * 根据三角形方向，计算三角形三个点的实际位置
     */
    private void calculateTrianglePoint() {
        int centerX, centerY;
        switch (direction) {
            case LEFT:
                centerY = getHeight() / 2;
                point1.set(rect.left, centerY + triangleWidth / 2);
                point2.set(strokeWidth / 2, centerY);
                point3.set(rect.left, centerY - triangleWidth / 2);
                break;
            case RIGHT:
                centerY = getHeight() / 2;
                point1.set(rect.right, centerY - triangleWidth / 2);
                point2.set(getWidth() - strokeWidth / 2, centerY);
                point3.set(rect.right, centerY + triangleWidth / 2);
                break;
            case TOP:
                centerX = getWidth() / 2;
                point1.set(centerX - triangleWidth / 2, rect.top);
                point2.set(centerX, strokeWidth / 2);
                point3.set(centerX + triangleWidth / 2, rect.top);
                break;
            case BOTTOM:
                centerX = getWidth() / 2;
                point1.set(centerX + triangleWidth / 2, rect.bottom);
                point2.set(centerX, getHeight() - strokeWidth / 2);
                point3.set(centerX - triangleWidth / 2, rect.bottom);
                break;
        }
        calculateTriangleOffset();
    }

    /**
     * 计算三角形的偏移量
     */
    private void calculateTriangleOffset() {
        switch (direction) {
            case LEFT:
            case RIGHT:
                point1.set(point1.x, point1.y + offset);
                point2.set(point2.x, point2.y + offset);
                point3.set(point3.x, point3.y + offset);
                break;
            case TOP:
            case BOTTOM:
                point1.set(point1.x + offset, point1.y);
                point2.set(point2.x + offset, point2.y);
                point3.set(point3.x + offset, point3.y);
                break;
        }
    }

    /**
     * 根据三角形方向，构建边框
     */
    private void buildRectPosition() {
        switch (direction) {
            case LEFT:
                rect.left = triangleWidth / 2;
                rect.top = 0;
                rect.right = getWidth();
                rect.bottom = getHeight();
                break;
            case RIGHT:
                rect.left = 0;
                rect.top = 0;
                rect.right = getWidth() - triangleWidth / 2;
                rect.bottom = getHeight();
                break;
            case TOP:
                rect.left = 0;
                rect.top = triangleWidth / 2;
                rect.right = getWidth();
                rect.bottom = getHeight();
                break;
            case BOTTOM:
                rect.left = 0;
                rect.top = 0;
                rect.right = getWidth();
                rect.bottom = getHeight() - triangleWidth / 2;
                break;
        }

        if (strokeWidth > 0) {
            rect.set(rect.left + strokeWidth, rect.top + strokeWidth, rect.right - strokeWidth, rect.bottom - strokeWidth);
//            rectInner.set(rect.left + strokeWidth, rect.top + strokeWidth, rect.right - strokeWidth, rect.bottom - strokeWidth);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calculateTrianglePoint();
        arcToCorners();

        drawFill(canvas);
        if (strokeWidth > 0)
            drawStroke(canvas);
    }

    /**
     * 绘制边框部分
     */
    private void drawStroke(Canvas canvas) {
        paint.setColor(strokeColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(strokeWidth);

        canvas.drawPath(path, paint);
    }

    /**
     * 绘制填充部分
     */
    private void drawFill(Canvas canvas) {
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);//抗锯齿
        paint.setDither(true);//抖动
        paint.setStrokeWidth(strokeWidth);

        canvas.drawPath(path, paint);
    }

    private void arcToCorners() {
        // 箭头路径
        path.moveTo(point1.x, point1.y);
        path.lineTo(point2.x, point2.y);
        path.lineTo(point3.x, point3.y);
        switch (direction) {
            case LEFT:
                arcToLeftTopCorner();
                arcToRightTopCorner();
                arcToRightBottomCorner();
                arcToLeftBottomCorner();
                break;
            case RIGHT:
                arcToRightBottomCorner();
                arcToLeftBottomCorner();
                arcToLeftTopCorner();
                arcToRightTopCorner();
                break;
            case TOP:
                arcToRightTopCorner();
                arcToRightBottomCorner();
                arcToLeftBottomCorner();
                arcToLeftTopCorner();
                break;
            case BOTTOM:
                arcToLeftBottomCorner();
                arcToLeftTopCorner();
                arcToRightTopCorner();
                arcToRightBottomCorner();
                break;
        }

        path.close();
    }

    /**
     * 绘制左上方圆角
     */
    private void arcToLeftTopCorner() {
        path.lineTo(rect.left, rect.top + corner);
        connerRect.set(rect.left, rect.top, rect.left + corner, rect.top + corner);
        path.arcTo(connerRect, 180, 90);
    }

    /**
     * 绘制左下方圆角
     */
    private void arcToLeftBottomCorner() {
        path.lineTo(rect.left + corner, rect.bottom);
        connerRect.set(rect.left, rect.bottom - corner, rect.left + corner, rect.bottom);
        path.arcTo(connerRect, 90, 90);
    }

    /**
     * 绘制右上方圆角
     */
    private void arcToRightTopCorner() {
        path.lineTo(rect.right - corner, rect.top);
        connerRect.set(rect.right - corner, rect.top, rect.right, rect.top + corner);
        path.arcTo(connerRect, 270, 90);
    }

    /**
     * 绘制右下方圆角
     */
    private void arcToRightBottomCorner() {
        path.lineTo(rect.right, rect.bottom - corner);
        connerRect.set(rect.right - corner, rect.bottom - corner, rect.right, rect.bottom);
        path.arcTo(connerRect, 0, 90);
    }

    /**
     * 通过箭头方向计算顺时针、逆时针
     *
     * @return
     */
    private Path.Direction getPathDirection() {
        switch (direction) {
            case LEFT:
            case BOTTOM:
                return Path.Direction.CCW;
            default:
                return Path.Direction.CW;
        }
    }

    public enum Direction {
        LEFT(1), RIGHT(2), TOP(3), BOTTOM(4);

        int value;

        Direction(int i) {
            this.value = i;
        }

        private static Direction getDirection(int value) {
            for (Direction direction : Direction.values()) {
                if (value == direction.value)
                    return direction;
            }
            return TOP;
        }
    }

    /**
     * 设置填充的颜色
     */
    public TrianglePopView setSolidColor(@ColorInt int color) {
        this.color = color;
        invalidate();
        return this;
    }

    /**
     * 设置三角形的方向
     */
    public TrianglePopView setDirection(Direction direction) {
        this.direction = direction;
        invalidate();
        return this;
    }

    /**
     * 设置泡泡窗口的圆角
     *
     * @param corner 单位 DP
     */
    public TrianglePopView setPopCorner(int corner) {
        this.corner = (int) dp2px(corner);
        invalidate();
        return this;
    }

    /**
     * 设置边框的颜色
     */
    public TrianglePopView setStrokeColor(@ColorInt int color) {
        this.strokeColor = color;
        invalidate();
        return this;
    }

    /**
     * 设置边框的宽度
     */
    public TrianglePopView setStrokeWidth(int width) {
        this.strokeWidth = (int) dp2px(width);
        invalidate();
        return this;
    }

    /**
     * 设置 三角形的偏移量，正数为坐标轴的正方向偏移，负数为坐标轴的负方向偏移
     *
     * @param offset 单位 DP
     */
    public TrianglePopView setTriangleOffset(int offset) {
        this.offset = (int) dp2px(offset);
        invalidate();
        return this;
    }

    /**
     * 设置三角形的长度
     *
     * @param width 单位 DP
     */
    public TrianglePopView setTriangleWidth(int width) {
        this.triangleWidth = (int) dp2px(width);
        invalidate();
        return this;
    }
}
