# Getting Started

### Reference Documentation
# Survey Application API
Provides backend API for survey application

**Survey Backend API**

The project consist of 2 controllers. 
1. control-plane
2. data-plane 
Currently it is part of same application but it can be separated to 
support high scalability. 
- control-plane is the micro-service which serves 
administration of the survey and questions like create survey, add/edit/delete/get questions 
and get distribution report for question. 
- data-plane is the micro-service 
which serves customer requests like listing all questions and submitting survey
response.

**Assumptions:**
1. Survey need to be created before question creation
2. All the questions are mandatory in the survey
3. The application uses local memory for persisting the data.


For simplicity, User authentication and Authorization has been not
provided. As name suggests, it’s not a UI but backend.


**Prerequisites:**

-   Install JDK 11

-   Install maven 3 (settings.xml is provided in case you face issue connecting to publc repo)

**How to build:**

cd to path where application is downloaded/cloned.

-   cd market

-   mvn clean install

-   mvn spring-boot:run

**Swagger Docs:**

You can access swagger docs by accessing below URL:

<http://localhost:8080/swagger-ui.html> .

-   Click on data-plane-controller to see available API. You can even use the
    UI for calling the APIs
-   Click on control-plane-controller to see available API. You can even use the
    UI for calling the APIs
    