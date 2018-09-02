package space.schrws.sunchart.android

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * Created by Schrws on 2018-03-25.
 */
class LineChart : View {
    private val dataList = ArrayList<Float>()
    private val animList = ArrayList<Float>()
    private val textList = ArrayList<String>()
    private var paint = Paint()
    private var outerCirPaint = Paint()
    private var innerCirPaint = Paint()
    private val dateTextPaint = Paint()
    private val valueTextPaint = Paint()

    private var max = 0f
    private var min = 0f
    private var sub = 0f
    private var frm = 0f

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val animator = object : Runnable {
        override fun run() {
            var needNewFrame = false
            for (i in dataList.indices) {
                if (animList[i] < dataList[i]) {
                    animList[i] = animList[i] + frm
                    needNewFrame = true
                } else if (animList[i] > dataList[i]) {
                    animList[i] = animList[i] - frm
                    needNewFrame = true
                }
                if (Math.abs(dataList[i] - animList[i]) < frm) {
                    animList[i] = dataList[i]
                }
            }
            if (needNewFrame) {
                postDelayed(this, 20)
            }
            invalidate()
        }
    }

    init {
        paint.isAntiAlias = true
        outerCirPaint.isAntiAlias = true
        innerCirPaint.isAntiAlias = true
        valueTextPaint.isAntiAlias = true
        dateTextPaint.isAntiAlias = true
        paint.color = Color.parseColor("#2ba848")
        outerCirPaint.color = Color.parseColor("#2ba848")
        innerCirPaint.color = Color.parseColor("#ffffff")
        valueTextPaint.color = Color.parseColor("#2ba848")
        valueTextPaint.textSize = dip2px(10f)
        dateTextPaint.color = Color.parseColor("#aeaeae")
        dateTextPaint.textSize = dip2px(10f)
    }

    /**
     *
     * @param list The ArrayList of Integer with the range of [0-max].
     */
    fun setDataList(list: ArrayList<Float>) {
        dataList.clear()
        animList.clear()

        max = list.max()!!
        min = list.min()!!
        sub = max - min
        frm = sub / (2 * 30)


        for (dat in list) {
            dataList.add(max - dat)
            animList.add(sub / 2)
        }

        removeCallbacks(animator)
        post(animator)
    }

    fun setTextList(list: ArrayList<String>) {
        textList.clear()

        for (dat in list)
            textList.add(dat)
    }

    override fun onDraw(canvas: Canvas) {
        val marginTop = height * 0.35f
        val sideWidth = width * 47f / 330f
        val sideMargin = width * 24f / 330f
        val outerCir = width * 3f / 330f
        val innerCir = width * 2f / 330f

        val scale = if (sub > 0) height * 0.3f / sub else 1f

        if (animList.isNotEmpty()) {
            for (i in 0 until animList.size - 1)
                canvas.drawLine(sideWidth * i + sideMargin, marginTop + animList[i] * scale, sideWidth * (i + 1) + sideMargin, marginTop + animList[i + 1] * scale, paint)
            for (i in animList.indices) {
                canvas.drawCircle(sideWidth * i + sideMargin, marginTop + animList[i] * scale, outerCir, outerCirPaint)
                canvas.drawCircle(sideWidth * i + sideMargin, marginTop + animList[i] * scale, innerCir, innerCirPaint)
                canvas.drawText(String.format("%.1f kg", max - animList[i]),
                        sideWidth * i + sideMargin - valueTextPaint.measureText(String.format("%.1f kg", max - animList[i])) / 2,
                        marginTop + animList[i] * scale - dip2px(6f),
                        valueTextPaint)
            }
        }

        if (textList.isNotEmpty()) {
            for ((i, d) in textList.withIndex())
                canvas.drawText(d, sideWidth * i + sideMargin - dateTextPaint.measureText(d) / 2, height - 30f, dateTextPaint)
        }
    }

    private fun dip2px(dipValue: Float): Float = dipValue * resources.displayMetrics.density
}