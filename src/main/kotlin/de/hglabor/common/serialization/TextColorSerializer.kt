package de.hglabor.common.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.text.format.TextColor
import java.util.*

object TextColorSerializer : KSerializer<TextColor> {
    override val descriptor = PrimitiveSerialDescriptor("TextColor", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): TextColor {
        return TextColor.fromHexString(decoder.decodeString())!!
    }

    override fun serialize(encoder: Encoder, value: TextColor) {
        encoder.encodeString(value.asHexString())
    }
}