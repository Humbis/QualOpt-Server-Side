# QualOpt-Server-Side
Server side code for the QualOpt project

This is a JAX RS web service that uses a MySQL database to store and retrieve user data. The web service allows researchers to sign up,
login and create studies. Researchers can also select and email participants of the study using filters. Participants may opt out of future
emails by clicking a link to unsubscribe.

To test this code, use Eclipse IDE (or any IDE that supports Maven) to build the project. Currently I use Glassfish 4 to host the web service
on localhost for testing. The database must be manually set up before use.
