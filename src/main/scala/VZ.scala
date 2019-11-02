import com.mypro.spark.stream.model.DataBean
import org.apache.spark.sql.catalyst.analysis.UnresolvedAttribute
import org.apache.spark.unsafe.types.CalendarInterval

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * @author fgzhong
  * @since 2019/3/29
  */
object VZ {

  def main(args: Array[String]): Unit = {
    val bean1 = new DataBean
    val bean2 = new DataBean
//    println(bean1 semanticEquals bean2)
    println(UnresolvedAttribute("timestamp").getClass)
    println(max(y = 1, x= 1))
    mutable.HashMap
    ;
  }

  def max(x:Int, y:Int) : Int = {
    if (x > y)
      x
    else
      y
  }

  def defaultParamter(x:String = "PK") = {
    println(x)
  }

  def sum(x:Int*):Int = {
    var r = 0;
    for (x1 <- x) {
      r = r+x1;
    }
    r
  }

}


class A(val a1:String, val a2:String) {
  var name:String = _

  val arr = new Array[Int](5)
  val arrb = Array("1","2")
  val arrc = ArrayBuffer[Int]()
  private [this] val n2 = "n2";
  val l = 1::Nil
  val add = (x:Int,y:Int) => x+1
  def add1 = (x:Int) => x+1
  def add2(x:Int)(y:Int) = x+y
  def add3(x:Int) = (y:Int) => x + y
  def mulBy(factor:Double) = (x:Double) => factor * x
  val tripe = mulBy(3)
  val half = mulBy(0.5)
  println(tripe(14) + " " + half(14))
  def add4(x:Int)(y:Int) = x+y
  def add5(x:Int) = (y:Int) => x+y
  implicit def int2Fraction(n:Int) = add(n,1)
  val result = 3 * add(4,5)
  def this(a1:String,a2:String,a3:String) {
    this(a1,a2)
  }
}

class B(a1:String, a2:String) extends A(a1 ,a2) {

  override def toString: String = super.toString

}

abstract class C {}

class D{}
object D{}

case class E(name:String) {}

//class F extends Atriat with Btrait with ...