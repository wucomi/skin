package com.skin.library

import android.app.Activity
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import java.util.WeakHashMap

/**
 * Activity切面
 * 由于AppCompat库在super.onCreate中默认实现了Factory2，如果在调用super.onCreate之后重新设置，会引发异常。
 * 因此，必须在调用super.onCreate之前设置Factory2。
 * 然而，系统提供的ActivityLifecycleCallbacks中的onActivityCreated方法是在super.onCreate之后、setContentView之前执行的，
 * 无法满足需求。因此，需要使用AspectJ来实现这一功能.
 */
@Aspect
class ActivityAspect {
    private val mMap = WeakHashMap<Any, Any>()

    // 定义切点，匹配所有Activity子类的onCreate方法
    @Pointcut("execution(void android.app.Activity+.onCreate(android.os.Bundle))")
    fun activityOnCreate() {
    }

    // Android实现的Activity中定义的所有方法
    @Pointcut(
        "within(androidx.appcompat.app.AppCompatActivity)" +
                "||within(androidx.fragment.app.FragmentActivity)" +
                "||within(androidx.activity.ComponentActivity)" +
                "||within(androidx.core.app.ComponentActivity)" +
                "||within(android.support.v4.app.FragmentActivity)" +
                "||within(android.support.v7.app.AppCompatActivity)"
    )
    fun parentActivity() {
    }

    // 在onCreate方法执行前插入Factory2, 避免在父类Activity重复植入
    @Before("activityOnCreate()&&!parentActivity()")
    @Throws(Throwable::class)
    fun setFactory2(joinPoint: JoinPoint) {
        val target = joinPoint.target
        if (mMap[target] != null) {
            // 避免重复植入换肤逻辑造成性能损耗
            return
        }
        mMap[target] = true
        SkinManager.setFactory2(target as Activity)
    }
}