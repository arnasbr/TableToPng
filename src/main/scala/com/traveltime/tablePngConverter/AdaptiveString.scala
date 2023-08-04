package com.traveltime.tablePngConverter

sealed abstract case class AdaptiveString private (value: String)

object AdaptiveString {
  private val MaxLength = 30

  def apply(input: String): Either[String, AdaptiveString] =
    if (input.length <= MaxLength) Right(new AdaptiveString(input) {})
    else Left(s"Input \"$input\" is too long, maximum length is $MaxLength characters")
}
