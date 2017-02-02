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

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by José
 */
public class GroupingByAndMaxByTest {

    @Test
    public void should_return_an_empty_optional_for_groupingBy_and_maxBy_on_an_empty_stream() {

        // Given
        Stream<String> strings = Stream.empty();

        Collector<String, ?, Optional<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndMaxBy(
                        identity(),
                        counting(),
                        Map.Entry.comparingByValue()
                );

        // When
        Optional<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void should_return_the_max_for_groupingBy_and_maxBy_on_a_non_empty_stream() {

        // Given
        Stream<String> strings = Stream.of("one", "one", "two", "two", "two");

        Collector<String, ?, Optional<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndMaxBy(
                        identity(),
                        counting(),
                        Map.Entry.comparingByValue()
                );

        // When
        Optional<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(new AbstractMap.SimpleImmutableEntry<>("two", 3L));
    }

    @Test
    public void should_return_an_empty_optional_for_groupingBy_and_max_by_value_on_an_empty_stream() {

        // Given
        Stream<String> strings = Stream.empty();

        Collector<String, ?, Optional<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndMaxByValue(
                        identity(),
                        counting()
                );

        // When
        Optional<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void should_return_the_result_for_groupingBy_and_max_by_value_on_a_non_empty_stream() {

        // Given
        Stream<String> strings = Stream.of("one", "one", "two", "two", "two");

        Collector<String, ?, Optional<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndMaxByValue(
                        identity(),
                        counting()
                );

        // When
        Optional<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(new AbstractMap.SimpleImmutableEntry<>("two", 3L));
    }

    @Test
    public void should_return_an_empty_optional_for_groupingBy_and_max_by_value_with_a_comparator_on_an_empty_stream() {

        // Given
        Stream<String> strings = Stream.empty();

        Collector<String, ?, Optional<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndMaxByValue(
                        identity(),
                        counting(),
                        Comparator.reverseOrder()
                );

        // When
        Optional<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void should_return_the_result_for_groupingBy_and_max_by_value_with_a_comparator_on_a_non_empty_stream() {

        // Given
        Stream<String> strings = Stream.of("one", "one", "two", "two", "two");

        Collector<String, ?, Optional<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndMaxByValue(
                        identity(),
                        counting(),
                        Comparator.reverseOrder()
                );

        // When
        Optional<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(new AbstractMap.SimpleImmutableEntry<>("one", 2L));
    }

    @Test
    public void should_return_an_empty_optional_for_groupingBySelf_and_max_by_value_on_an_empty_stream() {

        // Given
        Stream<String> strings = Stream.empty();

        Collector<String, ?, Optional<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingBySelfAndMaxByValue(
                        counting()
                );

        // When
        Optional<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void should_return_the_result_for_groupingBySelf_and_max_by_value_on_a_non_empty_stream() {

        // Given
        Stream<String> strings = Stream.of("one", "one", "two", "two", "two");

        Collector<String, ?, Optional<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingBySelfAndMaxByValue(
                        counting()
                );

        // When
        Optional<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(new AbstractMap.SimpleImmutableEntry<>("two", 3L));
    }

    @Test
    public void should_return_an_empty_optional_for_groupingBy_and_max_by_counting_on_an_empty_stream() {

        // Given
        Stream<String> strings = Stream.empty();

        Collector<String, ?, Optional<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndMaxByCounting(
                        Function.identity()
                );

        // When
        Optional<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void should_return_the_result_for_groupingBy_and_max_by_counting_on_a_non_empty_stream() {

        // Given
        Stream<String> strings = Stream.of("one", "one", "two", "two", "two");

        Collector<String, ?, Optional<Map.Entry<String, Long>>> collector =
                CollectorsUtils.groupingByAndMaxByCounting(
                        Function.identity()
                );

        // When
        Optional<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(new AbstractMap.SimpleImmutableEntry<>("two", 3L));
    }
}
