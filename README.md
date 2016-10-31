# AWS Lambda Local Runner (JAVA)

This is a AWS Lambda JAVA simulator that allows running and debugging Lambda functions authored in JAVA locally. You can add this tool to your Lambda project as a provided dependency and enjoy testing locally your Lambda functions in JAVA.  

## Installation

Add this custom repository to your `pom.xml`

    <repositories>
        <repository>
            <id>cagatay-gurturk</id>
            <url>http://maven.cagataygurturk.com/releases</url>
        </repository>
    </repositories>

And add the dependency as a provided dependency:

        <dependency>
            <groupId>com.cagataygurturk</groupId>
            <artifactId>aws-lambda-local-runner</artifactId>
            <version>0.0.1</version>
            <!--
            Scope: provided makes this dependency to be excluded from deployment package
            So it extremely reduces JAR package size
            -->
            <scope>provided</scope>  
        </dependency>


And as a last step add `exec-maven plugin` to your build plugins:

        <build>
            <plugins>
            ....
                    <plugin>
                        <groupId>org.codehaus.mojo</groupId>
                        <artifactId>exec-maven-plugin</artifactId>
                        <version>1.2.1</version>
                        <configuration>
                            <mainClass>com.cagataygurturk.lambda.LocalRunner</mainClass>
                            <arguments>
                                <argument>YOUR_HANDLER_FUNCTION</argument>
                            </arguments>
                        </configuration>
                    </plugin>
            .....
            </plugins>
        <build>


Note: With provided option, you ensure that this JAR and its dependencies are not included in the JAR file of your application.

## Usage

Your handler function should implement `com.amazonaws.services.lambda.runtime.RequestHandler<I, O>` interface as it is shown in [AWS Lambda documentation](http://docs.aws.amazon.com/lambda/latest/dg/java-handler-using-predefined-interfaces.html).

In case of your handler function implements a `public String getExampleEvent()` method that returns a valid JSON Script, the return value of this method is injected to your Lambda function as an event. An example implementation would be:

    public String getExampleEvent() {
        return "{\"firstName\": \"John Doe\"}";
    }

Once you finish the configuration, in the root folder of your application fire this command:

    mvn compile exec:java

Your handler is invoked and the result is printed to the screen.

See [aws-lambda-java-boilerplate](https://github.com/cagataygurturk/aws-lambda-java-boilerplate) project for a real implementation.

## Contribution

Feel free to create an issue and send a pull request.
