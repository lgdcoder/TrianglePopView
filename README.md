# 
带三角形的泡泡窗口

![](https://github.com/lgdcoder/TrianglePopView/blob/master/images/1.jpg)

![](https://github.com/lgdcoder/TrianglePopView/blob/master/images/2.jpg)

```java
<TrianglePopView
    android:id="@+id/vTrianglePop"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:padding="10dp"
    app:direction="top"
    app:pop_corner="6dp"
    app:solid_color="#2683F5"
    app:stroke_color="#888"
    app:stroke_width="0dp"
    app:triangle_offset="-40dp"
    app:triangle_width="12dp">
```

```java
TrianglePopView vTrianglePop = v.findViewById(R.id.vTrianglePop);
vTrianglePop.setDirection(TrianglePopView.Direction.LEFT)
        .setPopCorner(10)
        .setStrokeWidth(2)
        .setSolidColor(Color.WHITE)
        .setTriangleOffset(-10)
        .setTriangleWidth(20)
        .setStrokeColor(Color.BLUE);
```