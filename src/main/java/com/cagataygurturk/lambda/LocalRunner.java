package com.cagataygurturk.lambda;

import java.io.IOException;
import java.lang.reflect.*;

import com.amazonaws.services.lambda.runtime.*;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class LocalRunner<I, O> {

    public static <I, O> void main(final String[] args) throws
            Exception {

        Context context = new Context() {
            public String getAwsRequestId() {
                return null;
            }

            public String getLogGroupName() {
                return null;
            }

            public String getLogStreamName() {
                return null;
            }

            public String getFunctionName() {
                return null;
            }

            public String getFunctionVersion() {
                return null;
            }

            public String getInvokedFunctionArn() {
                return null;
            }

            public CognitoIdentity getIdentity() {
                return null;
            }

            public ClientContext getClientContext() {
                return null;
            }

            public int getRemainingTimeInMillis() {
                return 0;
            }

            public int getMemoryLimitInMB() {
                return 0;
            }

            public LambdaLogger getLogger() {
                return null;
            }
        };


        if (args.length != 1) {
            throw new RuntimeException("You should give handler class name as an argument");
        }

        String handlerClassName = args[0];

        Object object;
        try {
            Class<?> clazz = Class.forName(handlerClassName);
            Constructor<?> ctor = clazz.getConstructor();
            object = ctor.newInstance();

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(handlerClassName + " not found in classpath");
        }

        if (!(object instanceof RequestHandler)) {
            throw new RuntimeException("Request handler class does not implement " + RequestHandler.class + " interface");
        }

        @SuppressWarnings("unchecked")
        RequestHandler<I, O> requestHandler = (RequestHandler<I, O>) object;
        I requestObject = getRequestObject(requestHandler);

        try {
            O output = requestHandler.handleRequest(requestObject, context);
            System.out.println("SUCCESS: " + (new ObjectMapper().writeValueAsString(output)));
        } catch (RuntimeException e) {
            System.out.println("FAIL:");
            e.printStackTrace();
            System.exit(1);
        }

    }

    private static <I> I getRequestObject(RequestHandler handler) throws IOException {

        Type requestClass = null;

        for (Type genericInterface : handler.getClass().getGenericInterfaces()) {
            if (genericInterface instanceof ParameterizedType) {
                Type[] genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
                requestClass = genericTypes[0];
            }
        }

        if (null == requestClass) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            Method exampleEventMethod = handler.getClass().getMethod("getExampleEvent", (Class<?>[]) null);

            if (exampleEventMethod.getReturnType() != String.class) {
                throw new RuntimeException();
            }

            String json = (String) exampleEventMethod.invoke(handler);
            return mapper.readValue((String) json, mapper.getTypeFactory().constructType(requestClass));
        } catch (NoSuchMethodException | RuntimeException | IllegalAccessException | InvocationTargetException e) {
            return mapper.readValue("{}", mapper.getTypeFactory().constructType(requestClass));
        }
    }
}
