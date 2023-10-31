package com.abdiel.schoolio.base.activity

import androidx.databinding.ViewDataBinding
import com.crocodic.core.base.activity.CoreActivity
import com.crocodic.core.base.viewmodel.CoreViewModel
import com.crocodic.core.data.CoreSession
import javax.inject.Inject

open class BaseActivity<VB: ViewDataBinding, VM: CoreViewModel>(layoutRes: Int): CoreActivity<VB, VM>(layoutRes) {

    @Inject
    lateinit var session: CoreSession
}