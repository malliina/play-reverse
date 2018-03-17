package tests

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import org.apache.commons.codec.digest.HmacUtils
import org.scalatest.FunSuite

class SignatureTests extends FunSuite {
  ignore("signature") {
    val file = Paths.get(sys.props("user.home")).resolve("test.json")
    val payload = new String(Files.readAllBytes(file), StandardCharsets.UTF_8)
    val signature = HmacUtils.hmacSha1Hex("todo", payload)
    println(signature)
  }
}
