FROM alpine:3.22
COPY mc /usr/local/bin/mc
ENTRYPOINT ["/usr/local/bin/mc"]
