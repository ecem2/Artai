package com.adentech.artai.data.billing

import com.adentech.artai.ArtaiApplication
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.adentech.artai.core.common.Constants.MONTHLY_PREMIUM
import com.adentech.artai.core.common.Constants.YEARLY_PREMIUM
import com.android.billingclient.api.*
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BillingClientWrapper(
    context: Context
) : PurchasesUpdatedListener, ProductDetailsResponseListener {

    // New Subscription ProductDetails
    private val _productWithProductDetails =
        MutableStateFlow<Map<String, ProductDetails>>(emptyMap())
    val productWithProductDetails = _productWithProductDetails.asStateFlow()

    // Current Purchases
    private val _purchases = MutableStateFlow<List<Purchase>>(listOf())
    val purchases = _purchases.asStateFlow()

    // Tracks new purchases acknowledgement state.
    // Set to true when a purchase is acknowledged and false when not.
    private val _isNewPurchaseAcknowledged = MutableStateFlow(value = false)
    val isNewPurchaseAcknowledged = _isNewPurchaseAcknowledged.asStateFlow()

    private val _isNewPurchaseDone = MutableStateFlow(value = false)
    val isNewPurchaseDone = _isNewPurchaseDone.asStateFlow()

    // Initialize the BillingClient.
    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    // Establish a connection to Google Play.
    fun startBillingConnection(billingConnectionState: MutableLiveData<Boolean>) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "startBillingConnection: Billing response OK")
                    // The BillingClient is ready. You can query purchases and product details here
                    queryPurchases()
                    queryProductDetails()
                    billingConnectionState.postValue(true)
                } else {
                    Log.e(TAG, billingResult.debugMessage)
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.i(TAG, "onBillingServiceDisconnected: Billing connection disconnected")
                startBillingConnection(billingConnectionState)
            }
        })
    }

    // Query Google Play Billing for existing purchases.
    // New purchases will be provided to PurchasesUpdatedListener.onPurchasesUpdated().
    fun queryPurchases() {
        if (!billingClient.isReady) {
            Log.e(TAG, "queryPurchases: BillingClient is not ready")
        }
        // Query for existing subscription products that have been purchased.
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        ) { billingResult, purchaseList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "queryPurchases: purchaseList000 === $purchaseList")
                for (purchase in purchaseList) {
                    Log.d(TAG, "queryPurchases: purchase === $purchase")
                }
                if (!purchaseList.isNullOrEmpty()) {
                    ArtaiApplication.hasSubscription = true
                    _purchases.value = purchaseList
                    Log.d(TAG, "queryPurchases: purchaseList111 === $purchaseList")
                } else {
                    Log.d(TAG, "queryPurchases: purchaseList222 === $purchaseList")
                    _purchases.value = emptyList()
                    ArtaiApplication.hasSubscription = false // false olacak
                }
            } else {
                Log.e(TAG, billingResult.debugMessage)
            }
        }
    }

    // Query Google Play Billing for products available to sell and present them in the UI
    fun queryProductDetails() {
        val productList: ImmutableList<QueryProductDetailsParams.Product> = ImmutableList.of(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(MONTHLY_PREMIUM)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(YEARLY_PREMIUM)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        billingClient.queryProductDetailsAsync(
            params
        ) { _: BillingResult?, prodDetailsList: List<ProductDetails> ->
            val newMap: Map<String, ProductDetails> = prodDetailsList.associateBy {
                it.productId
            }
            _productWithProductDetails.value = newMap
        }
    }

    // [ProductDetailsResponseListener] implementation
    // Listen to response back from [queryProductDetails] and emits the results
    // to [_productWithProductDetails].
    override fun onProductDetailsResponse(
        billingResult: BillingResult,
        productDetailsList: MutableList<ProductDetails>
    ) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                var newMap = emptyMap<String, ProductDetails>()
                Log.d(TAG, "onProductDetailsResponse: productDetailsList === $productDetailsList")
                if (productDetailsList.isNullOrEmpty()) {
                    Log.e(
                        TAG,
                        "onProductDetailsResponse: " +
                                "Found null or empty ProductDetails. " +
                                "Check to see if the Products you requested are correctly " +
                                "published in the Google Play Console."
                    )
                } else {
                    newMap = productDetailsList.associateBy {
                        it.productId
                    }
                }
                _productWithProductDetails.value = newMap
                Log.d(TAG, "onProductDetailsResponse: productDetailsList === $productDetailsList")
                Log.d(TAG, "onProductDetailsResponse: newMap === $productDetailsList")

            }
            else -> {
                Log.i(TAG, "onProductDetailsResponse: $responseCode $debugMessage")
            }
        }
    }

    // Launch Purchase flow
    fun launchBillingFlow(activity: Activity, params: BillingFlowParams) {
        if (!billingClient.isReady) {
            Log.e(TAG, "launchBillingFlow: BillingClient is not ready")
        }
        billingClient.launchBillingFlow(activity, params)
    }

    // PurchasesUpdatedListener that helps handle new purchases returned from the API
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: List<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK
            && !purchases.isNullOrEmpty()
        ) {
            // Post new purchase List to _purchases
            _purchases.value = purchases
            _isNewPurchaseDone.value = true
            ArtaiApplication.hasSubscription = true //false
            // Then, handle the purchases
            for (purchase in purchases) {
                acknowledgePurchases(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Log.e(TAG, "User has cancelled")
        } else {
            // Handle any other error codes.
        }
    }

    // Perform new subscription purchases' acknowledgement client side.
    private fun acknowledgePurchases(purchase: Purchase?) {
        purchase?.let {
            if (!it.isAcknowledged) {
                val params = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(it.purchaseToken)
                    .build()

                billingClient.acknowledgePurchase(
                    params
                ) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK &&
                        it.purchaseState == Purchase.PurchaseState.PURCHASED
                    ) {
                        _isNewPurchaseAcknowledged.value = true
                    }
                }
            }
        }
    }

    // End Billing connection.
    fun terminateBillingConnection() {
        Log.i(TAG, "Terminating connection")
        billingClient.endConnection()
    }

    companion object {
        private const val TAG = "BillingClientWrapper"
    }
}