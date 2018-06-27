## Synopsis

This code base is a springboot RESTful API that accepts the necessary information and sends emails. The application provides an abstraction between two different email service providers. 

This solution caters for multiple email recipients, CCs and BCCs but there is no support for HTML email body types only plain text.


## Code Example

TBD


## Installation
Download the folder restful-ws/build/libs/gs-rest-service-0.1.0.jar

Add the correct api key and url values.

To run the springboot application use 

java -jar build/libs/gs-rest-service-0.1.0.jar

## Tests
Use curl

curl -X POST \
  http://localhost:8080/ws/email \
  -H 'cache-control: no-cache' \
  -H 'content-type: application/json' \
  -H 'postman-token: 2b02e242-40d9-3f51-cc2d-ae0d8d2385dc' \
  -d '{"from":"test@gmail.com","to":["test01@gmail.com","test02@gmail.com"],"cc":["test13@example.com","test23@example.com"],"subject":"Having fun with coding","body":"Having fun with coding and testing and blah blah blah ... "}  

Use postman

POST /ws/email HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Cache-Control: no-cache
Postman-Token: 9db34751-cf7c-4191-6b4e-86d769dee025

{"from":"test@gmail.com","to":["test01@gmail.com","test02@gmail.com"],"cc":["test13@example.com","test23@example.com"],"subject":"Having fun with coding","body":"Having fun with coding and testing and blah blah blah ... "}        

