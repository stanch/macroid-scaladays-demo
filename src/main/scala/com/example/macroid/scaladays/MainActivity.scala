package com.example.macroid.scaladays

import android.os.Bundle
import android.widget.{LinearLayout, TextView, Button}
import android.view.ViewGroup.LayoutParams._
import android.view.{Gravity, View}
import android.app.Activity

import macroid._
import macroid.contrib.Layouts.VerticalLinearLayout
import macroid.contrib.ExtraTweaks._
import macroid.util.Ui
import macroid.FullDsl._
import macroid.util.Effector

import rx._
import rx.ops._

// support for Scala.Rx
// will be a part of macroid-frp
trait RxSupport {
  var refs = List.empty[AnyRef]
  implicit def rxEffector = new Effector[Rx] {
    override def foreach[A](fa: Rx[A])(f: A ⇒ Any): Unit =
      refs ::= fa.foreach(f andThen (_ ⇒ ()))
  }
}

class MainActivity extends Activity with Contexts[Activity] with RxSupport {

  // some reactive variables
  val rx1 = Var(1)
  val rx2 = Var(2)

  // the sum will be updated automatically!
  val rx3 = Rx { rx1() + rx2() }

  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)

    // a tweak that wires the caption to the
    // reactive variable
    def rxText(rx: Rx[Int]) =
      rx.map(_.toString).map(text)

    // a tweak that sets a click handler
    // to increment the reactive variable
    def rxClick(rx: Var[Int]) =
      On.click(Ui {
        rx() = rx() + 1
      })

    // button style
    val style = TextSize.large + padding(all = 16 dp)

    // layout
    val view: Ui[View] = l[VerticalLinearLayout](
      w[Button] <~ style <~ rxText(rx1) <~ rxClick(rx1),
      w[Button] <~ style <~ rxText(rx2) <~ rxClick(rx2),
      w[Button] <~ style <~ rxText(rx3)
    )

    setContentView(getUi(view))
  }
}
