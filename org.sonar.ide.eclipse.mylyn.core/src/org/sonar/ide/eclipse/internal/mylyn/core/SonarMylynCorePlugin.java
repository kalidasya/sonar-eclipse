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

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class SonarMylynCorePlugin extends Plugin {

  public static final String PLUGIN_ID = "org.sonar.ide.eclipse.mylyn.core"; //$NON-NLS-1$

  /**
   * Minimal supported version of Sonar server.
   */
  public static final String MINIMAL_SONAR_VERSION = "3.4"; //$NON-NLS-1$

  private static SonarMylynCorePlugin plugin;

  private SonarConnector connector;

  public SonarMylynCorePlugin() {
  }

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  public SonarConnector getConnector() {
    return connector;
  }

  void setConnector(SonarConnector connector) {
    this.connector = connector;
  }

  public static SonarMylynCorePlugin getDefault() {
    return plugin;
  }

}
