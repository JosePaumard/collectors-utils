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
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by José
 */
public class FlatMappingTest {

    @Test
    public void should_collect_flatmap_an_empty_stream_into_an_empty_stream() {

        // Given
        Stream<String> strings = Stream.empty();
        Function<String, Stream<Character>> flatMapper = string -> string.chars().mapToObj(letter -> (char)letter);
        Collector<String, ?, Stream<Character>> streamCollector = CollectorsUtils.flatMapping(flatMapper);

        // When
        List<Character> characters = strings.collect(streamCollector).collect(toList());

        // Then
        assertThat(characters).isEmpty();
    }

    @Test
    public void should_collect_flat_map_a_non_empty_stream_into_a_stream() {

        // Given
        Stream<String> strings = Stream.of("one", "two", "three");
        Function<String, Stream<Character>> streamMapper = string -> string.chars().mapToObj(letter -> (char)letter);

        Collector<String, ?, Stream<Character>> streamCollector = CollectorsUtils.flatMapping(streamMapper);

        // When
        List<Character> characters = strings.collect(streamCollector).collect(toList());

        // Then
        assertThat(characters.size()).isEqualTo(11);
        assertThat(characters).containsExactly('o', 'n', 'e', 't', 'w', 'o', 't', 'h', 'r', 'e', 'e');
    }

    @Test
    public void should_collect_flat_map_with_a_downstream_collector_a_non_empty_stream_into_a_stream() {

        // Given
        Stream<String> strings = Stream.of("one", "two", "three");
        Function<String, Stream<Character>> streamMapper = string -> string.chars().mapToObj(letter -> (char)letter);
        Collector<Stream<Character>, ?, Stream<Stream<Character>>> downstream = CollectorsUtils.mapToStream();

        Collector<String, ?, Stream<Character>> streamCollector = CollectorsUtils.flatMapping(streamMapper, downstream);


        // When
        List<Character> characters = strings.collect(streamCollector).collect(toList());

        // Then
        assertThat(characters.size()).isEqualTo(11);
        assertThat(characters).containsExactly('o', 'n', 'e', 't', 'w', 'o', 't', 'h', 'r', 'e', 'e');
    }

    @Test
    public void should_collect_flat_map_with_a_downstream_collector_and_a_flat_mapper_a_non_empty_stream_into_a_stream() {

        // Given
        Stream<String> strings = Stream.of("one", "two", "three");
        Function<String, Stream<Character>> streamMapper = string -> string.chars().mapToObj(letter -> (char)letter);
        Collector<Stream<Character>, ?, Stream<Stream<Character>>> downstream = CollectorsUtils.mapToStream();
        Function<Stream<Character>, Stream<String>> mapper = stream -> stream.map(letter -> Character.toString(letter));

        Collector<String, ?, Stream<String>> streamCollector = CollectorsUtils.flatMapping(streamMapper, downstream, mapper);


        // When
        List<String> characters = strings.collect(streamCollector).collect(toList());

        // Then
        assertThat(characters.size()).isEqualTo(11);
        assertThat(characters).containsExactly("o", "n", "e", "t", "w", "o", "t", "h", "r", "e", "e");
    }
}
