package com.flutterwave.raveandroid.rave_presentation.account;


import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import com.flutterwave.raveandroid.rave_core.models.Bank;
import com.flutterwave.raveandroid.rave_java_commons.Payload;

import java.util.List;

/**
 * Created by hamzafetuga on 20/07/2017.
 */

public interface AccountContract {

    interface View {
        /**
         * Called to indicate that a network call has just begun or has just ended.
         * E.g., by showing or hiding a progress bar
         *
         * @param active This is true if the progress bar should be shown,
         *               and false if it should be hidden
         */
        void showProgressIndicator(boolean active);

        /**
         * To be called when the list of banks available for direct account debit has been retrieved
         * from the server.
         * Typically, this list should be shown to the user to select their bank.
         *
         * @param banks List of {@link Bank} objects to chose from
         */
        void onBanksListRetrieved(List<Bank> banks);

        /**
         * Called when the call to {@link UserActionsListener#getBanksList()} fails.
         *
         * @param message The error message returned
         */
        void onGetBanksRequestFailed(String message);


        /**
         * Called when the call to {@link UserActionsListener#fetchFee(Payload) get the list of banks} has been completed successfully.
         *
         * @param charge_amount The total charge amount (fee inclusive)
         * @param payload       The payload used to initiate the fee request
         */
        void onTransactionFeeRetrieved(String charge_amount, Payload payload);

        /**
         * Called to collect an OTP from the user.
         * After user inputs OTP, call {@link UserActionsListener#authenticateAccountCharge(String, String, String)}
         * with the OTP collected, to continue the transaction.
         *
         * @param publicKey            Public key the transaction was initiated with
         * @param flutterwaveReference The Flutterwave transaction reference
         * @param validateInstruction  Instruction message to be shown to the user
         */
        void collectOtp(String publicKey, String flutterwaveReference, String validateInstruction);

        /**
         * Called to display a {@link android.webkit.WebView} for accounts that need internet banking for authentication.
         * When the payment is completed, the authentication page redirects to a {@link com.flutterwave.raveandroid.rave_java_commons.RaveConstants#RAVE_3DS_CALLBACK predefined url}
         * with the payment details appended to the url.
         * <p>
         * You should override the webview client's {@link android.webkit.WebViewClient#shouldOverrideUrlLoading(WebView, WebResourceRequest) shouldOverrideUrlLoading}
         * function to check if the {@link WebResourceRequest#getUrl() url being loaded} contains the
         * {@link com.flutterwave.raveandroid.rave_java_commons.RaveConstants#RAVE_3DS_CALLBACK predefined redirect url}.
         * <p>
         * If it does, it means the transaction has been completed and you can now call {@link UserActionsListener#requeryTx(String, String)} with the {@code flwRef} to check the transaction status.
         *
         * @param authurl The url to the authentication page
         * @param flwRef  The Flutterwave transaction reference
         */
        void displayInternetBankingPage(String authurl, String flwRef);

        /**
         * Called when the payment has been completed successfully.
         *
         * @param responseAsJSONString The response containing the transaction details in JSON format
         */
        void onPaymentSuccessful(String responseAsJSONString);

        /**
         * Called when the payment attempt is complete, but the payment failed.
         *
         * @param responseAsJSONString The response containing the transaction details in JSON format
         */
        void onPaymentFailed(String responseAsJSONString);

        /**
         * Called when there is an error during the payment attempt.
         *
         * @param errorMessage The error message that can be displayed to the user
         */
        void onPaymentError(String errorMessage);
    }

    interface UserActionsListener {
        /**
         * Fetch the list of available banks for direct debit. The result is returned in {@link View#onBanksListRetrieved(List)}
         */
        void getBanksList();

        /**
         * Check for the fee applicable for this transaction.
         *
         * @param payload Object containing the charge details.
         *                Can be generated with the {@link com.flutterwave.raveandroid.rave_presentation.PayloadBuilder PayloadBuilder}
         */
        void fetchFee(Payload payload);

        /**
         * Initiate the account charge.
         * This is the starting point for the payment.
         *
         * @param payload       Object containing the charge details.
         *                      Can be generated with the {@link com.flutterwave.raveandroid.rave_presentation.PayloadBuilder PayloadBuilder}
         * @param encryptionKey Your Flutterwave encryption key. Can be gotten from <a href="https://dashboard.flutterwave.com/dashboard/settings/apis">your dashboard</a>
         */
        void chargeAccount(Payload payload, String encryptionKey);

        /**
         * Authenticate an account payment with OTP gotten from the customer.
         *
         * @param flwRef    The Flutterwave reference for the transaction
         * @param otp       OTP gotten from the user
         * @param publicKey Your Flutterwave public key. Can be gotten from <a href="https://dashboard.flutterwave.com/dashboard/settings/apis">your dashboard</a>
         */
        void authenticateAccountCharge(String flwRef, String otp, String publicKey);

        /**
         * Check for a transactions status. Result is sent to either {@link View#onPaymentSuccessful(String)}
         * when it's successful, or {@link View#onPaymentFailed(String)} if it's failed.
         *
         * @param flwRef    The Flutterwave reference for the transaction
         * @param publicKey The public Key used to initiate the transaction
         */
        void requeryTx(String flwRef, String publicKey);

        /**
         * Reattaches the view to the presenter.
         * Call this in activity or fragment onStart() function.
         *
         * @param view View to be attached
         */
        void onAttachView(AccountContract.View view);

        /**
         * Detaches the view from the presenter.
         * Call this in activity or fragment onStop() function.
         */
        void onDetachView();
    }

}