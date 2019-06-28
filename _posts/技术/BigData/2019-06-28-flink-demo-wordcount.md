---
layout: post
title:  flink 入门程序-wordcount
category: 技术
tags: BigData
keywords: 
description: 
---
 
{:toc}

### word count 代码

```java
public class WordCount {

    private static final Logger log = LoggerFactory.getLogger(WordCount.class);

    public static final String ZK_HOSTS = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    /**
     * main.
     */
    public static void main(String[] args) throws Exception {

        /***===========--------解析参数--------==================*/
        ParameterTool tool = ParameterTool.fromArgs(args);
        String brokers = tool.get("brokers");
        String topic = tool.get("topic");
        Properties properties = new Properties();
        String brokerServerList = brokers ;//"192.168.3.8:9092";
        String firstTopic = topic; //"beam-on-flink";
        String secondTopic = "beam-on-flink-res";
        properties.setProperty("bootstrap.servers", brokerServerList);
        properties.setProperty("group.id", "consumer-flink");
        properties.setProperty("zookeeper.connect",ZK_HOSTS);

        /***===========--------执行环境--------==================*/
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(1000);


        /***===========--------设置数据源--------==================*/
        FlinkKafkaConsumer011<String> flinkKafkaConsumer011 = new FlinkKafkaConsumer011<>(firstTopic, new SimpleStringSchema(), properties);
        DataStreamSource<String> source = env.addSource(flinkKafkaConsumer011);


        /***===========--------transform--------==================*/
        DataStream<WC> flatMap = source.flatMap(new Splitter()).uid("2. split data");
        KeyedStream<WC, Tuple> keyBy = flatMap.keyBy(0);
        WindowedStream<WC, Tuple, TimeWindow> window = keyBy.timeWindow(Time.seconds(10));
        DataStream<WC> dataStream = window.sum(1).uid("3. sum");

        DataStream<String> dateStreamRes = dataStream.map(WC::toString);

        /***===========--------sink to out--------==================*/
        //sink 到 kafka中
        sink2Kafka(brokerServerList, secondTopic, dateStreamRes);

        /***===========--------execute--------==================*/
        env.execute("Window WordCount");

    }

    private static void sink2Kafka(String brokerServerList, String secondTopic, DataStream<String> dateStreamRes) {
        FlinkKafkaProducer011<String> sink2Kafka = new FlinkKafkaProducer011<>(brokerServerList,secondTopic, new SimpleStringSchema());
        dateStreamRes.addSink(sink2Kafka);
    }

    public static class Splitter implements FlatMapFunction<String, WC> {
        @Override
        public void flatMap(String sentence, Collector<WC> out) {
            for (String word: sentence.split(" ")) {
                out.collect(new WC(word, 1));
                log.info("word:{},count:{}",word,1);
            }
        }

    }

    /**
     * 将Tuple 替换为 Pojo对象
     */
    public static class WC extends Tuple2<String,Integer> {

        /**  */
        private String word;

        /**  */
        private Integer count;

        public WC() {
            super();
        }

        public WC(String word, Integer count) {
            super(word, count);
            this.word = word;
            this.count = count;
        }

        public String getWord() {
            return getField(0);
        }

        public void setWord(String word) {
            this.word = word;
            setField(word,0);
        }

        public Integer getCount() {
            return getField(1);
        }

        public void setCount(Integer count) {
            this.count = count;
            setField(count,1);
        }

        public String toJsonString() {
            return JSON.toJSONString(this);
        }


        @Override
        public String toString() {
            return toJsonString();
        }
    }
}
```

### pom 内依赖引入

```xml
<dependencies>
    <dependency>
      <groupId>com.alibaba</groupId>
      <artifactId>fastjson</artifactId>
      <version>1.2.55</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-jdbc_2.12</artifactId>
      <version>1.7.0</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-connector-wikiedits_2.12</artifactId>
      <version>1.7.0</version>
    </dependency>
    dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-java</artifactId>
      <version>${flink.version}</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-clients_${scala.binary.version}</artifactId>
      <version>${flink.version}</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-scala_${scala.binary.version}</artifactId>
      <version>${flink.version}</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-streaming-java_${scala.binary.version}</artifactId>
      <version>${flink.version}</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-streaming-scala_${scala.binary.version}</artifactId>
      <version>${flink.version}</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-cep_${scala.binary.version}</artifactId>
      <version>${flink.version}</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-cep-scala_${scala.binary.version}</artifactId>
      <version>${flink.version}</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-gelly_${scala.binary.version}</artifactId>
      <version>${flink.version}</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-table_${scala.binary.version}</artifactId>
      <version>${flink.version}</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-gelly-scala_${scala.binary.version}</artifactId>
      <version>${flink.version}</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-gelly-examples_${scala.binary.version}</artifactId>
      <version>${flink.version}</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-connector-kafka-0.11_${scala.binary.version}</artifactId>
      <version>${flink.version}</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-connector-elasticsearch2_${scala.binary.version}</artifactId>
      <version>${flink.version}</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-test-utils-junit</artifactId>
      <version>${flink.version}</version>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-streaming-java_2.11</artifactId>
      <version>${flink.version}</version>
      <scope>test</scope>
      <type>test-jar</type>
    </dependency>

    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>mockito-all</artifactId>
      <version>1.10.19</version>
      <type>jar</type>
      <scope>test</scope>
    </dependency>

    <dependency>
      <groupId>org.apache.flink</groupId>
      <artifactId>flink-runtime_2.11</artifactId>
      <version>${flink.version}</version>
      <scope>test</scope>
      <type>test-jar</type>
    </dependency>

    <dependency>
      <groupId>joda-time</groupId>
      <artifactId>joda-time</artifactId>
      <version>2.7</version>
    </dependency>

    <dependency>
      <groupId>org.apache.commons</groupId>
      <artifactId>commons-math3</artifactId>
      <version>3.5</version>
    </dependency>

    <dependency>
      <groupId>org.influxdb</groupId>
      <artifactId>influxdb-java</artifactId>
      <version>2.3</version>
    </dependency>

    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>${junit.version}</version>
    </dependency>
  </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>2.4.1</version>
                <executions>
                    <!-- Run shade goal on package phase -->
                    <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```


### 编译代码

`mvn clean package`

### 上传代码到 flink 平台

![flink-web-submit](//raw.githubusercontent.com/George5814/blog-pic/master/image/flink-web-submit.png)

![flink-web-submit-2](//raw.githubusercontent.com/George5814/blog-pic/master/image/flink-web-submit-2.png)

### flink 平台执行监控

![flink-job-running](//raw.githubusercontent.com/George5814/blog-pic/master/image/flink/flink-job-running.png)







