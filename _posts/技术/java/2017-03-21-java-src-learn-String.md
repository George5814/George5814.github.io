---
layout:  post
title: java源码学习之String
category: 技术
tags: Java
keywords: 
description: 阅读源码分析String类的实现

---

## String类实现

`String`类实现了`Serializable`、`Comparable`和`CharSequence`接口。

### String类实现特性

1. `String`为常量，在`String`对象被创建后值就不能再变化；
1. Java程序中所有的字面量值都被实现为`String`类的实例；
1. String的缓冲区是支持多变的，但String对象是不可变的；
1. `String`类包含了检查单个字符序列、比较字符串、搜索字符串、提取子串、创建字符串所有字符的全大写和全小写副本的方法；
1. 大小写映射基于`Character`类中指定的Unicode标准版本；
1. Java语言提供了特殊的支持string字符串连接的操作（+），并将其他对象转为字符串。同时String的连接是通过`StringBuilder`或者`StringBuffer`及其方法实现的；而转换操作是通过`toString`方法，在`Object`中定义，并且被所有的类继承
1. String表示UTF-16格式化的字符串(A String represents a string in the UTF-16 format in which supplementary characters are represented by surrogate pairs)，索引值参照字符码单元，因此补充的字符在String中使用了两个位置；
1. String提供了处理Unicode代码点的方法，另外处理Unicode的码单元。

### String实现原理

存储：每个String对象的value存储在final修饰的`char`数组中。

实例化：无参构造方法会创建空字符串`""`的对象。因为String是不可变的，因此可以不必使用String的构造方法创建对象，可以使用如`String str = "abc"`方式。而且对于String的所有构造方法，虽然代码中可能没有的源值进行非空检查，但编译器在编译时会对其检查，如果判断为`null`，会直接抛出`NullPointerException`异常。


### 相关方法内部实现说明

- `public String(char value[])`

      内部通过`Arrays.copyof()`方法将char数组的值复制给新的String对象，对原来char数组值的修改不会影响该新String对象。

- `public String(char value[], int offset, int count)`

      先检查offset和count，对于这两个值小于0的会抛出`StringIndexOutOfBoundsException`异常；
      在offset不小于0，count为0时，会为新对象赋`""`值;
      在`offset+count>value.length`时也会抛出`StringIndexOutOfBoundsException`异常；
      在以上都通过后，会调用`Arrays.copyOfRange()`从value中复制值并赋给新对象的value。

- `public String(byte bytes[], int offset, int length, String charsetName)`  

      先检查获取范围是否合法，然后通过调用"StringCoding.decode()"将自定位置范围的字节码转为对应的ASCII的字符，并设置为指定的编码。


- `public char charAt(int index)`

      参数为char数组的下标索引，返回的为数组指定索引位置的某个字符（包括中文字符）。

- `public int codePointAt(int index)`

      返回索引所在位置的字符对应的Unicode码值(十进制)

- `public int codePointBefore(int index)`

      返回索引所在前一个位置的字符的Unicode码值(十进制)

- `public int codePointCount(int beginIndex, int endIndex)`




- `public int compareTo(String anotherString)`

      先判断有没有空字符串，没有空字串符则比较每个字符，否则比较两个串长度。

- `public int compareToIgnoreCase(String str)`

      会调用String内部静态final类`CASE_INSENSITIVE_ORDER`的`compare（s1,s2）`方法。该方法会循环比较s1和s2对应字符A和B，A和B不相等时，都转为大写比较，还不相等则都转为小写比较，如果还不相等，直接返回A和B的ASCII值的差；如果所有的字符都相等则比较字符串s1和s2长度。


- `public String concat(String str)`

      连接字符串。字符串长度为0，直接返回当前字符串s1；否则用`Arrays.copyOf()`对s1进行扩容，然后将str的字符全都复制到中间变量buf字符数组中，然后使用buf创建新的字符串并返回；


- `public boolean contains(CharSequence s)`

      内部是对`public int indexOf(String str)`是否大于-1的判断，如果大于-1则判定为存在该子字符串。具体实现在`public int indexOf(String str)`中阐述。


- `public boolean contentEquals(CharSequence cs)`

      对于AbstractStringBuilder的类型，根据cs是否为`StringBuffer`判断是否同步调用`private boolean nonSyncContentEquals(AbstractStringBuilder sb)`;
      如果是String类型，调用String的`equal()`方法，否则通过比对两个字符序列的长度和每个字符是否相等判断两个字符序列是否相等。


- `public default IntStream chars()`

      JDK1.8中新添加的接口默认方法，返回字符的IntStream。该IntStream支持Lambda表达式，而且该`chars()`方法是在`CharSequence`中添加的，因此所有实现了该接口的类(包括`String`、`StringBuilder`、`StringBuffer`)都可以使用jdk8的函数式编程特性了，通过该方法就可以应用JAVA8的特性了。

```java
String str="123456";
IntStream s=str.chars();
s.forEach(ch -> {
      System.out.println(Character.toChars(ch));
});

//亦可
str.chars().mapToObj(ch -> Character.toChars(ch)).forEach(System.out::println);
```

遍历出来的为每个字符的Unicode代理码位置，但可以通过`Character.toChars(ch)`方法将Unicode位置码转为对应的字符。


- `public default IntStream codePoints()`

      也是JDK1.8 中新加默认方法，用法与`chars()`类似。


- `public boolean equals(Object s2)`

      这应该是String类型最常见的方法了。该方法的判断逻辑如下：如果传入的s2==this,认为是同一个对象直接返回true;如果是String类型，先比较长度然后比较每个字符，只有在长度和字符都相等时返回true，其他情况返回false。


- `public boolean endsWith(String suffix)`

      很奇怪吧，内部调用的`public boolean startsWith(String prefix, int toffset)`,通过设置起始匹配位置实现匹配后缀的操作。


- `public boolean equalsIgnoreCase(String s2)`

      先判断是否为当前对象，然后判断对象s2长度，并通过`public boolean regionMatches(boolean ignoreCase, int toffset,String other, int ooffset, int len)`方法判断在忽略大小写情况下两个对象是否相等。


- `public byte[] getBytes()`

      内部调用`StringCoding.encode(value, 0, value.length)`方法实现对字符串编码。


- `public byte[] getBytes(Charset charset)`

      内部调用`StringCoding.encode(charset, value, 0, value.length)`实现


- `public byte[] getBytes(String charsetName)`

      内部调用`StringCoding.encode(charsetName, value, 0, value.length)`实现,声明了`UnsupportedEncodingException`异常


- `public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin)`

      内部校验检查后调用`System.arraycopy(value, srcBegin, dst, dstBegin, srcEnd - srcBegin)`将当前字符串的内容复制到指定的字符数组中。


- `public int hashCode()`

      计算公式为:`s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]`,s为字符数组，n为字符数组长度


- `public int indexOf(int ch, int fromIndex)`

      从指定formIndex向后查找第一个ch字符出现的位置；
      判断fromIndex小于0，将formIndex设置为0；
      formIndex大于字符串长度，返回-1；
      判断字符code point如果小于最小代理code point,然后从字符串中查找第一个匹配的位置并返回；
      否则调用`indexOfSupplementary(ch, fromIndex)`内部方法通过对高代理和低代理计算进行判断；

      高代理和低代理的概念是因为JDK在早期使用UTF-16对字符编码，但因字符数后来超过UTF-16能容纳范围，又兼容16位内存效率高的优点，Unicode引入新的设计方法！具体参考<http://www.ibm.com/developerworks/cn/java/j-unicode/>



- `public int indexOf(String str, int fromIndex)`

      自定字符串在当前子串的开始位置；内部调用`indexOf(char[] source, int sourceOffset, int sourceCount,char[] target, int targetOffset, int targetCount, int fromIndex) `；主要实现逻辑如下：for循环遍历中先查找target的第一个元素，如果没有找到则一直循环直到找到或结束，当第一个元素在范围内，则for循环匹配其后的元素是否完全匹配，如果完全匹配直接返回位置(index-sourceOffset)；否则继续循环找下一个匹配第一个目标元素的位置重复上面的操作。


- `public native String intern()`

      调用原生方法；返回String对象的标准表示法，会初始化一个空的被String类内部维持的String池，也就是String支持字面量的原因。调用该方法会使得对象去池中查找是否存在与当前对象相等(通过`equal()`)的string,如果存在则返回该string，否则将当前的String对象添加到String对象池中，并返回其引用.

      **注意**如果s.equal(t)相等，那么s.intern()和t.intern()也一定相等，因为返回的是String对象池中同一个对象的引用。


- `public boolean isEmpty()`

      判断内部存储char数组的长度是否为0。

- `public static String join(CharSequence delimiter, CharSequence... elements)`

      JDK1.8中新添加方法,使用指定分隔符delimiter连接多个字符序列元素。
      内部使用了`StringJoiner`循环调用`add()`方法调用。

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4);
String commaSeparatedNumbers = numbers.stream()
   .map(i -> i.toString())
   .collect(Collectors.joining(", "));
```


- `public int length()`

      内部存储char数组的长度；


- `public int lastIndexOf(int ch, int fromIndex)`


      如果ch小于最小的增补码位置，则从后向前遍历，直到找到第一个匹配的字符，并返回所在位置。
      否则，调用`lastIndexOfSupplementary(ch, fromIndex)`方法。


- `public int lastIndexOf(String str, int fromIndex)`

      内部调用`static int lastIndexOf(char[] source, int sourceOffset, int sourceCount, char[] target, int targetOffset, int targetCount,int fromIndex)`,
      如果fromIndex小于0，直接返回-1；如果fromIndex大于(rightIndex=sourceCount-targetCount)，fromIndexw设置为rightIndex;如果targetCount等于0，直接返回fromIndex;

      核心：
      预置条件：最后一个字符strLastChar，向前查找的字符限制位置min，开始匹配的字符位置(i=min+fromIndex)，
      获取到最后一个字符strLastChar，然后从后向前匹配当前字符是否与strLastChar相等，不相等则一直向前遍历；否则通过内部while循环遍历其他的字符是否匹配，如果不匹配则使用goto语句跳转到外部while从新的位置再次执行查找与strLastChar匹配的字符。重复上面的操作，直到完全匹配返回第一个字符的位置或者没有匹配到返回-1。

```java
//核心代码
int strLastIndex = targetOffset + targetCount - 1;
char strLastChar = target[strLastIndex];
int min = sourceOffset + targetCount - 1;
int i = min + fromIndex;

startSearchForLastChar:
      while (true) {
            while (i >= min && source[i] != strLastChar) {
                  i--;
            }
            if (i < min) {
                  return -1;
            }
            int j = i - 1;
            int start = j - (targetCount - 1);
            int k = strLastIndex - 1;

            while (j > start) {
                  if (source[j--] != target[k--]) {
                        i--;
                        continue startSearchForLastChar;
                  }
            }
      return start - sourceOffset + 1;
}
```


- `public boolean matches(String regex)`

      内部调用`Pattern.matches(regex, this)`查找字符串中是否有符合正则表达式的内容。


- `public boolean regionMatches(boolean ignoreCase, int toffset, String other, int ooffset, int len)`

      while循环匹配每个字符，先对原字符进行相等判断，不相等时都转为大写进行判断，还不相等时都转为小写判断，如果还不相等直接返回false，否则遍历other字符串的其他字符。如果所有字符都匹配成功则返回true。


- `public String[] split(String regex, int limit)`

      内部实现使用的正则表达式。


- `public boolean startsWith(String prefix, int toffset)`

      从指定位置开始匹配每个字符，只要发现有字符不匹配立即返回false。


- `public String substring(int beginIndex, int endIndex)`

      如果beginIndex=0,endIndex=value.length,直接返回this，因为字符串不需要截取；否则调用String构造方法创建新的对象。


- `public CharSequence subSequence(int beginIndex, int endIndex)`

      调用`substring(int beginIndex, int endIndex)`方法。

- `public String toLowerCase()`

      内部调用`public String toLowerCase(Locale locale)`,通过unicode的code point方式转换，并最终生成新的String对象返回。

- `public String toString()`

      返回this。

- `public String toUpperCase()`

      内部调用`public String toUpperCase(Locale locale)`,通过unicode的code point方式转换，并最终生成新的String对象返回。

- `public char[] toCharArray() `

      调用`System.arraycopy()`生成新的char数组。

- `public String trim()`

      检查字符串前后各部位空字符的位置，以该两个位置为区间，调用`subString()`创建新的String对象；或者前后没有空字符，直接返回this。

- `private int lastIndexOfSupplementary(int ch, int fromIndex)`


## 特别声明

- 如果通过构造方法或者其他方法通过参数`null`创建String对象将会报`NullPointerException`异常。

```java
String str=new String((String)null);
```


## 参考

> JDk1.8中String类
>
>dd
