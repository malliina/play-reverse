package tests

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import controllers.JenkinsProxy
import org.scalatest.FunSuite

class SignatureTests extends FunSuite {
  ignore("signature") {
    val file = Paths.get(sys.props("user.home")).resolve("hook2.json")
    val payload = new String(Files.readAllBytes(file), StandardCharsets.UTF_8)
    val signature = JenkinsProxy.computeSignature("secret", payload)
    println(signature)
  }
}
