package com.abdiel.schoolio.helper

import android.widget.ImageView
import androidx.databinding.BindingAdapter
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
        @BindingAdapter(value = ["thumbnail"], requireAll = true)
        fun thumbnail(view: ImageView, thumbnail: String?) {

            view.post {
                view.setImageDrawable(null)

                thumbnail?.let {
                    Glide
                        .with(view.context)
                        .load(StringHelper.validateEmpty(thumbnail))
                        .apply(RequestOptions.centerCropTransform())
                        .into(view)
                }
            }
        }
    }
}