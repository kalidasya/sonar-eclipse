/*
 * Sonar Eclipse
 * Copyright (C) 2010-2012 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.ide.eclipse.internal.mylyn.core;

import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class UtilsTest {

  @Test
  public void testToMylynPriority() {
    assertThat(Utils.toMylynPriority("BLOCKER"), is(PriorityLevel.P1));
    assertThat(Utils.toMylynPriority("CRITICAL"), is(PriorityLevel.P2));
    assertThat(Utils.toMylynPriority("MAJOR"), is(PriorityLevel.P3));
    assertThat(Utils.toMylynPriority("MINOR"), is(PriorityLevel.P4));
    assertThat(Utils.toMylynPriority("INFO"), is(PriorityLevel.P5));
    assertThat(Utils.toMylynPriority("unknown"), nullValue());
    assertThat(Utils.toMylynPriority(null), nullValue());
  }

}
