package pro.apir.tko.presentation.platform.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.FloatRange
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Антон Сарматин
 * Date: 22.01.2020
 * Project: tko-android
 */
class PeekingLinearLayoutManager @JvmOverloads constructor(
    context: Context?,
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
    reverseLayout: Boolean = false,
    @FloatRange(from = 0.0, to = 1.0) private val ratio: Float = defaultRatio,
) : LinearLayoutManager(context, orientation, reverseLayout) {

//    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
//        context,
//        attrs,
//        defStyleAttr,
//        defStyleRes)

    override fun generateDefaultLayoutParams() =
        scaledLayoutParams(super.generateDefaultLayoutParams())

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?) =
        scaledLayoutParams(super.generateLayoutParams(lp))

    override fun generateLayoutParams(c: Context?, attrs: AttributeSet?) =
        scaledLayoutParams(super.generateLayoutParams(c, attrs))

    private fun scaledLayoutParams(layoutParams: RecyclerView.LayoutParams) =
        layoutParams.apply {
            when (orientation) {
                HORIZONTAL -> width = (horizontalSpace * ratio).toInt()
                VERTICAL -> height = (verticalSpace * ratio).toInt()
            }
        }

    private val horizontalSpace get() = width - paddingStart - paddingEnd

    private val verticalSpace get() = height - paddingTop - paddingBottom

    companion object {
        private val defaultRatio = 0.7f
    }
}