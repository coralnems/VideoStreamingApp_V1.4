package io.github.jitinsharma.bottomnavbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import io.github.jitinsharma.bottomnavbar.model.CustomProps
import io.github.jitinsharma.bottomnavbar.model.NavObject
import kotlinx.android.synthetic.main.bottom_nav_bar.view.*

@Suppress("MemberVisibilityCanPrivate")
@SuppressLint("NewApi")
class BottomNavBar(c: Context?, attrs: AttributeSet?) : ConstraintLayout(c!!, attrs) {
    private var weight: Float = 20.0f
    private var itemSize: Int = 0
    private val properties: CustomProps = CustomProps()
    private var coloredItemIndex: Int = -1

    init {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.BottomNavBar,
            0, 0
        )
        setSecondaryTextColor(
            typedArray.getColor(
                R.styleable.BottomNavBar_secondary_txt_color,
                Color.BLACK
            )
        )
        setSecondaryImageColor(
            typedArray.getColor(
                R.styleable.BottomNavBar_secondary_image_color,
                Color.BLACK
            )
        )
        setPrimaryTextColor(
            typedArray.getColor(
                R.styleable.BottomNavBar_primary_txt_color,
                Color.BLACK
            )
        )
        setPrimaryButtonBackground(
            typedArray.getColor(
                R.styleable.BottomNavBar_primary_btn_bg,
                Color.BLACK
            )
        )
        setLineColor(
            typedArray.getColor(
                R.styleable.BottomNavBar_line_color,
                Color.BLACK
            )
        )
        setStripBackground(
            typedArray.getColor(
                R.styleable.BottomNavBar_strip_bg,
                Color.WHITE
            )
        )
        setSecondaryItemTextClickedColor(
            typedArray.getColor(
                R.styleable.BottomNavBar_secondary_item_text_clicked,
                -1
            )
        )
        setSecondaryItemImageClickedColor(
            typedArray.getColor(
                R.styleable.BottomNavBar_secondary_image_clicked,
                -1
            )
        )
        typedArray.recycle()
        inflate()
    }

    private fun inflate() {
        val layoutInflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        layoutInflater.inflate(R.layout.bottom_nav_bar, this, true)
    }

    /**
     * Primary initialization function
     * @param primaryNavObject - Object for center/floating button
     * @param secondaryNavObjects - List of objects for other buttons on the strip. The size of this list should be 2 or 4
     * @param listener - Callback for button click
     *
     */
    fun init(
        primaryNavObject: NavObject,
        secondaryNavObjects: List<NavObject>,
        listener: (position: Int, primaryClicked: Boolean) -> Unit
    ) {
        setSizeVariables(secondaryNavObjects)
        setItemStrip()
        setUpPrimaryItem(primaryNavObject)
        setUpSecondaryItems(secondaryNavObjects)
        addDummyView()
        setUpPrimaryItemListener(listener)
        setUpSecondaryItemListener(listener)
    }

    private fun setUpSecondaryItemListener(listener: (position: Int, primaryClicked: Boolean) -> Unit) {
        for (k in 0 until itemStrip.childCount) {
            val child = itemStrip.getChildAt(k)
            child.setOnClickListener {
                if (properties.secondaryItemTextClickedColor != -1) {
                    resetColoredItem(k)
                    setColorToCurrentItem(child)
                }
                when {
                    k > itemSize / 2 -> listener(k - 1, false)
                    else -> listener(k, false)
                }
            }
        }
    }

    private fun setUpPrimaryItemListener(listener: (position: Int, primaryClicked: Boolean) -> Unit) {
        primaryButton.setOnClickListener {
            listener(-1, true)
            resetColoredItem(-2) //Set comment because stop reset all menu item select center menu
//            val animation = AnimationUtils.loadAnimation(context, R.anim.bounce)
//            val bounceInterpolator = BounceInterpolator(0.2, 20.0)
//            animation.interpolator = bounceInterpolator
//            primaryButton.startAnimation(animation)
        }
        primaryText.setOnClickListener {
            listener(-1, true)
        }
    }

    private fun setColorToCurrentItem(child: View) {
        coloredItemIndex = itemStrip.indexOfChild(child)
        val layout = child as LinearLayout
        val image = layout.findViewById<ImageView>(R.id.navItemImage)
        val text = layout.findViewById<TextView>(R.id.navItemText)
        text.setTextSize(12f)
        text.setTextColor(properties.secondaryItemTextClickedColor)
        image.setColorFilter(properties.secondaryItemImageClickedColor, PorterDuff.Mode.SRC_ATOP)
    }

    fun resetColoredItem(currentItemIndex: Int) {
        if (currentItemIndex != coloredItemIndex && coloredItemIndex != -1) {//-1
            val layout = itemStrip.getChildAt(coloredItemIndex) as LinearLayout
            val image = layout.findViewById<ImageView>(R.id.navItemImage)
            val text = layout.findViewById<TextView>(R.id.navItemText)
            text.setTextSize(12f)
            text.setTextColor(properties.secondaryTextColor)
            image.setColorFilter(properties.secondaryImageColor, PorterDuff.Mode.SRC_ATOP)
        }
    }

    fun setCurrentItem(position: Int) {
        coloredItemIndex = position
        val layout = itemStrip.getChildAt(position) as LinearLayout
        val image = layout.findViewById<ImageView>(R.id.navItemImage)
        val text = layout.findViewById<TextView>(R.id.navItemText)
        text.setTextSize(12f)
        text.setTextColor(properties.secondaryItemTextClickedColor)
        image.setColorFilter(properties.secondaryItemImageClickedColor, PorterDuff.Mode.SRC_ATOP)
    }

    fun resetAll(position: Int) {
        for (i in 0 until position) {
            val layout = itemStrip.getChildAt(i) as LinearLayout
            val image = layout.findViewById<ImageView>(R.id.navItemImage)
            val text = layout.findViewById<TextView>(R.id.navItemText)
            text.setTextSize(12f)
            text.setTextColor(properties.secondaryTextColor)
            image.setColorFilter(properties.secondaryImageColor, PorterDuff.Mode.SRC_ATOP)
        }
    }

    private fun setSizeVariables(navObjects: List<NavObject>) {
        itemSize = navObjects.size
        when {
            itemSize % 2 != 0 -> {
                Toast.makeText(
                    context, "Secondary items should be of even size",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            itemSize > 4 -> navObjects.dropLast(4)
            itemSize == 2 -> weight = 33.3f
        }
    }

    private fun setItemStrip() {
        itemStrip.setBackgroundColor(properties.stripBg)
        lollipopAndAbove {
            mainLayout.elevation = 8.toPx()
        }
    }

    private fun setUpPrimaryItem(primaryNavObject: NavObject) {
        primaryText.text = primaryNavObject.name
        primaryButton.setImageDrawable(primaryNavObject.image)
//        val gradient: GradientDrawable = primaryButton.background as GradientDrawable
//        gradient.setColor(properties.primaryButtonBg)
        primaryText.setTextColor(properties.primaryTextColor)
        lollipopAndAbove {
            primaryButton.elevation = 8.toPx()
        }
    }

    private fun setUpSecondaryItems(navObjects: List<NavObject>) {
        navObjects.forEach { navObject: NavObject ->
            val navItem = NavItem(context, null)
            val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.weight = weight
            navItem.layoutParams = params
            navItem.setItem(
                navObject.name,
                navObject.image,
                properties.secondaryTextColor,
                properties.secondaryImageColor
            )
            navItem.gravity = Gravity.CENTER_HORIZONTAL
            itemStrip.addView(navItem)
        }
    }

    private fun addDummyView() {
        val navItem = NavItem(context, null)
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.weight = weight
        navItem.layoutParams = params
        navItem.gravity = Gravity.CENTER_HORIZONTAL
        navItem.visibility = View.INVISIBLE
        navItem.isClickable = false
        itemStrip.addView(navItem, itemSize / 2)
    }

    /**
     * Setters
     */

    fun setSecondaryTextColor(color: Int) {
        properties.secondaryTextColor = color
    }

    fun setSecondaryImageColor(color: Int) {
        properties.secondaryImageColor = color
    }

    fun setPrimaryTextColor(color: Int) {
        properties.primaryTextColor = color
    }

    fun setPrimaryButtonBackground(color: Int) {
        properties.primaryButtonBg = color
    }

    fun setLineColor(color: Int) {
        properties.lineColor = color
    }

    fun setStripBackground(color: Int) {
        properties.stripBg = color
    }

    fun setSecondaryItemTextClickedColor(color: Int) {
        properties.secondaryItemTextClickedColor = color
    }

    fun setSecondaryItemImageClickedColor(color: Int) {
        properties.secondaryItemImageClickedColor = color
    }

    /**
     * Utility functions
     */

    private fun Int.toPx(): Float {
        val displayMetrics = context.resources.displayMetrics
        return Math.round(this * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).toFloat()
    }

    private inline fun lollipopAndAbove(body: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            body()
        }
    }
}