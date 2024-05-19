package com.pukimen.social.ui.customView

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.pukimen.social.R

class VisibilityToggleButton : AppCompatEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        initToggleListener()
    }

    private fun initToggleListener() {
        Log.d("VisibilityToggleButton", "Init Toggle Listener dipanggil")
        val visibilityToggleOff: Drawable =
            ContextCompat.getDrawable(context, R.drawable.ic_visibility_off)!!
        visibilityToggleOff.setBounds(
            0, 0, visibilityToggleOff.intrinsicWidth, visibilityToggleOff.intrinsicHeight
        )

        val visibilityToggleOn: Drawable =
            ContextCompat.getDrawable(context, R.drawable.ic_visibility_on)!!
        visibilityToggleOn.setBounds(
            0, 0, visibilityToggleOn.intrinsicWidth, visibilityToggleOn.intrinsicHeight
        )

        setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableRight = compoundDrawablesRelative[2]
                Log.d("VisibilityToggleButton", "Sentuhan terdeteksi $drawableRight")
                if (drawableRight != null && event.rawX >= right - drawableRight.bounds.width()) {
                    Log.d("VisibilityToggleButton", "Tombol visibilitas ditekan")
                    toggleVisibility(visibilityToggleOff, visibilityToggleOn)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun toggleVisibility(visibilityToggleOff: Drawable, visibilityToggleOn: Drawable) {
        if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, visibilityToggleOff, null)
        } else {
            inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, visibilityToggleOn, null)
        }
    }
}
