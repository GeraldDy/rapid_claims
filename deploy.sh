echo off
echo Cleaning and building the project...
mvn clean install

echo Deploying WAR to WebLogic autodeploy folder...
cp target/rapid_claims_api-1.1.war C:/oracle/Middleware/Oracle_Home/user_projects/domains/base_domain/autodeploy



echo Done.