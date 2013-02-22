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
package org.gradle.api.internal.tasks.testing.junit.report;

import org.gradle.api.Action;
import org.gradle.reporting.CodePanelRenderer;
import org.w3c.dom.Element;

class ClassPageRenderer extends PageRenderer<ClassTestResults> {
    private final CodePanelRenderer codePanelRenderer = new CodePanelRenderer();

    @Override protected void renderBreadcrumbs(Element parent) {
        Element div = append(parent, "div");
        div.setAttribute("class", "breadcrumbs");
        appendLink(div, "index.html", "all");
        appendText(div, " > ");
        appendLink(div, String.format("%s.html", getResults().getPackageResults().getName()), getResults().getPackageResults().getName());
        appendText(div, String.format(" > %s", getResults().getSimpleName()));
    }

    private void renderTests(Element parent) {
        Element table = append(parent, "table");
        Element thead = append(table, "thead");
        Element tr = append(thead, "tr");
        appendWithText(tr, "th", "Test");
        appendWithText(tr, "th", "Duration");
        appendWithText(tr, "th", "Result");
        for (TestResult test : getResults().getTestResults()) {
            tr = append(table, "tr");
            Element td = appendWithText(tr, "td", test.getName());
            td.setAttribute("class", test.getStatusClass());
            appendWithText(tr, "td", test.getFormattedDuration());
            td = appendWithText(tr, "td", test.getFormattedResultType());
            td.setAttribute("class", test.getStatusClass());
        }
    }

    @Override protected void renderFailures(Element parent) {
        for (TestResult test : getResults().getFailures()) {
            Element div = append(parent, "div");
            div.setAttribute("class", "test");
            append(div, "a").setAttribute("name", test.getId().toString());
            appendWithText(div, "h3", test.getName()).setAttribute("class", test.getStatusClass());
            for (TestFailure failure : test.getFailures()) {
                codePanelRenderer.render(failure.getStackTrace(), div);
            }
        }
    }

    private void renderStdOut(Element parent) {
        codePanelRenderer.render(getResults().getStandardOutput().toString(), parent);
    }

    private void renderStdErr(Element parent) {
        codePanelRenderer.render(getResults().getStandardError().toString(), parent);
    }

    @Override protected void registerTabs() {
        addFailuresTab();
        addTab("Tests", new Action<Element>() {
            public void execute(Element element) {
                renderTests(element);
            }
        });
        if (getResults().getStandardOutput().length() > 0) {
            addTab("Standard output", new Action<Element>() {
                public void execute(Element element) {
                    renderStdOut(element);
                }
            });
        }
        if (getResults().getStandardError().length() > 0) {
            addTab("Standard error", new Action<Element>() {
                public void execute(Element element) {
                    renderStdErr(element);
                }
            });
        }
    }
}
