/*
 * Copyright 2011 the original author or authors.
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
package org.gradle.integtests.fixtures

import org.gradle.CacheUsage
import org.gradle.api.Action
import org.gradle.cache.PersistentCache
import org.gradle.cache.internal.CacheFactory
import org.gradle.cache.internal.DefaultCacheFactory
import org.gradle.cache.internal.DefaultFileLockManager
import org.gradle.cache.internal.DefaultProcessMetaDataProvider
import org.gradle.cache.internal.FileLockManager.LockMode
import org.gradle.internal.jvm.Jvm
import org.gradle.internal.nativeplatform.ProcessEnvironment
import org.gradle.internal.nativeplatform.services.NativeServices
import org.gradle.internal.os.OperatingSystem
import org.gradle.launcher.daemon.registry.DaemonRegistry
import org.gradle.util.DistributionLocator
import org.gradle.util.GradleVersion
import org.gradle.util.TestFile

public class PreviousGradleVersionExecuter extends AbstractGradleExecuter implements BasicGradleDistribution {
    private static final CACHE_FACTORY = createCacheFactory()

    private static CacheFactory createCacheFactory() {
        return new DefaultCacheFactory(
                new DefaultFileLockManager(
                        new DefaultProcessMetaDataProvider(
                                new NativeServices().get(ProcessEnvironment)),
                        20 * 60 * 1000 // allow up to 20 minutes to download a distribution
                )).create()
    }

    private final GradleDistribution dist
    final GradleVersion version
    private final TestFile versionDir
    private final TestFile zipFile
    private final TestFile homeDir
    private PersistentCache cache

    PreviousGradleVersionExecuter(GradleDistribution dist, String version) {
        this.dist = dist
        this.version = GradleVersion.version(version)
        versionDir = dist.previousVersionsDir.file(version)
        zipFile = versionDir.file("gradle-$version-bin.zip")
        homeDir = versionDir.file("gradle-$version")
    }

    def String toString() {
        version.toString()
    }

    String getVersion() {
        return version.version
    }

    boolean worksWith(Jvm jvm) {
        // Milestone 4 was broken on the IBM jvm
        if (jvm.ibmJvm && version == GradleVersion.version('1.0-milestone-4')) {
            return false
        }
        // 0.9-rc-1 was broken for Java 5
        if (version == GradleVersion.version('0.9-rc-1')) {
            return jvm.javaVersion.isJava6Compatible()
        }

        return jvm.javaVersion.isJava5Compatible()
    }

    boolean worksWith(OperatingSystem os) {
        // 1.0-milestone-5 was broken where jna was not available
        if (version == GradleVersion.version("1.0-milestone-5")) {
            return os.windows || os.macOsX || os.linux
        }
        return true
    }

    DaemonRegistry getDaemonRegistry() {
        throw new UnsupportedOperationException()
    }
    
    boolean isDaemonSupported() {
        // Milestone 7 was broken on the IBM jvm
        if (Jvm.current().ibmJvm && version == GradleVersion.version('1.0-milestone-7')) {
            return false
        }

        if (OperatingSystem.current().isWindows()) {
            // On windows, daemon is ok for anything > 1.0-milestone-3
            return version > GradleVersion.version('1.0-milestone-3')
        } else {
            // Daemon is ok for anything >= 0.9
            return version >= GradleVersion.version('0.9')
        }
    }

    boolean isDaemonIdleTimeoutConfigurable() {
        return version > GradleVersion.version('1.0-milestone-6')
    }

    boolean isOpenApiSupported() {
        return version >= GradleVersion.version('0.9-rc-1')
    }

    boolean isToolingApiSupported() {
        return version >= GradleVersion.version('1.0-milestone-3')
    }

    boolean wrapperCanExecute(String version) {
        if (version == '0.8' || this.version == GradleVersion.version('0.8')) {
            // There was a breaking change after 0.8
            return false
        }
        if (this.version == GradleVersion.version('0.9.1')) {
            // 0.9.1 couldn't handle anything with a timestamp whose timezone was behind GMT
            return version.matches('.*+\\d{4}')
        }
        if (this.version >= GradleVersion.version('0.9.2') && this.version <= GradleVersion.version('1.0-milestone-2')) {
            // These versions couldn't handle milestone patches
            if (version.matches('1.0-milestone-\\d+[a-z]-.+')) {
                return false
            }
        }
        return true
    }

    protected ExecutionResult doRun() {
        ForkingGradleExecuter executer = new ForkingGradleExecuter(gradleHomeDir)
        executer.inDirectory(dist.testDir)
        copyTo(executer)
        return executer.run()
    }

    GradleExecuter executer() {
        this
    }

    TestFile getBinDistribution() {
        download()
        return zipFile
    }

    private URL getBinDistributionUrl() {
        return new DistributionLocator().getDistributionFor(version).toURL()
    }

    def TestFile getGradleHomeDir() {
        download()
        return homeDir
    }

    private void download() {
        if (cache == null) {
            def downloadAction = { cache ->
                URL url = binDistributionUrl
                System.out.println("downloading $url");
                zipFile.copyFrom(url)
                zipFile.usingNativeTools().unzipTo(versionDir)
            }
            cache = CACHE_FACTORY.open(versionDir, version.toString(), CacheUsage.ON, null, [:], LockMode.Shared, downloadAction as Action)
        }
        zipFile.assertIsFile()
        homeDir.assertIsDir()
    }

    protected ExecutionFailure doRunWithFailure() {
        throw new UnsupportedOperationException();
    }
}
