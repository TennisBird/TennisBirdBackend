package org.tennis_bird.api.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.tennis_bird.api.ControllersTestSupport;
import org.tennis_bird.api.data.WorkerInfoResponse;
import org.tennis_bird.core.repositories.PersonRepository;
import org.tennis_bird.core.repositories.TeamRepository;
import org.tennis_bird.core.repositories.WorkerRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static uk.org.webcompere.modelassert.json.JsonAssertions.assertJson;

public class WorkerControllerTests extends ControllersTestSupport {
    @Autowired
    private WorkerRepository workerRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private TeamRepository teamRepository;

    @Override
    protected boolean testWithCorrectUuid() {
        return false;
    }

    @BeforeEach
    public void resetDb() {
        personRepository.deleteAll();
        teamRepository.deleteAll();
        workerRepository.deleteAll();
    }

    @Test
    public void testCreateWorker() throws Exception {
        String personUuid = mapper.readTree(createPerson()).get("uuid").asText();
        String teamId = mapper.readTree(createTeam(TEAM_NAME)).get("id").asText();
        assertCorrectWorkerBodyResponse(personUuid, teamId, createWorker(personUuid, teamId));
    }
    @Test
    public void testGetWorker() throws Exception {
        String personUuid = mapper.readTree(createPerson()).get("uuid").asText();
        String teamId = mapper.readTree(createTeam(TEAM_NAME)).get("id").asText();
        String id = mapper.readTree(createWorker(personUuid, teamId)).get("id").asText();
        assertCorrectWorkerBodyResponse(personUuid, teamId, getWorker(id));
    }
    @Test
    public void testDeleteWorker() throws Exception {
        String personUuid = mapper.readTree(createPerson()).get("uuid").asText();
        String teamId = mapper.readTree(createTeam(TEAM_NAME)).get("id").asText();
        String id = mapper.readTree(createWorker(personUuid, teamId)).get("id").asText();
        assertCorrectWorkerBodyResponse(personUuid, teamId, getWorker(id));

        assertEquals(getResponse(delete(WORKER_BASE_URL.concat(id))), "true");
        assertEquals(NULL_RESPONSE, getWorker(id));
    }
    @Test
    public void testUpdateWorkerPersonRole() throws Exception {
        String personUuid = mapper.readTree(createPerson()).get("uuid").asText();
        String teamId = mapper.readTree(createTeam(TEAM_NAME)).get("id").asText();
        String id = mapper.readTree(createWorker(personUuid, teamId)).get("id").asText();
        assertEquals(mapper.readTree(getWorker(id)).get("personRole").asText(), "developer");

        getResponse(put(WORKER_BASE_URL.concat(id).concat("/role?role=backend_developer")));
        assertEquals(mapper.readTree(getWorker(id)).get("personRole").asText(), "backend_developer");
    }

    protected void assertCorrectWorkerBodyResponse(String personId, String teamId, String responseBody) throws Exception {
        String personBody = getPerson(personId);
        String teamBody = getTeam(teamId);
        assertJson(mapper.readTree(responseBody))
                .where()
                .path("id").isIgnored()
                .path("person").isEqualTo(mapper.readTree(personBody))
                .path("team").isEqualTo(mapper.readTree(teamBody))
                .keysInAnyOrder()
                .arrayInAnyOrder()
                .isEqualTo(mapper.readTree("{\"id\":2, \"person\":{}, \"team\":{}, \"personRole\":\"developer\"}"));
    }
}