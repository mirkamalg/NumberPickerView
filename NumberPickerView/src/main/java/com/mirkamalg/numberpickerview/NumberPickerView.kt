package com.mirkamalg.numberpickerview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.card.MaterialCardView

/**
 * Created by Mirkamal on 10 May 2021
 */
class NumberPickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        const val COLOR_BACKGROUND_DEFAULT = "#0A578BFE"
        const val COLOR_PLUS_MINUS_TEXT_DEFAULT = "#407BFE"
        const val COLOR_INDICATOR_TEXT_DEFAULT = "#001D5B"

        enum class SENSITIVITY {
            LOW, MEDIUM, HIGH
        }
    }

    private var mHandler: Handler? = null
    private var vibrator: Vibrator? = null
    private var mGestureDetector: GestureDetector? = null

    var plusButtonBackgroundColor = 0
    var minusButtonBackgroundColor = 0
    var indicatorBackgroundColor = 0
    var plusTextColor = 0
    var minusTextColor = 0
    var indicatorTextColor = 0
    var maxValue = 100
    var minValue = 0
    var enableSwipeGesture = true
    var enableLongPressToReset = true
    var enableVibration = true
    var swipeSensitivity = SENSITIVITY.MEDIUM

    private var mCurrentValue = minValue
    val currentValue: Int
        get() = mCurrentValue

    private lateinit var minusTextView: TextView
    private lateinit var indicatorTextView: TextView
    private lateinit var plusTextView: TextView
    private lateinit var minusButton: MaterialCardView
    private lateinit var indicatorView: MaterialCardView
    private lateinit var plusButton: MaterialCardView

    private var mOnNumberChangedListener: OnNumberChangedListener? = null

    init {
        initializeView(attrs)
    }

    /**
     * Initialize view by configuring attribute values and adding internal views
     */
    private fun initializeView(attrs: AttributeSet?) {
        attrs?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.NumberPickerView)
            attributes.apply {
                plusButtonBackgroundColor = getColor(
                    R.styleable.NumberPickerView_plusButtonBackgroundColor, parseColor(
                        COLOR_BACKGROUND_DEFAULT
                    )
                )
                minusButtonBackgroundColor = getColor(
                    R.styleable.NumberPickerView_minusButtonBackgroundColor, parseColor(
                        COLOR_BACKGROUND_DEFAULT
                    )
                )
                indicatorBackgroundColor = getColor(
                    R.styleable.NumberPickerView_indicatorBackgroundColor, parseColor(
                        COLOR_BACKGROUND_DEFAULT
                    )
                )
                plusTextColor = getColor(
                    R.styleable.NumberPickerView_plusTextColor, parseColor(
                        COLOR_PLUS_MINUS_TEXT_DEFAULT
                    )
                )
                minusTextColor = getColor(
                    R.styleable.NumberPickerView_minusTextColor, parseColor(
                        COLOR_PLUS_MINUS_TEXT_DEFAULT
                    )
                )
                indicatorTextColor = getColor(
                    R.styleable.NumberPickerView_indicatorTextColor, parseColor(
                        COLOR_INDICATOR_TEXT_DEFAULT
                    )
                )
                maxValue = getInt(R.styleable.NumberPickerView_maxValue, 100)
                minValue = getInt(R.styleable.NumberPickerView_minValue, 0)
                mCurrentValue = minValue
                enableSwipeGesture =
                    getBoolean(R.styleable.NumberPickerView_enableSwipeGesture, true)
                enableLongPressToReset =
                    getBoolean(R.styleable.NumberPickerView_enableLongPressToReset, true)
                enableVibration = getBoolean(R.styleable.NumberPickerView_enableVibration, true)
                swipeSensitivity =
                    when (getInt(R.styleable.NumberPickerView_swipeGestureSensitivity, 1)) {
                        0 -> SENSITIVITY.LOW
                        1 -> SENSITIVITY.MEDIUM
                        2 -> SENSITIVITY.HIGH
                        else -> SENSITIVITY.MEDIUM
                    }

                recycle()
            }
        }
        addViews()
        configureInternalViews()

        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        mGestureDetector = GestureDetector(context, GestureListener())
    }

    /**
     * Add internal views
     */
    private fun addViews() {
        minusButton = MaterialCardView(context)
        indicatorView = MaterialCardView(context)
        plusButton = MaterialCardView(context)
        addView(
            minusButton
        )
        addView(
            indicatorView
        )
        addView(
            plusButton
        )
    }

    /**
     * Configure internal views (i.e. layout params and colors)
     */
    @SuppressLint("SetTextI18n")
    private fun configureInternalViews() {
        //Cardviews
        minusButton.apply {
            val params = layoutParams as LayoutParams
            params.weight = 12f
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT
            layoutParams = params
            setCardBackgroundColor(minusButtonBackgroundColor)
            cardElevation = 0f
            minusTextView = TextView(context)
            addView(minusTextView)

            setOnClickListener {} //Enable ripple
        }
        indicatorView.apply {
            val params = layoutParams as LayoutParams
            params.weight = 20f
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT
            params.marginStart = 8.dp
            params.marginEnd = 8.dp
            layoutParams = params
            setCardBackgroundColor(indicatorBackgroundColor)
            cardElevation = 0f
            indicatorTextView = TextView(context)
            addView(indicatorTextView)
        }
        plusButton.apply {
            val params = layoutParams as LayoutParams
            params.weight = 12f
            cardElevation = 0f
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT
            layoutParams = params
            setCardBackgroundColor(plusButtonBackgroundColor)
            cardElevation = 0f
            plusTextView = TextView(context)
            addView(plusTextView)

            setOnClickListener {} //Enable ripple
        }

        //Textviews
        minusTextView.apply {
            val textViewParams = layoutParams as FrameLayout.LayoutParams
            textViewParams.gravity = Gravity.CENTER
            textViewParams.width = FrameLayout.LayoutParams.WRAP_CONTENT
            textViewParams.height = FrameLayout.LayoutParams.WRAP_CONTENT
            layoutParams = textViewParams
            text = "-"
            setTextColor(minusTextColor)
        }
        indicatorTextView.apply {
            val textViwParams = layoutParams as FrameLayout.LayoutParams
            textViwParams.gravity = Gravity.CENTER
            textViwParams.width = FrameLayout.LayoutParams.WRAP_CONTENT
            textViwParams.height = FrameLayout.LayoutParams.WRAP_CONTENT
            layoutParams = textViwParams
            text = minValue.toString()
            setTextColor(indicatorTextColor)
        }
        plusTextView.apply {
            val textViewParams = layoutParams as FrameLayout.LayoutParams
            textViewParams.gravity = Gravity.CENTER
            textViewParams.width = FrameLayout.LayoutParams.WRAP_CONTENT
            textViewParams.height = FrameLayout.LayoutParams.WRAP_CONTENT
            layoutParams = textViewParams
            text = "+"
            setTextColor(plusTextColor)
        }

        configureGestures()
    }

    /**
     * Configure click, swipe, and long click gestures
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun configureGestures() {
        plusButton.setOnTouchListener { _, event ->
            val mAction = object : Runnable {
                override fun run() {
                    increment()
                    mOnNumberChangedListener?.onChanged(mCurrentValue)
                    mHandler?.postDelayed(this, 150)
                }

            }

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (mHandler != null) return@setOnTouchListener true
                    mHandler = Handler(Looper.getMainLooper())
                    mOnNumberChangedListener?.onChanged(mCurrentValue)
                    mHandler?.postDelayed(mAction, 100)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (mHandler == null) return@setOnTouchListener true
                    mHandler?.removeCallbacks(mAction)
                    mHandler = null
                }
            }
            return@setOnTouchListener false
        }
        minusButton.setOnTouchListener { _, event ->
            val mAction = object : Runnable {
                override fun run() {
                    decrement()
                    mOnNumberChangedListener?.onChanged(mCurrentValue)
                    mHandler?.postDelayed(this, 150)
                }

            }

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (mHandler != null) return@setOnTouchListener true
                    mHandler = Handler(Looper.getMainLooper())
                    mHandler?.postDelayed(mAction, 100)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (mHandler == null) return@setOnTouchListener true
                    mHandler?.removeCallbacks(mAction)
                    mHandler = null
                }
            }
            return@setOnTouchListener false
        }
        indicatorView.setOnTouchListener { _, event ->
            mGestureDetector?.onTouchEvent(event)
            true
        }
    }

    /**
     * Increment the value and update UI
     */
    private fun increment() {
        if (mCurrentValue < maxValue) mCurrentValue++
        indicatorTextView.text = mCurrentValue.toString()
    }

    /**
     * Decrement the value and update UI
     */
    private fun decrement() {
        if (mCurrentValue > minValue) mCurrentValue--
        indicatorTextView.text = mCurrentValue.toString()
    }

    private fun vibrate(duration: Long, amplitude: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(
                    duration,
                    amplitude
                )
            )
        } else {
            vibrator?.vibrate(duration)
        }
    }

    /**
     * Parse color strings to color integers to be internally used in the view
     */
    private fun parseColor(color: String): Int {
        return Color.parseColor(color)
    }

    /**
     * Set a listener to get notified every time number is changed
     */
    fun setOnNumberChangedListener(listener: OnNumberChangedListener?) {
        mOnNumberChangedListener = listener
    }

    /**
     * Get dp from provided number value
     */
    private inline val Number.dp: Int
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            resources.displayMetrics
        ).toInt()

    /**
     * GestureListener implementation which is used internally for swipe and long click gesture
     */
    private inner class GestureListener : GestureDetector.OnGestureListener {

        var startX = 0f
        var old = mCurrentValue

        override fun onDown(event: MotionEvent): Boolean {
            startX = event.x
            old = indicatorTextView.text.toString().toInt()
            return true
        }

        override fun onShowPress(e: MotionEvent?) {}

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (enableSwipeGesture) {
                (e2?.x?.minus(startX))?.div(
                    when (swipeSensitivity) {
                        SENSITIVITY.LOW -> 100
                        SENSITIVITY.MEDIUM -> 50
                        SENSITIVITY.HIGH -> 25
                    }
                )?.let {
                    val newValue = old + it
                    if (newValue >= minValue && newValue <= maxValue + 1) {
                        if (mCurrentValue != newValue.toInt()) {
                            if (enableVibration) vibrate(20, 20)
                            mCurrentValue = newValue.toInt()
                            indicatorTextView.text = mCurrentValue.toString()
                            mOnNumberChangedListener?.onChanged(currentValue)
                        }
                    }
                }
            }
            return true
        }

        override fun onLongPress(e: MotionEvent?) {
            if (enableLongPressToReset) {
                mCurrentValue = 0
                indicatorTextView.text = "0"
                if (enableVibration) vibrate(100, 200)
                mOnNumberChangedListener?.onChanged(mCurrentValue)
            }
        }

        override fun onFling(
            event1: MotionEvent,
            event2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            return true
        }
    }
}