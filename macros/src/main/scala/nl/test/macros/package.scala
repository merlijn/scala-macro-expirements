package nl.test

import shapeless.Nat

package object macros {

  trait NatWrapper {
    type T <: Nat
  }
}
