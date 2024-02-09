package com.example.fareshare.utils

fun calculateTipAmt(amt: Double, tipCent: Int): Double {
    if(amt > 1 && amt.toString().isNotEmpty())
        return (amt * tipCent) / 100

    return 0.0
}

fun calculateTotalPerPerson(
    totalBill: Double,
    splitBy: Int,
    tipCent: Int
): Double {
    val bill = calculateTipAmt(totalBill, tipCent) + totalBill

    return bill / splitBy
}