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
package org.sonar.ide.wsclient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.ide.api.SourceCode;
import org.sonar.ide.api.SourceCodeDiff;
import org.sonar.ide.shared.violations.ViolationUtils;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;
import org.sonar.wsclient.services.Source;
import org.sonar.wsclient.services.SourceQuery;
import org.sonar.wsclient.services.Violation;
import org.sonar.wsclient.services.ViolationQuery;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Evgeny Mandrikov
 * @since 0.2
 */
class RemoteSourceCode implements SourceCode {

  private static final Logger LOG = LoggerFactory.getLogger(RemoteSourceCode.class);

  private final String key;
  private final String name;
  private RemoteSonarIndex index;

  private String localContent;

  /**
   * Lazy initialization - see {@link #getDiff()}.
   */
  private SourceCodeDiff diff;

  /**
   * Lazy initialization - see {@link #getRemoteContentAsArray()}.
   */
  private String[] remoteContent;

  /**
   * Lazy initialization - see {@link #getChildren()}.
   */
  private Set<SourceCode> children;

  public RemoteSourceCode(String key) {
    this(key, null);
  }

  public RemoteSourceCode(String key, String name) {
    this.key = key;
    this.name = name;
  }

  /**
   * {@inheritDoc}
   */
  public String getKey() {
    return key;
  }

  /**
   * {@inheritDoc}
   */
  public String getName() {
    return name;
  }

  /**
   * {@inheritDoc}
   */
  public Set<SourceCode> getChildren() {
    if (children == null) {
      ResourceQuery query = new ResourceQuery().setDepth(1).setResourceKeyOrId(getKey());
      Collection<Resource> resources = index.getSonar().findAll(query);
      children = new HashSet<SourceCode>();
      for (Resource resource : resources) {
        children.add(new RemoteSourceCode(resource.getKey(), resource.getName()).setRemoteSonarIndex(index));
      }
    }
    return children;
  }

  /**
   * {@inheritDoc}
   */
  public SourceCode setLocalContent(final String content) {
    this.localContent = content;
    return this;
  }

  private String getLocalContent() {
    if (localContent == null) {
      return "";
    }
    return localContent;
  }

  private String[] getRemoteContentAsArray() {
    if (remoteContent == null) {
      remoteContent = SimpleSourceCodeDiffEngine.getLines(getCode());
    }
    return remoteContent;
  }

  public String getRemoteContent() {
    return StringUtils.join(getRemoteContentAsArray(), "\n");
  }

  private SourceCodeDiff getDiff() {
    if (diff == null) {
      diff = index.getDiffEngine().diff(SimpleSourceCodeDiffEngine.split(getLocalContent()), getRemoteContentAsArray());
    }
    return diff;
  }

  /**
   * {@inheritDoc}
   */
  public List<Violation> getViolations() {
    LOG.debug("Loading violations for {}", getKey());
    final Collection<Violation> violations = index.getSonar().findAll(ViolationQuery.createForResource(getKey()).setIncludeReview(true));
    LOG.debug("Loaded {} violations: {}", violations.size(), ViolationUtils.toString(violations));
    return ViolationUtils.convertLines(violations, getDiff());
  }

  /**
   * {@inheritDoc}
   */
  public List<Violation> getViolations2() {
    return getRemoteSonarIndex().getSonar().findAll(new ViolationQuery(key).setDepth(-1).setIncludeReview(true));
  }

  private Source getCode() {
    return index.getSonar().find(new SourceQuery(getKey()));
  }

  /**
   * {@inheritDoc}
   */
  public int compareTo(final SourceCode resource) {
    return key.compareTo(resource.getKey());
  }

  @Override
  public boolean equals(final Object obj) {
    return (obj instanceof RemoteSourceCode) && (key.equals(((RemoteSourceCode) obj).key));
  }

  @Override
  public int hashCode() {
    return key.hashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("key", key).toString();
  }

  protected RemoteSourceCode setRemoteSonarIndex(final RemoteSonarIndex index) {
    this.index = index;
    return this;
  }

  protected RemoteSonarIndex getRemoteSonarIndex() {
    return index;
  }
}
