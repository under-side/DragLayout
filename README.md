# DragLayout
运用V4包中的ViewDragHelper实现两个layout拖拽切换的功能。

#### 实现过程
1. 先根据需求画出该组件的静态图；
2. 运用v4包的ViewDragHelper封装的方法实现两个layout的拖拽移动处理；
3. 向两个layout添加拖拽过程的伴随动画效果处理；
  1. BackView：等比缩放动画、平移动画、透明度动画；
  2. FrontView：等比缩放动画；
  3. BackGround：亮度变化；
4. 自定义监听接口，将该组件信息透露出去，并与外界组件关联；
5. 根据拖拽的过程中状态变化，去回调接口中的方法；
6. 对触摸的一些优化：如，open状态时，frontView的ListView拦截滑动事件，不让其滑动；

### 难点
1. getViewHorizontalDragRange(View child)该方法的理解。默认返回0，当返回0时，，则表示水平方向不能移动的建议，Vertical也是一样；当子view可以获取焦点，但是无法移动时，重写该方法；
2. 利用view.set...方法去实现view的动画变换；利用该方法实现view的动画效果，必须是在一个当view变化时就调用的方法中设置，才能达到向Animation一样的动画效果。