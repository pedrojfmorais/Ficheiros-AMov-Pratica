package pt.isec.ans.sketches

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

const val TAG_DAREA = "DrawingAreaEvent"

data class Line(val path: Path, val color : Int)

class DrawingArea @JvmOverloads constructor(
    context      : Context,
    attrs        : AttributeSet? = null,
    defStyleAttr : Int = 0,
    defStyleRes  : Int = 0
) : View(context,attrs,defStyleAttr,defStyleRes),GestureDetector.OnGestureListener {

    var backColor = Color.WHITE
    constructor(context: Context, backColor : Int) : this(context) {
        this.backColor = backColor
        setBackgroundColor(backColor)
    }

    var imageFile : String? = null
    constructor(context: Context, imageFile: String) : this(context) {
        this.imageFile = imageFile
        setPic(this,imageFile)
    }

    private val paint = Paint(Paint.DITHER_FLAG or Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 4.0f
        style = Paint.Style.FILL_AND_STROKE
    }

    /* 1ª fase
    // Depois de incluir o GestureDetector incluir um único path
    // com pontos+linhas inseridas no OnDown+onScroll
    var path = Path()
    */

    /* 2ª fase */
    var lineColor: Int = Color.BLACK
        set(value) {
            field = value
            drawing.add(Line(Path(),value))
        }
    private val drawing : ArrayList<Line> = arrayListOf(Line(Path(),lineColor))
    private val path : Path
        get() = drawing.last().path

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // primeira versão apenas com uma linha fixa
        //canvas?.drawLine(50f,50f,250f,150f,paint)

        // 1ª fase
        //canvas?.drawPath(path,paint)

        // 2ª fase
        for ( line in drawing ) {
            paint.color = line.color
            canvas?.drawPath(line.path,paint)
            // alternativa: canvas?.drawPath(line.path, paint.apply { color = line.color })
        }
    }

    /* Gesture Detector */

    private val gestureDetector : GestureDetector by lazy {
        GestureDetector(context, this)
    }

   override fun onTouchEvent(event: MotionEvent?): Boolean {
       if (gestureDetector.onTouchEvent(event!!))
           return true
       return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onDown(e: MotionEvent): Boolean {
        Log.i(TAG_DAREA, "onDown: ")
        path.moveTo(e.x,e.y)
        invalidate()
        return true
    }

    override fun onShowPress(e: MotionEvent) {
        Log.i(TAG_DAREA, "onShowPress: ")
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        Log.i(TAG_DAREA, "onSingleTapUp: ")
        return false
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        Log.i(TAG_DAREA, "onScroll: ")
        //return false

        path.lineTo(e2.x,e2.y)
        path.moveTo(e2.x,e2.y)

        invalidate()
        return true
    }

    override fun onLongPress(e: MotionEvent) {
        Log.i(TAG_DAREA, "onLongPress: ")
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        Log.i(TAG_DAREA, "onFling: ")
        return false
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageFile?.let { setPic(this, it) }
        //invalidate()
    }
}