/*
 * Copyright (C) 2017 José Paumard
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

import org.testng.annotations.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by José
 */
public class GroupingByAndAllMaxTest {


    @Test
    public void should_create_the_correct_stream_on_groupingBy_then_stream_all_max_for_an_empty_stream() {

        // Given
        Stream<String> strings = Stream.empty();
        Collector<String, ?, Stream<Map.Entry<Integer, List<String>>>> collector =
                CollectorsUtils.groupingByAndStreamAllMaxBy(
                        String::length,
                        toList(),
                        Map.Entry.comparingByKey()
                );

        // When
        List<Map.Entry<Integer, List<String>>> entries =
                strings.collect(collector).collect(toList());

        // Then
        assertThat(entries).isEmpty();
    }

    @Test
    public void should_create_the_correct_stream_on_groupingBy_then_stream_all_max_with_one_max() {

        // Given
        Stream<String> strings = Stream.of("one", "two", "two", "three", "three", "four", "four", "four");

        Collector<String, ?, Stream<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndStreamAllMaxBy(
                        identity(),
                        counting(),
                        Map.Entry.comparingByValue()
                );

        // When
        List<Map.Entry<String, Long>> result = strings.collect(collector).collect(toList());

        // Then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result).contains(new AbstractMap.SimpleImmutableEntry<>("four", 3L));
    }

    @Test
    public void should_create_the_correct_stream_on_groupingBy_then_stream_all_max_with_several_maxes() {

        // Given
        Stream<String> strings = Stream.of("one", "two", "two", "three", "three", "three", "four", "four", "four");

        Collector<String, ?, Stream<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndStreamAllMaxBy(
                        identity(),
                        counting(),
                        Map.Entry.comparingByValue()
                );

        // When
        List<Map.Entry<String, Long>> result = strings.collect(collector).collect(toList());

        // Then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(new AbstractMap.SimpleImmutableEntry<>("three", 3L));
        assertThat(result).contains(new AbstractMap.SimpleImmutableEntry<>("four", 3L));
    }

    @Test
    public void should_create_the_correct_stream_on_groupingBy_then_all_max_by_value_for_an_empty_stream() {

        // Given
        Stream<String> strings = Stream.empty();

        Collector<String, ?, Set<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndAllMaxByValue(
                        Function.<String>identity(),
                        counting()
                );

        // When
        Set<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result).isEmpty();
    }

    @Test @SuppressWarnings("unchecked")
    public void should_create_the_correct_stream_on_groupingBy_then_all_max_by_value_with_one_max() {

        // Given
        Stream<String> strings = Stream.of("one", "two", "two", "three", "three", "four", "four", "four");

        Collector<String, ?, Set<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndAllMaxByValue(
                        Function.<String>identity(),
                        counting()
                );

        // When
        Set<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result).contains(new AbstractMap.SimpleImmutableEntry<>("four", 3L));
    }

    @Test @SuppressWarnings("unchecked")
    public void should_create_the_correct_stream_on_groupingBy_then_all_max_by_value_with_several_maxes() {

        // Given
        Stream<String> strings = Stream.of("one", "two", "two", "three", "three", "three", "four", "four", "four");

        Collector<String, ?, Set<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndAllMaxByValue(
                        Function.<String>identity(),
                        counting()
                );

        // When
        Set<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(new AbstractMap.SimpleImmutableEntry<>("three", 3L));
        assertThat(result).contains(new AbstractMap.SimpleImmutableEntry<>("four", 3L));
    }

    @Test
    public void should_create_the_correct_stream_on_groupingBy_then_all_max_by_value_and_a_comparator_for_an_empty_stream() {

        // Given
        Stream<String> strings = Stream.empty();

        Collector<String, ?, Set<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndAllMaxByValue(
                        Function.<String>identity(),
                        counting(),
                        Comparator.reverseOrder()
                );

        // When
        Set<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result).isEmpty();
    }

    @Test @SuppressWarnings("unchecked")
    public void should_create_the_correct_stream_on_groupingBy_then_all_max_by_value_and_a_comparator_with_one_max() {

        // Given
        Stream<String> strings = Stream.of("one", "two", "two", "three", "three", "four", "four", "four");

        Collector<String, ?, Set<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndAllMaxByValue(
                        Function.<String>identity(),
                        counting(),
                        Comparator.reverseOrder()
                );

        // When
        Set<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result).contains(new AbstractMap.SimpleImmutableEntry<>("one", 1L));
    }

    @Test @SuppressWarnings("unchecked")
    public void should_create_the_correct_stream_on_groupingBy_then_all_max_by_value_and_a_comparator_with_several_maxes() {

        // Given
        Stream<String> strings = Stream.of("one", "one", "two", "two", "three", "three", "three", "four", "four", "four");

        Collector<String, ?, Set<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndAllMaxByValue(
                        Function.<String>identity(),
                        counting(),
                        Comparator.reverseOrder()
                );

        // When
        Set<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(new AbstractMap.SimpleImmutableEntry<>("one", 2L));
        assertThat(result).contains(new AbstractMap.SimpleImmutableEntry<>("two", 2L));
    }

    @Test
    public void should_create_the_correct_stream_on_groupingBy_then_all_max_by_value_and_a_result_supplier_for_an_empty_stream() {

        // Given
        Stream<String> strings = Stream.empty();

        Collector<String, ?, List<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndAllMaxByValue(
                        Function.<String>identity(),
                        ArrayList::new,
                        counting()
                );

        // When
        List<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result).isEmpty();
        assertThat(result).isExactlyInstanceOf(ArrayList.class);
    }

    @Test
    public void should_create_the_correct_stream_on_groupingBy_then_all_max_by_value_and_a_result_supplier_with_one_max() {

        // Given
        Stream<String> strings = Stream.of("one", "two", "two", "three", "three", "four", "four", "four");

        Collector<String, ?, List<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndAllMaxByValue(
                        Function.<String>identity(),
                        ArrayList::new,
                        counting()
                );

        // When
        List<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result).isExactlyInstanceOf(ArrayList.class);
        assertThat(result).contains(new AbstractMap.SimpleImmutableEntry<>("four", 3L));
    }

    @Test
    public void should_create_the_correct_stream_on_groupingBy_then_all_max_by_value_and_a_result_supplier_with_several_maxes() {

        // Given
        Stream<String> strings = Stream.of("one", "one", "two", "two", "three", "three", "three", "four", "four", "four");

        Collector<String, ?, List<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndAllMaxByValue(
                        Function.<String>identity(),
                        ArrayList::new,
                        counting()
                );

        // When
        List<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).isExactlyInstanceOf(ArrayList.class);
        assertThat(result).contains(new AbstractMap.SimpleImmutableEntry<>("three", 3L));
        assertThat(result).contains(new AbstractMap.SimpleImmutableEntry<>("four", 3L));
    }

    @Test
    public void should_create_the_correct_stream_on_groupingBy_then_all_max_for_an_empty_stream_in_a_set() {

        // Given
        Stream<String> strings = Stream.empty();

        Collector<String, ?, Set<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndAllMaxBy(
                        identity(),
                        HashSet::new,
                        Collectors.counting(),
                        Map.Entry.comparingByValue()
                );

        // When
        Set<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result).isEmpty();
        assertThat(result).isExactlyInstanceOf(HashSet.class);
    }

    @Test @SuppressWarnings("unchecked")
    public void should_create_the_correct_stream_on_groupingBy_then_all_max_with_one_max_in_a_set() {

        // Given
        Stream<String> strings = Stream.of("one", "two", "two", "three", "three", "four", "four", "four");

        Collector<String, ?, Set<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndAllMaxBy(
                        identity(),
                        HashSet::new,
                        Collectors.counting(),
                        Map.Entry.comparingByValue()
                );

        // When
        Set<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result).isExactlyInstanceOf(HashSet.class);
        assertThat(result).contains(new AbstractMap.SimpleImmutableEntry<>("four", 3L));
    }

    @Test @SuppressWarnings("unchecked")
    public void should_create_the_correct_stream_on_groupingBy_then_all_max_with_several_maxes_in_a_set() {

        // Given
        Stream<String> strings = Stream.of("one", "two", "two", "three", "three", "three", "four", "four", "four");

        Collector<String, ?, Set<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndAllMaxBy(
                        identity(),
                        HashSet::new,
                        Collectors.counting(),
                        Map.Entry.comparingByValue()
                );

        // When
        Set<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).isExactlyInstanceOf(HashSet.class);
        assertThat(result).contains(new AbstractMap.SimpleImmutableEntry<>("three", 3L));
        assertThat(result).contains(new AbstractMap.SimpleImmutableEntry<>("four", 3L));
    }

    @Test
    public void should_create_the_correct_stream_on_groupingBy_then_first_maxes_for_an_empty_stream() {

        // Given
        Stream<String> strings = Stream.empty();
        Collector<String, ?, List<Map.Entry<Integer, List<String>>>> collector =
                CollectorsUtils.groupingByAndMaxesBy(
                        String::length,
                        toList(),
                        10,
                        Map.Entry.comparingByKey()
                );

        // When
        List<Map.Entry<Integer, List<String>>> entries =
                strings.collect(collector);

        // Then
        assertThat(entries).isEmpty();
    }

    @Test
    public void should_create_the_correct_stream_on_groupingBy_then_n_maxes_with_one_max_and_n_bigger() {

        // Given
        Stream<String> strings = Stream.of("one", "two", "two", "three", "three", "four", "four", "four");

        Collector<String, ?, List<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndMaxesBy(
                        identity(),
                        counting(),
                        10,
                        Map.Entry.comparingByValue()
                );

        // When
        List<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.size()).isEqualTo(4);
        assertThat(result).containsExactly(
                new AbstractMap.SimpleImmutableEntry<>("four", 3L),
                new AbstractMap.SimpleImmutableEntry<>("three", 2L),
                new AbstractMap.SimpleImmutableEntry<>("two", 2L),
                new AbstractMap.SimpleImmutableEntry<>("one", 1L)
        );
    }

    @Test
    public void should_create_the_correct_stream_on_groupingBy_then_n_maxes_with_one_max_and_n_smaller() {

        // Given
        Stream<String> strings = Stream.of("one", "two", "two", "three", "three", "four", "four", "four");

        Collector<String, ?, List<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndMaxesBy(
                        identity(),
                        counting(),
                        2,
                        Map.Entry.comparingByValue()
                );

        // When
        List<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).containsExactly(
                new AbstractMap.SimpleImmutableEntry<>("four", 3L),
                new AbstractMap.SimpleImmutableEntry<>("three", 2L),
                new AbstractMap.SimpleImmutableEntry<>("two", 2L)
        );
    }

    @Test
    public void should_create_the_correct_stream_on_groupingBy_then_n_maxes_with_several_maxes_and_n_greater() {

        // Given
        Stream<String> strings = Stream.of("one", "two", "two", "three", "three", "three", "four", "four", "four");

        Collector<String, ?, List<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndMaxesBy(
                        identity(),
                        counting(),
                        10,
                        Map.Entry.comparingByValue()
                );

        // When
        List<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.size()).isEqualTo(4);
        assertThat(result).containsExactly(
                new AbstractMap.SimpleImmutableEntry<>("four", 3L),
                new AbstractMap.SimpleImmutableEntry<>("three", 3L),
                new AbstractMap.SimpleImmutableEntry<>("two", 2L),
                new AbstractMap.SimpleImmutableEntry<>("one", 1L)
        );
    }

    @Test
    public void should_create_the_correct_stream_on_groupingBy_then_n_maxes_with_several_maxes_and_n_smaller() {

        // Given
        Stream<String> strings = Stream.of("one", "two", "two", "three", "three", "three", "four", "four", "four");

        Collector<String, ?, List<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndMaxesBy(
                        identity(),
                        counting(),
                        3,
                        Map.Entry.comparingByValue()
                );

        // When
        List<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).containsExactly(
                new AbstractMap.SimpleImmutableEntry<>("four", 3L),
                new AbstractMap.SimpleImmutableEntry<>("three", 3L),
                new AbstractMap.SimpleImmutableEntry<>("two", 2L)
        );
    }

    @Test
    public void should_create_the_correct_stream_on_groupingBy_then_n_maxes_with_several_maxes_more_n_results() {

        // Given
        Stream<String> strings = Stream.of("one", "two", "two", "three", "three", "four", "four");

        Collector<String, ?, List<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndMaxesBy(
                        identity(),
                        counting(),
                        2,
                        Map.Entry.comparingByValue()
                );

        // When
        List<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).containsExactly(
                new AbstractMap.SimpleImmutableEntry<>("four", 2L),
                new AbstractMap.SimpleImmutableEntry<>("three", 2L),
                new AbstractMap.SimpleImmutableEntry<>("two", 2L)
        );
    }
}
