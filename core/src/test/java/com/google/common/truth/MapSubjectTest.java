/*
 * Copyright (c) 2011 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.common.truth;

import static com.google.common.truth.TestCorrespondences.CASE_INSENSITIVE_EQUALITY;
import static com.google.common.truth.TestCorrespondences.STRING_PARSES_TO_INTEGER_CORRESPONDENCE;
import static com.google.common.truth.TestCorrespondences.WITHIN_10_OF;
import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.Assert.fail;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for {@link Map} subjects.
 *
 * @author Christian Gruber
 * @author Kurt Alfred Kluever
 */
@RunWith(JUnit4.class)
public class MapSubjectTest extends BaseSubjectTestCase {

  @Test
  public void containsExactlyWithNullKey() {
    Map<String, String> actual = Maps.newHashMap();
    actual.put(null, "value");

    assertThat(actual).containsExactly(null, "value");
    assertThat(actual).containsExactly(null, "value").inOrder();
    assertThat(actual).containsExactlyEntriesIn(actual);
    assertThat(actual).containsExactlyEntriesIn(actual).inOrder();
  }

  @Test
  public void containsExactlyWithNullValue() {
    Map<String, String> actual = Maps.newHashMap();
    actual.put("key", null);

    assertThat(actual).containsExactly("key", null);
    assertThat(actual).containsExactly("key", null).inOrder();
    assertThat(actual).containsExactlyEntriesIn(actual);
    assertThat(actual).containsExactlyEntriesIn(actual).inOrder();
  }

  @Test
  public void containsExactlyEmpty() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of();

    assertThat(actual).containsExactly();
    assertThat(actual).containsExactly().inOrder();
    assertThat(actual).containsExactlyEntriesIn(actual);
    assertThat(actual).containsExactlyEntriesIn(actual).inOrder();
  }

  @Test
  public void containsExactlyEmpty_fails() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1);

    expectFailureWhenTestingThat(actual).containsExactly();
    assertFailureKeys("expected to be empty", "but was");
  }

  @Test
  public void containsExactlyEntriesInEmpty_fails() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1);

    expectFailureWhenTestingThat(actual).containsExactlyEntriesIn(ImmutableMap.of());
    assertFailureKeys("expected to be empty", "but was");
  }

  @Test
  public void containsExactlyOneEntry() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1);

    assertThat(actual).containsExactly("jan", 1);
    assertThat(actual).containsExactly("jan", 1).inOrder();
    assertThat(actual).containsExactlyEntriesIn(actual);
    assertThat(actual).containsExactlyEntriesIn(actual).inOrder();
  }

  @Test
  public void containsExactlyMultipleEntries() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);

    assertThat(actual).containsExactly("march", 3, "jan", 1, "feb", 2);
    assertThat(actual).containsExactly("jan", 1, "feb", 2, "march", 3).inOrder();
    assertThat(actual).containsExactlyEntriesIn(actual);
    assertThat(actual).containsExactlyEntriesIn(actual).inOrder();
  }

  @Test
  public void containsExactlyDuplicateKeys() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);

    try {
      assertThat(actual).containsExactly("jan", 1, "jan", 2, "jan", 3);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      assertThat(expected)
          .hasMessageThat()
          .isEqualTo("Duplicate keys ([jan x 3]) cannot be passed to containsExactly().");
    }
  }

  @Test
  public void containsExactlyMultipleDuplicateKeys() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);

    try {
      assertThat(actual).containsExactly("jan", 1, "jan", 1, "feb", 2, "feb", 2);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      assertThat(expected)
          .hasMessageThat()
          .isEqualTo("Duplicate keys ([jan x 2, feb x 2]) cannot be passed to containsExactly().");
    }
  }

  @Test
  public void containsExactlyExtraKey() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
    expectFailureWhenTestingThat(actual).containsExactly("feb", 2, "jan", 1);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, feb=2, march=3}> contains exactly <{feb=2, jan=1}>. "
                + "It has the following entries with unexpected keys: {march=3}");
  }

  @Test
  public void containsExactlyExtraKeyInOrder() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
    expectFailureWhenTestingThat(actual).containsExactly("feb", 2, "jan", 1).inOrder();
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, feb=2, march=3}> contains exactly <{feb=2, jan=1}>. "
                + "It has the following entries with unexpected keys: {march=3}");
  }

  @Test
  public void namedMapContainsExactlyExtraKey() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
    expectFailureWhenTestingThat(actual).named("foo").containsExactly("feb", 2, "jan", 1);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "name: foo\n"
                + "Not true that foo (<{jan=1, feb=2, march=3}>) contains exactly "
                + "<{feb=2, jan=1}>. "
                + "It has the following entries with unexpected keys: {march=3}");
  }

  @Test
  public void containsExactlyMissingKey() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2);
    expectFailureWhenTestingThat(actual).containsExactly("jan", 1, "march", 3, "feb", 2);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, feb=2}> contains exactly <{jan=1, march=3, feb=2}>. "
                + "It is missing keys for the following entries: {march=3}");
  }

  @Test
  public void containsExactlyWrongValue() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
    expectFailureWhenTestingThat(actual).containsExactly("jan", 1, "march", 33, "feb", 2);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, feb=2, march=3}> contains exactly <{jan=1, march=33, feb=2}>. "
                + "It has the following entries with matching keys but different values: "
                + "{march=(expected 33 but got 3)}");
  }

  @Test
  public void containsExactlyWrongValueWithNull() {
    // Test for https://github.com/google/truth/issues/468
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
    expectFailureWhenTestingThat(actual).containsExactly("jan", 1, "march", null, "feb", 2);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, feb=2, march=3}> contains exactly "
                + "<{jan=1, march=null, feb=2}>. It has the following entries with matching keys "
                + "but different values: {march=(expected null but got 3)}");
  }

  @Test
  public void containsExactlyExtraKeyAndMissingKey() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "march", 3);
    expectFailureWhenTestingThat(actual).containsExactly("jan", 1, "feb", 2);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, march=3}> contains exactly <{jan=1, feb=2}>. "
                + "It is missing keys for the following entries: {feb=2} "
                + "and has the following entries with unexpected keys: {march=3}");
  }

  @Test
  public void containsExactlyExtraKeyAndWrongValue() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
    expectFailureWhenTestingThat(actual).containsExactly("jan", 1, "march", 33);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, feb=2, march=3}> contains exactly <{jan=1, march=33}>. "
                + "It has the following entries with unexpected keys: {feb=2} "
                + "and has the following entries with matching keys but different values: "
                + "{march=(expected 33 but got 3)}");
  }

  @Test
  public void containsExactlyMissingKeyAndWrongValue() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "march", 3);
    expectFailureWhenTestingThat(actual).containsExactly("jan", 1, "march", 33, "feb", 2);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, march=3}> contains exactly <{jan=1, march=33, feb=2}>. "
                + "It is missing keys for the following entries: {feb=2} "
                + "and has the following entries with matching keys but different values: "
                + "{march=(expected 33 but got 3)}");
  }

  @Test
  public void containsExactlyExtraKeyAndMissingKeyAndWrongValue() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "march", 3);
    expectFailureWhenTestingThat(actual).containsExactly("march", 33, "feb", 2);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, march=3}> contains exactly <{march=33, feb=2}>. "
                + "It is missing keys for the following entries: {feb=2} "
                + "and has the following entries with unexpected keys: {jan=1} "
                + "and has the following entries with matching keys but different values: "
                + "{march=(expected 33 but got 3)}");
  }

  @Test
  public void containsExactlyNotInOrder() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
    assertThat(actual).containsExactlyEntriesIn(actual);
    assertThat(actual).containsExactlyEntriesIn(actual).inOrder();

    assertThat(actual).containsExactly("jan", 1, "march", 3, "feb", 2);
    expectFailureWhenTestingThat(actual).containsExactly("jan", 1, "march", 3, "feb", 2).inOrder();
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, feb=2, march=3}> contains exactly these entries in order "
                + "<{jan=1, march=3, feb=2}>");
  }

  @Test
  @SuppressWarnings("ShouldHaveEvenArgs")
  public void containsExactlyBadNumberOfArgs() {
    ImmutableMap<String, Integer> actual =
        ImmutableMap.of("jan", 1, "feb", 2, "march", 3, "april", 4, "may", 5);
    assertThat(actual).containsExactlyEntriesIn(actual);
    assertThat(actual).containsExactlyEntriesIn(actual).inOrder();

    try {
      assertThat(actual)
          .containsExactly("jan", 1, "feb", 2, "march", 3, "april", 4, "may", 5, "june", 6, "july");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      assertThat(expected)
          .hasMessageThat()
          .isEqualTo(
              "There must be an equal number of key/value pairs "
                  + "(i.e., the number of key/value parameters (13) must be even).");
    }
  }

  @Test
  public void containsExactlyWrongValue_sameToStringForValues() {
    expectFailureWhenTestingThat(ImmutableMap.of("jan", 1L, "feb", 2L))
        .containsExactly("jan", 1, "feb", 2);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, feb=2}> contains exactly <{jan=1, feb=2}>. "
                + "It has the following entries with matching keys but different values: "
                + "{jan=(expected 1 (java.lang.Integer) but got 1 (java.lang.Long)), "
                + "feb=(expected 2 (java.lang.Integer) but got 2 (java.lang.Long))}");
  }

  @Test
  public void containsExactlyWrongValue_sameToStringForKeys() {
    expectFailureWhenTestingThat(ImmutableMap.of(1L, "jan", 1, "feb"))
        .containsExactly(1, "jan", 1L, "feb");
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{1=jan, 1=feb}> contains exactly <{1=jan, 1=feb}>. "
                + "It has the following entries with matching keys but different values: "
                + "{1 (java.lang.Integer)=(expected jan but got feb), "
                + "1 (java.lang.Long)=(expected feb but got jan)}");
  }

  @Test
  public void containsExactlyExtraKeyAndMissingKey_failsWithSameToStringForKeys() {
    expectFailureWhenTestingThat(ImmutableMap.of(1L, "jan", 2, "feb"))
        .containsExactly(1, "jan", 2, "feb");
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{1=jan, 2=feb}> contains exactly <{1=jan, 2=feb}>. "
                + "It is missing keys for the following entries: {1 (java.lang.Integer)=jan} "
                + "and has the following entries with unexpected keys: {1 (java.lang.Long)=jan}");
  }

  @Test
  public void containsAtLeastWithNullKey() {
    Map<String, String> actual = Maps.newHashMap();
    actual.put(null, "value");
    actual.put("unexpectedKey", "unexpectedValue");
    Map<String, String> expected = Maps.newHashMap();
    expected.put(null, "value");

    assertThat(actual).containsAtLeast(null, "value");
    assertThat(actual).containsAtLeast(null, "value").inOrder();
    assertThat(actual).containsAtLeastEntriesIn(expected);
    assertThat(actual).containsAtLeastEntriesIn(expected).inOrder();
  }

  @Test
  public void containsAtLeastWithNullValue() {
    Map<String, String> actual = Maps.newHashMap();
    actual.put("key", null);
    actual.put("unexpectedKey", "unexpectedValue");
    Map<String, String> expected = Maps.newHashMap();
    expected.put("key", null);

    assertThat(actual).containsAtLeast("key", null);
    assertThat(actual).containsAtLeast("key", null).inOrder();
    assertThat(actual).containsAtLeastEntriesIn(expected);
    assertThat(actual).containsAtLeastEntriesIn(expected).inOrder();
  }

  @Test
  public void containsAtLeastEmpty() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("key", 1);

    assertThat(actual).containsAtLeastEntriesIn(ImmutableMap.of());
    assertThat(actual).containsAtLeastEntriesIn(ImmutableMap.of()).inOrder();
  }

  @Test
  public void containsAtLeastOneEntry() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1);

    assertThat(actual).containsAtLeast("jan", 1);
    assertThat(actual).containsAtLeast("jan", 1).inOrder();
    assertThat(actual).containsAtLeastEntriesIn(actual);
    assertThat(actual).containsAtLeastEntriesIn(actual).inOrder();
  }

  @Test
  public void containsAtLeastMultipleEntries() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "mar", 3, "apr", 4);

    assertThat(actual).containsAtLeast("apr", 4, "jan", 1, "feb", 2);
    assertThat(actual).containsAtLeast("jan", 1, "feb", 2, "apr", 4).inOrder();
    assertThat(actual).containsAtLeastEntriesIn(ImmutableMap.of("apr", 4, "jan", 1, "feb", 2));
    assertThat(actual).containsAtLeastEntriesIn(actual).inOrder();
  }

  @Test
  public void containsAtLeastDuplicateKeys() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);

    try {
      assertThat(actual).containsAtLeast("jan", 1, "jan", 2, "jan", 3);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      assertThat(expected)
          .hasMessageThat()
          .isEqualTo("Duplicate keys ([jan x 3]) cannot be passed to containsAtLeast().");
    }
  }

  @Test
  public void containsAtLeastMultipleDuplicateKeys() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);

    try {
      assertThat(actual).containsAtLeast("jan", 1, "jan", 1, "feb", 2, "feb", 2);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      assertThat(expected)
          .hasMessageThat()
          .isEqualTo("Duplicate keys ([jan x 2, feb x 2]) cannot be passed to containsAtLeast().");
    }
  }

  @Test
  public void containsAtLeastMissingKey() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2);
    expectFailureWhenTestingThat(actual).containsAtLeast("jan", 1, "march", 3);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, feb=2}> contains at least <{jan=1, march=3}>. "
                + "It is missing keys for the following entries: {march=3}");
  }

  @Test
  public void namedMapContainsAtLeastMissingKey() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2);
    expectFailureWhenTestingThat(actual).named("foo").containsAtLeast("march", 3);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "name: foo\n"
                + "Not true that foo (<{jan=1, feb=2}>) contains at least "
                + "<{march=3}>. "
                + "It is missing keys for the following entries: {march=3}");
  }

  @Test
  public void containsAtLeastWrongValue() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
    expectFailureWhenTestingThat(actual).containsAtLeast("jan", 1, "march", 33);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, feb=2, march=3}> contains at least <{jan=1, march=33}>. "
                + "It has the following entries with matching keys but different values: "
                + "{march=(expected 33 but got 3)}");
  }

  @Test
  public void containsAtLeastWrongValueWithNull() {
    // Test for https://github.com/google/truth/issues/468
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
    expectFailureWhenTestingThat(actual).containsAtLeast("jan", 1, "march", null);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, feb=2, march=3}> contains at least "
                + "<{jan=1, march=null}>. It has the following entries with matching keys "
                + "but different values: {march=(expected null but got 3)}");
  }

  @Test
  public void containsAtLeastExtraKeyAndMissingKeyAndWrongValue() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "march", 3);
    expectFailureWhenTestingThat(actual).containsAtLeast("march", 33, "feb", 2);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, march=3}> contains at least <{march=33, feb=2}>. "
                + "It is missing keys for the following entries: {feb=2} "
                + "and has the following entries with matching keys but different values: "
                + "{march=(expected 33 but got 3)}");
  }

  @Test
  public void containsAtLeastNotInOrder() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);

    assertThat(actual).containsAtLeast("march", 3, "feb", 2);
    expectFailureWhenTestingThat(actual).containsAtLeast("march", 3, "feb", 2).inOrder();
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, feb=2, march=3}> contains at least these entries in order "
                + "<{march=3, feb=2}>");
  }

  @Test
  @SuppressWarnings("ShouldHaveEvenArgs")
  public void containsAtLeastBadNumberOfArgs() {
    ImmutableMap<String, Integer> actual =
        ImmutableMap.of("jan", 1, "feb", 2, "march", 3, "april", 4, "may", 5);

    try {
      assertThat(actual)
          .containsAtLeast("jan", 1, "feb", 2, "march", 3, "april", 4, "may", 5, "june", 6, "july");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
      assertThat(expected)
          .hasMessageThat()
          .isEqualTo(
              "There must be an equal number of key/value pairs "
                  + "(i.e., the number of key/value parameters (13) must be even).");
    }
  }

  @Test
  public void containsAtLeastWrongValue_sameToStringForValues() {
    expectFailureWhenTestingThat(ImmutableMap.of("jan", 1L, "feb", 2L, "mar", 3L))
        .containsAtLeast("jan", 1, "feb", 2);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, feb=2, mar=3}> contains at least <{jan=1, feb=2}>. "
                + "It has the following entries with matching keys but different values: "
                + "{jan=(expected 1 (java.lang.Integer) but got 1 (java.lang.Long)), "
                + "feb=(expected 2 (java.lang.Integer) but got 2 (java.lang.Long))}");
  }

  @Test
  public void containsAtLeastWrongValue_sameToStringForKeys() {
    expectFailureWhenTestingThat(ImmutableMap.of(1L, "jan", 1, "feb"))
        .containsAtLeast(1, "jan", 1L, "feb");
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{1=jan, 1=feb}> contains at least <{1=jan, 1=feb}>. "
                + "It has the following entries with matching keys but different values: "
                + "{1 (java.lang.Integer)=(expected jan but got feb), "
                + "1 (java.lang.Long)=(expected feb but got jan)}");
  }

  @Test
  public void containsAtLeastExtraKeyAndMissingKey_failsWithSameToStringForKeys() {
    expectFailureWhenTestingThat(ImmutableMap.of(1L, "jan", 2, "feb"))
        .containsAtLeast(1, "jan", 2, "feb");
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{1=jan, 2=feb}> contains at least <{1=jan, 2=feb}>. "
                + "It is missing keys for the following entries: {1 (java.lang.Integer)=jan}");
  }

  @Test
  public void isEqualToPass() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
    ImmutableMap<String, Integer> expectedMap = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);

    assertThat(actual).isEqualTo(expectedMap);
  }

  @Test
  public void isEqualToFailureExtraMissingAndDiffering() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
    ImmutableMap<String, Integer> expectedMap = ImmutableMap.of("jan", 1, "april", 4, "march", 5);

    expectFailureWhenTestingThat(actual).isEqualTo(expectedMap);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, feb=2, march=3}> is equal to <{jan=1, april=4, march=5}>. "
                + "It is missing keys for the following entries: {april=4} and "
                + "has the following entries with unexpected keys: {feb=2} and "
                + "has the following entries with matching keys but different values: "
                + "{march=(expected 5 but got 3)}");
  }

  @Test
  public void isEqualToFailureDiffering() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
    ImmutableMap<String, Integer> expectedMap = ImmutableMap.of("jan", 1, "feb", 2, "march", 4);

    expectFailureWhenTestingThat(actual).isEqualTo(expectedMap);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, feb=2, march=3}> is equal to <{jan=1, feb=2, march=4}>. "
                + "It has the following entries with matching keys but different values: "
                + "{march=(expected 4 but got 3)}");
  }

  @Test
  public void namedMapIsEqualToFailureDiffering() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
    ImmutableMap<String, Integer> expectedMap = ImmutableMap.of("jan", 1, "feb", 2, "march", 4);

    expectFailureWhenTestingThat(actual).named("foo").isEqualTo(expectedMap);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "name: foo\n"
                + "Not true that foo (<{jan=1, feb=2, march=3}>) is equal to "
                + "<{jan=1, feb=2, march=4}>."
                + " It has the following entries with matching keys but different values: "
                + "{march=(expected 4 but got 3)}");
  }

  @Test
  public void isEqualToFailureExtra() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
    ImmutableMap<String, Integer> expectedMap = ImmutableMap.of("jan", 1, "feb", 2);

    expectFailureWhenTestingThat(actual).isEqualTo(expectedMap);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, feb=2, march=3}> is equal to <{jan=1, feb=2}>. "
                + "It has the following entries with unexpected keys: {march=3}");
  }

  @Test
  public void isEqualToFailureMissing() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2);
    ImmutableMap<String, Integer> expectedMap = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);

    expectFailureWhenTestingThat(actual).isEqualTo(expectedMap);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, feb=2}> is equal to <{jan=1, feb=2, march=3}>. "
                + "It is missing keys for the following entries: {march=3}");
  }

  @Test
  public void isEqualToFailureExtraAndMissing() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
    ImmutableMap<String, Integer> expectedMap = ImmutableMap.of("jan", 1, "feb", 2, "mar", 3);

    expectFailureWhenTestingThat(actual).isEqualTo(expectedMap);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, feb=2, march=3}> is equal to <{jan=1, feb=2, mar=3}>. "
                + "It is missing keys for the following entries: {mar=3} "
                + "and has the following entries with unexpected keys: {march=3}");
  }

  @Test
  public void isEqualToFailureDiffering_sameToString() {
    ImmutableMap<String, Number> actual =
        ImmutableMap.<String, Number>of("jan", 1, "feb", 2, "march", 3L);
    ImmutableMap<String, Integer> expectedMap = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);

    expectFailureWhenTestingThat(actual).isEqualTo(expectedMap);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1, feb=2, march=3}> is equal to <{jan=1, feb=2, march=3}>. "
                + "It has the following entries with matching keys but different values: "
                + "{march=(expected 3 (java.lang.Integer) but got 3 (java.lang.Long))}");
  }

  @Test
  public void isEqualToNonMap() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
    expectFailureWhenTestingThat(actual).isEqualTo("something else");
    assertFailureKeys("expected", "but was");
  }

  /**
   * A broken implementation of {@link Map} whose {@link #equals} method does not implement the
   * contract. Users sometimes write tests using broken implementations, and we should test that
   * {@code isEqualTo} is consistent with their implementation.
   */
  private static class BrokenMap<K, V> extends ForwardingMap<K, V> {

    static <K, V> Map<K, V> wrapWithAlwaysTrueEquals(Map<K, V> delegate) {
      return new BrokenMap<>(delegate, true);
    }

    static <K, V> Map<K, V> wrapWithAlwaysFalseEquals(Map<K, V> delegate) {
      return new BrokenMap<>(delegate, false);
    }

    private final Map<K, V> delegate;
    private final boolean equalsStub;

    private BrokenMap(Map<K, V> delegate, boolean equalsStub) {
      this.delegate = delegate;
      this.equalsStub = equalsStub;
    }

    @Override
    public Map<K, V> delegate() {
      return delegate;
    }

    @Override
    public boolean equals(Object other) {
      return equalsStub;
    }
  }

  @Test
  public void isEqualTo_brokenMapEqualsImplementation_trueWhenItShouldBeFalse() {
    // These maps are not equal according to the contract of Map.equals, but have a broken equals()
    // implementation that always returns true. So the isEqualTo assertion should pass.
    Map<String, Integer> map1 = BrokenMap.wrapWithAlwaysTrueEquals(ImmutableMap.of("jan", 1));
    Map<String, Integer> map2 = BrokenMap.wrapWithAlwaysTrueEquals(ImmutableMap.of("feb", 2));
    assertThat(map1).isEqualTo(map2);
  }

  @Test
  public void isEqualTo_brokenMapEqualsImplementation_falseWhenItShouldBeTrue() {
    // These maps are equal according to the contract of Map.equals, but have a broken equals()
    // implementation that always returns false. So the isEqualTo assertion should fail.
    Map<String, Integer> map1 = BrokenMap.wrapWithAlwaysFalseEquals(ImmutableMap.of("jan", 1));
    Map<String, Integer> map1clone = BrokenMap.wrapWithAlwaysFalseEquals(ImmutableMap.of("jan", 1));
    expectFailureWhenTestingThat(map1).isEqualTo(map1clone);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{jan=1}> is equal to <{jan=1}>. It is equal according to the contract "
                + "of Map.equals(Object), but this implementation returned false");
  }

  @Test
  public void isNotEqualTo() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);
    ImmutableMap<String, Integer> unexpected = ImmutableMap.of("jan", 1, "feb", 2, "march", 3);

    expectFailureWhenTestingThat(actual).isNotEqualTo(unexpected);
  }

  @Test
  public void isEmpty() {
    ImmutableMap<String, String> actual = ImmutableMap.of();
    assertThat(actual).isEmpty();
  }

  @Test
  public void isEmptyWithFailure() {
    ImmutableMap<Integer, Integer> actual = ImmutableMap.of(1, 5);
    expectFailureWhenTestingThat(actual).isEmpty();
    assertFailureKeys("expected to be empty", "but was");
  }

  @Test
  public void isNotEmpty() {
    ImmutableMap<Integer, Integer> actual = ImmutableMap.of(1, 5);
    assertThat(actual).isNotEmpty();
  }

  @Test
  public void isNotEmptyWithFailure() {
    ImmutableMap<Integer, Integer> actual = ImmutableMap.of();
    expectFailureWhenTestingThat(actual).isNotEmpty();
    assertFailureKeys("expected not to be empty");
  }

  @Test
  public void hasSize() {
    assertThat(ImmutableMap.of(1, 2, 3, 4)).hasSize(2);
  }

  @Test
  public void hasSizeZero() {
    assertThat(ImmutableMap.of()).hasSize(0);
  }

  @Test
  public void hasSizeNegative() {
    try {
      assertThat(ImmutableMap.of(1, 2)).hasSize(-1);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test
  public void containsKey() {
    ImmutableMap<String, String> actual = ImmutableMap.of("kurt", "kluever");
    assertThat(actual).containsKey("kurt");
  }

  @Test
  public void containsKeyFailure() {
    ImmutableMap<String, String> actual = ImmutableMap.of("kurt", "kluever");
    expectFailureWhenTestingThat(actual).containsKey("greg");
    assertFailureKeys("value of", "expected to contain", "but was", "map was");
    assertFailureValue("value of", "map.keySet()");
    assertFailureValue("expected to contain", "greg");
    assertFailureValue("but was", "[kurt]");
  }

  @Test
  public void containsKeyNullFailure() {
    ImmutableMap<String, String> actual = ImmutableMap.of("kurt", "kluever");
    expectFailureWhenTestingThat(actual).containsKey(null);
    assertFailureKeys("value of", "expected to contain", "but was", "map was");
    assertFailureValue("value of", "map.keySet()");
    assertFailureValue("expected to contain", "null");
    assertFailureValue("but was", "[kurt]");
  }

  @Test
  public void containsKey_failsWithSameToString() {
    expectFailureWhenTestingThat(ImmutableMap.of(1L, "value1", 2L, "value2", "1", "value3"))
        .containsKey(1);
    assertFailureKeys(
        "value of",
        "expected to contain",
        "an instance of",
        "but did not",
        "though it did contain",
        "full contents",
        "map was");
    assertFailureValue("value of", "map.keySet()");
    assertFailureValue("expected to contain", "1");
  }

  @Test
  public void containsKey_failsWithNullStringAndNull() {
    Map<String, String> actual = Maps.newHashMap();
    actual.put("null", "value1");

    expectFailureWhenTestingThat(actual).containsKey(null);
    assertFailureKeys(
        "value of",
        "expected to contain",
        "an instance of",
        "but did not",
        "though it did contain",
        "full contents",
        "map was");
    assertFailureValue("value of", "map.keySet()");
    assertFailureValue("expected to contain", "null");
  }

  @Test
  public void containsNullKey() {
    Map<String, String> actual = Maps.newHashMap();
    actual.put(null, "null");
    assertThat(actual).containsKey(null);
  }

  @Test
  public void doesNotContainKey() {
    ImmutableMap<String, String> actual = ImmutableMap.of("kurt", "kluever");
    assertThat(actual).doesNotContainKey("greg");
    assertThat(actual).doesNotContainKey(null);
  }

  @Test
  public void doesNotContainKeyFailure() {
    ImmutableMap<String, String> actual = ImmutableMap.of("kurt", "kluever");
    expectFailureWhenTestingThat(actual).doesNotContainKey("kurt");
    assertFailureKeys("value of", "expected not to contain", "but was", "map was");
    assertFailureValue("value of", "map.keySet()");
    assertFailureValue("expected not to contain", "kurt");
    assertFailureValue("but was", "[kurt]");
  }

  @Test
  public void doesNotContainNullKey() {
    Map<String, String> actual = Maps.newHashMap();
    actual.put(null, "null");
    expectFailureWhenTestingThat(actual).doesNotContainKey(null);
    assertFailureKeys("value of", "expected not to contain", "but was", "map was");
    assertFailureValue("value of", "map.keySet()");
    assertFailureValue("expected not to contain", "null");
    assertFailureValue("but was", "[null]");
  }

  @Test
  public void containsEntry() {
    ImmutableMap<String, String> actual = ImmutableMap.of("kurt", "kluever");
    assertThat(actual).containsEntry("kurt", "kluever");
  }

  @Test
  public void containsEntryFailure() {
    ImmutableMap<String, String> actual = ImmutableMap.of("kurt", "kluever");
    expectFailureWhenTestingThat(actual).containsEntry("greg", "kick");
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo("Not true that <{kurt=kluever}> contains entry <greg=kick>");
  }

  @Test
  public void containsEntry_failsWithSameToStringOfKey() {
    expectFailureWhenTestingThat(ImmutableMap.of(1L, "value1", 2L, "value2"))
        .containsEntry(1, "value1");
    assertWithMessage("Full message: %s", expectFailure.getFailure().getMessage())
        .that(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{1=value1, 2=value2}> contains entry "
                + "<1=value1 (Map.Entry<java.lang.Integer, java.lang.String>)>. "
                + "However, it does contain keys <[1] (java.lang.Long)>.");
  }

  @Test
  public void containsEntry_failsWithSameToStringOfValue() {
    expectFailureWhenTestingThat(ImmutableMap.of(1, "null")).containsEntry(1, null);
    assertWithMessage("Full message: %s", expectFailure.getFailure().getMessage())
        .that(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{1=null}> contains entry <1=null "
                + "(Map.Entry<java.lang.Integer, null type>)>. However, it does contain values "
                + "<[null] (java.lang.String)>.");
  }

  @Test
  public void containsNullKeyAndValue() {
    ImmutableMap<String, String> actual = ImmutableMap.of("kurt", "kluever");
    expectFailureWhenTestingThat(actual).containsEntry(null, null);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo("Not true that <{kurt=kluever}> contains entry <null=null>");
  }

  @Test
  public void containsNullEntry() {
    Map<String, String> actual = Maps.newHashMap();
    actual.put(null, null);
    assertThat(actual).containsEntry(null, null);
  }

  @Test
  public void containsNullEntryValue() {
    Map<String, String> actual = Maps.newHashMap();
    actual.put(null, null);
    expectFailureWhenTestingThat(actual).containsEntry("kurt", null);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{null=null}> contains entry <kurt=null>. "
                + "However, the following keys are mapped to <null>: [null]");
  }

  private static final String KEY_IS_PRESENT_WITH_DIFFERENT_VALUE =
      "key is present but with a different value";

  @Test
  public void containsNullEntryKey() {
    Map<String, String> actual = Maps.newHashMap();
    actual.put(null, null);
    expectFailureWhenTestingThat(actual).containsEntry(null, "kluever");
    assertFailureValue("value of", "map.get(null)");
    assertFailureValue("expected", "kluever");
    assertFailureValue("but was", "null");
    assertFailureValue("map was", "{null=null}");
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .contains(KEY_IS_PRESENT_WITH_DIFFERENT_VALUE);
  }

  @Test
  public void doesNotContainEntry() {
    ImmutableMap<String, String> actual = ImmutableMap.of("kurt", "kluever");
    assertThat(actual).doesNotContainEntry("greg", "kick");
    assertThat(actual).doesNotContainEntry(null, null);
    assertThat(actual).doesNotContainEntry("kurt", null);
    assertThat(actual).doesNotContainEntry(null, "kluever");
  }

  @Test
  public void doesNotContainEntryFailure() {
    ImmutableMap<String, String> actual = ImmutableMap.of("kurt", "kluever");
    expectFailureWhenTestingThat(actual).doesNotContainEntry("kurt", "kluever");
    assertFailureKeys("value of", "expected not to contain", "but was");
    assertFailureValue("value of", "map.entrySet()");
    assertFailureValue("expected not to contain", "kurt=kluever");
    assertFailureValue("but was", "[kurt=kluever]");
  }

  @Test
  public void doesNotContainNullEntry() {
    Map<String, String> actual = Maps.newHashMap();
    actual.put(null, null);
    assertThat(actual).doesNotContainEntry("kurt", null);
    assertThat(actual).doesNotContainEntry(null, "kluever");
  }

  @Test
  public void doesNotContainNullEntryFailure() {
    Map<String, String> actual = Maps.newHashMap();
    actual.put(null, null);
    expectFailureWhenTestingThat(actual).doesNotContainEntry(null, null);
    assertFailureKeys("value of", "expected not to contain", "but was");
    assertFailureValue("value of", "map.entrySet()");
    assertFailureValue("expected not to contain", "null=null");
    assertFailureValue("but was", "[null=null]");
  }

  @Test
  public void failMapContainsKey() {
    ImmutableMap<String, String> actual = ImmutableMap.of("a", "A");
    expectFailureWhenTestingThat(actual).containsKey("b");
    assertFailureKeys("value of", "expected to contain", "but was", "map was");
    assertFailureValue("value of", "map.keySet()");
    assertFailureValue("expected to contain", "b");
    assertFailureValue("but was", "[a]");
  }

  @Test
  public void failMapContainsKeyWithNull() {
    ImmutableMap<String, String> actual = ImmutableMap.of("a", "A");
    expectFailureWhenTestingThat(actual).containsKey(null);
    assertFailureKeys("value of", "expected to contain", "but was", "map was");
    assertFailureValue("value of", "map.keySet()");
    assertFailureValue("expected to contain", "null");
    assertFailureValue("but was", "[a]");
  }

  @Test
  public void failMapLacksKey() {
    ImmutableMap<String, String> actual = ImmutableMap.of("a", "A");
    expectFailureWhenTestingThat(actual).doesNotContainKey("a");
    assertFailureKeys("value of", "expected not to contain", "but was", "map was");
    assertFailureValue("value of", "map.keySet()");
    assertFailureValue("expected not to contain", "a");
    assertFailureValue("but was", "[a]");
  }

  @Test
  public void containsKeyWithValue() {
    ImmutableMap<String, String> actual = ImmutableMap.of("a", "A");
    assertThat(actual).containsEntry("a", "A");
  }

  @Test
  public void containsKeyWithNullValueNullExpected() {
    Map<String, String> actual = Maps.newHashMap();
    actual.put("a", null);
    assertThat(actual).containsEntry("a", null);
  }

  @Test
  public void failMapContainsKeyWithValue() {
    ImmutableMap<String, String> actual = ImmutableMap.of("a", "A");
    expectFailureWhenTestingThat(actual).containsEntry("a", "a");
    assertFailureValue("value of", "map.get(a)");
    assertFailureValue("expected", "a");
    assertFailureValue("but was", "A");
    assertFailureValue("map was", "{a=A}");
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .doesNotContain(KEY_IS_PRESENT_WITH_DIFFERENT_VALUE);
  }

  @Test
  public void failMapContainsKeyWithNullValuePresentExpected() {
    Map<String, String> actual = Maps.newHashMap();
    actual.put("a", null);
    expectFailureWhenTestingThat(actual).containsEntry("a", "A");
    assertFailureValue("value of", "map.get(a)");
    assertFailureValue("expected", "A");
    assertFailureValue("but was", "null");
    assertFailureValue("map was", "{a=null}");
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .contains(KEY_IS_PRESENT_WITH_DIFFERENT_VALUE);
  }

  @Test
  public void failMapContainsKeyWithPresentValueNullExpected() {
    ImmutableMap<String, String> actual = ImmutableMap.of("a", "A");
    expectFailureWhenTestingThat(actual).containsEntry("a", null);
    assertFailureValue("value of", "map.get(a)");
    assertFailureValue("expected", "null");
    assertFailureValue("but was", "A");
    assertFailureValue("map was", "{a=A}");
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .contains(KEY_IS_PRESENT_WITH_DIFFERENT_VALUE);
  }

  @Test
  public void comparingValuesUsing_containsEntry_success() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
    assertThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsEntry("def", 456);
  }

  @Test
  public void comparingValuesUsing_containsEntry_failsExpectedKeyHasWrongValue() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "+123", "def", "+456");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsEntry("def", 123);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=+123, def=+456}> contains an entry with "
                + "key <def> and a value that parses to <123>. "
                + "However, it has a mapping from that key to <+456>");
  }

  @Test
  public void comparingValuesUsing_containsEntry_failsWrongKeyHasExpectedValue() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "+123", "def", "+456");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsEntry("xyz", 456);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=+123, def=+456}> contains an entry with "
                + "key <xyz> and a value that parses to <456>. "
                + "However, the following keys are mapped to such values: <[def]>");
  }

  @Test
  public void comparingValuesUsing_containsEntry_failsMissingExpectedKeyAndValue() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "+123", "def", "+456");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsEntry("xyz", 321);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=+123, def=+456}> contains an entry with "
                + "key <xyz> and a value that parses to <321>");
  }

  @Test
  public void comparingValuesUsing_containsEntry_diffExpectedKeyHasWrongValue() {
    ImmutableMap<String, Integer> actual = ImmutableMap.of("abc", 35, "def", 71);
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(WITHIN_10_OF)
        .containsEntry("def", 60);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=35, def=71}> contains an entry with key <def> and a value that is "
                + "within 10 of <60>. However, it has a mapping from that key to <71> (diff: 11)");
  }

  @Test
  public void comparingValuesUsing_containsEntry_handlesFormatDiffExceptions() {
    Map<String, Integer> actual = new LinkedHashMap<>();
    actual.put("abc", 35);
    actual.put("def", null);
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(WITHIN_10_OF)
        .containsEntry("def", 60);
    assertFailureKeys(
        "Not true that <{abc=35, def=null}> contains an entry with key <def> and a value that is "
            + "within 10 of <60>. However, it has a mapping from that key to <null>",
        "additionally, one or more exceptions were thrown while comparing values",
        "first exception",
        "additionally, one or more exceptions were thrown while formatting diffs",
        "first exception");
    assertThatFailure()
        .factValue("first exception", 0)
        .startsWith("compare(null, 60) threw java.lang.NullPointerException");
    assertThatFailure()
        .factValue("first exception", 1)
        .startsWith("formatDiff(null, 60) threw java.lang.NullPointerException");
  }

  @Test
  public void comparingValuesUsing_containsEntry_handlesExceptions_expectedKeyHasWrongValue() {
    Map<Integer, String> actual = new LinkedHashMap<>();
    actual.put(1, "one");
    actual.put(2, null);
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(CASE_INSENSITIVE_EQUALITY)
        .containsEntry(2, "TWO");
    // The test fails because the expected key has a null value which causes compare() to throw.
    // We should report that the key has the wrong value, and also that we saw an exception.
    assertFailureKeys(
        "Not true that <{1=one, 2=null}> contains an entry with key <2> and a value that equals "
            + "(ignoring case) <TWO>. However, it has a mapping from that key to <null>",
        "additionally, one or more exceptions were thrown while comparing values",
        "first exception");
    assertThatFailure()
        .factValue("first exception")
        .startsWith("compare(null, TWO) threw java.lang.NullPointerException");
  }

  @Test
  public void comparingValuesUsing_containsEntry_handlesExceptions_wrongKeyHasExpectedValue() {
    Map<Integer, String> actual = new LinkedHashMap<>();
    actual.put(1, null);
    actual.put(2, "three");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(CASE_INSENSITIVE_EQUALITY)
        .containsEntry(3, "THREE");
    // The test fails and does not contain the expected key, but does contain the expected value for
    // a different key. No reasonable implementation would find this value in the second entry
    // without hitting the exception from trying the first entry (which has a null value), so we
    // should report the exception as well.
    assertFailureKeys(
        "Not true that <{1=null, 2=three}> contains an entry with key <3> and a value that equals "
            + "(ignoring case) <THREE>. However, the following keys are mapped to such values: "
            + "<[2]>",
        "additionally, one or more exceptions were thrown while comparing values",
        "first exception");
    assertThatFailure()
        .factValue("first exception")
        .startsWith("compare(null, THREE) threw java.lang.NullPointerException");
  }

  @Test
  public void comparingValuesUsing_doesNotContainEntry_successExcludedKeyHasWrongValues() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "+123", "def", "+456");
    assertThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .doesNotContainEntry("def", 123);
  }

  @Test
  public void comparingValuesUsing_doesNotContainEntry_successWrongKeyHasExcludedValue() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "+123", "def", "+456");
    assertThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .doesNotContainEntry("xyz", 456);
  }

  @Test
  public void comparingValuesUsing_doesNotContainEntry_successMissingExcludedKeyAndValue() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
    assertThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .doesNotContainEntry("xyz", 321);
  }

  @Test
  public void comparingValuesUsing_doesNotContainEntry_failure() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "+123", "def", "+456");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .doesNotContainEntry("def", 456);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=+123, def=+456}> does not contain an entry with "
                + "key <def> and a value that parses to <456>. It maps that key to <+456>");
  }

  @Test
  public void comparingValuesUsing_doesNotContainEntry_handlesException() {
    Map<Integer, String> actual = new LinkedHashMap<>();
    actual.put(1, "one");
    actual.put(2, null);
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(CASE_INSENSITIVE_EQUALITY)
        .doesNotContainEntry(2, "TWO");
    // This test would pass if compare(null, "TWO") returned false. But it actually throws, so the
    // test must fail.
    assertFailureKeys(
        "one or more exceptions were thrown while comparing values",
        "first exception",
        "comparing contents by testing that no entry had the forbidden key and a value that "
            + "equals (ignoring case) the forbidden value",
        "forbidden key",
        "forbidden value",
        "but was");
    assertThatFailure()
        .factValue("first exception")
        .startsWith("compare(null, TWO) threw java.lang.NullPointerException");
    assertFailureValue("forbidden key", "2");
    assertFailureValue("forbidden value", "TWO");
  }

  @Test
  public void comparingValuesUsing_containsExactly_success() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
    assertThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsExactly("def", 456, "abc", 123);
  }

  @Test
  public void comparingValuesUsing_containsExactly_inOrder_success() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
    assertThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsExactly("abc", 123, "def", 456)
        .inOrder();
  }

  @Test
  public void comparingValuesUsing_containsExactly_failsExtraEntry() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsExactly("def", 456);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=123, def=456}> contains exactly one entry that has a key that is "
                + "equal to and a value that parses to the key and value of each entry of "
                + "<{def=456}>. It has the following entries with unexpected keys: {abc=123}");
  }

  @Test
  public void comparingValuesUsing_containsExactly_failsMissingEntry() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsExactly("def", 456, "xyz", 999, "abc", 123);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=123, def=456}> contains exactly one entry that has a key that is "
                + "equal to and a value that parses to the key and value of each entry of "
                + "<{def=456, xyz=999, abc=123}>. It is missing keys for the following entries: "
                + "{xyz=999}");
  }

  @Test
  public void comparingValuesUsing_containsExactly_failsWrongKey() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsExactly("def", 456, "cab", 123);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=123, def=456}> contains exactly one entry that has a key that is "
                + "equal to and a value that parses to the key and value of each entry of "
                + "<{def=456, cab=123}>. It is missing keys for the following entries: {cab=123} "
                + "and has the following entries with unexpected keys: {abc=123}");
  }

  @Test
  public void comparingValuesUsing_containsExactly_failsWrongValue() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsExactly("def", 456, "abc", 321);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=123, def=456}> contains exactly one entry that has a key that is "
                + "equal to and a value that parses to the key and value of each entry of "
                + "<{def=456, abc=321}>. It has the following entries with matching keys but "
                + "different values: {abc=(expected 321 but got 123)}");
  }

  @Test
  public void comparingValuesUsing_containsExactly_handlesExceptions() {
    Map<Integer, String> actual = new LinkedHashMap<>();
    actual.put(1, "one");
    actual.put(2, null);
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(CASE_INSENSITIVE_EQUALITY)
        .containsExactly(1, "ONE", 2, "TWO");
    assertFailureKeys(
        "Not true that <{1=one, 2=null}> contains exactly one entry that has a key that is "
            + "equal to and a value that equals (ignoring case) the key and value of each entry of "
            + "<{1=ONE, 2=TWO}>. It has the following entries with matching keys but different "
            + "values: {2=(expected TWO but got null)}",
        "additionally, one or more exceptions were thrown while comparing values",
        "first exception");
    assertThatFailure()
        .factValue("first exception")
        .startsWith("compare(null, TWO) threw java.lang.NullPointerException");
  }

  @Test
  public void comparingValuesUsing_containsExactly_inOrder_failsOutOfOrder() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsExactly("def", 456, "abc", 123)
        .inOrder();
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=123, def=456}> contains, in order, exactly one entry that has"
                + " a key that is equal to and a value that parses to the key and value of each"
                + " entry of <{def=456, abc=123}>");
  }

  @Test
  public void comparingValuesUsing_containsExactly_wrongValueTypeInActual() {
    ImmutableMap<String, Object> actual = ImmutableMap.<String, Object>of("abc", "123", "def", 456);
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsExactly("def", 456, "abc", 123);
    assertFailureKeys(
        "Not true that <{abc=123, def=456}> contains exactly one entry that has a key that is "
            + "equal to and a value that parses to the key and value of each entry of "
            + "<{def=456, abc=123}>. It has the following entries with matching keys but "
            + "different values: {def=(expected 456 but got 456)}",
        "additionally, one or more exceptions were thrown while comparing values",
        "first exception");
    assertThatFailure()
        .factValue("first exception")
        .startsWith("compare(456, 456) threw java.lang.ClassCastException");
  }

  @Test
  public void comparingValuesUsing_containsExactly_wrongValueTypeInExpected() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsExactly("def", 456, "abc", 123L);
    assertFailureKeys(
        "Not true that <{abc=123, def=456}> contains exactly one entry that has a key that is "
            + "equal to and a value that parses to the key and value of each entry of "
            + "<{def=456, abc=123}>. It has the following entries with matching keys but "
            + "different values: {abc=(expected 123 but got 123)}",
        "additionally, one or more exceptions were thrown while comparing values",
        "first exception");
    assertThatFailure()
        .factValue("first exception")
        .startsWith("compare(123, 123) threw java.lang.ClassCastException");
  }

  @Test
  public void comparingValuesUsing_containsExactlyEntriesIn_success() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456, "abc", 123);
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
    assertThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsExactlyEntriesIn(expected);
  }

  @Test
  public void comparingValuesUsing_containsExactlyEntriesIn_inOrder_success() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of("abc", 123, "def", 456);
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
    assertThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsExactlyEntriesIn(expected)
        .inOrder();
  }

  @Test
  public void comparingValuesUsing_containsExactlyEntriesIn_failsExtraEntry() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456);
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsExactlyEntriesIn(expected);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=123, def=456}> contains exactly one entry that has a key that is "
                + "equal to and a value that parses to the key and value of each entry of "
                + "<{def=456}>. It has the following entries with unexpected keys: {abc=123}");
  }

  @Test
  public void comparingValuesUsing_containsExactlyEntriesIn_failsMissingEntry() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456, "xyz", 999, "abc", 123);
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsExactlyEntriesIn(expected);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=123, def=456}> contains exactly one entry that has a key that is "
                + "equal to and a value that parses to the key and value of each entry of "
                + "<{def=456, xyz=999, abc=123}>. It is missing keys for the following entries: "
                + "{xyz=999}");
  }

  @Test
  public void comparingValuesUsing_containsExactlyEntriesIn_failsWrongKey() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456, "cab", 123);
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsExactlyEntriesIn(expected);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=123, def=456}> contains exactly one entry that has a key that is "
                + "equal to and a value that parses to the key and value of each entry of "
                + "<{def=456, cab=123}>. It is missing keys for the following entries: {cab=123} "
                + "and has the following entries with unexpected keys: {abc=123}");
  }

  @Test
  public void comparingValuesUsing_containsExactlyEntriesIn_failsWrongValue() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456, "abc", 321);
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsExactlyEntriesIn(expected);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=123, def=456}> contains exactly one entry that has a key that is "
                + "equal to and a value that parses to the key and value of each entry of "
                + "<{def=456, abc=321}>. It has the following entries with matching keys but "
                + "different values: {abc=(expected 321 but got 123)}");
  }

  @Test
  public void comparingValuesUsing_containsExactlyEntriesIn_diffMissingAndExtraAndWrongValue() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of("abc", 30, "def", 60, "ghi", 90);
    ImmutableMap<String, Integer> actual = ImmutableMap.of("abc", 35, "fed", 60, "ghi", 101);
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(WITHIN_10_OF)
        .containsExactlyEntriesIn(expected);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=35, fed=60, ghi=101}> contains exactly one entry that has a key "
                + "that is equal to and a value that is within 10 of the key and value of each "
                + "entry of <{abc=30, def=60, ghi=90}>. It is missing keys for the following "
                + "entries: {def=60} and has the following entries with unexpected keys: {fed=60} "
                + "and has the following entries with matching keys but different values: "
                + "{ghi=(expected 90 but got 101, diff: 11)}");
  }

  @Test
  public void comparingValuesUsing_containsExactlyEntriesIn_handlesFormatDiffExceptions() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of("abc", 30, "def", 60, "ghi", 90);
    Map<String, Integer> actual = new LinkedHashMap<>();
    actual.put("abc", 35);
    actual.put("def", null);
    actual.put("ghi", 95);
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(WITHIN_10_OF)
        .containsExactlyEntriesIn(expected);
    assertFailureKeys(
        "Not true that <{abc=35, def=null, ghi=95}> contains exactly one entry that has a key that "
            + "is equal to and a value that is within 10 of the key and value of each entry of "
            + "<{abc=30, def=60, ghi=90}>. It has the following entries with matching keys but "
            + "different values: {def=(expected 60 but got null)}",
        "additionally, one or more exceptions were thrown while comparing values",
        "first exception",
        "additionally, one or more exceptions were thrown while formatting diffs",
        "first exception");
    assertThatFailure()
        .factValue("first exception", 0)
        .startsWith("compare(null, 60) threw java.lang.NullPointerException");
    assertThatFailure()
        .factValue("first exception", 1)
        .startsWith("formatDiff(null, 60) threw java.lang.NullPointerException");
  }

  @Test
  public void comparingValuesUsing_containsExactlyEntriesIn_inOrder_failsOutOfOrder() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456, "abc", 123);
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsExactlyEntriesIn(expected)
        .inOrder();
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=123, def=456}> contains, in order, exactly one entry that has"
                + " a key that is equal to and a value that parses to the key and value of each"
                + " entry of <{def=456, abc=123}>");
  }

  @Test
  public void comparingValuesUsing_containsExactlyEntriesIn_empty() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of();
    ImmutableMap<String, String> actual = ImmutableMap.of();
    assertThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsExactlyEntriesIn(expected);
  }

  @Test
  public void comparingValuesUsing_containsExactlyEntriesIn_failsEmpty() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of();
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsExactlyEntriesIn(expected);
    assertFailureKeys("expected to be empty", "but was");
  }

  @Test
  public void comparingValuesUsing_containsExactlyEntriesIn_wrongValueTypeInActual() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456, "abc", 123);
    ImmutableMap<String, Object> actual = ImmutableMap.<String, Object>of("abc", "123", "def", 456);
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsExactlyEntriesIn(expected);
    assertFailureKeys(
        "Not true that <{abc=123, def=456}> contains exactly one entry that has a key that is "
            + "equal to and a value that parses to the key and value of each entry of "
            + "<{def=456, abc=123}>. It has the following entries with matching keys but "
            + "different values: {def=(expected 456 but got 456)}",
        "additionally, one or more exceptions were thrown while comparing values",
        "first exception");
    assertThatFailure()
        .factValue("first exception")
        .startsWith("compare(456, 456) threw java.lang.ClassCastException");
  }

  @Test
  public void comparingValuesUsing_containsAtLeast_success() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456", "ghi", "789");
    assertThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsAtLeast("def", 456, "abc", 123);
  }

  @Test
  public void comparingValuesUsing_containsAtLeast_inOrder_success() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "ghi", "789", "def", "456");
    assertThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsAtLeast("abc", 123, "def", 456)
        .inOrder();
  }

  @Test
  public void comparingValuesUsing_containsAtLeast_failsMissingEntry() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456", "ghi", "789");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsAtLeast("def", 456, "xyz", 999, "abc", 123);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=123, def=456, ghi=789}> contains at least one entry that has a "
                + "key that is equal to and a value that parses to the key and value of each entry "
                + "of <{def=456, xyz=999, abc=123}>. It is missing keys for the following entries: "
                + "{xyz=999}");
  }

  @Test
  public void comparingValuesUsing_containsAtLeast_failsWrongKey() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsAtLeast("def", 456, "cab", 123);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=123, def=456}> contains at least one entry that has a key that is "
                + "equal to and a value that parses to the key and value of each entry of "
                + "<{def=456, cab=123}>. It is missing keys for the following entries: {cab=123}");
  }

  @Test
  public void comparingValuesUsing_containsAtLeast_failsWrongValue() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsAtLeast("abc", 321);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=123, def=456}> contains at least one entry that has a key that is "
                + "equal to and a value that parses to the key and value of each entry of "
                + "<{abc=321}>. It has the following entries with matching keys but "
                + "different values: {abc=(expected 321 but got 123)}");
  }

  @Test
  public void comparingValuesUsing_containsAtLeast_handlesExceptions() {
    Map<Integer, String> actual = new LinkedHashMap<>();
    actual.put(1, "one");
    actual.put(2, null);
    actual.put(3, "three");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(CASE_INSENSITIVE_EQUALITY)
        .containsAtLeast(1, "ONE", 2, "TWO");
    assertFailureKeys(
        "Not true that <{1=one, 2=null, 3=three}> contains at least one entry that has a key that "
            + "is equal to and a value that equals (ignoring case) the key and value of each "
            + "entry of <{1=ONE, 2=TWO}>. It has the following entries with matching keys but "
            + "different values: {2=(expected TWO but got null)}",
        "additionally, one or more exceptions were thrown while comparing values",
        "first exception");
    assertThatFailure()
        .factValue("first exception")
        .startsWith("compare(null, TWO) threw java.lang.NullPointerException");
  }

  @Test
  public void comparingValuesUsing_containsAtLeast_inOrder_failsOutOfOrder() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456", "ghi", "789");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsAtLeast("def", 456, "abc", 123)
        .inOrder();
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=123, def=456, ghi=789}> contains, in order, at least one entry "
                + "that has a key that is equal to and a value that parses to the key and value of "
                + "each entry of <{def=456, abc=123}>");
  }

  @Test
  public void comparingValuesUsing_containsAtLeast_wrongValueTypeInExpectedActual() {
    ImmutableMap<String, Object> actual = ImmutableMap.<String, Object>of("abc", "123", "def", 456);
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsAtLeast("def", 456);
    assertFailureKeys(
        "Not true that <{abc=123, def=456}> contains at least one entry that has a key that is "
            + "equal to and a value that parses to the key and value of each entry of "
            + "<{def=456}>. It has the following entries with matching keys but "
            + "different values: {def=(expected 456 but got 456)}",
        "additionally, one or more exceptions were thrown while comparing values",
        "first exception");
    assertThatFailure()
        .factValue("first exception")
        .startsWith("compare(456, 456) threw java.lang.ClassCastException");
  }

  @Test
  public void comparingValuesUsing_containsAtLeast_wrongValueTypeInUnexpectedActual_success() {
    ImmutableMap<String, Object> actual = ImmutableMap.<String, Object>of("abc", "123", "def", 456);
    assertThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsAtLeast("abc", 123);
  }

  @Test
  public void comparingValuesUsing_containsAtLeast_wrongValueTypeInExpected() {
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456", "ghi", "789");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsAtLeast("def", 456, "abc", 123L);
    assertFailureKeys(
        "Not true that <{abc=123, def=456, ghi=789}> contains at least one entry that has a key "
            + "that is equal to and a value that parses to the key and value of each entry of "
            + "<{def=456, abc=123}>. It has the following entries with matching keys but "
            + "different values: {abc=(expected 123 but got 123)}",
        "additionally, one or more exceptions were thrown while comparing values",
        "first exception");
    assertThatFailure()
        .factValue("first exception")
        .startsWith("compare(123, 123) threw java.lang.ClassCastException");
  }

  @Test
  public void comparingValuesUsing_containsAtLeastEntriesIn_success() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456, "abc", 123);
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456", "ghi", "789");
    assertThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsAtLeastEntriesIn(expected);
  }

  @Test
  public void comparingValuesUsing_containsAtLeastEntriesIn_inOrder_success() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of("abc", 123, "ghi", 789);
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456", "ghi", "789");
    assertThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsAtLeastEntriesIn(expected)
        .inOrder();
  }

  @Test
  public void comparingValuesUsing_containsAtLeastEntriesIn_failsMissingEntry() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456, "xyz", 999, "abc", 123);
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456", "ghi", "789");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsAtLeastEntriesIn(expected);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=123, def=456, ghi=789}> contains at least one entry that has a "
                + "key that is equal to and a value that parses to the key and value of each entry "
                + "of <{def=456, xyz=999, abc=123}>. It is missing keys for the following entries: "
                + "{xyz=999}");
  }

  @Test
  public void comparingValuesUsing_containsAtLeastEntriesIn_failsWrongKey() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456, "cab", 123);
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsAtLeastEntriesIn(expected);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=123, def=456}> contains at least one entry that has a key that is "
                + "equal to and a value that parses to the key and value of each entry of "
                + "<{def=456, cab=123}>. It is missing keys for the following entries: {cab=123}");
  }

  @Test
  public void comparingValuesUsing_containsAtLeastEntriesIn_failsWrongValue() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456, "abc", 321);
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456", "ghi", "789");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsAtLeastEntriesIn(expected);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=123, def=456, ghi=789}> contains at least one entry that has a "
                + "key that is equal to and a value that parses to the key and value of each entry "
                + "of <{def=456, abc=321}>. It has the following entries with matching keys but "
                + "different values: {abc=(expected 321 but got 123)}");
  }

  @Test
  public void comparingValuesUsing_containsAtLeastEntriesIn_diffMissingAndExtraAndWrongValue() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of("abc", 30, "def", 60, "ghi", 90);
    ImmutableMap<String, Integer> actual = ImmutableMap.of("abc", 35, "fed", 60, "ghi", 101);
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(WITHIN_10_OF)
        .containsAtLeastEntriesIn(expected);
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=35, fed=60, ghi=101}> contains at least one entry that has a key "
                + "that is equal to and a value that is within 10 of the key and value of each "
                + "entry of <{abc=30, def=60, ghi=90}>. It is missing keys for the following "
                + "entries: {def=60} "
                + "and has the following entries with matching keys but different values: "
                + "{ghi=(expected 90 but got 101, diff: 11)}");
  }

  @Test
  public void comparingValuesUsing_containsAtLeastEntriesIn_handlesFormatDiffExceptions() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of("abc", 30, "def", 60, "ghi", 90);
    Map<String, Integer> actual = new LinkedHashMap<>();
    actual.put("abc", 35);
    actual.put("def", null);
    actual.put("ghi", 95);
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(WITHIN_10_OF)
        .containsAtLeastEntriesIn(expected);
    assertFailureKeys(
        "Not true that <{abc=35, def=null, ghi=95}> contains at least one entry that has a key that "
            + "is equal to and a value that is within 10 of the key and value of each entry of "
            + "<{abc=30, def=60, ghi=90}>. It has the following entries with matching keys but "
            + "different values: {def=(expected 60 but got null)}",
        "additionally, one or more exceptions were thrown while comparing values",
        "first exception",
        "additionally, one or more exceptions were thrown while formatting diffs",
        "first exception");
    assertThatFailure()
        .factValue("first exception", 0)
        .startsWith("compare(null, 60) threw java.lang.NullPointerException");
    assertThatFailure()
        .factValue("first exception", 1)
        .startsWith("formatDiff(null, 60) threw java.lang.NullPointerException");
  }

  @Test
  public void comparingValuesUsing_containsAtLeastEntriesIn_inOrder_failsOutOfOrder() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of("ghi", 789, "abc", 123);
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456", "ghi", "789");
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsAtLeastEntriesIn(expected)
        .inOrder();
    assertThat(expectFailure.getFailure())
        .hasMessageThat()
        .isEqualTo(
            "Not true that <{abc=123, def=456, ghi=789}> contains, in order, at least one entry "
                + "that has a key that is equal to and a value that parses to the key and value of "
                + "each entry of <{ghi=789, abc=123}>");
  }

  @Test
  public void comparingValuesUsing_containsAtLeastEntriesIn_empty() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of();
    ImmutableMap<String, String> actual = ImmutableMap.of("abc", "123", "def", "456");
    assertThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsAtLeastEntriesIn(expected);
  }

  @Test
  public void comparingValuesUsing_containsAtLeastEntriesIn_wrongValueTypeInExpectedActual() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of("def", 456);
    ImmutableMap<String, Object> actual = ImmutableMap.<String, Object>of("abc", "123", "def", 456);
    expectFailureWhenTestingThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsAtLeastEntriesIn(expected);
    assertFailureKeys(
        "Not true that <{abc=123, def=456}> contains at least one entry that has a key that is "
            + "equal to and a value that parses to the key and value of each entry of "
            + "<{def=456}>. It has the following entries with matching keys but "
            + "different values: {def=(expected 456 but got 456)}",
        "additionally, one or more exceptions were thrown while comparing values",
        "first exception");
    assertThatFailure()
        .factValue("first exception")
        .startsWith("compare(456, 456) threw java.lang.ClassCastException");
  }

  @Test
  public void
      comparingValuesUsing_containsAtLeastEntriesIn_wrongValueTypeInUnexpectedActual_success() {
    ImmutableMap<String, Integer> expected = ImmutableMap.of("abc", 123);
    ImmutableMap<String, Object> actual = ImmutableMap.<String, Object>of("abc", "123", "def", 456);
    assertThat(actual)
        .comparingValuesUsing(STRING_PARSES_TO_INTEGER_CORRESPONDENCE)
        .containsAtLeastEntriesIn(expected);
  }

  private MapSubject expectFailureWhenTestingThat(Map<?, ?> actual) {
    return expectFailure.whenTesting().that(actual);
  }
}
