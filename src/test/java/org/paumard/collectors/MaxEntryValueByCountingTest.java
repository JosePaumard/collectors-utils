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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by José
 */
public class MaxEntryValueByCountingTest {

    @Test
    public void should_return_an_empty_map_for_max_entry_value_by_counting_on_an_empty_stream() {

        // Given
        Map<Integer, Stream<String>> map = new HashMap<>();
        Stream<Map.Entry<Integer, Stream<String>>> entries =
                FunctionsUtils.<Integer, Stream<String>>toStreamOfEntries().apply(map);

        Collector<Map.Entry<Integer, Stream<String>>, ?, Map<Integer, Map.Entry<String, Long>>> collector =
                CollectorsUtils.maxEntryValueByCounting();

        // When
        Map<Integer, Map.Entry<String, Long>> collect = entries.collect(collector);

        // Then
        assertThat(collect).isEmpty();
    }

    @Test
    public void should_return_a_correct_map_for_max_entry_value_by_counting_on_correct_stream() {

        // Given
        Map<Integer, Stream<String>> map = new HashMap<>();
        map.put(1, Stream.of("one", "two", "three"));
        map.put(2, Stream.of("one", "two", "two", "three"));
        map.put(3, Stream.of("one", "two", "two", "three", "three", "three"));
        Stream<Map.Entry<Integer, Stream<String>>> entries =
                FunctionsUtils.<Integer, Stream<String>>toStreamOfEntries().apply(map);

        Collector<Map.Entry<Integer, Stream<String>>, ?, Map<Integer, Map.Entry<String, Long>>> collector =
                CollectorsUtils.maxEntryValueByCounting();

        // When
        Map<Integer, Map.Entry<String, Long>> collect = entries.collect(collector);

        // Then
        assertThat(collect.size()).isEqualTo(3);
        assertThat(collect.get(1)).isEqualTo(new AbstractMap.SimpleImmutableEntry<>("one", 1L));
        assertThat(collect.get(2)).isEqualTo(new AbstractMap.SimpleImmutableEntry<>("two", 2L));
        assertThat(collect.get(3)).isEqualTo(new AbstractMap.SimpleImmutableEntry<>("three", 3L));
    }
}
