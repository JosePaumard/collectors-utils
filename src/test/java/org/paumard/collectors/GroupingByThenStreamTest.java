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

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by José
 */
public class GroupingByThenStreamTest {


    @Test
    public void should_create_the_correct_stream_on_groupingBy_for_an_empty_stream() {

        // Given
        Stream<String> strings = Stream.empty();
        Collector<String, ?, Stream<Map.Entry<Integer, List<String>>>> groupingByThenStream =
                CollectorsUtils.groupingByThenStream(
                        String::length
                );

        // When
        List<Map.Entry<Integer, List<String>>> entries =
                strings.collect(groupingByThenStream).collect(Collectors.toList());

        // Then
        assertThat(entries).isEmpty();
    }

    @Test
    public void should_create_the_correct_stream_on_groupingBy_with_a_downstream_for_an_empty_stream() {

        // Given
        Stream<String> strings = Stream.empty();
        Collector<String, ?, Stream<Map.Entry<Integer, Long>>> groupingByThenStream =
                CollectorsUtils.groupingByThenStream(
                        String::length, counting()
                );

        // When
        List<Map.Entry<Integer, Long>> entries =
                strings.collect(groupingByThenStream).collect(Collectors.toList());

        // Then
        assertThat(entries).isEmpty();
    }

    @Test
    public void should_create_the_correct_stream_on_groupingBy_for_a_non_empty_stream() {

        // Given
        Stream<String> strings = Stream.of("one", "two", "three", "four");
        Collector<String, ?, Stream<Map.Entry<Integer, List<String>>>> groupingByThenStream =
                CollectorsUtils.groupingByThenStream(
                        String::length
                );

        // When
        List<Map.Entry<Integer, List<String>>> entries =
                strings.collect(groupingByThenStream).collect(Collectors.toList());
        Map<Integer, List<String>> result = entries.stream().collect(toMap(
                entry -> entry.getKey(),
                entry -> entry.getValue()
        ));

        // Then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(3)).containsExactly("one", "two");
        assertThat(result.get(4)).containsExactly("four");
        assertThat(result.get(5)).containsExactly("three");
    }

    @Test
    public void should_create_the_correct_stream_on_groupingBy_with_a_downstream_for_a_non_empty_stream() {

        // Given
        Stream<String> strings = Stream.of("one", "two", "three", "four");
        Collector<String, ?, Stream<Map.Entry<Integer, Long>>> groupingByThenStream =
                CollectorsUtils.groupingByThenStream(
                        String::length, counting()
                );

        // When
        List<Map.Entry<Integer, Long>> entries =
                strings.collect(groupingByThenStream).collect(Collectors.toList());
        Map<Integer, Long> result = entries.stream().collect(toMap(
                entry -> entry.getKey(),
                entry -> entry.getValue()
        ));

        // Then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(3)).isEqualTo(2L);
        assertThat(result.get(4)).isEqualTo(1L);
        assertThat(result.get(5)).isEqualTo(1L);
    }
}
