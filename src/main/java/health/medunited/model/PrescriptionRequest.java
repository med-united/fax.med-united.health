package health.medunited.model;

public class PrescriptionRequest {

    private final String practiceManagementTranslation;
    private final String fhirBundle;

    public PrescriptionRequest(String practiceManagementTranslation, String fhirBundle) {
        this.practiceManagementTranslation = practiceManagementTranslation;
        this.fhirBundle = fhirBundle;
    }

    public String getPracticeManagementTranslation() {
        return practiceManagementTranslation;
    }

    public String getFhirBundle() {
        return fhirBundle;
    }

}
