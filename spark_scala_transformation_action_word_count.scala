// Databricks notebook source
val text_file = sc.textFile("/FileStore/tables/spark_info.txt")

// COMMAND ----------

text_file.count()

// COMMAND ----------

text_file.getNumPartitions

// COMMAND ----------


text_file.glom().collect

// COMMAND ----------

text_file.first()

// COMMAND ----------

val s_line = text_file.filter(line => line.contains("Scala"))

// COMMAND ----------

s_line.collect()

// COMMAND ----------

val map_word= text_file.map(line=>line.split(" "))

// COMMAND ----------

map_word.collect()

// COMMAND ----------

val flatmap = text_file.flatMap(line => line.split(" "))
flatmap.collect()

// COMMAND ----------

val word_d=flatmap.filter(word=>word.length>3)
word_d.collect()


// COMMAND ----------

val wo=word_d.map(word=>(word,1))
val wor = wo.reduceByKey((x,y) => x + y)
// show the word length less the 
wor.collect()

// COMMAND ----------

val word_len_more2=flatmap.filter(word=>word.length>2)
                   .map(word=>(word,1)).reduceByKey((x,y)=>x+y)
word_len_more2.collect()

// COMMAND ----------

// apply line word count in iris data set
val iris_file= sc.textFile("/FileStore/tables/iris.txt")

// COMMAND ----------

val word_count_gr3=iris_file.flatMap(line=>line.split(","))
                   .filter(word=>word.length>3)
                   .map(word=>(word,1))
                   .reduceByKey((x,y)=>x+y)
word_count_gr3.collect()

// COMMAND ----------

val word_count_gr2=iris_file.flatMap(line=>line.split(","))
                   .filter(word=>word.length>2)
                   .map(word=>(word,1))
                   .reduceByKey((x,y)=>x+y)
word_count_gr2.collect()

// COMMAND ----------

// 1.what is the difference between map and flatmap. 
// here is real time example of map and flatmap
val mapf=text_file.map(line=>line.split(" "))
mapf.collect()

// if we look at below given example means its clear that map which take each and every line as list and store so if
// the file contains four line its conver ted that in four list

// COMMAND ----------

val mapflt=text_file.flatMap(line=>line.split(" "))
mapflt.collect()
// if we look at bewlow given exaple its clear that file is conversted store in one list

// COMMAND ----------

// what is the difference between reduceByKey and %md groupByKey.
//groupByKey() is just to group your dataset based on a key. It will result in data shuffling when RDD is not already partitioned.
//reduceByKey() is something like grouping + aggregation. We can say reduceBykey() equvelent to dataset.group(...).reduce(...). 
//It will shuffle less data unlike groupByKey().

//Although both of us will fetch the same results, there is a significant difference in the performance of both the functions. reduceByKey() works better with larger datasets when compared to groupByKey().

//In reduceByKey(), pairs on the same machine with the same key are combined (by using the function passed into reduceByKey()) before the data is shuffled. Then the function is called again to reduce all the values from each partition to produce one final result.

// COMMAND ----------

// Give example for each transformation and action

// COMMAND ----------

//transformtion

val tex=sc.textFile("/FileStore/tables/spark_info.txt")

// is tranformation function we assigned the textfile content to tex

// COMMAND ----------

// action which able to view result
val wordsp=tex.flatMap(line=>line.split(" "))
// action method which are ided to display the result
wordsp.collect()

// COMMAND ----------

//Coalesce works well for taking an RDD with a lot of partitions and combining partitions on a single 
//worker node to produce a final RDD with less partitions.

//Repartition will reshuffle the data in your RDD to produce the final number of partitions you request.

// COMMAND ----------

// Mow many transformations and action spark has ?Define each of them with example
//The map function iterates over every line in RDD and split into new RDD. Using map() transformation we take in any function, and that function is applied to every element of RDD.

val tf = sc.textFile("/FileStore/tables/spark_info.txt")

// COMMAND ----------

tf.collect()

// COMMAND ----------

// display the number of charater present in line
val map = tf.map(line => (line,line.length))
map.collect()

// COMMAND ----------

//With the help of flatMap() function, to each input element, we have many elements in an output RDD. The most simple use of flatMap() is to split each input string into words.

//Map and flatMap are similar in the way that they take a line from input RDD and apply a function on that line. The key difference between map() and flatMap() is map() returns only one element, while flatMap() can return a list of elements.

val fmap = tf.flatMap(line => line.split(" "))
fmap.collect()

// COMMAND ----------

// Spark RDD filter() function returns a new RDD, containing only the elements that meet a predicate. It is a narrow operation because it does not shuffle data from one partition to many partitions.

val mapFile = tf.flatMap(lines => lines.split(" ")).filter(value => value=="Apache")
mapFile.count()

// COMMAND ----------

// union() function, we get the elements of both the RDD in new RDD. The key rule of this function is that the two RDDs should be of the same type.

val rdd1 = sc.parallelize(Seq(("vinoth",3),("harish",4)))
val rdd2 = sc.parallelize(Seq(("varun",6),("kamal",5)))
val rdd3 = sc.parallelize(Seq(("vikky",6),("kamal",5)))
val rddUnion = rdd1.union(rdd2).union(rdd3)
rddUnion.collect()

// COMMAND ----------

// intersection() function, we get only the common element of both the RDD in new RDD. The key rule of this function is that the two RDDs should be of the same type.
val rrdin= rdd2.intersection(rdd3)
rrdin.collect()

// COMMAND ----------

 //the distinct elements of the source dataset. It is helpful to remove duplicate data.
val d3=rddUnion.distinct()
d3.collect()

// COMMAND ----------

//  groupByKey() on a dataset of (K, V) pairs, the data is shuffled according to the key value K in another RDD. In this transformation, lots of unnecessary data get to transfer over the network.

val group = wor.groupByKey()
group.collect

// COMMAND ----------

// the sortByKey() function on a dataset of (K, V) pairs, the data is sorted according to the key K in another RDD

val sorted = wor.sortByKey()
sorted.collect

// COMMAND ----------

//The Join is database term. It combines the fields from two table using common values. join() operation in Spark is defined on pair-wise RDD. Pair-wise RDDs are RDD in which each element is in the form of tuples. Where the first element is key and the second element is the value.

val data = sc.parallelize(Array(('A',1),('b',2),('c',3)))
val data2 =sc.parallelize(Array(('A',4),('A',6),('b',7),('c',3),('c',8)))
val result = data.join(data2)
result.collect

// COMMAND ----------

// RDD Action
//Action count() returns the number of elements in RDD.

result.count()

// COMMAND ----------

//The action take(n) returns n number of elements from RDD. It tries to cut the number of partition it accesses
result.take(2)

// COMMAND ----------

//The countByValue() returns, many times each element occur in RDD.

wor.countByValue()

// COMMAND ----------

val rdd1 = sc.parallelize(List(("maths", 80),("science", 90)))
val additionalMarks = ("extra", 4)
val sum = rdd1.fold(additionalMarks){ (acc, marks) => val add = acc._2 + marks._2
("total", add)
}
sum

// COMMAND ----------

//foreach() function is useful. For example, inserting a record into the database.
val data=sc.parallelize(Array(('k',5),('s',3),('s',4),('p',7),('p',5),('t',8),('k',6)),3)
val group = data.groupByKey().collect()
group.foreach(println)

// COMMAND ----------


