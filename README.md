# **[TrianglePopView](https://github.com/lgdcoder/TrianglePopView)**
带三角形的泡泡控件，内部添加子控件，可以自定义三角形4个方向、圆角角度等属性

## 截图预览

![](https://github.com/lgdcoder/TrianglePopView/blob/master/images/1.png)

![](https://github.com/lgdcoder/TrianglePopView/blob/master/images/2.png)

## 属性说明

```java
<TrianglePopView
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

| 属性名称        | 说明           | 备注                                                         |
| :-------------- | -------------- | ------------------------------------------------------------ |
| solid_color     | 填充颜色       | 默认：#2683F5                                                |
| triangle_width  | 三角形宽度     | 默认：12dp                                                   |
| triangle_offset | 三角形的偏移量 | 默认为0，显示在中间<br />正数为坐标轴的正方向偏移<br />负数为坐标轴的负方向偏移 |
| pop_corner      | 圆角           | 默认：4dp                                                    |
| stroke_width    | 边框宽度       |                                                              |
| stroke_color    | 边框颜色       |                                                              |
| direction       | 三角形的方向   | 默认：显示在上面<br />left、right、top、bottom               |

```
<attr name="solid_color" format="color" />
<attr name="triangle_width" format="dimension" />
<attr name="triangle_offset" format="dimension" />
<attr name="pop_corner" format="dimension" />
<attr name="stroke_width" format="dimension" />
<attr name="stroke_color" format="color" />
<attr name="direction" format="integer">
    <enum name="left" value="1" />
    <enum name="right" value="2" />
    <enum name="top" value="3" />
    <enum name="bottom" value="4" />
</attr>
```