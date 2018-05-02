pipeline {
    agent any //If we don't care where the script is executed

    environment {
        //Modify the env. map
        //params map is global. It's available everywhere. Also all params are added to env.
        GLOBALVAR = "This env var is set in environment{}. Available in the whole script. ${params.url} $params.url $env.url $url"
        //Value must be a "string" or a method called
        //GLOBALVAR="Set in env. for the whole script." + params.url // Not allowed to use string concat in environment{}

    }
    //no groovy allowed outside a steps/script block
    //myglobalvar="set value"


    stages {
        stage("Pull source") {
            steps {
                //Use "checkout scm" to checkout the git repo where this Jenkinsfile is located. (esb-cicd project)
                git url: 'https://github.com/bszeti/camel-springboot.git'
                // mypom = readMavenPom() // How to assign the return value of a build step to a variable without script{}?
                script {
                    // pom object is created without def, so it's available in the whole script. But these are not environment variables
                    pom = readMavenPom()
                    artifactName = pom.artifactId
                }
            }
        }

        //Can't have environment{} here

        stage("First") {
            environment {
                //These values are not available after the stage
                STAGE1VAR = "For stage1 only"
                APP_NAME = readMavenPom().getArtifactId()
                APP_NAME_2 = "$pom.artifactId"
                APP_NAME_3 = "$artifactName"
            }
            steps {
                echo "First"

                script {
                    def myvar = 'HELLO' //Local var, works only within this block
                    mynodef = "${myvar} WORLD" //Global variable available within the whole script. Variable binding happens right away.
                    println "PRINT FROM SCRIPT ${mynodef} ${System.currentTimeMillis() + 123}" //Wow, do we run groovy within a template?
                    env.SCRIPTVAR = "Defined in script" //Available in the whole script

                    //"echo" is pipeline step. Step can be used within script{}.
                    echo myvar
                    echo "myvar: ${myvar}" // Scoped variabled work within their scopr
                    echo "myvar:" + myvar
                    echo mynodef //added to template binding, but not an envvar
                    echo "mynodef: ${mynodef}"
                    echo "mynodef:" + mynodef

                    echo "env.GLOBALVAR:" + env.GLOBALVAR
                    echo "env.GLOBALVAR:" + GLOBALVAR

                    echo "Pom.artifactId:" + pom.artifactId
                    echo "Pom.artifactId: ${pom.artifactId}"
                    echo "Pom.artifactId: $pom.artifactId"
                    //echo "Pom.artifacId: $artifactId" //Doesn't work of course. Only values from env. (and params.)
                }
                // Jenkins steps can be used outside a script{}, but no groovy code
                // https://jenkins.io/doc/pipeline/steps/
                sh "env"
                //Variables available:
                //- See "Pipeline Syntax" link: env.*, params.*, openshift, etc...
                //- Environment variables and params also exist without "env." or "params."
                //- Valiables defined in script{} without "def"
                echo 'env.NODE_NAME: ${env.NODE_NAME}' //No $ binding in single quotes
                echo 'env.NODE_NAME:' + env.NODE_NAME //Any variable available at this scope can be used in step strings
                echo "env.NODE_NAME: ${env.NODE_NAME}" //${name} ${obj.field} or without {} as below
                echo "env.NODE_NAME: $env.NODE_NAME" //$name $obj.field
                echo 'env.NODE_NAME:' + NODE_NAME //All the env vars are also defined as global variables
                echo "env.NODE_NAME: $NODE_NAME"
                echo "env.NODE_NAME:" + env.NODE_NAME
                echo "env.SCRIPTVAR:" + env.SCRIPTVAR
                echo "env.SCRIPTVAR:" + SCRIPTVAR
                echo "env.SCRIPTVAR: $SCRIPTVAR"

                echo "env.STAGE1VAR:" + env.STAGE1VAR
                echo "env.STAGE1VAR:" + STAGE1VAR
                echo "env.STAGE1VAR: $STAGE1VAR"
                echo "env.GLOBALVAR:" + env.GLOBALVAR
                echo "env.GLOBALVAR:" + GLOBALVAR
                echo "env.GLOBALVAR: $GLOBALVAR"

                echo "params.url: ${params.url}"
                echo 'params.url: ${params.url}'
                echo "params.url: $params.url"
                echo "params.url:" + params.url
                echo "mynodef: ${mynodef} ${System.currentTimeMillis()}"
                // echo "myvar: $myvar" //Not available outside its scope

            }
        }
        stage("Second") {
            steps {
                script {
                    //echo "myvar in second:" +myvar //Doesn't work
                    echo "mynodef in second:" + mynodef //global var
                    //Print all available variables
                    //Need to approve step under jenkinhist/scriptApproval/
                    //binding.variables.each {k,v -> println "** $k = $v"}
                }
                echo "mynodef in second stage: ${mynodef}"
                echo "mynodef in second stage:" + mynodef
                echo "env.SCRIPTVAR:" + env.SCRIPTVAR
                echo "env.STAGE1VAR:" + env.STAGE1VAR
                echo "env.GLOBALVAR:" + env.GLOBALVAR
            }
        }
    }
}
