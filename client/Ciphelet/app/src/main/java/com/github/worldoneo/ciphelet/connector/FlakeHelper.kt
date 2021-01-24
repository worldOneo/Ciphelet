package com.github.worldoneo.ciphelet.connector

object FlakeHelper {
    private val chars = "ABCDEFGH=JKLMN!PQRSTUVWXYZabcdefghijk/mnopqrstuvwxyz1234567890-+".toCharArray()
    fun humanIDFromFlake(flake: Long): String {
        var vf = flake
        val stringBuilder = StringBuilder()
        while (vf > 0) {
            stringBuilder.append(chars[vf.toInt() and 0x3F])
            vf = vf shr 6
        }
        return stringBuilder.toString()
    }

    fun flakFromHumanID(humanID: String?): Long {
        var flake = 0L
        for (b in StringBuilder(humanID!!).reverse().toString().toByteArray()) {
            flake = flake shl 6
            for (i in chars.indices) if (chars[i].toByte() == b) flake += i.toLong()
        }
        return flake
    }

    fun timeStampOfFlake(flake: Long): Long {
        return flake shr 21
    }
}