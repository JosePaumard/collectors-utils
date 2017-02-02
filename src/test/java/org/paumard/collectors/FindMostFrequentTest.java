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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by José
 */
public class FindMostFrequentTest {

    @Test
    public void should_return_an_empty_set_for_find_most_frequent_on_an_empty_stream() {

        // Given
        Stream<String> strings = Stream.empty();

        Collector<String, ?, Set<Map.Entry<String, Long>>> collector = CollectorsUtils.findMostFrequent();

        // When
        Set<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    public void should_return_the_most_frequent_for_find_most_frequent_on_a_non_empty_stream_with_one_max() {

        // Given
        Stream<String> strings = Stream.of("one", "one", "two", "two", "two", "three");

        Collector<String, ?, Set<Map.Entry<String, Long>>> collector = CollectorsUtils.findMostFrequent();

        // When
        Set<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.contains(new AbstractMap.SimpleImmutableEntry<>("two", 3L))).isTrue();
    }

    @Test
    public void should_return_the_most_frequent_for_find_most_frequent_on_a_non_empty_stream_with_several_maxes() {

        // Given
        Stream<String> strings = Stream.of("one", "one", "one", "two", "two", "two", "three");

        Collector<String, ?, Set<Map.Entry<String, Long>>> collector = CollectorsUtils.findMostFrequent();

        // When
        Set<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.contains(new AbstractMap.SimpleImmutableEntry<>("one", 3L))).isTrue();
        assertThat(result.contains(new AbstractMap.SimpleImmutableEntry<>("two", 3L))).isTrue();
    }

    @Test
    public void should_return_an_empty_stream_for_find_most_frequent_on_an_empty_stream() {

        // Given
        Stream<String> strings = Stream.empty();

        Collector<String, ?, Stream<Map.Entry<String, Long>>> collector = CollectorsUtils.streamMostFrequent();

        // When
        Stream<Map.Entry<String, Long>> result = strings.collect(collector);

        // Then
        assertThat(result.count()).isEqualTo(0L);
    }

    @Test
    public void should_stream_the_most_frequent_for_find_most_frequent_on_a_non_empty_stream_with_one_max() {

        // Given
        Stream<String> strings = Stream.of("one", "one", "two", "two", "two", "three");

        Collector<String, ?, Stream<Map.Entry<String, Long>>> collector = CollectorsUtils.streamMostFrequent();

        // When
        List<Map.Entry<String, Long>> result = strings.collect(collector).collect(toList());

        // Then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.contains(new AbstractMap.SimpleImmutableEntry<>("two", 3L))).isTrue();
    }

    @Test
    public void should_stream_the_most_frequent_for_find_most_frequent_on_a_non_empty_stream_with_several_maxes() {

        // Given
        Stream<String> strings = Stream.of("one", "one", "one", "two", "two", "two", "three");

        Collector<String, ?, Stream<Map.Entry<String, Long>>> collector = CollectorsUtils.streamMostFrequent();

        // When
        List<Map.Entry<String, Long>> result = strings.collect(collector).collect(toList());

        // Then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.contains(new AbstractMap.SimpleImmutableEntry<>("one", 3L))).isTrue();
        assertThat(result.contains(new AbstractMap.SimpleImmutableEntry<>("two", 3L))).isTrue();
    }
}
