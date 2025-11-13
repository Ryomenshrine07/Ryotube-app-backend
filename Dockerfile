FROM ubuntu:latest
LABEL authors="vinayak"

ENTRYPOINT ["top", "-b"]