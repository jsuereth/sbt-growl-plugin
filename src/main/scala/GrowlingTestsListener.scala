package growl

import sbt._
import org.scalatools.testing.{Logger => TLogger, Event => TEvent, Result => TResult}

/** Encapsulates info about a group's test results
 *  @param name name of test group
 *  @param status final Result.Value for a test group
 */
case class GroupResult(
 name: String, status: TestResult.Value
)

/** Encapsulates info about all test results
 *  @param status the final result of the tests
 *  @param count total number of tests in a uni
 *  @param failures number of test failures
 *  @param errors number of test errors
 *  @param passed number of tests passed
 *  @param skipped number of skipped tests
 */
case class AggregateResult(
  status: TestResult.Value, count: Int, failures: Int, errors: Int, passed: Int, skipped: Int
)

/** Output formatting info for growl notification
 *  @param id optional unique display id
 *  @param title title of growl notification
 *  @param message body of growl notification
 *  @param sticky if true growl message will sticky
 *  @param imagePath optional absolute path to image to display
 */
case class GrowlResultFormat(
  id: Option[String], title: String, message: String, sticky: Boolean, imagePath: Option[String]
)

/** GrowlTestReportListener will Growl test result notification */
class GrowlingTestsListener(
  groupFormatter: GroupResult => GrowlResultFormat,
  exceptionFormatter:(String, Throwable) => GrowlResultFormat,
  aggregateFormatter: AggregateResult => GrowlResultFormat,
  growler: Growler, log: sbt.Logger) extends TestsListener {

  private var skipped, errors, passed, failures = 0

  def doInit {
    skipped = 0
    errors = 0
    passed = 0
    failures = 0
  }

  /** called for each class or equivalent grouping */
  def startGroup(name: String) = { }

  /** called for each test method or equivalent */
  def testEvent(event: TestEvent) = event.detail.foreach(count)

  private def count(event: TEvent): Unit =
    event.result match {
      case TResult.Error => errors += 1
      case TResult.Success => passed += 1
      case TResult.Failure => failures += 1
      case TResult.Skipped => skipped += 1
    }

  /** called when test group is completed */
  def endGroup(name: String, result: TestResult.Value) =
    growler.notify(groupFormatter(GroupResult(name, result)))

  /** called when all tests are complete */
  def doComplete(status: TestResult.Value) = {
		val all = failures + errors + skipped + passed
    growler.notify(aggregateFormatter(AggregateResult(status, all, failures, errors, passed, skipped)))
	}

  /** called if there was an error during test */
  def endGroup(name: String, t: Throwable) =
    growler.notify(exceptionFormatter(name, t))
}
