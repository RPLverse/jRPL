pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh './gradlew clean build -x test'
            }
        }
        stage('Test') {
            steps {
                sh './gradlew test'
            }
        }
    }
    post {
        always {
            junit 'build/test-results/test/*.xml'
            archiveArtifacts artifacts: 'build/reports/tests/test/**, build/gen-classes/**/*.class, build/distributions/*', allowEmptyArchive: true
        }
    }
}
