package org.tennis_bird.core.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tennis_bird.core.entities.PersonEntity;
import org.tennis_bird.core.repositories.PersonRepository;
import org.tennis_bird.core.services.email.EmailValidationService;

@Component
@Transactional
public class PersonService implements UserDetailsService {

    @Autowired
    private EmailValidationService emailValidator;
    @Autowired
    private PersonRepository repository;
    private static final Logger logger = LogManager.getLogger(PersonService.class.getName());

    public PersonEntity create(PersonEntity person) {
        logger.info("create person with uuid {}", person.getUuid());
        // TODO make optional
        if(emailValidator.isValid(person.getMailAddress())){
            return repository.save(person);
        }
        else{
            return null;
        }
    }

    public Optional<PersonEntity> update(PersonEntity person) {
        if (repository.findById(person.getUuid()).isPresent()) {
            logger.info("update person {}", person);
            return Optional.of(repository.save(person));
        }
        logger.info("try update person but it not exist {}", person);
        return Optional.empty();
    }

    public Optional<PersonEntity> find(UUID personUuid) {
        logger.info("find person with uuid {}", personUuid);
        return repository.findById(personUuid);
    }

    // todo make boolean?
    public PersonEntity findByEmail(String email) {
        logger.info("Finding person with email " + email);
        return repository.findByMailAddress(email).orElseThrow();
    }

    public PersonEntity findByLogin(String login) {
        logger.info("Finding person with login " + login);
        return repository.findByLogin(login).orElseThrow();
    }

    public List<PersonEntity> findAll() {
        return repository.findAll();
    }

    public Optional<PersonEntity> verifyUser(UUID uuid) {
        logger.info("Verifying user with uuid {}", uuid);
        Optional<PersonEntity> person = find(uuid);
        person.ifPresent(personEntity -> personEntity.setEmailVerified(true));
        return person;
    }

    public boolean delete(UUID uuid) {
        logger.info("delete person with uuid {}", uuid);
        if (repository.existsById(uuid)) {
            repository.deleteById(uuid);
            return true;
        }
        return false;
    }

    @Override
    public PersonEntity loadUserByUsername(String login) throws UsernameNotFoundException {
        PersonEntity person = findByLogin(login);
        return person;
    }
}
