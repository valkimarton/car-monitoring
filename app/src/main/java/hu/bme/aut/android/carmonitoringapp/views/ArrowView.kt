package hu.bme.aut.android.carmonitoringapp.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import hu.bme.aut.android.carmonitoringapp.R

class ArrowView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var linePaint: Paint = Paint()
    private var viewWidth: Float = resources.getDimension(R.dimen.arrow_view_width)
    private var viewHeight: Float = resources.getDimension(R.dimen.arrow_view_height)

    private var accX: Float = 0f
    private var accY: Float = 0f
    private var maxAcc: Float = 20f



    init {
        linePaint.color = Color.RED
        linePaint.strokeWidth = 8f
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centreX: Float = viewWidth/2
        val centreY: Float = viewHeight/2

        val absAcc: Float =  Math.sqrt((accX * accX + accY * accY).toDouble()).toFloat()

        if ( absAcc > maxAcc) {
            accX = accX * maxAcc/absAcc
            accY = accY * maxAcc/absAcc
        }

        var xCoord: Float = centreX - this.accX / maxAcc / 1.2f * this.viewWidth
        var yCoord: Float = centreY + this.accY / maxAcc / 1.2f * this.viewHeight

        canvas.drawLine(centreX, centreY, xCoord, yCoord, linePaint)

        Log.d("arrowView", "centre: ${centreX}, ${centreY}")
        Log.d("arrowView", "${this.x}, ${this.y}, ${this.width}, ${this.height}")
    }

    public fun setAccelerationAndUpdate(accX: Float, accY: Float){
        this.accX = accX
        this.accY = accY
        this.invalidate()
    }
}