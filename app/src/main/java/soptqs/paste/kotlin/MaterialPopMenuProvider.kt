package soptqs.paste.kotlin

import android.animation.ValueAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.github.zawadz88.materialpopupmenu.popupMenu
import soptqs.paste.R
import soptqs.paste.activities.ViewerActivity
import soptqs.paste.database.ClipSaves
import soptqs.paste.database.DataProcess
import soptqs.paste.dialog.CardEditDialog
import soptqs.paste.floatwindow.FloatWindow
import soptqs.paste.utils.AppUtils
import soptqs.paste.utils.PasteToastUtils
import soptqs.paste.utils.translation.HttpGet
import soptqs.paste.utils.translation.TransApi
import java.io.File
import java.util.*

/**
 * Created by S0ptq on 2018/3/17.
 */
object MaterialPopMenuProvider {
    @JvmStatic
    fun normalMaterialPopUpMenu(view: View, context: Context, phoneRec: TextView,
                                curCardItem: ClipSaves) {
        val popupMenu = popupMenu {
            section {
                customItem {
                    layoutResId = R.layout.view_custom_function_area
                    viewBoundCallback = { view ->
                        val edit: ImageView = view.findViewById(R.id.menu_edit)
                        val collect_anim: LottieAnimationView = view.findViewById(R.id.lottie_collect)
                        val read: ImageView = view.findViewById(R.id.menu_read)
                        collect_anim.useHardwareAcceleration(true)
                        if (DataProcess.dataIsCollect(curCardItem.time)) {
                            collect_anim.progress = 1f
                        }

                        collect_anim.setOnClickListener(View.OnClickListener {
                            if (DataProcess.dataIsCollect(curCardItem.time)) {
//                                现在已经选上了
                                val valueAnimator = ValueAnimator.ofFloat(1f, 0f)
                                valueAnimator.duration = 1316
                                valueAnimator.interpolator = DecelerateInterpolator()
                                valueAnimator.addUpdateListener {
                                    collect_anim.progress = it.animatedValue as Float
                                }
                                valueAnimator.start()
                            } else {
                                //现在还没有选上
                                collect_anim.playAnimation()
                            }
                            DataProcess.dataCollect(curCardItem.time)
                        })

                        edit.setOnClickListener(View.OnClickListener {
                            FloatWindow.hidePopupWindow()
                            val intent = Intent(context, CardEditDialog::class.java)
                            intent.putExtra("category", 1)
                            intent.putExtra("content", curCardItem.content)
                            intent.putExtra("time", curCardItem.time)
                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        })

                        read.setOnClickListener {
                            FloatWindow.hidePopupWindow()
                            val intent = Intent(context, ViewerActivity::class.java)
                            intent.putExtra("content", curCardItem.content)
                            context.startActivity(intent)
                        }
                    }
                }
            }
            section {
                title = context.resources.getString(R.string.other)
                item {
                    label = context.resources.getString(R.string.card_menu_share)
                    icon = R.drawable.ic_share_black_24dp
                    iconColor = R.color.colorWhite
                    callback = {
                        val shareIntent = Intent()
                        shareIntent.action = Intent.ACTION_SEND
                        shareIntent.type = "text/plain"
                        shareIntent.putExtra(Intent.EXTRA_TEXT, curCardItem.content)
                        FloatWindow.hidePopupWindow()
                        context.startActivity(Intent.createChooser(shareIntent,
                                context.resources.getText(R.string.card_menu_share)))
                    }
                }
                item {
                    if (DataProcess.dataIsTranslated(curCardItem.time)) {
                        label = context.resources.getString(R.string.card_menu_dismiss_translatino)
                    } else label = context.resources.getString(R.string.card_menu_translation)
                    icon = R.drawable.ic_translate_black_24dp
                    iconColor = R.color.colorWhite
                    callback = {
                        if (!AppUtils.isPro(context)) {
                            FloatWindow.hidePopupWindow()
                            Toast.makeText(context, R.string.isnotpro, Toast.LENGTH_SHORT).show()
                        } else {
                            if (!curCardItem.isTranslated) {
                                chooseLanguagePopUpMenu(view, context, curCardItem, phoneRec)
                            } else {
                                DataProcess.isNotTranslated(curCardItem.time)
                                phoneRec.visibility = View.INVISIBLE
                                phoneRec.invalidate()
                            }
                        }
                    }
                }
            }
        }
        popupMenu.show(context, view)
    }

    @JvmStatic
    fun urlMaterialPopUpMenu(view: View, context: Context, curCardItem: ClipSaves) {
        val popupMenu = popupMenu {
            section {
                customItem {
                    layoutResId = R.layout.view_custom_function_area
                    viewBoundCallback = { view ->
                        val edit: ImageView = view.findViewById(R.id.menu_edit)
                        val collect_anim: LottieAnimationView = view.findViewById(R.id.lottie_collect)
                        val read: ImageView = view.findViewById(R.id.menu_read)
                        if (DataProcess.dataIsCollect(curCardItem.time)) {
                            collect_anim.progress = 1f
                        }
                        edit.setOnClickListener(View.OnClickListener {
                            FloatWindow.hidePopupWindow()
                            val intent = Intent(context, CardEditDialog::class.java)
                            intent.putExtra("category", 1)
                            intent.putExtra("content", curCardItem.content)
                            intent.putExtra("time", curCardItem.time)
                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        })
                        collect_anim.setOnClickListener(View.OnClickListener {
                            if (DataProcess.dataIsCollect(curCardItem.time)) {
//                                现在已经选上了
                                val valueAnimator = ValueAnimator.ofFloat(1f, 0f)
                                valueAnimator.duration = 1316
                                valueAnimator.interpolator = DecelerateInterpolator()
                                valueAnimator.addUpdateListener {
                                    collect_anim.progress = it.animatedValue as Float
                                }
                                valueAnimator.start()
                            } else {
                                //现在还没有选上
                                collect_anim.playAnimation()
                            }
                            DataProcess.dataCollect(curCardItem.time)
                        })

                        read.setOnClickListener {
                            FloatWindow.hidePopupWindow()
                            val intent = Intent(context, ViewerActivity::class.java)
                            intent.putExtra("content", curCardItem.content)
                            context.startActivity(intent)
                        }
                    }
                }
            }
            section {
                title = context.resources.getString(R.string.other)
                item {
                    label = context.resources.getString(R.string.card_menu_open)
                    icon = R.drawable.ic_open_in_browser_black_24dp
                    iconColor = R.color.colorWhite
                    callback = {
                        val intent1 = Intent()
                        intent1.action = "android.intent.action.VIEW"
                        val uri = Uri.parse(curCardItem.content)
                        intent1.data = uri
                        try {
                            context.startActivity(intent1)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, R.string.webview_error, Toast.LENGTH_LONG).show()
                        }

                        FloatWindow.hidePopupWindow()
                    }
                }
            }
        }
        popupMenu.show(context, view)
    }

    @JvmStatic
    fun imageMaterialPopUpMenu(view: View, context: Context, curCardItem: ClipSaves) {
        val popupMenu = popupMenu {
            section {
                item {
                    icon = R.drawable.ic_star_black_24dp
                    if (DataProcess.dataIsCollect(curCardItem.time)) {
                        iconColor = ContextCompat.getColor(context, R.color.colorAPP)
                        label = context.resources.getString(R.string.card_menu_cancel_collect)
                    } else {
                        label = context.resources.getString(R.string.card_menu_collect)
                    }
                    callback = {
                        DataProcess.dataCollect(curCardItem.time)
                    }
                }
                item {
                    label = context.resources.getString(R.string.card_menu_share)
                    icon = R.drawable.ic_share_black_24dp
                    iconColor = R.color.colorWhite
                    callback = {
                        if (curCardItem.content.contains(",")) {
                            val Stringlist = ArrayList(Arrays.asList(*curCardItem.content.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()))
                            val uriArrayList = ArrayList<Uri>()
                            for (string in Stringlist) {
                                val name = "$string.png"
                                val imagePath = File(context.filesDir, name)
                                val uri = FileProvider.getUriForFile(context,
                                        "soptqs.paste.PasteFileProvider",
                                        imagePath)
                                uriArrayList.add(uri)
                            }
                            val shareIntent = Intent()
                            shareIntent.action = Intent.ACTION_SEND_MULTIPLE
                            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriArrayList)
                            shareIntent.type = "image/*"
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                            FloatWindow.hidePopupWindow()
                            context.startActivity(Intent.createChooser(shareIntent,
                                    context.resources.getText(R.string.card_menu_share)))
                        } else {
                            val name = curCardItem.content + ".png"
                            val imagePath = File(context.filesDir, name)
                            val uri = FileProvider.getUriForFile(context,
                                    "soptqs.paste.PasteFileProvider",
                                    imagePath)
                            val shareIntent = Intent()
                            shareIntent.action = Intent.ACTION_SEND
                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                            shareIntent.type = "image/*"
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                            FloatWindow.hidePopupWindow()
                            context.startActivity(Intent.createChooser(shareIntent,
                                    context.resources.getText(R.string.card_menu_share)))
                        }
                    }
                }
            }
        }
        popupMenu.show(context, view)
    }

    @JvmStatic
    fun chooseLanguagePopUpMenu(view: View, context: Context, cardItem: ClipSaves, phoneRec: TextView) {
        val arrayString = context.resources.getStringArray(R.array.translate_from)
        val arrayCode = context.resources.getStringArray(R.array.translate_from_code)
        val popupMenu = popupMenu {
            section {
                title = context.resources.getString(R.string.translation_section)
                for (i in arrayString.indices) {
                    item {
                        label = arrayString[i]
                        callback = {
                            val handler1 = Handler()
                            val transApi = TransApi("20180221000123308", "LUFWCOmdfr2DI5Uf62Jc")
                            Thread(Runnable {
                                val trans = HttpGet.transJson(transApi.getTransResult(cardItem.content, "auto", arrayCode[i]))
                                DataProcess.isTranslated(cardItem.time, trans)
                                handler1.post {
                                    phoneRec.visibility = View.VISIBLE
                                    phoneRec.invalidate()
                                    phoneRec.text = trans
                                }
                            }).start()
                        }
                    }
                }
            }
        }
        popupMenu.show(context, view)
    }

    @JvmStatic
    fun universalCopyLongClickPopUpMenu(view: View, context: Context, string: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val popupMenu = popupMenu {
            dropdownGravity = Gravity.END
            section {
                customItem {
                    layoutResId = R.layout.view_custom_menu_copy
                    viewBoundCallback = { view ->
                        val textView: TextView = view.findViewById(R.id.custome_copy_view_textview)
                        textView.text = string
                    }
                }
            }
            section {
                item {
                    label = context.resources.getString(R.string.copy)
                    icon = R.drawable.ic_content_paste_black_24dp
                    iconColor = R.color.colorWhite
                    callback = {
                        val clipData = ClipData.newPlainText(null, string)
                        if (clipData != null) {
                            clipboard.primaryClip = clipData
                            PasteToastUtils.getPasteToastUtils().ToastShow(context, null)
                        }
                    }
                }
            }
        }
        popupMenu.show(context, view)
    }
}