# KRAFT - Kotlin Resource Assembly and Flow Toolkit
KRAFT is a service orchestration toolkit that allows you to define and execute 
complex workflows in a simple and efficient way. It is designed to be used in a 
microservices architecture, where each service is responsible for a specific 
task. KRAFT allows you to define dependencies between services and then takes
care of loading and processing services and service calls in the correct order,
while also ensuring services are kept in scope only as long as they are required
to prevent memory leaks.

## License
This project is provided under the [MIT license](LICENSE).