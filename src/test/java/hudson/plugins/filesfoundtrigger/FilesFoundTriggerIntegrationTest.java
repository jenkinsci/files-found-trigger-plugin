package hudson.plugins.filesfoundtrigger;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import hudson.ExtensionList;
import hudson.model.Descriptor;
import hudson.model.FreeStyleProject;
import hudson.model.Hudson;

import java.util.Collections;

import org.apache.commons.lang.ObjectUtils;
import org.jvnet.hudson.test.HudsonTestCase;

/**
 * Integration test for the Files Found Trigger plugin. Each of the test methods
 * is executed inside a separate Jenkins instance.
 * 
 * @author Steven G. Brown
 */
public class FilesFoundTriggerIntegrationTest extends HudsonTestCase {

  /**
   */
  @SuppressWarnings("deprecation")
  @Override
  public void setUp() throws Exception {
    super.setUp();
    @SuppressWarnings("unchecked")
    ExtensionList<Descriptor> descriptors = Hudson.getInstance()
        .getExtensionList(Descriptor.class);
    descriptors.add(new FilesFoundTrigger.DescriptorImpl());
    descriptors.add(new FilesFoundTriggerConfig.DescriptorImpl());
  }

  /**
   */
  public void testGetConfigClassDescriptor() {
    assertThat(FilesFoundTriggerConfig.getClassDescriptor(),
        is(FilesFoundTriggerConfig.DescriptorImpl.class));
  }

  /**
   */
  public void testGetConfigInstanceDescriptor() {
    assertThat(new FilesFoundTriggerConfig("", "", "").getDescriptor(),
        is(FilesFoundTriggerConfig.DescriptorImpl.class));
  }

  /**
   * <pre>
   * http://wiki.jenkins-ci.org/display/JENKINS/Unit+Test#UnitTest-Configurationroundtriptesting
   * </pre>
   * 
   * @throws Exception
   *           on error
   */
  public void testSave() throws Exception {
    FreeStyleProject project = createFreeStyleProject();
    FilesFoundTrigger before = new FilesFoundTrigger("* * * * *", Collections
        .singletonList(new FilesFoundTriggerConfig("directory", "files",
            "ignoredFiles")));
    project.addTrigger(before);

    submit(createWebClient().getPage(project, "configure").getFormByName(
        "config"));

    FilesFoundTrigger after = project.getTrigger(FilesFoundTrigger.class);

    assertThat(ObjectUtils.toString(after), is(ObjectUtils.toString(before)));
  }
}
