package `in`.anandsingh.samay.views

import android.content.Context
import android.util.AttributeSet
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ContributionsGraphView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var data: Map<String, Int> = emptyMap()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun setData(data: Map<String, Int>) {
        this.data = data
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val calendar = Calendar.getInstance()
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val blockSize = width  // Adjust block size as needed
        val blockMargin = 2  // Adjust margin for smaller spacing between blocks
        val radius = blockSize / 4f  // Adjust radius for rounded blocks

        // Set calendar to the first day of the month
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        var y = 0f

        for (day in 1..daysInMonth) {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(calendar.time)
            val count = data[date] ?: 0

            paint.color = when {
                count > 0 -> Color.GREEN
                else -> Color.GRAY
            }

            // Draw the rounded block
            val rect = RectF(
                blockMargin.toFloat(),
                y + blockMargin,
                blockSize.toFloat() - blockMargin,
                y + blockSize - blockMargin
            )
            canvas.drawRoundRect(rect, radius, radius, paint)

            // Draw the day number
            paint.color = Color.BLACK
            paint.textSize = blockSize / 3f
            val textBounds = Rect()
            val dayStr = day.toString()
            paint.getTextBounds(dayStr, 0, dayStr.length, textBounds)
            val textWidth = paint.measureText(dayStr)
            val textHeight = textBounds.height()
            canvas.drawText(
                dayStr,
                blockSize / 2f - textWidth / 2,
                y + blockSize / 2f + textHeight / 2,
                paint
            )

            // Update the y coordinate for the next block
            y += blockSize

            // Increment the calendar day
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }
}
