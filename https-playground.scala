//> using toolkit typelevel:0.1.21

import cats.effect.*
import java.util.UUID
import cats.implicits.*
import cats.syntax.*

object Hello extends IOApp.Simple:
  type Student = String
  case class Instructor(name: String)
  case class Course(
      id: UUID,
      name: String,
      instructor: Instructor,
      students: List[Student]
  )
  object CourseRepository:
    val catsEffectCourse = Course(
      UUID.fromString("93cc6b10-2964-4e61-b3cb-88a1fd12e619"),
      "Cats Effect",
      Instructor("John"),
      List("Alice", "Bob")
    )
    private val courses = Map(
      catsEffectCourse.id -> catsEffectCourse
    )

    def findCourse(id: UUID): Option[Course] =
      courses.get(id)
    def findCourseByInstructor(instructor: Instructor): Option[Course] =
      courses.values.find(_.instructor == instructor)

  def run =
    for
      _ <- IO.println("Hello world!")
      _ <- IO.println(
        CourseRepository.findCourse(
          UUID.fromString("93cc6b10-2964-4e61-b3cb-88a1fd12e619")
        )
      )
    yield ()
