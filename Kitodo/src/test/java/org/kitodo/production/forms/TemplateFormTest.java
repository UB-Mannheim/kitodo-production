/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.kitodo.production.forms;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.kitodo.data.database.beans.Folder;
import org.kitodo.data.database.beans.Project;
import org.kitodo.data.database.beans.Task;
import org.kitodo.data.database.beans.Template;

public class TemplateFormTest {
    @Test
    public void testGettingGeneratableFolderSwitches() {
        Project projectWithoutSourceFolder = new Project();
        Folder folderInProjectWithoutSourceFolder = new Folder();
        folderInProjectWithoutSourceFolder.setId(1);
        folderInProjectWithoutSourceFolder.setPath("folderInProjectWithoutSourceFolder");
        folderInProjectWithoutSourceFolder.setImageScale(1.0);
        projectWithoutSourceFolder.getFolders().add(folderInProjectWithoutSourceFolder);
        List<Project> projects = new ArrayList<>();
        projects.add(projectWithoutSourceFolder);

        Project project = new Project();
        List<Folder> projectFolders = project.getFolders();
        Folder sourceFolder = new Folder();
        sourceFolder.setId(2);
        sourceFolder.setPath("sourceFolder");
        sourceFolder.setImageScale(1.0);
        projectFolders.add(sourceFolder);
        project.setGeneratorSource(sourceFolder);
        Folder folderWhichHasNothingToBeGenerated = new Folder();
        folderWhichHasNothingToBeGenerated.setId(3);
        folderWhichHasNothingToBeGenerated.setPath("folderWhichHasNothingToBeGenerated");
        projectFolders.add(folderWhichHasNothingToBeGenerated);
        Folder folderToBeGenerated = new Folder();
        folderToBeGenerated.setId(4);
        folderToBeGenerated.setPath("folderToBeGenerated");
        folderToBeGenerated.setImageScale(1.0);
        projectFolders.add(folderToBeGenerated);
        projects.add(project);

        Template template = new Template();
        template.setProjects(projects);
        TemplateForm templateForm = new TemplateForm();
        templateForm.setTemplate(template);
        Task task = new Task();
        task.setContentFolders(new ArrayList<>());
        templateForm.setTask(task);

        List<FolderProcessingSwitch> testOutcome = templateForm.getGeneratableFolderSwitches();
        List<String> switchesGenerated = testOutcome.parallelStream().map(folderSwitch -> folderSwitch.getLabel())
                .collect(Collectors.toList());

        assertFalse(switchesGenerated.contains("folderInProjectWithoutSourceFolder"));
        assertFalse(switchesGenerated.contains("sourceFolder"));
        assertFalse(switchesGenerated.contains("folderWhichHasNothingToBeGenerated"));
        assertTrue(switchesGenerated.contains("folderToBeGenerated"));
    }

    @Test
    public void testGettingValidatableFolderSwitches() {
        List<Project> projects = new ArrayList<>();
        Project project = new Project();
        List<Folder> projectFolders = project.getFolders();
        Folder folderToBeValidated = new Folder();
        folderToBeValidated.setId(1);
        folderToBeValidated.setPath("folderToBeValidated");
        projectFolders.add(folderToBeValidated);
        projects.add(project);

        Template template = new Template();
        template.setProjects(projects);
        TemplateForm templateForm = new TemplateForm();
        templateForm.setTemplate(template);
        Task task = new Task();
        task.setValidationFolders(new ArrayList<>());
        templateForm.setTask(task);

        List<FolderProcessingSwitch> testOutcome = templateForm.getValidatableFolderSwitches();
        List<String> switchesGenerated = testOutcome.parallelStream().map(folderSwitch -> folderSwitch.getLabel())
                .collect(Collectors.toList());

        assertTrue(switchesGenerated.contains("folderToBeValidated"));
    }
}
