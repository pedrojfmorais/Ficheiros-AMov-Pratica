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

data class Line(val path: Path,val color : Int)

class DrawingArea @JvmOverloads constructor(
    context : Context,
    attrs : AttributeSet? =  null,
    defStyleAttr : Int = 0,
    defStyleRes : Int = 0
) : View(context,attrs,defStyleAttr,defStyleRes), OnGestureListener {
    companion object {
        private const val TAG = "DrawingArea"
    }
//    init {
//        setBackgroundColor(Color.BLUE)
//    }
    var backColor : Int = Color.WHITE

    constructor(context: Context, color : Int) : this(context) {
        this.backColor = color
        setBackgroundColor(color)
    }

    var imageFile : String? = null
    constructor(context: Context, imageFile: String) : this(context){
        this.imageFile = imageFile
        setPic(this, imageFile)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageFile?.let { setPic(this, it) }
    }

    val paint = Paint(
        Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG
    ).apply {
        color = Color.BLACK
        strokeWidth = 4.0f
        style = Paint.Style.FILL_AND_STROKE
    }

    //val path = Path()

    var lineColor = Color.BLACK
        set(value) {
            field = value
            drawing.add(Line(Path(),value))
        }

    private val drawing : ArrayList<Line> = arrayListOf(
        Line(Path(),lineColor)
    )
    private val path : Path
        get() = drawing.last().path

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //canvas?.drawLine(50f,50f,150f,250f,paint)
        for(line in drawing) {
            //paint.color=line.color
            canvas?.drawPath(line.path, paint.apply { color=line.color })
        }
    }

    private val gestureDetector : GestureDetector by lazy {
        GestureDetector(context,this)
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (gestureDetector.onTouchEvent(event))
            return true
        return super.onTouchEvent(event)
    }

    override fun onDown(e: MotionEvent): Boolean {
        Log.i(TAG, "onDown: ")
        path.moveTo(e.x,e.y)
        invalidate()
        return true
    }

    override fun onShowPress(e: MotionEvent) {
        Log.i(TAG, "onShowPress: ")
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        Log.i(TAG, "onSingleTapUp: ")
        return false
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        Log.i(TAG, "onScroll: ${e1.x} ${e2.x}")
        path.lineTo(e2.x,e2.y)
        path.moveTo(e2.x,e2.y)
        invalidate()
        return true
    }

    override fun onLongPress(e: MotionEvent) {
        Log.i(TAG, "onLongPress: ")
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        Log.i(TAG, "onFling: ")
        return false
    }

}