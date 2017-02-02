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
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by José
 */
public class ToStreamTest {

    @Test
    public void should_stream_an_empty_stream_into_an_empty_stream() {

        // Given
        Stream<String> strings = Stream.empty();

        // When
        Collector<String, ?, Stream<String>> collector = CollectorsUtils.mapToStream();
        List<String> result = strings.collect(collector).collect(toList());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    public void should_stream_a_non_empty_stream_into_a_stream() {

        // Given
        Stream<String> strings = Stream.of("one", "two", "three", "four", "five", "six");

        // When
        Map<Integer, Stream<String>> map =
                strings.collect(
                        Collectors.groupingBy(
                                String::length,
                                CollectorsUtils.mapToStream()
                        )
                );

        // Then
        assertThat(map.size()).isEqualTo(3);
        assertThat(map.get(3).collect(toList())).containsExactly("one", "two", "six");
        assertThat(map.get(4).collect(toList())).containsExactly("four", "five");
        assertThat(map.get(5).collect(toList())).containsExactly("three");
    }

    @Test
    public void should_stream_a_non_empty_stream_into_a_stream_parallel() {

        // Given
        Stream<String> strings = Stream.of("one", "two", "three", "four", "five", "six");

        // When
        Map<Integer, Stream<String>> map =
                strings.parallel().collect(
                        Collectors.groupingBy(
                                String::length,
                                CollectorsUtils.mapToStream()
                        )
                );

        // Then
        assertThat(map.size()).isEqualTo(3);
        assertThat(map.get(3).collect(toList())).containsExactly("one", "two", "six");
        assertThat(map.get(4).collect(toList())).containsExactly("four", "five");
        assertThat(map.get(5).collect(toList())).containsExactly("three");
    }

    @Test
    public void should_stream_an_empty_stream_with_a_collection_mapper_into_an_empty_stream() {

        // Given
        Stream<String> strings = Stream.empty();

        // When
        Collector<String, ?, Stream<Stream<Integer>>> collector = CollectorsUtils.mapToStream((String s) -> s.chars().boxed().collect(toList()));
        long result = strings.collect(collector).count();

        // Then
        assertThat(result).isEqualTo(0L);
    }

    @Test
    public void should_stream_a_empty_stream_with_a_collection_mapper_into_the_correct_stream() {

        // Given
        Stream<String> strings = Stream.of("Hello", "world");
        Function<String, List<Character>> mapper = (String s) -> s.chars().mapToObj(letter -> (char)letter).collect(toList());

        // When
        Collector<String, ?, Stream<Stream<Character>>> collector = CollectorsUtils.mapToStream(mapper);
        Stream<Stream<Character>> result = strings.collect(collector);
        List<List<Character>> list = result.map(stream -> stream.collect(toList())).collect(toList());

        // Then
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).containsExactly('H', 'e', 'l', 'l', 'o');
        assertThat(list.get(1)).containsExactly('w', 'o', 'r', 'l', 'd');
    }

    @Test
    public void should_stream_a_empty_stream_with_a_collection_mapper_into_the_correct_stream_in_parallel() {

        // Given
        Stream<String> strings = Stream.of("Hello", "world");
        Function<String, List<Character>> mapper = (String s) -> s.chars().mapToObj(letter -> (char)letter).collect(toList());

        // When
        Collector<String, ?, Stream<Stream<Character>>> collector = CollectorsUtils.mapToStream(mapper);
        Stream<Stream<Character>> result = strings.parallel().collect(collector);
        List<List<Character>> list = result.map(stream -> stream.collect(toList())).collect(toList());

        // Then
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).containsExactly('H', 'e', 'l', 'l', 'o');
        assertThat(list.get(1)).containsExactly('w', 'o', 'r', 'l', 'd');
    }

    @Test
    public void should_stream_an_empty_stream_with_a_stream_mapper_into_an_empty_stream() {

        // Given
        Stream<String> strings = Stream.empty();

        // When
        Collector<String, ?, Stream<Stream<Integer>>> collector = CollectorsUtils.toStream((String s) -> s.chars().boxed());
        long result = strings.collect(collector).count();

        // Then
        assertThat(result).isEqualTo(0L);
    }

    @Test
    public void should_stream_a_empty_stream_with_a_stream_mapper_into_the_correct_stream() {

        // Given
        Stream<String> strings = Stream.of("Hello", "world");
        Function<String, Stream<Character>> mapper = (String s) -> s.chars().mapToObj(letter -> (char)letter);

        // When
        Collector<String, ?, Stream<Stream<Character>>> collector = CollectorsUtils.toStream(mapper);
        Stream<Stream<Character>> result = strings.collect(collector);
        List<List<Character>> list = result.map(stream -> stream.collect(toList())).collect(toList());

        // Then
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).containsExactly('H', 'e', 'l', 'l', 'o');
        assertThat(list.get(1)).containsExactly('w', 'o', 'r', 'l', 'd');
    }

    @Test
    public void should_stream_a_empty_stream_with_a_stream_mapper_into_the_correct_stream_in_parallel() {

        // Given
        Stream<String> strings = Stream.of("Hello", "world");
        Function<String, Stream<Character>> mapper = (String s) -> s.chars().mapToObj(letter -> (char)letter);

        // When
        Collector<String, ?, Stream<Stream<Character>>> collector = CollectorsUtils.toStream(mapper);
        Stream<Stream<Character>> result = strings.parallel().collect(collector);
        List<List<Character>> list = result.map(stream -> stream.collect(toList())).collect(toList());

        // Then
        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).containsExactly('H', 'e', 'l', 'l', 'o');
        assertThat(list.get(1)).containsExactly('w', 'o', 'r', 'l', 'd');
    }

    @Test
    public void should_stream_an_empty_stream_with_a_stream_flat_mapper_collection_into_an_empty_stream() {

        // Given
        Stream<String> strings = Stream.empty();

        // When
        Collector<String, ?, Stream<Integer>> collector = CollectorsUtils.flattenToStream((String s) -> s.chars().boxed().collect(toList()));
        long result = strings.collect(collector).count();

        // Then
        assertThat(result).isEqualTo(0L);
    }

    @Test
    public void should_stream_a_empty_stream_with_a_stream_flat_mapper_collection_into_the_correct_stream() {

        // Given
        Stream<String> strings = Stream.of("Hello", "world");
        Function<String, List<Character>> mapper = (String s) -> s.chars().mapToObj(letter -> (char)letter).collect(toList());

        // When
        Collector<String, ?, Stream<Character>> collector = CollectorsUtils.flattenToStream(mapper);
        Stream<Character> result = strings.collect(collector);
        List<Character> list = result.collect(toList());

        // Then
        assertThat(list).containsExactly('H', 'e', 'l', 'l', 'o', 'w', 'o', 'r', 'l', 'd');
    }

    @Test
    public void should_stream_a_empty_stream_with_a_stream_flat_mapper_collection_into_the_correct_stream_in_parallel() {

        // Given
        Stream<String> strings = Stream.of("Hello", "world");
        Function<String, List<Character>> mapper = (String s) -> s.chars().mapToObj(letter -> (char)letter).collect(toList());

        // When
        Collector<String, ?, Stream<Character>> collector = CollectorsUtils.flattenToStream(mapper);
        Stream<Character> result = strings.parallel().collect(collector);
        List<Character> list = result.collect(toList());

        // Then
        assertThat(list).containsExactly('H', 'e', 'l', 'l', 'o', 'w', 'o', 'r', 'l', 'd');
    }

    @Test
    public void should_stream_an_empty_stream_with_a_stream_flat_mapper_stream_into_an_empty_stream() {

        // Given
        Stream<String> strings = Stream.empty();

        // When
        Collector<String, ?, Stream<Integer>> collector = CollectorsUtils.flatMapToStream((String s) -> s.chars().boxed());
        long result = strings.collect(collector).count();

        // Then
        assertThat(result).isEqualTo(0L);
    }

    @Test
    public void should_stream_a_empty_stream_with_a_stream_flat_mapper_stream_into_the_correct_stream() {

        // Given
        Stream<String> strings = Stream.of("Hello", "world");
        Function<String, Stream<Character>> mapper = (String s) -> s.chars().mapToObj(letter -> (char)letter);

        // When
        Collector<String, ?, Stream<Character>> collector = CollectorsUtils.flatMapToStream(mapper);
        Stream<Character> result = strings.collect(collector);
        List<Character> list = result.collect(toList());

        // Then
        assertThat(list).containsExactly('H', 'e', 'l', 'l', 'o', 'w', 'o', 'r', 'l', 'd');
    }

    @Test
    public void should_stream_a_empty_stream_with_a_stream_flat_mapper_stream_into_the_correct_stream_in_parallel() {

        // Given
        Stream<String> strings = Stream.of("Hello", "world");
        Function<String, Stream<Character>> mapper = (String s) -> s.chars().mapToObj(letter -> (char)letter);

        // When
        Collector<String, ?, Stream<Character>> collector = CollectorsUtils.flatMapToStream(mapper);
        Stream<Character> result = strings.parallel().collect(collector);
        List<Character> list = result.collect(toList());

        // Then
        assertThat(list).containsExactly('H', 'e', 'l', 'l', 'o', 'w', 'o', 'r', 'l', 'd');
    }
}
