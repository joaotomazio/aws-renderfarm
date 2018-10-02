

# RenderFarm

CNV 2016-2017

### Group 21

* João Leite, ist177907
* António Freire, ist177969
* João Tomázio, ist178039


### Project structure

* Our project is in a parent maven project (renderfarm).

* This project as multiple sub-projects (maven modules):

    * `loadbalancer`: project to receive the entry point http requests
    * `webserver`: project to receive the loadbalancer requests and call the instrumented raytracer
    * `mss`: dependency project to handle the DynamoDB read/writes and Amazon EC2 Instances API calls

* The `loadbalancer` and `webserver` project:
 
    * have the `mss` project as dependency 
    * and are packaged as a JAR file to be deployed to a EC2 Instance
    
* All configuration scripts are located in `scripts` folder inside every sub-project:
    
    * `deploy.sh` to package the JAR and send all files needed to a EC2 Instance
    * `setup.sh` to run on remote server to setup the EC2 Instance
    * `rc.local` used by `setup.sh` to change `/etc/rc.local` contents on server


### Setup project

Read `.md` files in `docs` folder in parent project

(files are ordered)


### Script to make requests

Run script in `loadbalancer/scripts/requests.sh`


