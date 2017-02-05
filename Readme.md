# Collectors Utils

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.paumard/collectors-utils/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.paumard/collectors-utils)
[![Build Status](https://travis-ci.org/JosePaumard/collectors-utils.png?branch=master)](https://travis-ci.org/JosePaumard/collectors-utils) 
[![Coverage Status](https://coveralls.io/repos/github/JosePaumard/collectors-utils/badge.svg?branch=master)](https://coveralls.io/github/JosePaumard/collectors-utils?branch=master) 
[![Javadocs](http://javadoc.io/badge/org.paumard/collectors-utils.svg)](http://javadoc.io/doc/org.paumard/collectors-utils)

The CollectorsUtils API offers a family of collectors ready to be used in the Java 8 Stream API. On the model of the `Collectors` factory class available in the JDK, that offers about 40 collectors in about 12 categories, the `CollectorsUtils` factory class offers 28 collectors in 6 categories. 

You can use this API directly with Maven, by adding the following dependency.  

```
<dependency>
    <groupId>org.paumard</groupId>
    <artifactId>collectors-utils</artifactId>
    <version>1.0</version>
</dependency>
```

# Operations provided

There are currently 28 factory methods in the `CollectorsUtils` class, belonging to 10 categories. Another 7 factory methods are also available in `FunctionsUtils`, as helper methods for `CollectorsUtils`. 

## Find most frequent

There are two collectors in this category. Those collectors extract the most seen elements from a stream. It returns all the values in a set or in a stream. 
- `findMostFrequent()`: this collector returns the set of all the most seen values in a stream. If there are several most seen values, they are all present in the set. This set can be empty if the processed stream is empty.  
- `streamMostFrequent()`: this collector does the same as the previous one, but puts the result in a stream, allowing for further processing. This stream can be empty if the processed stream is empty.  

The returned elements are wrapped in entries, with the element itself as the key, and the number of times it appears in the stream as the value. 

## Grouping by and max by 

There are five collectors in this category. Those collectors extract a max from a stream, and return it wrapped in an optional, which is empty if the processed stream is empty. 
- `groupingByAndMaxBy()`: this collector wraps several operations in one. It does a `groupingBy()`using the provided key extractor and downstream collector. It then streams the resulting map into a stream of entries, and takes the max using the provided comparator. 
- `groupingByAndMaxByCounting()`: this collector is the same as the previous one with the `counting()` downstream collector and `comparingByValue()` comparator.  
- `groupingByAndMaxByValue()`: this collector is the same as the first one, but takes a comparator of values to extract the max.  
- `groupingBySelfAndMaxByValue()`: this collector is the same as the first one. It uses `identity()` as a key extractor, takes only a downstream collector that computes comparable values. In case the downstream collector is the `counting()` collector, this collector does the same as `findMostFrequent()`, but only returns the first max found, wrapped in a optional.  
- `maxByCounting()`: this collector is the same as the first one, with the identity function as a key extractor. 

For all these collectors, if there is more than on max, only one of them is returned. 

## Grouping by and all max by

There are three collectors in this category. Those collectors extract all the max values from a stream, and return them in a set, that can be empty if the processed stream is empty. If there are more than one value in the set, they are all equals according to the comparator used in that collector. 
- `groupingByAndAllMaxBy()`: this collector comes in two versions. The first one takes the key extractor, the downstream collector and the comparator as parameters. The second one also takes the resulting collection supplier. It does a `groupingBy()` using the key extractor and the downstream collector, streams the built map in a stream of entries and extract all the maxes according to the provided comparator of entries. The result is put in a set by default, or in the collection built using the supplier. This set or collection can be empty in the case the processed stream is empty.   
- `groupingByAndAllMaxByValue()`: this collector comes in three versions, and is basically the same as the previous one. The only difference is that it takes a comparator of values instead of a comparator of entries. 
- `groupingByAndStreamAllMaxByValue()`: this collector is the same as the other, but returns a stream of the values found, for further processing. 

## Grouping by and maxes by

There is one collector in this category. 
- `groupingByAndMaxesBy()`: this collector takes the usual set of parameters (key extractor, downstream collector, comparator). It also takes the number of elements that should be returned. It allows to select as many max elements as needed. This collector uses the `StreamUtils.filteringMaxValue()` utility stream, and follows the same rules. Especially, it is built on an implementation that works well when the number of kept maxes is small compared to the number of elements in the stream.  

## Map to stream
There is one collector in this category. They can be useful as downstream collectors, to create map with stream as values for example.  
- `mapToStream()` and `toStream()`: this collector just streams the value. It can take a mapper as a parameter that can return a collection or a stream. 
 
## Flat map to stream
There are two one collector in this category. 
- `flattenToStream()` and `flatMapToStream()`: these collector use a flat mapper, a function that returns a collection or a stream. The result is then flat mapped into a single stream. 

## To map then stream
There are two collectors in this category. Those collectors creates map, either from a collector that returns a map (built using a `groupingBy()` or a `toMap()`) or from a key mapper / value mapper pair, or from a key extractor. 
- `toMapThenStream()`: this collector returns the stream of the entries of the created map. It takes either the map collector, or the key mapper / value mapper pair.   
- `groupingByThenStream()`: this collector returns the same kind of stream as the previous one. It takes the key extractor, and may take also the downstream collector. 

## Max entry value by counting
There is one collector here. It works on map which values are streams of elements. It can transform this map into a map of the same keys, with values that are the most frequent values of each stream. The transformed values are entries, with values as key and number of appearance as values. 

## To Stream
The `toStream()` collector collects the current stream in another stream using a mapper. It is built on the model of the `toList()` and `toSet()` collectors. 

## Flat mapping
There are three collectors in this category. It allows for a very flexible way of flat mapping a stream using a collector. 
- `flatMapping()`: takes at least a function that makes a stream out of each element of the processed stream. It can also takes a collector to transform this stream further, and a final mapper that acts as a finisher. 
