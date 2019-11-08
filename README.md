# http4s-doobie.g8
Personal Template for quickstarting REST APIs

Bootstraps a scala REST API with the following setup:
  - http4s
  - doobie
  - circe
  - pureconfig
  - sl4j
  - scalatest
  
  It comes with 
   - Health Check
   - Routes definition with error handling for two sample resources: Users & Feedback
   - Persistence Layer for these resources
   - MySQL migration file
   - Problem model for returning error information to the caller
   - Logger
   - Config Loader per environment
