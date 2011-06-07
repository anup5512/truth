/*
 * Copyright (c) 2011 David Saff
 * Copyright (c) 2011 Christian Gruber
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
package org.junit.contrib.truth;

import java.util.Collection;

import org.junit.contrib.truth.subjects.CollectionSubject;
import org.junit.contrib.truth.subjects.IntegerSubject;
import org.junit.contrib.truth.subjects.StringSubject;
import org.junit.contrib.truth.subjects.Subject;

public class TestVerb {
  private final FailureStrategy failureStrategy;

  public TestVerb(FailureStrategy failureStrategy) {
    this.failureStrategy = failureStrategy;
  }
  
  public Subject<Object> that(Object o) {
    return new Subject<Object>(getFailureStrategy(), o);
  }

  public IntegerSubject that(Long i) {
    return new IntegerSubject(getFailureStrategy(), i);
  }

  public IntegerSubject that(Integer i) {
    return new IntegerSubject(getFailureStrategy(), i);
  }

  public StringSubject that(String string) {
    return new StringSubject(getFailureStrategy(), string);
  }

  protected FailureStrategy getFailureStrategy() {
    return failureStrategy;
  }

  public <T> CollectionSubject<T> that(Collection<T> list) {
    return new CollectionSubject<T>(getFailureStrategy(), list);
  }
}
