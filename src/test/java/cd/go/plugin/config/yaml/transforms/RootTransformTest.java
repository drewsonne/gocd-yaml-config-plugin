package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.JsonConfigCollection;
import cd.go.plugin.config.yaml.YamlConfigException;
import com.esotericsoftware.yamlbeans.YamlReader;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static cd.go.plugin.config.yaml.TestUtils.readYamlObject;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class RootTransformTest {
    private RootTransform rootTransform;
    private PipelineTransform pipelineTransform;
    private EnvironmentsTransform environmentsTransform;
    private TemplateTransform templatesTransform;

    @Before
    public void Setup() {
        pipelineTransform = mock(PipelineTransform.class);
        environmentsTransform = mock(EnvironmentsTransform.class);
        templatesTransform = mock(TemplateTransform.class);
        rootTransform = new RootTransform(pipelineTransform, environmentsTransform, templatesTransform);
    }

    @Test
    public void shouldTransformEmptyRoot() throws IOException {
        JsonConfigCollection empty = readRootYaml("empty");
        assertThat(empty.getJsonObject().isJsonObject(), is(true));
        assertThat(empty.getJsonObject().get("environments").isJsonArray(), is(true));
        assertThat(empty.getJsonObject().get("pipelines").isJsonArray(), is(true));
        assertThat(empty.getJsonObject().get("environments").getAsJsonArray().size(), is(0));
        assertThat(empty.getJsonObject().get("pipelines").getAsJsonArray().size(), is(0));
    }

    @Test
    public void shouldTransformRootWhen2PipelinesAnd2Environments() throws IOException {
        JsonConfigCollection collection = readRootYaml("2");
        assertThat(collection.getJsonObject().get("environments").getAsJsonArray().size(), is(2));
        assertThat(collection.getJsonObject().get("pipelines").getAsJsonArray().size(), is(2));
    }

    @Test(expected = YamlReader.YamlReaderException.class)
    public void shouldNotTransformRootWhenYAMLHasDuplicateKeys() throws IOException {
        readRootYaml("duplicate.materials.pipe");
        fail("should have thrown duplicate keys error");
    }

    private JsonConfigCollection readRootYaml(String caseFile) throws IOException {
        return rootTransform.transform(readYamlObject("parts/roots/" + caseFile + ".yaml"), "test code");
    }
}