# Solar Village Project

## Overview:
Solar Village is an energy provider company. The following POC describes the functionality and implementation of the business processes associated with various steps of requesting new order permits and their approval process.


Solar Village requires a 30 to 40-hour proof of concept (POC) using JBoss BPM Suite. The POC interacts with business and technical associates at Solar Village.

## Repository Organization:
This repository consists of following modules:

### solarvillage-domainmodel
A java-maven based domain model that includes a java class for New Order Permit Requests. The jar file produced by this model is consumed by the processes created on Red Hat JBoss BPM Suite - *business-central*.

### solarvillage-mockservice
A java-SpringBoot application project that mocks the government agencies' remote online services.
- This mock services accepts new permit requests: **/submitPermitRequest**
- Provides statuses of electrical and structural permit requests individually: **/getStatusElectrical**, **/getStatusStructural**
- Set electrical and/or structural statuses: **/setAllPermit**, **/setElectricalPermit**, **/setStructuralPermit**
- Allows deletion/cancellation of permit requests: **/deletePermitRequest**
- Allows rescinding permit requests: **/rescindPermitRequest**

When a new permit request is added into the data-base the status for it is : **NOT_STARTED**

The REST services can set the statuses to: **APPROVED/DENIED/IN_PROGRESS** and BPM Suite processes consume the statuses via REST services to decide the status of the permit requests.

### SolarVillageProj
A RedHat JBoss BPM Suite based project that consume the domainmodel and mockservice, and consists of two processes - NewOrder and GovernmentPermit. The project can be build and deployed as KJar and processes could be started using business-central or intelligent server curl scripts.

#### NewOrder Process
This is the main process of the project which also acts as a wrapper to the GovernmentPermit Process. First of all, the process builds the new permit request and checks if the process requires HOA permit (if the residence is a member of Home Owner's Association - HOA):
- If the residence is a member of HOA, an user task is created where the task gets assigned to the sales.
- If by one week prior to the HOA meeting, the task doesn't get started by anyone from the *sales* group, then the task is re-assigned to the *executive* group and an email is sent out.
- The application waits until the task gets **completed**.
- If the HOA Approval is **denied**, then the permit is declined and the the decision is logged and finally the process ends.

If the HOA Approval is **approved**, then the permit continues and invokes the *GovernmentPermit* subprocess and based on it's approval/deniel decides the permit approval and logs the final decision.

Following is the process diagram for NewOrder process:
![final-solarvillageproj neworder](https://user-images.githubusercontent.com/20824893/37574769-422ff77c-2af1-11e8-8ee3-59a57db618c3.png)

#### GovernmentPermit Process
This is a subprocess that gets invoked from NewOrder process when the execution requires Government Electrical and Structural permits.
The process binds the reference-id recieved from its parent process (NewOrder) and builds a permit request requesting the government-mock-service to add an entry in the database for the reference-id. The process splits the token of execution using a parallel gateway so the electrical and structural permit approvals can execute in parallel.
The execution waits until the government permits are either approved/denied. 

A 5-seconds timer keeps the permit requests waiting until their statuses are decided. The permit is approved only if both electrical and structural permits approved. If the process is denied (at least one process was denied) then a compensation event is triggered that rescinds the permits status in the database.

Following is the process diagram for GovernmentPermit process:
![final-solarvillageproj governmentpermit](https://user-images.githubusercontent.com/20824893/37574768-421e77c2-2af1-11e8-9e56-95c5412f3312.png)

### solarvillage-clientscript
- **env_variables.sh**: Sets variables needed to run the client scripts.
- **get_container_status.sh**: Displays status of intelligent server, containers, and process-definitions.
- **post_new_order.sh**: Create and start a new order permit process instance.
- **get_process_instances.sh**: Lists all of the process-instances.
- **delete_new_order.sh**: Abort a new order permit process instance.
- **list-hoa-tasks.sh**: List of user tasks for potential and/or current owners.
- **claim-start-hoa-task.sh**: Claim and start a HOA User task.
- **complete-hoa-task.sh**: Complete a HOA user task and set whether the order is approved or not.

## Getting Started:
### Installation:
1. Download RedHat JBoss EAP 7.x from [here](https://developers.redhat.com/products/eap/download/).
2. Download RedHat JBoss BPM Suite 6.4 for EAP 7 from [here](https://developers.redhat.com/products/bpmsuite/download/).
3. Follow section 2.2 of the BPM Suite [Installation Guide](https://access.redhat.com/documentation/en-US/Red_Hat_JBoss_BPM_Suite/6.4/pdf/Installation_Guide/Red_Hat_JBoss_BPM_Suite-6.4-Installation_Guide-en-US.pdf) to install BPM Suite on top of EAP.
4. Add users:
```
$ ./add-user.sh -a --user ragrahari --password password@1 --role kie-server,admin,rest-all,analyst,sales
$ ./add-user.sh -a --user execuser --password password@1 --role kie-server,admin,rest-all,analyst,executive
-- Start the EAP:
$ ./standalone.sh
```
### Set up Email Service:
- Start CLI and connect BPMS's server management server running on 127.0.0.1:9990
```
$ cd ....bpms-eap/bin/
$ ./jboss-cli.sh -c --controller=127.0.0.1:9990
```
- Set system property value:
```
[standalone@127.0.0.1:9990] /system-property=org.kie.mail.session:add(value="java:jboss/mail/Default")
```

- Set up the main subsystem:
```
[standalone@127.0.0.1:9990] /subsystem=mail/mail-session=default:write-attribute(name=from, value=bpms@solarVillage.org)
[standalone@127.0.0.1:9990] /subsystem=mail/mail-session=default/server=smtp:write-attribute(name=username,value=admin)
[standalone@127.0.0.1:9990] /subsystem=mail/mail-session=default/server=smtp:write-attribute(name=password,value=password)
```
- Configure mail-socket outbound socket binding to match settings of SMTP server listening on 127.0.0.1:2525
```
[standalone@127.0.0.1:9990] /socket-binding-group=standard-sockets/remote-destination-outbound-socket-binding=mail-smtp:write-attribute(name=host,value=localhost)
[standalone@127.0.0.1:9990] /socket-binding-group=standard-sockets/remote-destination-outbound-socket-binding=mail-smtp:write-attribute(name=port,value=2525)
[standalone@127.0.0.1:9990] exit
```
### Clone, build, install and start the *domainmodel* and *mockservice*
1. Clone the repository: https://github.com/ragrahari/neworderpermit.git 
2. Get into solarvillage-domainmodel folder and execute following: 
```
cd ~/<PATH-TO-REPO>/solarvillage-domainmodel/
mvn clean install
```
3. Get into solarvillage-mockservice folder, build and start the SpringBoot application: 
```
cd ~/<PATH-TO-REPO>/solarvillage-mockservice/
mvn clean install
mvn spring-boot:run
```

### Clone the SolarVillageProj KIE Project
1. Login to business-central using URL: http://localhost:8080/business-central and login using the authentication credentials previously added: ```ragrahari/password@1```
2. Add an organization unit. Go to Authoring -->  Administration and Click on Organization Unit --> Manager Organizational Units
3. Click on "Add" button to add an organization unit - ```solarvillage```
4. Go to Repositories --> Clone repository. Provide following information on the pop-up window:
```
Repository Name: neworderpermit-repo (or give any name of your wish)
Organizational Unit: solarvillage
Git URL: https://github.com/ragrahari/neworderpermit.git 
Username: *Leave blank*
Password: *Leave blank*
```

### Build the SolarVillageProj
1. In *business-central*, click on Authoring --> Project Authoring and select SolarVillageProj.
2. Click on *Open Project Editor* 
3. Select *Dependencies* to add the domainmodel jar file. Click on *Add* and provide the following GAV:
```
Group ID: ragrahari
Artifact ID: solarvillage-domainmodel
Version: 0.0.1-SNAPSHOT
```
4. Save the added dependency and then click on Build --> Build and Deploy
	You can validate the build on Deploy --> Process Deployments.
5. You can start processes from Process Management --> Process Definitions screen by starting NewOrder process.

### Setup KIE Server and KIE Container
#### Setup the *system-properties*:
1. Go to ```~/<BPMS-HOME>/standalone/configuration``` and open ```standalone.xml``` file.
2. Add/Update/Uncomment the *system-properties* section with following properties:
```
<property name="org.kie.example" value="true"/>
<property name="org.jbpm.designer.perspective" value="full"/>
<property name="designerdataobjects" value="false"/>
<property name="org.kie.server.location" value="http://localhost:8080/kie-server/services/rest/server"/>
<property name="org.kie.server.controller" value="http://localhost:8080/business-central/rest/controller"/>
<property name="org.kie.server.controller.user" value="controllerUser"/>
<property name="org.kie.server.controller.pwd" value="controllerUser1234;"/>
<property name="org.kie.server.user" value="controllerUser"/>
<property name="org.kie.server.pwd" value="controllerUser1234;"/>
<property name="org.kie.server.id" value="default-kieserver"/>
<property name="org.kie.server.persistence.dialect" value="org.hibernate.dialect.H2Dialect"/>
<property name="org.kie.executor.jms.queue" value="queue/KIE.SERVER.EXECUTOR"/>
<property name="org.kie.server.persistence.ds" value="java:jboss/datasources/ExampleDS"/>
<property name="org.kie.mail.session" value="java:jboss/mail/Default"/>
```
3. Restart the server.

#### Setup a KIE Container
1. Open *business-central* and go to Deploy --> Execution Servers
2. Select the *default-kieserver* template and click on *Add Container*.
3. Fill *New Container* window with following:
```
Name: solar-village-container
Group Name: solarvillage
Artifact Id: SolarVillageProj
Version: 2.1
```
4. Click on Next and select *Per Process Instance* runtime strategy.
5. Finally Click on "Start" to start the container.

Once the container is up and running, curl client scripts can be executed from terminal.

## Running tests using client-scripts
### Scenario 1: Approval scenario for NewOrder process with approved HOA user task
1. View status of the container:
```
./get_container_status.sh
```
2. Post a new order:
```
./post_new_order.sh "4201 Spring Valley Rd" "78000" "true" "05/15/2018"
```
3. View status of the processes:
```
./get_process_instances.sh
```
4. List the HOA user tasks:
```
./list-hoa-tasks.sh sales
```
5. Claim and start a HOA user task:
```
./claim-start-hoa-task.sh 1
```
6. Complete the HOA user task:
```
./complete-hoa-task 1 "true"
```
7. Set the mocking services:
```
http://localhost:8009/setAllPermit?id=78000&electricalStatus=APPROVED&structuralStatus=APPROVED
```
Check the terminal output:
```
20:16:27,276 INFO  [stdout] (pool-35-thread-1) Electrical Permit Request approved flag: APPROVED
20:16:27,282 INFO  [stdout] (pool-35-thread-1) Electrical permit approved flag: true
20:16:27,283 INFO  [stdout] (pool-35-thread-1) Structural permit approved flag: true
20:16:27,285 INFO  [stdout] (pool-35-thread-1) Government Permit approved!
20:16:27,286 INFO  [stdout] (pool-35-thread-1) govtPermitApproved flag: true
20:16:27,357 INFO  [stdout] (pool-35-thread-1) Government permit request process completed!
20:16:27,358 INFO  [stdout] (pool-35-thread-1) Government Permit Process approval flag: true
20:16:27,361 INFO  [stdout] (pool-35-thread-1) Permit Approved for order id: 78000
```

### Scenario 2: Approval scenario for NewOrder process without HOA user task
1. View status of the container:
```
./get_container_status.sh
```
2. Post a new order:
```
./post_new_order.sh "4201 Spring Valley Rd" "93000" "false"
```
3. View status of the processes:
```
./get_process_instances.sh
```
4. Set the mocking services:
```
http://localhost:8009/setAllPermit?id=93000&electricalStatus=APPROVED&structuralStatus=APPROVED
```
Check the terminal output:
```
20:16:27,276 INFO  [stdout] (pool-35-thread-1) Electrical Permit Request approved flag: APPROVED
20:16:27,282 INFO  [stdout] (pool-35-thread-1) Electrical permit approved flag: true
20:16:27,283 INFO  [stdout] (pool-35-thread-1) Structural permit approved flag: true
20:16:27,285 INFO  [stdout] (pool-35-thread-1) Government Permit approved!
20:16:27,286 INFO  [stdout] (pool-35-thread-1) govtPermitApproved flag: true
20:16:27,357 INFO  [stdout] (pool-35-thread-1) Government permit request process completed!
20:16:27,358 INFO  [stdout] (pool-35-thread-1) Government Permit Process approval flag: true
20:16:27,361 INFO  [stdout] (pool-35-thread-1) Permit Approved for order id: 93000
```

### Scenario 3: Denial scenario for NewOrder process with denied HOA user task
1. Post a new order:
```
./post_new_order.sh "4201 Spring Valley Rd" "23000" "true" "05/15/2018"
```
2. View status of the processes:
```
./get_process_instances.sh
```
3. List the HOA user tasks:
```
./list-hoa-tasks.sh sales
```
4. Claim and start a HOA user task:
```
./claim-start-hoa-task.sh 2
```
5. Complete the HOA user task:
```
./complete-hoa-task 2 "false"
```
Check the terminal output:
```
20:29:26,998 INFO  [stdout] (default task-3) User approval: false
20:29:26,999 INFO  [stdout] (default task-3) HOA Meeting Date: Sat May 05 00:00:00 CDT 2018
20:29:27,013 INFO  [stdout] (default task-3) HOA approval declined by the sales representative.
20:29:27,015 INFO  [stdout] (default task-3) New order permit request denied!
```

### Scenario 4: Denial scenario for NewOrder process with approved HOA user task with compensation
1. Post a new order:
```
./post_new_order.sh "4201 Spring Valley Rd" "55005" "true" "05/15/2018"
```
2. View status of the processes:
```
./get_process_instances.sh
```
3. List the HOA user tasks:
```
./list-hoa-tasks.sh sales
```
4. Claim and start a HOA user task:
```
./claim-start-hoa-task.sh 3
```
5. Complete the HOA user task:
```
./complete-hoa-task 3 "true"
```
6. Set the mocking services:
```
http://localhost:8009/setAllPermit?id=55005&electricalStatus=DENIED&structuralStatus=DENIED
```
Check the terminal output:
```
20:33:17,378 INFO  [stdout] (pool-35-thread-2) Structural Permit Request approved flag: DENIED
20:33:17,379 INFO  [stdout] (pool-35-thread-2) Electrical permit approved flag: false
20:33:17,379 INFO  [stdout] (pool-35-thread-2) Structural permit approved flag: false
20:33:17,380 INFO  [stdout] (pool-35-thread-2) Government Permit denied!
20:33:17,384 INFO  [stdout] (pool-35-thread-2) Initiated: Compensation process
20:33:17,391 INFO  [stdout] (pool-35-thread-2) Completed: Compensation process
20:33:17,402 INFO  [stdout] (pool-35-thread-2) Government permit request process completed!
20:33:17,402 INFO  [stdout] (pool-35-thread-2) Government Permit Process approval flag: false
20:33:17,405 INFO  [stdout] (pool-35-thread-2) New order permit request denied!
```

### Scenario 5: Approval scenario for NewOrder process with re-assigned, approved HOA user task
1. Post a new order:
```
./post_new_order.sh "4201 Spring Valley Rd" "23500" "true" "03/15/2018"
```
2. View status of the processes:
```
./get_process_instances.sh
```
3. List the HOA user tasks:
```
./list-hoa-tasks.sh executive
```
4. Claim and start a HOA user task:
```
./claim-start-hoa-task.sh 4
```
5. Complete the HOA user task:
```
./complete-hoa-task 4 "true"
```
6. Set the mocking services:
```
http://localhost:8009/setAllPermit?id=23500&electricalStatus=APPROVED&structuralStatus=APPROVED
```
Check the terminal output:
```
20:16:27,276 INFO  [stdout] (pool-35-thread-1) Electrical Permit Request approved flag: APPROVED
20:16:27,282 INFO  [stdout] (pool-35-thread-1) Electrical permit approved flag: true
20:16:27,283 INFO  [stdout] (pool-35-thread-1) Structural permit approved flag: true
20:16:27,285 INFO  [stdout] (pool-35-thread-1) Government Permit approved!
20:16:27,286 INFO  [stdout] (pool-35-thread-1) govtPermitApproved flag: true
20:16:27,357 INFO  [stdout] (pool-35-thread-1) Government permit request process completed!
20:16:27,358 INFO  [stdout] (pool-35-thread-1) Government Permit Process approval flag: true
20:16:27,361 INFO  [stdout] (pool-35-thread-1) Permit Approved for order id: 23500
```

### Scenario 6: Denial scenario for NewOrder process: electrical permit denied, structural permit approved with compensation
1. Post a new order:
```
./post_new_order.sh "4201 Spring Valley Rd" "37000" "false"
```
2. View status of the processes:
```
./get_process_instances.sh
```
4. Set the mocking services:
```
http://localhost:8009/setAllPermit?id=37000&electricalStatus=DENIED&structuralStatus=APPROVED
```
Check the terminal output:
```
20:51:37,902 INFO  [stdout] (pool-35-thread-3) Completed: Electrical Permit REST Request
20:51:37,906 INFO  [stdout] (pool-35-thread-3) Electrical Permit Request approved flag: DENIED
20:51:37,922 INFO  [stdout] (pool-35-thread-1) Initiated: Structural Permit REST Request
20:51:37,922 INFO  [stdout] (pool-35-thread-1) For permit request ID: 37000
20:51:37,927 INFO  [stdout] (pool-35-thread-1) Completed: Structural Permit REST Request
20:51:37,929 INFO  [stdout] (pool-35-thread-1) Structural Permit Request approved flag: APPROVED
20:51:37,930 INFO  [stdout] (pool-35-thread-1) Electrical permit approved flag: false
20:51:37,930 INFO  [stdout] (pool-35-thread-1) Structural permit approved flag: true
20:51:37,931 INFO  [stdout] (pool-35-thread-1) Government Permit denied!
20:51:37,934 INFO  [stdout] (pool-35-thread-1) Initiated: Compensation process
20:51:37,941 INFO  [stdout] (pool-35-thread-1) Completed: Compensation process
20:51:37,953 INFO  [stdout] (pool-35-thread-1) Government permit request process completed!
20:51:37,953 INFO  [stdout] (pool-35-thread-1) Government Permit Process approval flag: false
20:51:37,955 INFO  [stdout] (pool-35-thread-1) New order permit request denied!

```



## References:
- https://developers.redhat.com/products/eap/download/
- https://developers.redhat.com/products/bpmsuite/download/ 
- https://access.redhat.com/documentation/en-US/Red_Hat_JBoss_BPM_Suite/6.4/pdf/Installation_Guide/Red_Hat_JBoss_BPM_Suite-6.4-Installation_Guide-en-US.pdf 


@Author: Rupesh Agrahari
