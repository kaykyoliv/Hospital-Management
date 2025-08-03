package com.kayky.domain.patient;

import com.kayky.commons.PatientUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PatientRepositoryTest {

    @Autowired
    private PatientRepository repository;

    @Test
    @DisplayName("findByEmail returns patient when email exists")
    void findByEmail_ShouldReturnPatient_WhenEmailExists(){
        var patientToSave = PatientUtils.patientToSave();
        var savedPatient = repository.save(patientToSave);

        var result = repository.findByEmail(savedPatient.getEmail()).orElseThrow();

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(savedPatient.getEmail());
    }

    @Test
    @DisplayName("findByEmail returns empty when email does not exist")
    void findByEmail_ShouldReturnEmpty_WhenEmailNotExists() {
        var email = "nonexistent@example.com";
        var result = repository.findByEmail(email);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByEmailAndIdNot returns empty when only patient with email has the same id")
    void  findByEmailAndIdNot_ShouldReturnEmpty_WhenOnlyPatientWithEmail(){
        var patientToSave = PatientUtils.patientToSave();
        var savedPatient = repository.save(patientToSave);
        var id = savedPatient.getId();
        var email = savedPatient.getEmail();

        assertThat(repository.findByEmailAndIdNot(email, id))
                .isEmpty();
    }

    @Test
    @DisplayName("findByEmailAndIdNot returns patient when email exists for a different id")
    void findByEmailAndIdNot_ShouldReturnPatient_WhenEmailExistsInAnotherId() {
        var patient1 = PatientUtils.patientToSave();
        var savedPatient1 = repository.save(patient1);

        var patient2 = PatientUtils.patientToSave();
        patient2.setEmail("test@example.com");
        var savedPatient2 = repository.save(patient2);

        var result = repository.findByEmailAndIdNot(savedPatient1.getEmail(), savedPatient2.getId());

        assertThat(result)
                .isPresent()
                .get()
                .satisfies(p -> {
                    assertThat(p.getEmail()).isEqualTo(savedPatient1.getEmail());
                    assertThat(p.getId()).isNotEqualTo(savedPatient2.getId());
                });
    }
}