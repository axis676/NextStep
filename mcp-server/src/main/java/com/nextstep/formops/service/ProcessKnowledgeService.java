package com.nextstep.formops.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextstep.formops.config.FormOpsProperties;
import com.nextstep.formops.model.FormRegistryCatalog;
import com.nextstep.formops.model.FormRegistryEntry;
import com.nextstep.formops.model.FormSchema;
import com.nextstep.formops.model.FormSchemaCatalog;
import com.nextstep.formops.model.ProcessCatalog;
import com.nextstep.formops.model.ProcessTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProcessKnowledgeService {
    private final ProcessCatalog processCatalog;
    private final FormRegistryCatalog formRegistryCatalog;
    private final FormSchemaCatalog formSchemaCatalog;

    public ProcessKnowledgeService(ObjectMapper objectMapper, FormOpsProperties properties) throws IOException {
        Path configDir = resolveConfigDir(properties.configDir());
        this.processCatalog = readJson(objectMapper, configDir.resolve("process-template.json"), ProcessCatalog.class);
        this.formRegistryCatalog = readJson(objectMapper, configDir.resolve("form-registry.json"), FormRegistryCatalog.class);
        this.formSchemaCatalog = readJson(objectMapper, configDir.resolve("form-field-schema.json"), FormSchemaCatalog.class);
    }

    public ProcessTemplate defaultProcess() {
        return processCatalog.processes().stream()
                .min(Comparator.comparing(ProcessTemplate::processCode))
                .orElseThrow(() -> new IllegalStateException("No process template configured."));
    }

    public ProcessTemplate processByCode(String processCode) {
        return processCatalog.processes().stream()
                .filter(process -> process.processCode().equals(processCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown processCode: " + processCode));
    }

    public Optional<FormRegistryEntry> formRegistry(String formCode) {
        return formRegistryCatalog.forms().stream()
                .filter(form -> form.formCode().equals(formCode))
                .findFirst();
    }

    public FormSchema formSchema(String formCode) {
        FormSchema schema = formSchemaCatalog.forms().get(formCode);
        if (schema == null) {
            throw new IllegalArgumentException("Unknown formCode in schema: " + formCode);
        }
        return schema;
    }

    private static Path resolveConfigDir(String configuredPath) {
        List<Path> candidates = List.of(
                Path.of(configuredPath),
                Path.of("config"),
                Path.of("../config")
        );

        return candidates.stream()
                .map(Path::toAbsolutePath)
                .filter(Files::isDirectory)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Cannot find config directory. Tried: " + candidates));
    }

    private static <T> T readJson(ObjectMapper objectMapper, Path path, Class<T> type) throws IOException {
        if (!Files.exists(path)) {
            throw new IllegalStateException("Missing config file: " + path);
        }
        return objectMapper.readValue(path.toFile(), type);
    }
}
