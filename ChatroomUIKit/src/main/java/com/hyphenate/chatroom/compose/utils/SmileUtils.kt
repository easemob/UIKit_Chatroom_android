package com.hyphenate.chatroom.compose.utils

import android.content.Context
import android.net.Uri
import android.text.Spannable
import android.text.style.ImageSpan
import com.hyphenate.chatroom.data.emojiMap
import java.io.File
import java.util.regex.Pattern

object SmileUtils {
    private val spannableFactory = Spannable.Factory
        .getInstance()

    private val emoticons: MutableMap<Pattern, Any> = HashMap()

    init {
        for (entry in emojiMap) {
            addPattern(entry.key,entry.value)
        }
    }

    fun addPattern(emojiText: String?, icon: Any?) {
        emojiText?.let {
            emoticons[Pattern.compile(Pattern.quote(it))] = icon as Any
        }
    }
    fun getSmiledText(context: Context?, text: CharSequence?): Spannable? {
        val spannable = spannableFactory.newSpannable(text)
        addSmiles(context, spannable)
        return spannable
    }

    private fun addSmiles(context: Context?, spannable: Spannable): Boolean {
        var hasChanges = false
        emoticons.entries.forEach{
            val matcher = it.key.matcher(spannable)
            while (matcher.find()) {
                var set = true
                for (span in spannable.getSpans(
                    matcher.start(),
                    matcher.end(), ImageSpan::class.java
                )) if (spannable.getSpanStart(span) >= matcher.start()
                    && spannable.getSpanEnd(span) <= matcher.end()
                ) spannable.removeSpan(span) else {
                    set = false
                    break
                }
                if (set) {
                    hasChanges = true
                    if (it.value is String && !(it.value as String).startsWith("http")) {
                        val file = File(it.value as String)
                        if (!file.exists() || file.isDirectory) {
                            return false
                        }
                        spannable.setSpan(
                            ImageSpan(context!!, Uri.fromFile(file)),
                            matcher.start(), matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    } else {
                        spannable.setSpan(
                            ImageSpan(context!!, (it.value as Int)),
                            matcher.start(), matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
            }
        }
        return hasChanges
    }
}