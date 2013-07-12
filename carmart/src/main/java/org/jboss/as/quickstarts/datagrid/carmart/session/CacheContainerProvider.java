/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.quickstarts.datagrid.carmart.session;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.infinispan.api.BasicCacheContainer;

/**
 * 
 * Subclasses should create an instance of a cache manager (DefaultCacheManager, 
 * RemoteCacheManager, etc.)
 * 
 * @author Martin Gencur
 * 
 */
public abstract class CacheContainerProvider {

   public static final String DATAGRID_HOST = "datagrid.host"; 
   public static final String HOTROD_PORT = "datagrid.hotrod.port"; 
   public static final String PROPERTIES_FILE = "META-INF" + File.separator + "datagrid.properties";
   
   abstract public BasicCacheContainer getCacheContainer();
   
   protected String jdgProperty(String name) {
       Properties props = new Properties();
       try { 
           props.load(this.getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE));
       } catch (IOException ioe) {
           throw new RuntimeException(ioe);
       }
       return props.getProperty(name);
   }
}
