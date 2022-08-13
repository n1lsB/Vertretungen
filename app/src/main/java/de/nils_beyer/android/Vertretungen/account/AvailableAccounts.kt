package de.nils_beyer.android.Vertretungen.account

//var availableAccounts = arrayOf(TeacherAccount, StudentAccount)
var availableAccounts = arrayOf(StudentAccount)

fun getAccountID(account : Account<out Dataset>) : Int {
    return availableAccounts.indexOf(account)
}