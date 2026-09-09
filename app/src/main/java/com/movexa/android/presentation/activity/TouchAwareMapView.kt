package com.movexa.android.presentation.activity

import android.content.Context
import android.view.MotionEvent
import android.view.ViewConfiguration
import org.osmdroid.views.MapView
import kotlin.math.abs

/**
 * An advanced MapView that balances BottomSheet gestures with Map interaction.
 * It uses the system touch slop to decide whether to let the parent intercept.
 */
class TouchAwareMapView(context: Context) : MapView(context) {

    private var startY = 0f
    private var startX = 0f
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var isDraggingSheet = false
    private var isInteractingWithMap = false

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                startY = ev.y
                startX = ev.x
                isDraggingSheet = false
                isInteractingWithMap = false
                // Initially allow both, but Map takes priority for panning
                parent?.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                // Multi-touch (pinch zoom) -> strictly Map
                isInteractingWithMap = true
                parent?.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_MOVE -> {
                if (!isInteractingWithMap && !isDraggingSheet) {
                    val dy = ev.y - startY
                    val dx = abs(ev.x - startX)
                    val absDy = abs(dy)

                    // If moving vertically significantly more than horizontally
                    if (absDy > touchSlop && absDy > dx) {
                        // Only allow sheet drag if swiping DOWN or if sheet is likely at peek
                        // Note: For simplicity, we yield all vertical swipes to parent interceptors
                        isDraggingSheet = true
                        parent?.requestDisallowInterceptTouchEvent(false)
                        return false 
                    } else if (dx > touchSlop || absDy > touchSlop) {
                        // Horizontal or diagonal move -> Map panning
                        isInteractingWithMap = true
                        parent?.requestDisallowInterceptTouchEvent(true)
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent?.requestDisallowInterceptTouchEvent(false)
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}
