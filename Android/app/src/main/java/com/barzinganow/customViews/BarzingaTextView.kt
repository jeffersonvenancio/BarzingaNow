package com.barzinganow.customViews

import android.content.Context
import android.util.AttributeSet
import com.barzinganow.R
import com.barzinganow.util.FontUtils
import com.barzinganow.util.FontUtils.setOpenSansFont

/**
 * Created by diego.santos on 03/10/17.
 */
class BarzingaTextView : android.support.v7.widget.AppCompatTextView {

    internal var mContext: Context

    constructor(context: Context) : super(context) {

        mContext = context

        initView(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        mContext = context

        initView(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        mContext = context

        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        if (attrs != null) {
            setTypefaceRoboto(attrs)
        } else {
            setOpenSansFont(this.context, this, FontUtils.FontFamily.OPENSANS)
        }
    }

    internal fun setTypefaceRoboto(attrs: AttributeSet?) {
        var style: String? = null

        if (attrs != null) {
            val ta = mContext.obtainStyledAttributes(attrs, R.styleable.BarzingaTextView)
            style = ta.getString(R.styleable.BarzingaTextView_typefaceOpenSans)
            ta.recycle()
            if (style != null && style !== "") {
                setOpenSansFont(mContext, this, FontUtils.getFontFamily(style))
            } else {
                setOpenSansFont(mContext, this, FontUtils.FontFamily.OPENSANS)
            }
        } else {
            setOpenSansFont(mContext, this, FontUtils.FontFamily.OPENSANS)
        }
    }
}