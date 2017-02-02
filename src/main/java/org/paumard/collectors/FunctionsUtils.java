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

import org.paumard.streams.StreamsUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;


/**
 * Created by José
 */
public class FunctionsUtils {


    /**
     * <p>A convenient function that extracts a max from a stream using a comparator.</p>
     *
     * @param comparator The comparator used to compare the elements of the stream
     * @param <E>        The type of the elements of the stream
     * @return the function
     */
    public static <E> Function<Stream<E>, Optional<E>> maxBy(Comparator<? super E> comparator) {
        Objects.requireNonNull(comparator);
        return stream -> stream.max(comparator);
    }

    /**
     * <p>A convenient function that transforms a map into a stream of its entries.</p>
     *
     * @param <K> The type of the keys of the map
     * @param <V> The type of the values of the map
     * @return the function
     */
    public static <K, V> Function<Map<K, V>, Stream<Map.Entry<K, V>>> toStreamOfEntries() {
        return map -> map.entrySet().stream();
    }

    /**
     * <p>A convenient function that extracts the n bests elements of a stream, using a comparator. </p>
     *
     * @param n          The number of elements to extract
     * @param comparator The comparator used to compare the elements of the stream
     * @param <E>        The type of the elements of the stream
     * @return the function
     */
    public static <E> Function<Stream<E>, Stream<E>> takeMaxValues(int n, Comparator<? super E> comparator) {
        return stream -> StreamsUtils.filteringMaxValues(stream, n, comparator);
    }

    /**
     * <p>A convenient method that collect a stream into a list.</p>
     *
     * @param <E> The type of the elements of the stream
     * @return the function
     */
    public static <E> Function<Stream<E>, List<E>> collectToList() {
        return stream -> stream.collect(toList());
    }

    /**
     * <p>A convenient method that collect a stream into a Set.</p>
     *
     * @param <E> The type of the elements of the stream
     * @return the function
     */
    public static <E> Function<Stream<E>, Set<E>> collectToSet() {
        return stream -> stream.collect(toSet());
    }

    /**
     * <p>A convenient method that collect a stream into a collection created using the given supplier.</p>
     *
     * @param collectionSupplier The supplier used to create the collection
     * @param <E>                The type of the elements of the stream
     * @return the function
     */
    public static <M extends Collection<E>, E> Function<Stream<E>, M>
    collectToCollection(Supplier<M> collectionSupplier) {
        Objects.requireNonNull(collectionSupplier);

        return stream -> stream.collect(toCollection(collectionSupplier));
    }

    /**
     * <p>A convenient method that creates a function that select all the max elements of a stream using
     * a given comparator. </p>
     *
     * @param comparator The comparator used to compare the elements of the stream
     * @param <E>        The type of the elements of the stream
     * @return the function
     */
    public static <E> Function<Stream<E>, Stream<E>> takeAllMaxElements(Comparator<? super E> comparator) {
        Objects.requireNonNull(comparator);
        return stream -> StreamsUtils.filteringAllMax(stream, comparator);
    }
}
