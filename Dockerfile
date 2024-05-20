# Launches Alpine OS, installs python3 in it and runs the hello.py after the container startup

# Based on the latest version of the alpine image
FROM alpine:latest 

# Responsible
MAINTAINER Varun Shrivastava

# Updates the package index and installs python3 in the alpine container
RUN apk --update add python3

# Copies the hello-docker.py file to the image
COPY hello-docker.py /opt/

# Executes python3 with /opt/hello-docker.py as the only parameter
CMD ["python3", "/opt/hello-docker.py"]
