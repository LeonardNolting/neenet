package net.nee

fun String.cutOverflow(maxLength: UInt, ellipsis: String = Typography.ellipsis.toString()): String {
	require(maxLength >= ellipsis.length.toUInt()) {
		"Max length needs to be at least as great as the given ellipsis length, because the ellipsis will replace the last letters of the text."
	}
	return if (length.toUInt() < maxLength) this
	else substring(0, maxLength.toInt() - ellipsis.length) + ellipsis
}