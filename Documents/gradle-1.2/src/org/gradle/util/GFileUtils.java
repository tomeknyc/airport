/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.gradle.api.UncheckedIOException;
import org.gradle.internal.UncheckedException;
import org.gradle.util.internal.LimitedDescription;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.Checksum;

/**
 * @author Hans Dockter
 */
public class GFileUtils {

    public static FileInputStream openInputStream(File file) {
        try {
            return FileUtils.openInputStream(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void touch(File file) {
        try {
            FileUtils.touch(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String readFile(File file) {
        return readFile(file, Charset.defaultCharset().name());
    }

    public static String readFile(File file, String encoding) {
        try {
            return FileUtils.readFileToString(file, encoding);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void writeFile(String content, File destination) {
        writeFile(content, destination, Charset.defaultCharset().name());
    }

    public static void writeFile(String content, File destination, String encoding) {
        try {
            FileUtils.writeStringToFile(destination, content, encoding);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Collection listFiles(File directory, IOFileFilter fileFilter, IOFileFilter dirFilter) {
        return FileUtils.listFiles(directory, fileFilter, dirFilter);
    }

    public static Collection listFiles(File directory, String[] extensions, boolean recursive) {
        return FileUtils.listFiles(directory, extensions, recursive);
    }

    public static List<String> toPaths(Collection<File> files) {
        List<String> paths = new ArrayList<String>();
        for (File file : files) {
            paths.add(file.getAbsolutePath());
        }
        return paths;
    }

    public static List<URL> urisToUrls(Iterable<URI> uris) {
        List<URL> urls = new ArrayList<URL>();
        for (URI uri : uris) {
            try {
                urls.add(uri.toURL());
            } catch (MalformedURLException e) {
                throw new UncheckedIOException(e);
            }
        }
        return urls;
    }

    public static void copyURLToFile(URL source, File destination) {
        try {
            FileUtils.copyURLToFile(source, destination);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void deleteDirectory(File directory) {
        try {
            FileUtils.deleteDirectory(directory);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void cleanDirectory(File directory) {
        try {
            FileUtils.cleanDirectory(directory);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static boolean deleteQuietly(File file) {
        return FileUtils.deleteQuietly(file);
    }

    public static class TailReadingException extends RuntimeException {
        public TailReadingException(Throwable throwable) {
            super(throwable);
        }
    }

    /**
     * @param file to read from tail
     * @param maxLines max lines to read
     * @return tail content
     * @throws org.gradle.util.GFileUtils.TailReadingException when reading failed
     */
    public static String tail(File file, int maxLines) throws TailReadingException {
        BufferedReader reader = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            reader = new BufferedReader(fileReader);

            LimitedDescription description = new LimitedDescription(maxLines);
            String line = reader.readLine();
            while (line != null) {
                description.append(line);
                line = reader.readLine();
            }
            return description.toString();
        } catch (Exception e) {
            throw new TailReadingException(e);
        } finally {
            IOUtils.closeQuietly(fileReader);
            IOUtils.closeQuietly(reader);
        }
    }

    public static void writeStringToFile(File file, String data) {
        try {
            FileUtils.writeStringToFile(file, data);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void forceDelete(File file) {
        try {
            FileUtils.forceDelete(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Checksum checksum(File file, Checksum checksum) {
        try {
            return FileUtils.checksum(file, checksum);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static File canonicalise(File src) {
        try {
            return src.getCanonicalFile();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    /**
     * Creates a directory and any unexisting parent directories. Throws an
     * UncheckedIOException if it fails to do so.
     */
    public static void createDirectory(File directory) {
        if (!directory.exists() && !directory.mkdirs()) {
            throw new UncheckedIOException("Failed to create directory " + directory);
        }
    }

    /**
     * Returns a relative path from 'from' to 'to'
     *
     * @param from where to calculate from
     * @param to where to calculate to
     * @return The relative path
     */
    public static String relativePath(File from, File to) {
        try {
            return org.apache.tools.ant.util.FileUtils.getRelativePath(from, to);
        } catch (Exception e) {
            throw UncheckedException.throwAsUncheckedException(e);
        }
    }
}
