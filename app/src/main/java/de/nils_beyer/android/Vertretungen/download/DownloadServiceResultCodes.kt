package de.nils_beyer.android.Vertretungen.download

enum class DownloadResultCodes {
    RESULT_SUCCESS,
    RESULT_ERROR,
    RESULT_AUTHENTICATION_ERROR {
        // The Authentification error passes the id of the account with the wrong credentials.
        override fun getAdditionalDataKey(): String? = "KEY_ACCOUNT_ID"
    };

    open fun getAdditionalDataKey() : String? = null;
}
