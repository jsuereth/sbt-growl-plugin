package growl

import sbt._
import javazoom.jl.player.Player

object NyanCat {

  /** Wraps a Growler with NYAN CAT POWERS. */
  def wrap(g: Growler): Growler = 
    new Growler {
      def notify(msg: GrowlResultFormat): Unit = {
        startIfNotStarted()
        g.notify(msg.copy(imagePath = Some(defaultIcon)))
      }
      override def toString = "Nyanified("+g+")"
    }

  private def nyanMp3Stream = getClass.getClassLoader.getResourceAsStream("nyan-cat.mp3")
  private lazy val defaultIcon = {
    val img = file(System.getProperty("user.home")) / ".sbt" / "growl" / "icons" / "nyan-cat.png"
    if(!img.exists) {
      IO.createDirectory(img.getParentFile)
      IO.transfer(getClass.getClassLoader.getResourceAsStream("nyan-cat.png"), img)
    }
    img.getAbsolutePath
  }     
  private var currentPlayer: Option[ThePlayer] = None
  
  /** Ensures the nyan cat song is playing for notifications. */
  private def startIfNotStarted() = synchronized {
    if (!currentPlayer.isDefined) {
      currentPlayer = Some(new ThePlayer)
      currentPlayer.foreach(_.start)
    }
    ()
  }
  // TODO - Figure out how to time out this thread if it hasn't been 'started' recently.
  private class ThePlayer extends Thread {
    lazy val p = new Player(nyanMp3Stream)
    override def run(): Unit = {
      p.play()
       NyanCat.synchronized { currentPlayer = None }
    }
    def stopMusic(): Unit = {
      p.close()
    }
  }
}
