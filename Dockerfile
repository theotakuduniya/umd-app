### Build Image ###
FROM --platform=$BUILDPLATFORM ubuntu AS BUILD_IMAGE
ARG TARGETARCH
ARG VERSION

# Dependencies
RUN apt update && apt install -y wget unzip

# Download the binary
RUN mkdir /umd
WORKDIR /umd
RUN wget https://github.com/vegidio/umd-app/releases/download/$VERSION/umd_linux_$TARGETARCH.zip
RUN unzip umd_linux_$TARGETARCH.zip

### Main Image ###
FROM ubuntu
LABEL org.opencontainers.image.source="https://github.com/vegidio/umd-app"

# Dependencies
RUN apt update && apt install -y ca-certificates && rm -rf /var/lib/apt/lists/*

# Define the image version
ENV IMAGE_VERSION=$VERSION

COPY --from=BUILD_IMAGE /umd/umd /usr/local/bin/
RUN echo "umd -d /tmp/umd \$UMD_URL" > /usr/local/bin/cmd.sh
RUN chmod +x /usr/local/bin/cmd.sh

CMD cmd.sh