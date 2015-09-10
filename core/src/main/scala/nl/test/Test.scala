package nl.test

import nl.test.macros.TestMacros._

object Test extends App {

  // should not compile
  unique(1, 2, 3, 3)
}
