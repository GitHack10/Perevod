package com.perevod.perevodkassa.data.network.sse

data class PaymentStatusEvent(
    val orderUuid: String,
    val message: String,
    val status: PaymentStatus,
    val paperPrint: String?
)

/**
 * @param CASHIER_PAYMENT_INIT = инициализация платежа на стороне клиента (МП)
 * @param CRYPTO_INIT = тригеррится после сканирования QR-кода пользователем
 * @param CRYPTO_APPROVED = тригеррится после успешной оплаты
 * @param VALIDATED = прошла валидацию на стороне блокчейна (завершаем флоу)
 * **/
enum class PaymentStatus(val type: String) {
    CashierPaymentInit("CASHIER_PAYMENT_INIT"),
    CryptoInit("CRYPTO_INIT"),
    CryptoApproved("CRYPTO_APPROVED"),
    Validated("VALIDATED"),
    Idle("IDLE");

    companion object {
        fun getStatusByValue(type: String) = values().find { it.type == type } ?: Idle
    }
}