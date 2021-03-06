package tests

import com.malliina.reverse.AppComponents
import play.api.test.FakeRequest
import play.api.test.Helpers._

class TestAppSuite extends AppSuite(new AppComponents(_))

class AppTestsScalaTest extends TestAppSuite {

  ignore("can make request") {
    val result = route(app, FakeRequest(GET, "/")).get
    assert(status(result) === 200)
  }
}
