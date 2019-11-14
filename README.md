# Build
mvn clean package && docker build -t com.ben/transactions .

# RUN

docker rm -f transactions || true && docker run -d -p 8080:8080 -p 4848:4848 --name transactions com.ben/transactions 