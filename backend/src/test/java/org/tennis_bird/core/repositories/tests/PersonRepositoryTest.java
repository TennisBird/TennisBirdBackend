package org.tennis_bird.core.repositories.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.tennis_bird.core.entities.PersonEntity;
import org.tennis_bird.core.repositories.PersonRepository;
import org.tennis_bird.core.repositories.TestRepositorySupport;

class PersonRepositoryTest extends TestRepositorySupport {
    @Autowired
    PersonRepository personRepository;

    @Test
    void testCreateAndFindPerson() {
        PersonEntity person = savePersonEntity();
        Assertions.assertTrue(personRepository.findById(person.getUuid()).isPresent());
    }

    @Test
    void testDeletePerson() {
        PersonEntity person = savePersonEntity();
        Assertions.assertTrue(personRepository.findById(person.getUuid()).isPresent());

        personRepository.delete(person);
        Assertions.assertFalse(personRepository.findById(person.getUuid()).isPresent());
    }
}
