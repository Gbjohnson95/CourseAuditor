@echo off

for %%a in (.) do set currentfolder=%%~na
java -jar CourseAuditor.jar > %currentfolder%.csv
