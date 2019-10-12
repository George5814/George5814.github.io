---
layout:  post
title: java源码学习之Integer
category: 技术
tags: Java
keywords: 
description: 阅读源码分析Integer类的实现

---

## 继承体系
继承自：

java.lang.Number，主要是定义各个数值类型间的转换方法

实现接口：

java.io.Serializable

java.lang.Comparable，所有数值类型都实现该接口，可对值进行比较

## 内部数据结构
基本类型： int

final 修饰的常量值。

## 常量值设置
最大值: 231-1

最小值：-231 

表示 Bit 的位数size： 32

表示的字节数：BYTES=4 个字节

## 核心方法和类
### 类 IntegerCache
**IntegerCache会在主动访问时初始化常用的int 值并将集合缓存起来。如果通过 valueOf 方法的值命令该缓存，将返回缓存的对象而不是新建对象。**

```java
/**
     * Returns an {@code Integer} instance representing the specified
     * {@code int} value.  If a new {@code Integer} instance is not
     * required, this method should generally be used in preference to
     * the constructor {@link #Integer(int)}, as this method is likely
     * to yield significantly better space and time performance by
     * caching frequently requested values.
     *
     * This method will always cache values in the range -128 to 127,
     * inclusive, and may cache other values outside of this range.
     *
     * @param  i an {@code int} value.
     * @return an {@code Integer} instance representing {@code i}.
     * @since  1.5
     */
    public static Integer valueOf(int i) {
        if (i >= IntegerCache.low && i <= IntegerCache.high)
            return IntegerCache.cache[i + (-IntegerCache.low)];
        return new Integer(i);
    }
```

**IntegerCache缓存的上线可以通过参数 -XX:AutoBoxCacheMax=<size> 在jvm 启动时设置，并存储在系统属性 java.lang.Integer.IntegerCache.high 中，该值会调整IntegerCache的 high 为比 127 更大的值，缓存更多的对象。**

### 方法

1. toString
   1. toString方法中会用到stringSize。 因为需要计算 int 的字符数量来初始化 char 数组，其中很巧妙的通过 sizeTable 的方式简化了对 int 的字符数量的判断（🐂）。只要判断获取到sizeTable的下标就可以得到字符数量了。对于负数，会转为正数比较，并对获得的字符数量结果+1。

      ```java
      final static int [] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999,
                                          99999999, 999999999, Integer.MAX_VALUE };

      // Requires positive x
      static int stringSize(int x) {
            for (int i=0; ; i++)
                  if (x <= sizeTable[i])
                  return i+1;
      }
      ```

   2. 通过 getChars 方法，依次从个位往前取出字符并找到对应的数字
    
      对于给定值，如果大于 65536，先取两位做按位取数运算，很神奇的 乘以 100 操作

      ```java
      // Generate two digits per iteration
        while (i >= 65536) {
            q = i / 100;
        // really: r = i - (q * 100);
            r = i - ((q << 6) + (q << 5) + (q << 2));
            i = q;
            buf [--charPos] = DigitOnes[r];
            buf [--charPos] = DigitTens[r];
        }
      ```
      
      i:                  11110001001000000    => 123456
      
      q:                 10011010010                  =>  1234
      
      q << 6：     10011010010000000    =>  78976 
      
      q << 5：       1001101001000000    =>  39488
      
      q << 2：              1001101001000    =>  4936
      
   3. 对于小于 65536 的数，进行神奇的乘以 10 的运算
    
      ```java
      // Fall thru to fast mode for smaller numbers
        // assert(i <= 65536, i);
        for (;;) {
            q = (i * 52429) >>> (16+3);
            r = i - ((q << 3) + (q << 1));  // r = i-(q*10) ...
            buf [--charPos] = digits [r];
            i = q;
            if (i == 0) break;
        }
        if (sign != 0) {
            buf [--charPos] = sign;
        }
      ```

   4. 最后的 sign 用来表示符号。
    
    最后将数组 buf 作为参数，new String 即得到字符串。

2. hashCode

    Integer 的 hashCode 是其值本身

3. equals

    先判断参数a 是否为 Integer 类型
    
    再判断两个比较对象的value 是否相等。

4. toUnsignedString0

    toUnsignedString0为进制转换的基础方法，支持转换为 二进制，八进制，十六进制。其他如toHexString，toOctalString，toBinaryString等方法都是对toUnsignedString0的使用。
    
    通过numberOfLeadingZeros方法计算数据转为 01 字符后的前导零的数量。int 为 32位。如果数据为 0，则前导零数量为 32

    调用formatUnsignedInt方法通过按位与操作获取到每个位置的值，并将之存入字符数组中

    最后已字符数组为参数创建新的字符串对象。

5. parseInt

    进制允许从二进制到 36 进制。默认是以 10 进制计算。36 进制中数字表示是从 0 到 z。9 个数字加上 26 个英文字母
    
    判断第一个字符小于 0 的情况下是不是正负号及长度大于 1，否则会抛出 NFE，NumberFormatException

## 继承方法
1. byteValue

    (byte)value

1. shortValue

    (short)value;

1. intValue

    value

1. longValue

    (long)value;

1. floatValue

    (float)value;

1. doubleValue

    (double)value;

1. compareTo

    因为对传入的参数没有限制和检查，如果传入参数为 null，会导致 NPE

    ```java
      /**
        * Compares two {@code Integer} objects numerically.
        *
        * @param   anotherInteger   the {@code Integer} to be compared.
        * @return  the value {@code 0} if this {@code Integer} is
        *          equal to the argument {@code Integer}; a value less than
        *          {@code 0} if this {@code Integer} is numerically less
        *          than the argument {@code Integer}; and a value greater
        *          than {@code 0} if this {@code Integer} is numerically
        *           greater than the argument {@code Integer} (signed
        *           comparison).
        * @since   1.2
        */
       public int compareTo(Integer anotherInteger) {
           return compare(this.value, anotherInteger.value);
       }
    ```

    正常情况下对两个 int 值进行比较。

 

## 工具方法

1. toUnsignedString

    通过与 0xffffffffL 做与操作，将 Int 转为无符号的 long 值。

    ```java
    /**
     * Returns a string representation of the first argument as an
     * unsigned integer value in the radix specified by the second
     * argument.
     *
     * <p>If the radix is smaller than {@code Character.MIN_RADIX}
     * or larger than {@code Character.MAX_RADIX}, then the radix
     * {@code 10} is used instead.
     *
     * <p>Note that since the first argument is treated as an unsigned
     * value, no leading sign character is printed.
     *
     * <p>If the magnitude is zero, it is represented by a single zero
     * character {@code '0'} ({@code '\u005Cu0030'}); otherwise,
     * the first character of the representation of the magnitude will
     * not be the zero character.
     *
     * <p>The behavior of radixes and the characters used as digits
     * are the same as {@link #toString(int, int) toString}.
     *
     * @param   i       an integer to be converted to an unsigned string.
     * @param   radix   the radix to use in the string representation.
     * @return  an unsigned string representation of the argument in the specified radix.
     * @see     #toString(int, int)
     * @since 1.8
     */
    public static String toUnsignedString(int i, int radix) {
        return Long.toUnsignedString(toUnsignedLong(i), radix);
    }

    ```
    通过 Long.toString将数字转为字符串

1. toHexString

1. toUnsignedString0(value,4)

1. toOctalString

1. toUnsignedString0(value,3)

1. toBinaryString

1. toUnsignedString0(value,1)

1. parseUnsignedInt

1. parseInt

1. valueOf

1. Integer.valueOf(parseInt);

    valueOf 和 parseInt的不同，parseInt会返回解析出的 int 值，是一个新值。valueOf 会使用parseInt 解析的值先在 IntegerCache 中查找缓存值，早不到然后再创建新的 Integer 对象。

1. getInteger

    参数为系统属性名称

    获取指定属性名称的值的数字值转换，值存在则返回值，否则返回 null

1. decode

## 相关问题

如何写一个方法交换两个 Integer 类型的值？

**考察点**

1. 值传递和引用传递。值传递不会改变原变量的值，值会复制一份给新的参数变量。引用传递会同时改变原变量的值。

    Integer 的内部变量 value 是 final 类型的，因此一旦被赋值，不可以被改变。

1. 反射。通过反射，改变变量的访问限制 ，并修改值。

    自动装箱和拆箱，IntegerCache 问题。避免自动拆箱。使用 Field.setInt 等具体类型的 set 方法。

    参考：https://blog.csdn.net/ZBylant/article/details/87875140

1. 自动装箱和拆箱操作

    自动装箱和拆箱是语言层面支持的特性，在 Java 代码编译时会自动判断是否需要进行 int 和 Integer 的转换操作，然后将直接赋值的代码进行装箱和拆箱操作。

通过反编译 Class 文件可以发现，编译器通过 Integer.valueOf() 方法对 Integer a = 12中的 12 数值进行装箱。通过 Integer.intValue() 对 int b= a 中的 a 变量进行拆箱
