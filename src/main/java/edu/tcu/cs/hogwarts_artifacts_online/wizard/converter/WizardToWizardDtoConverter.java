package edu.tcu.cs.hogwarts_artifacts_online.wizard.converter;

import edu.tcu.cs.hogwarts_artifacts_online.artifact.converter.ArtifactToArtifactDtoConverter;
import edu.tcu.cs.hogwarts_artifacts_online.wizard.Wizard;
import edu.tcu.cs.hogwarts_artifacts_online.wizard.dto.WizardDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class WizardToWizardDtoConverter implements Converter<Wizard, WizardDto> {
    private final ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter;

    public WizardToWizardDtoConverter(@Lazy ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter) {
        this.artifactToArtifactDtoConverter = artifactToArtifactDtoConverter;
    }

    @Override
    public WizardDto convert(Wizard source) {
        WizardDto wizardDto = new WizardDto(
                 source.getId()
                ,source.getName()
                ,source.getNumberOfArtifacts());

        return wizardDto;
    }
}
