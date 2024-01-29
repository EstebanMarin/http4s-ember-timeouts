//> using toolkit typelevel:0.1.21

import cats.effect.*
import io.circe.Decoder
import fs2.Stream
import fs2.io.file.*
import org.http4s.ember.client.*
import org.http4s.*
import org.http4s.implicits.*
import org.http4s.circe.*

object Main extends IOApp.Simple:
  case class Data(value: String)
  given Decoder[Data] = Decoder.forProduct1("data")(Data.apply)
  given EntityDecoder[IO, Data] = jsonOf[IO, Data]

  def run = EmberClientBuilder.default[IO].build.use { client =>
    val request: Request[IO] =
      Request(Method.POST, uri"https://httpbin.org/anything")
        .withEntity("file.txt bunchofdata")

    client
      .expect[Data](request)
      .map(_.value.split(" "))
      .flatMap { case Array(fileName, content) =>
        IO.println(s"Writing data to $fileName") *>
          Stream(content)
            .through(fs2.text.utf8.encode)
            .through(Files[IO].writeAll(Path(fileName)))
            .compile
            .drain
      }
  }
