@Library('Pipeline@develop')

import com.mastercard.labs.BuildPlan

labsMavenStandard(new BuildPlan([
        mavenTag: '3.6-jdk-8-slim',
        deployMtf: 'true',
        deployProd: 'true',
        pcfModule: '.',
        runSonarQube: false]))

