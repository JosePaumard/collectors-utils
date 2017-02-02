/*
 * Copyright (C) 2016 José Paumard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.paumard.collectors;


import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.mapping;
import static org.paumard.collectors.FunctionsUtils.collectToList;
import static org.paumard.collectors.FunctionsUtils.maxBy;
import static org.paumard.collectors.FunctionsUtils.takeMaxValues;
import static org.paumard.collectors.FunctionsUtils.collectToSet;
import static org.paumard.collectors.FunctionsUtils.takeAllMaxElements;
import static org.paumard.collectors.FunctionsUtils.toStreamOfEntries;
import static org.paumard.collectors.FunctionsUtils.collectToCollection;

/**
 * Created by José
 */
public class CollectorsUtils {

    /**
     * <p>Builds a collector that does a <code>groupingBy()</code> with the provided key extractor and
     * downstream collector. It then streams the resulting map using its entry set, and extract the
     * max of this stream, using provided comparator of entries.</p>
     * <p>Applying this collector returns an optional that is empty if used to collect and empty stream,
     * and that wraps the extracted max if not.</p>
     * <p>A {@code NullPointerException} will be thrown if one of the parameter is null.</p>
     *
     * @param keyExtractor the function applied to the stream elements to get the key of the map
     * @param downstream   the downstream collector applied to the streams of values
     * @param comparator   the comparator used to compare the entries
     * @param <T>          the type of the processed stream
     * @param <K>          the type of the keys
     * @param <V>          the type of the values
     * @return a collector that returns the first found max entry in a {@link Optional}
     */
    public static <T, K, V> Collector<T, ?, Optional<Map.Entry<K, V>>>
    groupingByAndMaxBy(Function<? super T, ? extends K> keyExtractor,
                       Collector<T, ?, V> downstream,
                       Comparator<? super Map.Entry<K, V>> comparator) {
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(downstream);
        Objects.requireNonNull(comparator);

        return collectingAndThen(
                groupingBy(keyExtractor, downstream),
                FunctionsUtils.<K, V>toStreamOfEntries().andThen(maxBy(comparator))
        );
    }

    /**
     * <p>Builds a collector that does a <code>groupingBy()</code> with the provided key extractor and
     * downstream collector. It then streams the resulting map using its entry set, and extract the
     * max of this stream, using the values of the entries.</p>
     * <p>Applying this collector returns an optional that is empty if used to collect and empty stream,
     * and that wraps the extracted max if not.</p>
     * <p>A <code>NullPointerException</code> will be thrown if one of the parameter is null.</p>
     *
     * @param keyExtractor the function applied to the stream elements to get the key of the map
     * @param downstream   the downstream collector applied to the streams of values
     * @param <T>          the type of the processed stream
     * @param <K>          the type of the keys
     * @param <V>          the type of the values
     * @return the collector
     */
    public static <T, K, V extends Comparable<? super V>> Collector<T, ?, Optional<Map.Entry<K, V>>>
    groupingByAndMaxByValue(Function<? super T, ? extends K> keyExtractor,
                            Collector<T, ?, V> downstream) {
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(downstream);

        return groupingByAndMaxBy(keyExtractor, downstream, Map.Entry.comparingByValue());
    }

    /**
     * <p>Builds a collector that does a <code>groupingBy()</code> with the identity function as a key extractor and
     * downstream collector. It then streams the resulting map using its entry set, and extract the
     * max of this stream, using the values of the entries.</p>
     * <p>Applying this collector returns an optional that is empty if used to collect and empty stream,
     * and that wraps the extracted max if not.</p>
     * <p>A <code>NullPointerException</code> will be thrown if one of the parameter is null.</p>
     *
     * @param downstream the downstream collector applied to the streams of values
     * @param <T>        the type of the processed stream
     * @param <V>        the type of the values
     * @return the collector
     */
    public static <T, V extends Comparable<? super V>> Collector<T, ?, Optional<Map.Entry<T, V>>>
    groupingBySelfAndMaxByValue(Collector<T, ?, V> downstream) {
        Objects.requireNonNull(downstream);

        return groupingByAndMaxBy(identity(), downstream, Map.Entry.comparingByValue());
    }

    /**
     * <p>Builds a collector that does a <code>groupingBy()</code> with the identity function as a key extractor and
     * the classical <code>counting()</code> downstream collector. It then streams the resulting map using its entry set,
     * and extract the max of this stream, using the values of the entries.</p>
     * <p>Applying this collector returns an optional that is empty if used to collect and empty stream,
     * and that wraps the extracted max if not.</p>
     *
     * @param <T> the type of the processed stream
     * @return the collector
     */
    public static <T> Collector<T, ?, Optional<Map.Entry<T, Long>>> maxByCounting() {

        return groupingByAndMaxBy(identity(), counting(), Map.Entry.comparingByValue());
    }

    /**
     * Returns a collector that returns the most frequently seen elements in a stream. In case there
     * are ex-aequo, all the elements are returned. In case the input stream is empty, the returned
     * {@link Set} is empty. Since the elements returned are entries, they are collected in a {@link Set}.
     *
     * @param <T> the type of the processed stream
     * @return a {@link Collector} implementing the all max extraction in a given collection
     */
    public static <T> Collector<T, ?, Set<Map.Entry<T, Long>>> findMostFrequent() {

        return groupingByAndAllMaxBy(identity(), counting(), Map.Entry.comparingByValue());
    }

    /**
     * Returns a collector that returns the most frequently seen elements in a stream. In case there
     * are ex-aequo, all the elements are returned. In case the input stream is empty, the returned
     * {@link Stream} is empty.
     *
     * @param <T> the type of the processed stream
     * @return a {@link Collector} implementing the all max extraction in a given collection
     */
    public static <T> Collector<T, ?, Stream<Map.Entry<T, Long>>> streamMostFrequent() {

        return groupingByAndStreamAllMaxBy(identity(), counting(), Map.Entry.comparingByValue());
    }

    /**
     * <p>Builds a collector that does a <code>groupingBy()</code> with the provided key extractor function
     * and the classical <code>counting()</code> downstream collector.
     * It then streams the resulting map using its entry set, and extract the
     * max of this stream, using the values of the entries.</p>
     * <p>Applying this collector returns an optional that is empty if used to collect and empty stream,
     * and that wraps the extracted max if not.</p>
     * <p>A <code>NullPointerException</code> will be thrown if the key extractor is null.</p>
     *
     * @param keyExtractor the function applied to the stream elements to get the key of the map
     * @param <T>          the type of the processed stream
     * @param <V>          the type of the values
     * @return the collector
     */
    public static <T, V> Collector<T, ?, Optional<Map.Entry<V, Long>>>
    groupingByAndMaxByCounting(Function<? super T, ? extends V> keyExtractor) {

        return groupingByAndMaxBy(keyExtractor, counting(), Map.Entry.comparingByValue());
    }

    /**
     * <p>Builds a collector that does a <code>groupingBy()</code> with the provided key extractor and
     * downstream collector. It then streams the resulting map using its entry set, and extract the
     * max of this stream, using the values of the entries, compared with the provided
     * value comparator.</p>
     * <p>Applying this collector returns an optional that is empty if used to collect and empty stream,
     * and that wraps the extracted max if not.</p>
     * <p>A <code>NullPointerException</code> will be thrown if one of the parameter is null.</p>
     *
     * @param keyExtractor    the function applied to the stream elements to get the key of the map
     * @param downstream      the downstream collector applied to the streams of values
     * @param valueComparator the comparator used to compare the values of the entries
     * @param <T>             the type of the processed stream
     * @param <K>             the type of the keys
     * @param <V>             the type of the values
     * @return an optional wrapping the max entry
     */
    public static <T, K, V> Collector<T, ?, Optional<Map.Entry<K, V>>>
    groupingByAndMaxByValue(Function<? super T, ? extends K> keyExtractor,
                            Collector<T, ?, V> downstream,
                            Comparator<? super V> valueComparator) {
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(downstream);
        Objects.requireNonNull(valueComparator);

        return groupingByAndMaxBy(keyExtractor, downstream, Map.Entry.comparingByValue(valueComparator));
    }

    /**
     * <p>Builds a collector that does a <code>groupingBy()</code> with the provided key extractor and
     * downstream collector. It then streams the resulting map using its entry set, and sort this stream
     * using the provided comparator of entries.</p>
     * <p>It then returns the <code>n</code> first elements of that stream, in a list.</p>
     * <p>A <code>NullPointerException</code> will be thrown if one of the parameter is null.</p>
     *
     * @param keyExtractor the function applied to the stream elements to get the key of the map
     * @param n            the number of elements to be returned in the list
     * @param downstream   the downstream collector applied to the streams of values
     * @param comparator   the comparator used to compare the entries
     * @param <T>          the type of the processed stream
     * @param <K>          the type of the keys
     * @param <V>          the type of the values
     * @return an optional wrapping the max entry
     */
    public static <T, K, V> Collector<T, ?, List<Map.Entry<K, V>>>
    groupingByAndMaxesBy(Function<? super T, ? extends K> keyExtractor,
                         Collector<T, ?, V> downstream,
                         int n,
                         Comparator<? super Map.Entry<K, V>> comparator) {
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(downstream);
        Objects.requireNonNull(comparator);

        return collectingAndThen(
                groupingBy(keyExtractor, downstream),
                FunctionsUtils.<K, V>toStreamOfEntries()
                        .andThen(takeMaxValues(n, comparator))
                        .andThen(collectToList())
        );
    }

    /**
     * <p>Returns a collector that returns all the max values of a stream according to the provided comparator.
     * This collector is useful when one needs to extract all the max values from a stream, and not just
     * the first max value found. Since the elements returned are entries, they are collected in a
     * {@link Set}.</p>
     * <p>First this collector does a <code>groupingBy()</code> with the provided key extractor and
     * downstream collector. It then streams the resulting map using its entry set, and find the first max entry
     * using the provided {@link Comparator}. It then streams the map again to extract all the
     * entries that are equals to the found entry, using the provided {@link Comparator}.</p>
     * <p>The provided comparator should enforce the comparator contract, that is two objects equals
     * with their <code>equals()</code> method should be also equals according to the comparator,
     * that should return 0 when comparing those objects.</p>
     * <p>A {@link NullPointerException} will be thrown if one of the parameter is null.</p>
     *
     * @param keyExtractor    the function applied to the stream elements to get the key of the map
     * @param downstream      the downstream collector applied to the streams of values
     * @param entryComparator the comparator used to compare the entries
     * @param <T>             the type of the processed stream
     * @param <K>             the type of the keys
     * @param <V>             the type of the values
     * @return a {@code Collector} implementing the all max extraction
     */
    public static <T, K, V> Collector<T, ?, Set<Map.Entry<K, V>>>
    groupingByAndAllMaxBy(Function<? super T, ? extends K> keyExtractor,
                          Collector<T, ?, V> downstream,
                          Comparator<Map.Entry<K, V>> entryComparator) {
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(downstream);
        Objects.requireNonNull(entryComparator);

        return collectingAndThen(
                groupingBy(keyExtractor, downstream),
                FunctionsUtils.<K, V>toStreamOfEntries()
                        .andThen(takeAllMaxElements(entryComparator))
                        .andThen(collectToSet())
        );
    }

    /**
     * <p>Returns a collector that returns all the max values of a stream according to the provided comparator.</p>
     * <p>This collector is useful when one needs to extract all the max values from a stream, and not just
     * the first max value found. Since the elements returned are entries, they are collected in a
     * {@link Set}.</p>
     * <p>A {@link NullPointerException} will be thrown if one of the parameter is null.</p>
     *
     * @param keyExtractor   the function applied to the stream elements to get the key of the map
     * @param resultSupplier the supplier used to build the container that holds the max entries
     * @param downstream     the downstream collector applied to the streams of values
     * @param comparator     the comparator used to compare the entries
     * @param <T>            the type of the processed stream
     * @param <M>            the type of the returned container
     * @param <K>            the type of the keys
     * @param <V>            the type of the values
     * @return a {@link Collector} implementing the all max extraction in a given collection
     */
    public static <T, M extends Collection<Map.Entry<K, V>>, K, V> Collector<T, ?, M>
    groupingByAndAllMaxBy(Function<? super T, ? extends K> keyExtractor,
                          Supplier<M> resultSupplier,
                          Collector<T, ?, V> downstream,
                          Comparator<? super Map.Entry<K, V>> comparator) {
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(resultSupplier);
        Objects.requireNonNull(downstream);
        Objects.requireNonNull(comparator);

        return collectingAndThen(
                groupingBy(keyExtractor, downstream),
                FunctionsUtils.<K, V>toStreamOfEntries()
                        .andThen(takeAllMaxElements(comparator))
                        .andThen(collectToCollection(resultSupplier))
        );
    }

    /**
     * <p>Returns a collector that returns all the max values of a stream according to the provided comparator.</p>
     * <p>This collector is useful when one needs to extract all the max values from a stream, and not just
     * the first max value found. Since the elements returned are entries, they are collected in a
     * {@link Set}.</p>
     * <p>A {@link NullPointerException} will be thrown if one of the parameter is null.</p>
     *
     * @param keyExtractor the function applied to the stream elements to get the key of the map
     * @param downstream   the downstream collector applied to the streams of values
     * @param comparator   the comparator used to compare the entries
     * @param <T>          the type of the processed stream
     * @param <K>          the type of the keys
     * @param <V>          the type of the values
     * @return a {@link Collector} implementing the all max extraction in a given collection
     */
    public static <T, K, V> Collector<T, ?, Stream<Map.Entry<K, V>>>
    groupingByAndStreamAllMaxBy(Function<? super T, ? extends K> keyExtractor,
                                Collector<T, ?, V> downstream,
                                Comparator<? super Map.Entry<K, V>> comparator) {
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(downstream);
        Objects.requireNonNull(comparator);

        return collectingAndThen(
                groupingBy(keyExtractor, downstream),
                FunctionsUtils.<K, V>toStreamOfEntries()
                        .andThen(takeAllMaxElements(comparator))
        );
    }

    /**
     * <p>Returns a collector that returns all the max values of a stream according the natural order of their values.</p>
     * <p>This collector is useful when one needs to extract all the max values from a stream, and not just
     * the first max value found. The return entry elements are then collected in a collection built with the
     * provided supplier.</p>
     * <p>A {@link NullPointerException} will be thrown if one of the parameter is null.</p>
     *
     * @param keyExtractor   the function applied to the stream elements to get the key of the map
     * @param resultSupplier the supplier used to build the container that holds the max entries
     * @param downstream     the downstream collector applied to the streams of values
     * @param <T>            the type of the processed stream
     * @param <M>            the type of the returned container
     * @param <K>            the type of the keys
     * @param <V>            the type of the values
     * @return a {@link Collector} implementing the all max extraction in a given collection
     */
    public static <T, M extends Collection<Map.Entry<K, V>>, K, V extends Comparable<? super V>> Collector<T, ?, M>
    groupingByAndAllMaxByValue(Function<? super T, ? extends K> keyExtractor,
                               Supplier<M> resultSupplier,
                               Collector<T, ?, V> downstream) {
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(resultSupplier);
        Objects.requireNonNull(downstream);

        return groupingByAndAllMaxBy(keyExtractor, resultSupplier, downstream, Map.Entry.comparingByValue());
    }

    /**
     * <p>Returns a collector that returns all the max values of a stream according the natural order of their values.</p>
     * <p>This collector is useful when one needs to extract all the max values from a stream, and not just
     * the first max value found. Since the elements returned are entries, they are collected in a
     * {@link Set}.</p>
     * <p>A {@link NullPointerException} will be thrown if one of the parameter is null.</p>
     *
     * @param keyExtractor the function applied to the stream elements to get the key of the map
     * @param downstream   the downstream collector applied to the streams of values
     * @param <T>          the type of the processed stream
     * @param <K>          the type of the keys
     * @param <V>          the type of the values
     * @return a {@link Collector} implementing the all max extraction in a given collection
     */
    public static <T, K, V extends Comparable<? super V>> Collector<T, ?, Set<Map.Entry<K, V>>>
    groupingByAndAllMaxByValue(Function<? super T, ? extends K> keyExtractor,
                               Collector<T, ?, V> downstream) {
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(downstream);

        return groupingByAndAllMaxBy(keyExtractor, downstream, Map.Entry.<K, V>comparingByValue());
    }

    /**
     * <p>Returns a collector that returns all the max values of a stream according to the provided comparator.</p>
     * <p>This collector is useful when one needs to extract all the max values from a stream, and not just
     * the first max value found. The return entry elements are then collected collection built with the
     * provided supplier.</p>
     * <p>A {@link NullPointerException} will be thrown if one of the parameter is null.</p>
     *
     * @param keyExtractor the function applied to the stream elements to get the key of the map
     * @param downstream   the downstream collector applied to the streams of values
     * @param comparator   the comparator used to compare the entries
     * @param <T>          the type of the processed stream
     * @param <K>          the type of the keys
     * @param <V>          the type of the values
     * @return a {@link Collector} implementing the all max extraction in a given collection
     */
    public static <T, K, V> Collector<T, ?, Set<Map.Entry<K, V>>>
    groupingByAndAllMaxByValue(Function<? super T, ? extends K> keyExtractor,
                               Collector<T, ?, V> downstream,
                               Comparator<V> comparator) {
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(downstream);

        return groupingByAndAllMaxBy(keyExtractor, downstream, Map.Entry.<K, V>comparingByValue(comparator));
    }

    /**
     * <p>Builds a collector that accumulates all the mapped elements in a single stream. The objects
     * of the input stream of {@code T} are first mapped using the provided mapper, and added to
     * the returned stream.</p>
     * <p>A {@link NullPointerException} will be thrown if {@code mapper} is null.</p>
     *
     * @param mapper the function modeling the 1:p relationship between T and U
     * @param <T>    the type of the elements of the provided stream
     * @param <U>    the type of the elements of the returned stream
     * @return a collector that builds a stream of the elements in relation
     */
    public static <T, U> Collector<T, ?, Stream<Stream<U>>>
    mapToStream(Function<? super T, ? extends Collection<U>> mapper) {
        Objects.requireNonNull(mapper);

        return Collector.<T, Stream.Builder<Stream<U>>, Stream<Stream<U>>>of(
                Stream::builder,
                (builder, element) -> builder.accept(mapper.apply(element).stream()),
                (builder1, builder2) -> {
                    builder2.build().forEach(builder1);
                    return builder1;
                },
                Stream.Builder::build
        );
    }

    /**
     * <p>Builds a collector that accumulates all the mapped elements in a single stream. The objects
     * of the input stream of {@code T} are streamed using the provided mapper.</p>
     * <p>A {@link NullPointerException} will be thrown if {@code mapper} is null.</p>
     *
     * @param mapper the function modeling the 1:p relationship between T and U
     * @param <T>    the type of the elements of the provided stream
     * @param <U>    the type of the elements of the returned stream
     * @return a collector that builds a stream of the elements in relation
     */
    public static <T, U> Collector<T, ?, Stream<Stream<U>>>
    toStream(Function<? super T, ? extends Stream<U>> mapper) {
        Objects.requireNonNull(mapper);

        return Collector.<T, Stream.Builder<Stream<U>>, Stream<Stream<U>>>of(
                Stream::builder,
                (builder, element) -> builder.accept(mapper.apply(element)),
                (builder1, builder2) -> {
                    builder2.build().forEach(builder1);
                    return builder1;
                },
                Stream.Builder::build
        );
    }

    /**
     * <p>Builds a collector that accumulates all the elements in a stream. The objects
     * of the input stream of {@code T} are added to the returned stream. This collector is useful
     * only as a downstream collector. </p>
     *
     * @param <T> the type of the elements of the provided stream
     * @return a collector that builds a stream of the elements
     */
    public static <T> Collector<T, ?, Stream<T>> mapToStream() {

        return Collector.<T, Stream.Builder<T>, Stream<T>>of(
                Stream::builder,
                Stream.Builder::accept,
                (builder1, builder2) -> {
                    builder2.build().forEach(builder1);
                    return builder1;
                },
                Stream.Builder::build
        );
    }

    /**
     * <p>Builds a collector that accumulates all the mapped elements in a single stream. The objects
     * of the input stream of {@code T} are first mapped using the provided mapper, and the elements of
     * the resulting collection added to the returned stream. It thus has the effect of flattening all
     * the elements of the collections.</p>
     * <p>A {@link NullPointerException} will be thrown if {@code mapper} is null.</p>
     *
     * @param mapper the function modeling the 1:p relationship between T and U
     * @param <T>    the type of the elements of the provided stream
     * @param <U>    the type of the elements of the returned stream
     * @return a collector that builds a stream of the elements in relation
     */
    public static <T, U> Collector<T, ?, Stream<U>> flattenToStream(Function<T, ? extends Collection<U>> mapper) {
        Objects.requireNonNull(mapper);

// This implementation can throw a StackOverflowExceptions for large streams
//        return Collectors.reducing(
//                Stream.empty(),
//                t -> mapper.apply(t).stream(),
//                (stream1, stream2) -> Stream.of(stream1, stream2).flatMap(identity()));

        return Collector.<T, Stream.Builder<U>, Stream<U>>of(
                Stream::builder,
                (builder, element) -> mapper.apply(element).forEach(builder),
                (builder1, builder2) -> {
                    builder2.build().forEach(builder1);
                    return builder1;
                },
                Stream.Builder::build
        );
    }

    /**
     * <p>Builds a collector that accumulates all the mapped elements in a single stream. The objects
     * of the input stream of {@code T} are first mapped using the provided mapper, and added to the returned
     * stream.</p>
     * <p>A {@link NullPointerException} will be thrown if {@code mapper} is null.</p>
     *
     * @param mapper the function modeling the 1:p relationship between T and U
     * @param <T>    the type of the elements of the provided stream
     * @param <U>    the type of the elements of the returned stream
     * @return a collector that builds a stream of the elements in relation
     */
    public static <T, U> Collector<T, ?, Stream<U>> flatMapToStream(Function<? super T, ? extends Stream<U>> mapper) {
        Objects.requireNonNull(mapper);

        return Collector.<T, Stream.Builder<U>, Stream<U>>of(
                Stream::builder,
                (builder, element) -> mapper.apply(element).forEach(builder),
                (builder1, builder2) -> {
                    builder2.build().forEach(builder1);
                    return builder1;
                },
                Stream.Builder::build
        );
    }

    /**
     * <p>Builds a collector that combines the collection of the stream elements in a map, using the provided
     * {@code toMapCollector}, and create a stream on the entry set of that map.</p>
     *
     * @param toMapCollector a collector that builds a map
     * @param <T>            the type of the elements of the provided stream
     * @param <K>            the type of the keys of the built map
     * @param <D>            the type of the values of the built map
     * @return a collector that builds a stream of entries
     */
    public static <T, K, D> Collector<T, ?, Stream<Map.Entry<K, D>>>
    toMapThenStream(Collector<T, ?, Map<K, D>> toMapCollector) {
        Objects.requireNonNull(toMapCollector);

        return collectingAndThen(
                toMapCollector,
                toStreamOfEntries()
        );
    }

    /**
     * <p>Builds a collector that combines the collection of the stream elements in a map, using the provided
     * {@code toMapCollector}, and create a stream on the entry set of that map.</p>
     *
     * @param <T>         the type of the elements of the provided stream
     * @param <K>         the type of the keys of the built map
     * @param <U>         the type of the values of the built map
     * @param keyMapper   a mapping function to produce keys
     * @param valueMapper a mapping function to produce values
     * @return a collector that builds a stream of entries
     */
    public static <T, K, U> Collector<T, ?, Stream<Map.Entry<K, U>>>
    toMapThenStream(Function<? super T, ? extends K> keyMapper,
                    Function<? super T, ? extends U> valueMapper) {
        Objects.requireNonNull(keyMapper);
        Objects.requireNonNull(valueMapper);

        return collectingAndThen(
                toMap(keyMapper, valueMapper),
                toStreamOfEntries()
        );
    }

    /**
     * <p>Builds a collector that combines the collection of the stream elements in a map, using a
     * <code>groupingBy()</code> built with the provided classifier, and create a stream on the entry set of that map.</p>
     *
     * @param classifier the classifier used to build the grouping by collector
     * @param downstream the downstream collector passed to the building of the grouping by collector
     * @param <T>        the type of the elements of the provided stream
     * @param <K>        the type of the keys of the built map
     * @param <D>        the type of the values of the built map
     * @return a collector that builds a stream of entries
     */
    public static <T, K, D> Collector<T, ?, Stream<Map.Entry<K, D>>>
    groupingByThenStream(Function<? super T, ? extends K> classifier, Collector<? super T, ?, D> downstream) {
        Objects.requireNonNull(classifier);
        Objects.requireNonNull(downstream);

        return toMapThenStream(groupingBy(classifier, downstream));
    }

    /**
     * <p>Builds a collector that combines the collection of the stream elements in a map, using a
     * <code>groupingBy()</code> built with the provided classifier, and create a stream on the entry set of that map.</p>
     *
     * @param classifier the classifier used to build the grouping by collector
     * @param <T>        the type of the elements of the provided stream
     * @param <K>        the type of the keys of the built map
     * @return a collector that builds a stream of entries
     */
    public static <T, K> Collector<T, ?, Stream<Map.Entry<K, List<T>>>>
    groupingByThenStream(Function<? super T, ? extends K> classifier) {
        Objects.requireNonNull(classifier);

        return toMapThenStream(groupingBy(classifier));
    }

    /**
     * <p>Builds a collector that makes a map from a stream of entries. The value of each entry is a stream that is
     * mapped to its most frequent element.</p>
     * <p>If one of the value stream is empty, then a <code>{@link NoSuchElementException}</code> will be thrown.</p>
     * <p>If there are equivalent values when computing the max, then the fist value found is put in the resulting
     * entry. </p>
     *
     * @param <K> type of the key
     * @param <V> type of the value
     * @return a collector that creates a map where the values are the most frequent values in the provided stream
     */
    public static <K, V> Collector<Map.Entry<K, Stream<V>>, ?, Map<K, Map.Entry<V, Long>>> maxEntryValueByCounting() {
        return toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().collect(maxByCounting()).get()
        );
    }

    /**
     * <p>Builds a collector that flatmaps a stream of <code>T</code> in a stream of <code>U</code>, using a
     * mapper that maps each <code>T</code> in a stream of <code>U</code>. </p>
     *
     * @param streamMapper the function that maps each <code>T</code> into a stream of <code>U</code>
     * @param <T>          the elements of the first stream
     * @param <U>          c
     * @return a collector that flatmaps a stream
     */
    public static <T, U> Collector<T, ?, Stream<U>>
    flatMapping(Function<? super T, ? extends Stream<U>> streamMapper) {

        return flatMapping(streamMapper, mapToStream(), identity());
    }

    /**
     * <p>Builds a collector that flatmaps a stream of <code>T</code> in a stream of <code>U</code>, using a
     * mapper that maps each <code>T</code> in a stream of <code>U</code> and a collector that collects each
     * stream of <code>U</code> into a stream of <code>R</code>.
     *
     * @param streamMapper the function that maps each <code>T</code> into a stream of <code>U</code>
     * @param downstream   the collector that collects the stream of V into a stream of R
     * @param <T>          the elements of the first stream
     * @param <U>          the elements of the intermediate stream
     * @param <R>          the elements collected from the streams of <code>U</code>
     * @return a collector that flatmaps a stream
     */
    public static <T, U, R extends Stream<U>> Collector<T, ?, Stream<U>>
    flatMapping(Function<? super T, ? extends Stream<U>> streamMapper,
                Collector<? super Stream<U>, ?, ? extends Stream<R>> downstream) {

        return flatMapping(streamMapper, downstream, identity());
    }

    /**
     * <p>Builds a collector that flatmaps a stream of <code>T</code> in a stream of <code>U</code>, using a
     * mapper that maps each <code>T</code> in a stream of <code>U</code> and a collector that collects each
     * stream of <code>U</code> into a stream of <code>R</code>. The resulting stream if then transformed into
     * another stream using a mapper.
     *
     * @param streamMapper the function that maps each <code>T</code> into a stream of <code>U</code>
     * @param downstream   the collector that collects the stream of V into a stream of R
     * @param mapper       the mapper that maps the stream of V into a stream of U
     * @param <T>          the elements of the first stream
     * @param <V>          the elements in relation with the elements T
     * @param <U>          the elements of the intermediate stream
     * @param <R>          the elements collected from the streams of <code>V</code>
     * @return a collector that flatmaps a stream
     */
    public static <T, U, V, R extends Stream<V>> Collector<T, ?, Stream<U>>
    flatMapping(Function<? super T, ? extends Stream<V>> streamMapper,
                Collector<? super Stream<V>, ?, ? extends Stream<R>> downstream,
                Function<R, ? extends Stream<? extends U>> mapper) {

        return collectingAndThen(
                mapping(streamMapper, downstream),
                r -> r.flatMap(mapper)
        );
    }
}
