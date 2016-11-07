# EasyGestureUnlock
A light gesture unlock view for Android.
## ScreenShots
![EasyGestureUnlock](https://github.com/huzenan/EasyGestureUnlock/blob/master/screenshots/guv.gif)
## Usage
> layout

```xml
    <com.hzn.easygestureunlock.GestureUnlockView
        android:id="@+id/lock_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:guvCircleColor="#9c9c9c"
        app:guvCircleColorInside="#dadada"
        app:guvCircleLineWidth="3dp"
        app:guvColorFailed="#aac15656"
        app:guvColorSuccess="#aa60b17a"
        app:guvColumns="3"
        app:guvLineColor="#aadadada"
        app:guvLineWidth="20dp"
        app:guvLines="3"
        app:guvPadding="45dp"
        app:guvRadius="25dp"
        app:guvRadiusInside="10dp"
        app:guvText="PLEASE UNLOCK"
        app:guvTextColor="#dadada"
        app:guvTextFailed="FAILED!"
        app:guvTextPadding="85dp"
        app:guvTextSize="20sp"
        app:guvTextSuccess="SUCCESS!"/>
```
> Activity

```java
    unlockView.setOnUnlockListener(new GestureUnlockView.OnUnlockListener() {
        @Override
        public boolean onUnlockFinished(ArrayList<Integer> password) {
            boolean success = pswdList.equals(password);
            ...
            unlockView.reset();
            ...
            return success;
        }
    });
```
