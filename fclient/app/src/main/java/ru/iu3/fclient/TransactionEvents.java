package ru.iu3.fclient;

interface TransactionEvents {
    String enterPin(int ptc, String amount);

    void transactionResult(boolean result);
}