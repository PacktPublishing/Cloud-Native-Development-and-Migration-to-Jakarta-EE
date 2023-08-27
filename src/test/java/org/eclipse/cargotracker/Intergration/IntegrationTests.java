package org.eclipse.cargotracker.Intergration;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;

import static io.restassured.RestAssured.given;

@Testcontainers
public class IntegrationTests {

    static MountableFile warFile = MountableFile.forHostPath(
            Paths.get("target/cargo-tracker.war").toAbsolutePath(), 0777);
    @Container
    static GenericContainer CargoContainer =
            new GenericContainer("payara/server-full:6.2023.2-jdk17")
                    .withCopyFileToContainer(warFile, "/opt/payara/deployments/cargo-tracker.war")
                    .withExposedPorts(8080)
                    .waitingFor(Wait.forHttp("/cargo-tracker"));

    @Test
    public void isDeployed(){
        Assertions.assertTrue(CargoContainer.isRunning());
    }

    @Test
    public void verify(){
        List<LinkedHashMap<String, Object>> cargoResult = given().get(String.format("http://localhost:%d/cargo-tracker/rest/cargo",
                        CargoContainer.getMappedPort(8080)))
                .then()
                .assertThat().statusCode(200)
                .and()
                .contentType(ContentType.JSON)
                .extract()
                .as(List.class);

        Assertions.assertEquals(4, cargoResult.size());

        cargoResult.forEach(cargo -> {
            Assertions.assertEquals(cargo.size(), 7);
        });
    }
}