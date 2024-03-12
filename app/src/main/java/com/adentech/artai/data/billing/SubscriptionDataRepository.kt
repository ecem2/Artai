package com.adentech.artai.data.billing

import com.adentech.artai.core.common.Constants.MONTHLY_PREMIUM
import com.adentech.artai.core.common.Constants.YEARLY_PREMIUM
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class SubscriptionDataRepository(billingClientWrapper: BillingClientWrapper) {

    val monthlyBasic: Flow<Boolean> = billingClientWrapper.purchases.map { purchaseList->
        purchaseList.any { purchase ->
            purchase.products.contains(MONTHLY_PREMIUM) && purchase.isAutoRenewing
        }
    }

    val yearlyBasic: Flow<Boolean> = billingClientWrapper.purchases.map { purchaseList->
        purchaseList.any { purchase ->
            purchase.products.contains(YEARLY_PREMIUM) && purchase.isAutoRenewing
        }
    }

    val monthlyDetail: Flow<ProductDetails> =
        billingClientWrapper.productWithProductDetails.filter {
            it.containsKey(MONTHLY_PREMIUM)
        }.map { it[MONTHLY_PREMIUM]!! }

    val yearlyDetail: Flow<ProductDetails> =
        billingClientWrapper.productWithProductDetails.filter {
            it.containsKey(YEARLY_PREMIUM)
        }.map { it[YEARLY_PREMIUM]!! }

    // List of current purchases returned by the Google PLay Billing client library.
    val purchases: Flow<List<Purchase>> = billingClientWrapper.purchases

    // Set to true when a purchase is acknowledged.
    val isNewPurchaseAcknowledged: Flow<Boolean> = billingClientWrapper.isNewPurchaseAcknowledged

    val isNewSubscriptionDone: Flow<Boolean> = billingClientWrapper.isNewPurchaseDone
}