package de.nils_beyer.android.Vertretungen.util

fun convertClassName(_input : String) : String {
    var output = _input
    // Convert 05A as 5a etc
    if (_input.matches("^\\d+[a-zA-Z]+$".toRegex())) {
        output = _input.toLowerCase()
        output = output.replace("^0(\\d[a-zA-Z]+)$".toRegex(), "$1")
    }
    return output
}