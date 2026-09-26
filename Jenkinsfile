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
        // Matches container_name in ci/jenkins/docker-compose.yml.
        JENKINS_CONTAINER = 'sdet-jenkins'
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
                    def safeBranch = (env.BRANCH_NAME ?: 'local').toLowerCase().replaceAll('[^a-z0-9_.-]', '-')
                    env.TEST_IMAGE = "sdet-tests:${safeBranch}-${env.BUILD_NUMBER}"
                    echo "Branch=${env.BRANCH_NAME} suite=${suite} image=${env.TEST_IMAGE}"
                }
            }
        }

        stage('Build test image') {
            steps {
                // The Docker engine is on the host, so it cannot read this workspace
                // path. Stream the context through the socket instead.
                sh '''
                    set -euo pipefail
                    tar -czf - \
                      --exclude=target \
                      --exclude=reports \
                      --exclude=logs \
                      --exclude=.git \
                      --exclude=.chrome \
                      --exclude=.cursor \
                      . | docker build -t "${TEST_IMAGE}" -
                '''
            }
        }

        stage('Test') {
            steps {
                sh '''
                    set -euo pipefail
                    docker run --rm \
                      --shm-size=2g \
                      --volumes-from "${JENKINS_CONTAINER}" \
                      -v sdet-m2:/root/.m2 \
                      -w "${WORKSPACE}" \
                      -e SUITE_XML \
                      "${TEST_IMAGE}" \
                      sh -c 'chmod +x mvnw && ./mvnw -B test -DsuiteXmlFile="${SUITE_XML}" -Dheadless=true; status=$?; chmod -R a+rwX target reports logs 2>/dev/null || true; exit $status'
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
            sh '''
                if [ -n "${TEST_IMAGE:-}" ]; then
                    docker rmi "${TEST_IMAGE}" || true
                fi
            '''
        }
    }
}
