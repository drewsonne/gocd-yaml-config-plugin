package cd.go.plugin.config.yaml.transforms;

import cd.go.plugin.config.yaml.YamlConfigException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import static cd.go.plugin.config.yaml.YamlUtils.*;
import static cd.go.plugin.config.yaml.transforms.EnvironmentVariablesTransform.JSON_ENV_VAR_FIELD;

public class TemplateTransform {
    private static final String JSON_TEMPLATE_NAME_FIELD = "name";
    private static final String JSON_TEMPLATE_STAGES_FIELD = "stages";

    private static final String YAML_TEMPLATE_STAGES_FIELD = "stages";

    private final StageTransform stageTransform;

    public TemplateTransform(StageTransform stageTransform) {
        this.stageTransform = stageTransform;
    }

    public JsonObject transform(Object maybeTemplate) {
        Map<String, Object> map = (Map<String, Object>) maybeTemplate;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            return transform(entry);
        }
        throw new RuntimeException("expected template hash to have 1 item");
    }

    public JsonObject transform(Map.Entry<String, Object> entry) {
        String templateName = entry.getKey();
        JsonObject template = new JsonObject();
        template.addProperty(JSON_TEMPLATE_NAME_FIELD, templateName);
        Map<String, Object> templateMap = (Map<String, Object>) entry.getValue();

        addStages(template, templateMap);

        return template;
    }

    private void addStages(JsonObject pipeline, Map<String, Object> templateMap) {
        Object stages = templateMap.get(YAML_TEMPLATE_STAGES_FIELD);
        if (stages == null || !(stages instanceof List))
            throw new YamlConfigException("expected a list of template stages");
        List<Object> stagesList = (List<Object>) stages;
        JsonArray stagesArray = transformStages(stagesList);
        pipeline.add(JSON_TEMPLATE_STAGES_FIELD, stagesArray);
    }

    private JsonArray transformStages(List<Object> stagesList) {
        JsonArray stagesArray = new JsonArray();
        for (Object stage : stagesList) {
            stagesArray.add(stageTransform.transform(stage));
        }
        return stagesArray;
    }

}
