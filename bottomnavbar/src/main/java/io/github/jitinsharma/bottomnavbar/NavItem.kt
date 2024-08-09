package io.github.jitinsharma.bottomnavbar

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.nav_item.view.*

internal class NavItem(c: Context?, attrs: AttributeSet?) : LinearLayout(c, attrs) {

    init {
        val layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        layoutInflater.inflate(R.layout.nav_item, this, true)
    }

    fun setItem(name: String?, image: Drawable?, color: Int, color_image: Int) {
        name?.let {
            navItemText.text = it
        }
        image?.let {
            navItemImage.setImageDrawable(it)
        }
        navItemImage.setColorFilter(color_image)
        navItemText.setTextColor(color)
    }
}