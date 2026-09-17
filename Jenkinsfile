pipeline {
    agent any

    parameters {
        choice(
            name: 'SUITE',
            choices: ['auto', 'smoke', 'regression'],
            description: 'auto = smoke on feature branches, regression on main'
        )
    }

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20'))
        disableConcurrentBuilds()
    }

    environment {
        JAVA_HOME = '/opt/java/openjdk'
        PATH = "/opt/java/openjdk/bin:${env.PATH}"
        CHROME_BIN = '/usr/lib/chromium/chromium'
        CHROMEDRIVER = '/usr/bin/chromedriver'
        HEADLESS = 'true'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Resolve suite') {
            steps {
                script {
                    def suite = params.SUITE ?: 'auto'
                    if (suite == 'auto') {
                        suite = (env.BRANCH_NAME == 'main') ? 'regression' : 'smoke'
                    }
                    env.SUITE_NAME = suite
                    env.SUITE_XML = "src/test/resources/suites/${suite}.xml"
                    echo "Branch=${env.BRANCH_NAME} suite=${suite}"
                }
            }
        }

        stage('Test') {
            steps {
                sh '''
                    set -eu
                    chmod +x mvnw
                    ./mvnw -B test -DsuiteXmlFile="${SUITE_XML}" -Dheadless=true
                '''
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
            archiveArtifacts allowEmptyArchive: true,
                artifacts: 'reports/**,logs/**,target/surefire-reports/**'
            publishHTML(target: [
                allowMissing         : true,
                alwaysLinkToLastBuild: true,
                keepAll              : true,
                reportDir            : 'reports',
                reportFiles          : 'extent-report.html',
                reportName           : 'Extent Report',
                includes             : '**/*',
                escapeUnderscores    : false
            ])
        }
    }
}
