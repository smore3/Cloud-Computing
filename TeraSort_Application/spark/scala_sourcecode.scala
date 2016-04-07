val conf = new SparkConf().setAppName("Sort")
val sc = new SparkContext(conf)

val lines = sc.textFile("hdfs://ec2-52-91-139-197.compute-1.amazonaws.com:9000/input")
val split = lines.map(x => (x.slice(0,10),x.slice(10,99)))
val sort = split.sortByKey()
sort.map(k => k._1+k._2).saveAsTextFile("hdfs://ec2-52-91-139-197.compute-1.amazonaws.com:9000/output")
