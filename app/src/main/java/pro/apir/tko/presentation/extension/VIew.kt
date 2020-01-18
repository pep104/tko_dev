package pro.apir.tko.presentation.extension

import android.app.Activity
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

fun View.isVisible() = this.visibility == View.VISIBLE

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.goneWithFade() {
    val view = this
    view.animate()
            .alpha(0f)
            .setDuration(200L)
            .withEndAction {
                view.gone()
                view.alpha = 1f
            }

}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int): View =
        LayoutInflater.from(context).inflate(layoutRes, this, false)


fun ImageView.loadFromUrl(url: String) =
        Glide.with(this.context.applicationContext)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(this)!!

fun EditText.getTextValue(): String {
    return this.text.toString().trim()
}

fun Fragment?.toast(text: String?) {
    if (this?.context != null) {
        Toast.makeText(this.context, text ?: "", Toast.LENGTH_SHORT).show()
    }
}

fun Activity?.toast(text: String) {
    if (this != null) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}

fun AppCompatActivity.alert(msg: String?) {
    try {
        val dialog = AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok) { dialog, which ->
                    dialog.dismiss()
                }
                .create()
        dialog.show()
    } catch (e: Exception) {

    }
}

fun Fragment.alert(msg: String?) {
    try {
        val dialog = AlertDialog.Builder(context!!)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok) { dialog, which ->
                    dialog.dismiss()
                }
                .create()
        dialog.show()
    } catch (e: Exception) {

    }

}

fun View.afterLayout(what: () -> Unit) {
    if (isLaidOut) {
        what.invoke()
    } else {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                what.invoke()
            }
        })
    }
}

fun FragmentActivity.hideKeyborad() {
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

    var view = this.currentFocus
    if (view == null)
        view = View(this)

    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.pxToDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

