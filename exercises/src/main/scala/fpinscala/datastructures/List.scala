package fpinscala.datastructures

sealed trait List[+A] // `List` data type, parameterized on a type, `A`
case object Nil extends List[Nothing] // A `List` data constructor representing the empty list
case class Cons[+A](head: A, tail: List[A]) extends List[A] // Another data constructor, representing nonempty lists. Note that `tail` is another `List[A]`, which may be `Nil` or another `Cons`.

object List { // `List` companion object. Contains functions for creating and working with lists.
  def sum(ints: List[Int]): Int = ints match { // A function that uses pattern matching to add up a list of integers
    case Nil => 0 // The sum of the empty list is 0.
    case Cons(x,xs) => x + sum(xs) // The sum of a list starting with `x` is `x` plus the sum of the rest of the list.
  }
  
  def product(ds: List[Double]): Double = ds match {
    case Nil => 1.0
    case Cons(0.0, _) => 0.0
    case Cons(x,xs) => x * product(xs)
  }
  
  def apply[A](as: A*): List[A] = // Variadic function syntax
    if (as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))

  val x = List(1,2,3,4,5) match {
    case Cons(x, Cons(2, Cons(4, _))) => x
    case Nil => 42 
    case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x + y
    case Cons(h, t) => h + sum(t)
    case _ => 101 
  }

  def append[A](a1: List[A], a2: List[A]): List[A] =
    a1 match {
      case Nil => a2
      case Cons(h,t) => Cons(h, append(t, a2))
    }

  def foldRight[A,B](as: List[A], z: B)(f: (A, B) => B): B = // Utility functions
    as match {
      case Nil => z
      case Cons(h, t) => f(h, foldRight(t, z)(f))
    }

  def foldLeft[A,B](l: List[A], z: B)(f: (B, A) => B): B =
    l match {
      case Nil => z
      case Cons(h, t) => foldLeft(t, f(z,h))(f)
    }

  def sum2(ns: List[Int]) = 
    foldRight(ns, 0)((x,y) => x + y)
  
  def product2(ns: List[Double]) = 
    foldRight(ns, 1.0)(_ * _) // `_ * _` is more concise notation for `(x,y) => x * y`; see sidebar


  def tail[A](l: List[A]): List[A] = l match {
    case Nil => sys.error("tail of empty list")
    case Cons(_, t) => t
  }

  def setHead[A](l: List[A], h: A): List[A] = l match {
    case Nil => sys.error("setHead on empty list")
    case Cons(_, t) => Cons(h, t)
  }

  def drop[A](l: List[A], n: Int): List[A] = 
    if (n <= 0) l
    else l match {
      case Nil => Nil
      case Cons(_, t) => drop(t, n-1)
    }

  def dropWhile[A](l: List[A], f: A => Boolean): List[A] = 
    l match {
      case Cons(h, t) if f(h) => dropWhile(t, f)
      case _ => l
  }

  def init[A](l: List[A]): List[A] =  {
    @annotation.tailrec
    def go(l: List[A], acc:List[A]):List[A] = l match {
      case Nil => sys.error("init of empty list")
      case Cons(h,Nil) => acc
      case Cons(h,t) => go(t, append(acc, List(h)))
    }
    go(l, Nil)
  }

  def init2[A](l: List[A]): List[A] = l match {
    case Nil => sys.error("init of empty list")
    case Cons(_, Nil) => Nil
    case Cons(h, t) => Cons(h, init(t))
  }

  def init3[A](l: List[A]): List[A] = {
    import collection.mutable.ListBuffer
    val buf = new ListBuffer[A]
    @annotation.tailrec
    def go(cur: List[A]): List[A] = cur match {
      case Nil => sys.error("init of empty list")
      case Cons(_,Nil) => List(buf.toList: _*)
      case Cons(h,t) => buf += h; go(t)
    }
    go(l)
  }

  def sumLeft(l: List[Int]):Int =
    foldLeft(l, 0)(_ + _)

  def productLeft(l: List[Double]): Double =
    foldLeft(l, 1.0)(_ * _)

  def lengthLeft[A](l: List[A]): Int =
    foldLeft(l, 0)((z, h) => z + 1)

  def reverse[A](l: List[A]):List[A]  =
    foldLeft(l, List[A]())((z,h) => Cons(h,z))

  def appendViaFoldRight[A](l: List[A], r: List[A]): List[A] =
    foldRight(l, r)(Cons(_,_))

  def length[A](l: List[A]): Int =
    foldRight(l, 0)((h, z) => z + 1) //Must be b + 1 and not a + 1 based on foldRight def

  def concat[A](l: List[List[A]]): List[A] =
    foldRight(l, Nil:List[A])((x,y) => append(x,y))

  def add1(l:List[Double]):List[Double] =
    foldRight(l, Nil:List[Double])((h,t) => Cons(h+1.0, t))

  def doubleToString(l:List[Double]):List[String] =
    foldRight(l, Nil:List[String])((h,t) => Cons(h.toString, t))

  def map[A,B](l: List[A])(f: A => B): List[B] =
    foldRight(l, Nil:List[B])((h,t) => Cons(f(h), t))

  def filter[A](as: List[A])(f: A => Boolean): List[A] =
    foldRight(as, Nil:List[A])((h,t) => if (f(h)) Cons(h,t) else t)

  def flatMap[A, B](as: List[A])(f: A => List[B]): List[B] =
    foldRight(as, Nil:List[B])((h,t) => concat(map(as)(f)))

  def filterViaFlatMap[A](as: List[A])(f: A => Boolean): List[A] =
    flatMap(as)(a => if(f(a)) List(a) else Nil)

  def addCorrespondingList(a: List[Int], b:List[Int]):List[Int] = (a,b) match {
    case (Nil, _) => Nil
    case (_, Nil) => Nil
    case (Cons(h1, t1), Cons(h2, t2)) => Cons((h1 + h2), addCorrespondingList(t1, t2))
  }

  def zipWith[A, B, C](a: List[A], b:List[B])(f:(A,B) => C): List[C] = (a,b) match {
    case (Nil, _) => Nil
    case (_, Nil) => Nil
    case (Cons(h1, t1), Cons(h2, t2)) => Cons(f(h1, h2), zipWith(t1, t2)(f))
  }

  def foldTail[A, B](l:List[A], z:B)(f: (B, A) => B): B =
   l match {
    case Nil => z
    case Cons(h,t) => foldTail(t, f(z, h))(f)
   }

  def foldNotTail[A, B](l:List[A], z:B)(f: (A, B) => B): B =
    l match {
      case Nil => z
      case Cons(h,t) =>  f(h, foldNotTail(t, z)(f))
    }

  def factorialTail(n:Int):Int = {  
    def go(n:Int, acc:Int):Int = {
      if (n <= 0) acc
      else go(n-1, acc*n) 
    }
    go(n, 1)
  }
}



