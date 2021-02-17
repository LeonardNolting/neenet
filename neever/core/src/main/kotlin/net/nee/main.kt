package net.nee

import net.nee.NBT.readNamedNBTCompound
import net.nee.NBT.toSNBT
import net.nee.NBT.writeNBT
import net.nee.NBT.writeNamedNBTCompound
import net.nee.event.Event
import net.nee.events.packet.Packet
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream

fun main() {
	neever {
		/*val beforeHandshake = events.before { event: Receive<Handshake> ->
			println("BEFORE handshake")
			"nice"
		}
		val afterHandshake = events.after { event: Receive<Handshake>, result ->
			println("AFTER handshake: $result")
			1
		}
		afterHandshake.before { event ->
			println("before AFTER handshake")
		}
		val afterAfterHandshake = afterHandshake.after { event, result ->
			println("after AFTER handshake: $result")
		}

		afterAfterHandshake.after { event, result ->
			println("after after AFTER handshake")
		}*/

		events.before { event: Event<Packet<*>> ->
//			println("Packet transaction")
		}

		// TEST NBT
		val outputStream = ByteArrayOutputStream()
		val output = DataOutputStream(outputStream)
		val nbt = listOf(
			mapOf(
				"foo" to 5,
				"bar" to listOf<Byte>(1, 2, 3),
				"array" to ByteArray(5) { (it * 2).toByte() }
			)
		)
		output.writeNBT(nbt)
		println(nbt.toSNBT())
//	val input = DataInputStream(ByteArrayInputStream(outputStream.toByteArray()))
		val file = File("C:\\Users\\Tommi\\AppData\\Roaming\\.minecraft\\servers.dat_older")
		val input = DataInputStream(FileInputStream(file))
		val nbt2 = input.readNamedNBTCompound().second// as Map<String, List<Map<String, Any>>>
		println(nbt2.toSNBT())
		val bytes =
			ByteArrayOutputStream().apply { DataOutputStream(this).writeNamedNBTCompound("", nbt2) }.toByteArray()
		val fileBytes = file.readBytes()
		println(bytes.contentToString())
		println(fileBytes.contentToString())
		println(bytes.contentEquals(fileBytes))

		val nbt3 =
			NBT.mergeAdding(
				nbt2,
				mapOf(
					"servers" to listOf(
//				nbt2["servers"]!![0],
//				nbt2["servers"]!![1],
						mapOf(
							"ip" to "foo.bar.baz",
							"name" to "Hello World!!!"
						)
					)
				)
			)
		val newFile = File("C:\\Users\\Tommi\\AppData\\Roaming\\.minecraft\\servers.dat")
		DataOutputStream(newFile.outputStream()).writeNamedNBTCompound("", nbt3)
		println(nbt3.toSNBT())

	}
}