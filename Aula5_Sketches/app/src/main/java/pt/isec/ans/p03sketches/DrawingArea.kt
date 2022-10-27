package pt.isec.ans.p03sketches

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.OnGestureListener
import android.view.MotionEvent
import android.view.View

data class Line(val path : Path, val color: Int)

class DrawingArea @JvmOverloads constructor(
    context : Context,
    attrs : AttributeSet? = null,
    defStyleAttr : Int = 0,
    defStyleRes : Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes), OnGestureListener {

    companion object {
        private const val TAG = "DrawingArea"
    }

//    init {
//        setBackgroundColor(Color.BLUE)
//    }

    var backColor : Int = Color.WHITE

    constructor(context: Context, color : Int) : this(context){
        this.backColor = color
        setBackgroundColor(color)
    }

    val paint = Paint(
        Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG
    ).apply {
        color = Color.BLACK
        strokeWidth = 4.0f
        style = Paint.Style.FILL_AND_STROKE
    }

//    val path = Path()

    var lineColor = Color.BLACK
        set(value){
            field = value
            drawing.add(Line(Path(), value))
        }

    private val drawing : ArrayList<Line> = arrayListOf(
        Line(Path(), lineColor)
    )

    private val path : Path
        get() = drawing.last().path

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        for (line in drawing)
            canvas?.drawPath(line.path, paint.apply { color = line.color })
    }

    private val gestureDetector : GestureDetector by lazy {
        GestureDetector(context, this)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (gestureDetector.onTouchEvent(event))
            return true

        return super.onTouchEvent(event)
    }

    override fun onDown(p0: MotionEvent): Boolean {
        Log.i(TAG, "onDown: ")
        path.moveTo(p0.x, p0.y)
        invalidate()
        return true
    }

    override fun onShowPress(p0: MotionEvent?) {
        Log.i(TAG, "onShowPress: ")
    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        Log.i(TAG, "onSingleTapUp: ")
        return false
    }

    override fun onScroll(
        p0: MotionEvent,
        p1: MotionEvent,
        p2: Float,
        p3: Float
    ): Boolean {
        Log.i(TAG, "onScroll: ${p0.x} ${p1.y}")
        path.lineTo(p1.x, p1.y)
        path.moveTo(p1.x, p1.y)
        invalidate()
        return false
    }

    override fun onLongPress(p0: MotionEvent?) {
        Log.i(TAG, "onLongPress: ")
    }

    override fun onFling(
        p0: MotionEvent?,
        p1: MotionEvent?,
        p2: Float,
        p3: Float
    ): Boolean {
        Log.i(TAG, "onFling: ")
        return false
    }
}