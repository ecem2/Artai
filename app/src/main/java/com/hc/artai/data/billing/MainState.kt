package com.hc.artai.data.billing

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase

data class MainState(
    val hasMonthlyBasic: Boolean? = false,
    val hasYearlyBasic: Boolean? = false,
    val basicMonthlyDetails: ProductDetails? = null,
    val basicYearlyDetails: ProductDetails? = null,
    val purchases: List<Purchase>? = null
)