package com.passport.creditpassport;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularStructureTest {

    // Point to main @SpringBootApplication class
    ApplicationModules modules =
            ApplicationModules.of(CreditPassportApplication.class);

    @Test
    void verifyModularStructure() {
        modules.verify(); // ✅ fails if bad dependencies or cycles exist
    }

    @Test
    void printModuleStructure() {
        // prints each module's contents to console — useful for debugging
        modules.forEach(System.out::println);
    }

    @Test
    void writeDocumentationDiagrams() {
        new Documenter(modules)
                .writeModulesAsPlantUml()          // whole app — C4 diagram
                .writeIndividualModulesAsPlantUml() // one diagram per module
                .writeModuleCanvases();             // table of beans/events per module
    }
}