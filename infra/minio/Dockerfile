FROM scratch
COPY minio /minio
COPY ca-certificates.crt /etc/ssl/certs/ca-certificates.crt
EXPOSE 9000 9001
ENTRYPOINT ["/minio"]
