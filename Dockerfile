FROM airhacks/glassfish
COPY ./target/transactions.war ${DEPLOYMENT_DIR}
