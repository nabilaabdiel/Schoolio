package com.abdiel.schoolio.helper

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.abdiel.schoolio.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crocodic.core.helper.StringHelper


class ViewBindingHelper {
    companion object {
        @JvmStatic
        @BindingAdapter(value = ["imageUrl"], requireAll = true)
        fun loadImageRecipe(view: ImageView, imageUrl: String?) {

            view.post {
                view.setImageDrawable(null)

                imageUrl?.let {
                    Glide
                        .with(view.context)
                        .load(StringHelper.validateEmpty(imageUrl))
                        .apply(RequestOptions.circleCropTransform())
                        .into(view)
                }
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["destinationImg"], requireAll = false)
        fun destinationImg(view: ImageView, destinationImg: String?) {

            view.setImageDrawable(null)

            destinationImg?.let {
                Glide
                    .with(view.context)
                    .load(destinationImg)
                    .apply(RequestOptions.centerCropTransform())
//                    .error(R.drawable.error_placeholder)
                    .into(view)

            }
        }
    }
}